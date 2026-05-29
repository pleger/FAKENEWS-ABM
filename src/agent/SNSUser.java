package agent;

import endorsement.AttributesSNSUser;
import endorsement.Endorsement;
import endorsement.EndorsementFactory;
import endorsement.Endorsements;
import gui.DataChart;
import inputManager.Configuration;
import inputManager.InnerSNSUser;
import utils.Console;
import reporter.ReportRegister;
import reporter.Reporter;
import reporter.EndorsementData;
import simulation.FlyWeight;
import simulation.Simulation;
import simulation.Step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SNSUser implements Step, FlyWeight, ReportRegister {
    private static int counter = 0;

    private final int ID;
    private final AttributesSNSUser attribute;
    private final List<SNSUser> friends;
    private final Endorsements endors;
    private List<NewsSource> knownNewsSources;

    private final DataChart data;
    private double currentNewsSourceEvaluation;

    SNSUser(InnerSNSUser ib) {
        this.ID = counter++;
        this.friends = new ArrayList<>();
        this.knownNewsSources = new ArrayList<>();
        this.endors = new Endorsements();

        ArrayList<Double[]> values = new ArrayList<>();
        for (Double value : ib.attributeValues) {
            values.add(new Double[]{value});
        }

        attribute = new AttributesSNSUser(ib.attributeNames, values);
        data = new DataChart(Integer.toString(ID));

        Console.info("SNSUser: " + this);
    }

    public void setFriends(List<SNSUser> snsUsers) {
        int friendCounter = 0;
        int friendSize = Math.min((int) (Configuration.CONTACTS * Configuration.FRIENDS), Math.max(0, snsUsers.size() - 1));

        while (friendCounter < friendSize) {
            SNSUser potentialContact = snsUsers.get((int) (Math.random() * snsUsers.size()));
            if (addFriend(potentialContact)) {
                ++friendCounter;
            }
        }
    }

    private boolean addFriend(SNSUser potentialContact) {
        if (potentialContact != this && !friends.contains(potentialContact)) {
            friends.add(potentialContact);
            return true;
        }
        return false;
    }

    public Endorsements getEndorsements() {
        return endors;
    }

    public AttributesSNSUser getAttribute() {
        return attribute;
    }

    public int getID() {
        return ID;
    }

    static void resetCounter() {
        counter = 0;
    }

    public DataChart getDataSeries() {
        return data;
    }

    public double getCurrentNewsSourceEvaluation() {
        return currentNewsSourceEvaluation;
    }

    public void setInitialEndorsements() {
        knownNewsSources.iterator().forEachRemaining(newsSource -> endors.addAll(EndorsementFactory.createInitial(-1, this, newsSource)));
    }

    public void setKnowNewsSources(List<NewsSource> newsSources) {
        this.knownNewsSources = new ArrayList<>(newsSources);
    }

    @Override
    public void doStep(int period) {
        if (knownNewsSources.size() > 0) { //snsUser could not ignore all newsSources
            endors.addAll(Interaction.interact(period, this, knownNewsSources));
            report(period);

            //adding data to draw (should be removed later)
            data.addData(period, endors.getSelectedNewsSource(period).getID());
        }
    }

    public void setCurrentEvaluation(double evaluation) {
        this.currentNewsSourceEvaluation = evaluation;
    }

    public ArrayList<EndorsementData> getEndorsementData(int period) {
        Endorsements currentEndors = endors.filterByPeriod(period);
        ArrayList<EndorsementData> endorsData = new ArrayList<>();
        currentEndors.forEach(endor -> endorsData.add(new EndorsementData(Simulation.ID, endor.getPeriod(), ID, endor.getNewsSource().getName(),
                endor.getAttributeName(), endor.getValue())));

        return endorsData;
    }

    public void receiveRecommendation(int period) {
        //System.out.println("---->RECEIVED RECOMMENDATION snsUser:" + getID() + " known newsSources:" + knownNewsSources.size() + " period:" + period);

        Map<Integer, Double> currentEvaluations = new HashMap<>();
        NewsSource recommendedMk;

        friends.iterator().forEachRemaining(friend -> {
            NewsSource newsSource = friend.getLastSelectMarked(period);
            if (newsSource != null) {
                currentEvaluations.put(newsSource.getID(), friend.getCurrentNewsSourceEvaluation());
            }
        });

        int selectedId = NewsSourceSelectionStrategies.BY_MAX(currentEvaluations);
        recommendedMk = NewsSourceFactory.getNewsSource(knownNewsSources, selectedId);
        if (recommendedMk == null) {
            //System.out.println("ADDING NOTHING:" + NewsSourceFactory.getNewsSource(selectedId).getName()+ " c_eval:"+currentEvaluations.size() + " getID:"+ID+ " period:"+period);
            recommendedMk = NewsSourceFactory.getNewsSource(selectedId);
            knownNewsSources.add(recommendedMk);
        }

        String attName = "WORD OF MOUTH";
        double mean = attribute.getValue(attName)/(2);
        endors.add(new Endorsement(period + 1, recommendedMk, attName, mean));
    }

    public NewsSource getLastSelectMarked(int period) {
        return endors.getSelectedNewsSource(period);
    }

    @Override
    public void reinit() {
        currentNewsSourceEvaluation = Double.MAX_VALUE * -1;
        endors.clear();
        friends.clear();
        knownNewsSources.clear();
    }

    @Override
    public void report(int period) {
        Reporter.addAgentDecisionData(Simulation.ID, period, getID(), getLastSelectMarked(period).getName(), this.currentNewsSourceEvaluation);
    }

    @Override
    public String toString() {
        StringBuilder attributeValue = new StringBuilder();
        StringBuilder knowMks = new StringBuilder();

        for (int i = 0; i < attribute.size(); ++i) {
            attributeValue.append(attribute.getName(i)).append("[").append(attribute.getValue(i)).append("], ");
        }

        for (NewsSource knownNewsSource : knownNewsSources) {
            knowMks.append(knownNewsSource.getName()).append(",");
        }

        return "SNSUser{" +
                "ID=" + ID +
                ", attribute=" + attributeValue +
                ", knownNewsSources={" + knowMks + "}" +
                ", currentEvaluation={" + currentNewsSourceEvaluation + "}" +
                '}';
    }
}
