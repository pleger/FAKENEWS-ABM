package simulation;

import agent.SNSUser;
import agent.NewsSource;
import gui.Chart;
import inputManager.Configuration;
import utils.Console;
import reporter.ReportRegister;
import reporter.Reporter;
import scenarios.ScenarioManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Simulation implements FlyWeight, Step, ReportRegister {
    public static int ID = 0;

    private final int periods;
    private final List<SNSUser> snsUsers;
    private final List<NewsSource> newsSources;

    public Simulation(List<SNSUser> snsUsers, List<NewsSource> newsSources, int periods) {
        this.periods = periods;
        this.snsUsers = snsUsers;
        this.newsSources = newsSources;

        reinit();
        Console.info("Simulation: created with " + snsUsers.size() + " snsUsers and " + newsSources.size() + " newsSources");
    }

    @Override
    public void reinit() {
        ++Simulation.ID;
        snsUsers.iterator().forEachRemaining(SNSUser::reinit);
        snsUsers.iterator().forEachRemaining(snsUser -> snsUser.setFriends(snsUsers));
        snsUsers.iterator().forEachRemaining(snsUser -> snsUser.setKnowNewsSources(filterReach(newsSources)));
        snsUsers.iterator().forEachRemaining(SNSUser::setInitialEndorsements);

        newsSources.iterator().forEachRemaining(NewsSource::reinit);
        System.gc(); //clean memory
    }

    private List<NewsSource> filterReach(List<NewsSource> newsSources) {
        if (!Configuration.SOURCE_REACH) {
            return newsSources; //all newsSources
        }

        List<NewsSource> filteredNewsSource = new ArrayList<>();

        double random;
        for (NewsSource mk : newsSources) {
            random = Math.random();
            if (random < mk.getReach()) {
                filteredNewsSource.add(mk);
            }
        }

        return filteredNewsSource;
    }

    private void generateRepostsPerData(int period) {
        int[] reposts = new int[newsSources.size()];
        int[] uniqueReposters = new int[newsSources.size()];

        snsUsers.iterator().forEachRemaining(snsUser -> {
            NewsSource selectedNewsSource = snsUser.getLastSelectMarked(period);

            if (selectedNewsSource != null) {
                selectedNewsSource.addSNSUsers(snsUser.getID());
                reposts[selectedNewsSource.getID()]++;
            }
        });

        newsSources.iterator().forEachRemaining(newsSource -> uniqueReposters[newsSource.getID()] = newsSource.getUniqueReposters());

        Reporter.addRepostsByNewsSourceData(ID, period, reposts);
        Reporter.addRepostsUniqueByNewsSourceData(ID, period, uniqueReposters);
    }

    public void run() {
        Console.info("Simulation: Starting " + Simulation.ID + " with periods=" + periods +
                ", learningPeriods=" + Configuration.LEARNING_PERIODS +
                ", wom=" + Configuration.WOM +
                ", sourceReach=" + Configuration.SOURCE_REACH);
        logNetworkSummary();

        for (int period = 1; period <= periods; ++period) {
            Console.info("Simulation: Period " + period + "/" + periods + " started");
            doStep(period);

            ScenarioManager.apply(period);
            report(period);

            int recommendations = 0;
            if (Configuration.WOM) {
                for (SNSUser snsUser : snsUsers) {
                    if (snsUser.receiveRecommendation(period)) {
                        ++recommendations;
                    }
                }
            }

            logPeriodSummary(period, recommendations);
        }

        if (Configuration.GUI) {
            //Chart.displaySelection(snsUsers, newsSources);
            Chart.displayReposts(newsSources);
        }

        Console.info("Simulation: Completed " + Simulation.ID + " periods=" + periods);
        reinit();
    }

    private void logNetworkSummary() {
        int totalKnownSources = 0;
        int totalFriends = 0;

        for (SNSUser snsUser : snsUsers) {
            totalKnownSources += snsUser.getKnownNewsSourceCount();
            totalFriends += snsUser.getFriendCount();
        }

        double averageKnownSources = snsUsers.isEmpty() ? 0 : (double) totalKnownSources / snsUsers.size();
        double averageFriends = snsUsers.isEmpty() ? 0 : (double) totalFriends / snsUsers.size();

        Console.info("Simulation: Network ready for " + Simulation.ID +
                " users=" + snsUsers.size() +
                ", newsSources=" + newsSources.size() +
                ", avgKnownSources=" + String.format(Locale.US, "%.2f", averageKnownSources) +
                ", avgFriends=" + String.format(Locale.US, "%.2f", averageFriends));
    }

    private void logPeriodSummary(int period, int recommendations) {
        int selections = 0;
        for (SNSUser snsUser : snsUsers) {
            if (snsUser.getLastSelectMarked(period) != null) {
                ++selections;
            }
        }

        String reportingState = period > Configuration.LEARNING_PERIODS ? "saved" : "learning";
        Console.info("Simulation: Period " + period + "/" + periods +
                " completed selections=" + selections + "/" + snsUsers.size() +
                ", reportState=" + reportingState +
                ", womRecommendations=" + recommendations);
    }


    @Override
    public void doStep(int period) {
        snsUsers.iterator().forEachRemaining(snsUser -> snsUser.doStep(period));
    }

    @Override
    public void report(int period) {
       if (period > Configuration.LEARNING_PERIODS) generateRepostsPerData(period);
        snsUsers.iterator().forEachRemaining(snsUser -> Reporter.addEndorsementData(snsUser.getEndorsementData(period)));
    }

    @Override
    public String toString() {
        return "Simulation{" +
                "ID=" + Simulation.ID +
                ", periods=" + periods +
                ", snsUsers=" + snsUsers.size() +
                ", newsSources=" + newsSources.size() +
                '}';
    }
}
