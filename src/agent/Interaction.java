package agent;

import endorsement.EndorsementFactory;
import endorsement.Endorsements;
import inputManager.Configuration;
import utils.Error;
import simulation.Simulation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interaction {

    public static Endorsements interact(int period, SNSUser snsUser, List<NewsSource> newsSources) {
        NewsSource selectedNewsSource = selectNewsSource(period, snsUser, newsSources);
        Error.setAssert(selectedNewsSource != null, "Interaction: No NewsSource selected. Selected:" + selectedNewsSource + " newsSourceSize:" + newsSources.size() + " snsUserSize:" + snsUser.getID());

        return EndorsementFactory.createByStep(period, snsUser, selectedNewsSource);
    }

    private static NewsSource selectNewsSource(int period, SNSUser snsUser, List<NewsSource> newsSources) {
        Map<Integer, Double> evaluations = new HashMap<>();

        for (NewsSource newsSource : newsSources) {
            Endorsements endors = snsUser.getEndorsements().filterByNewsSource(newsSource).filterByMemory(period);
            double eval = evaluateNewsSource(endors.toArray());
            evaluations.put(newsSource.getID(), eval);

            report(period, snsUser, newsSource, eval);
        }
        
        int idSelected = NewsSourceSelectionStrategies.BY_PROBABILITY(evaluations);
        NewsSource mkSelected = null;

        for (NewsSource mk: newsSources) {
            if (mk.getID() == idSelected) {
                mkSelected = mk;
                break;
            }
        }
        
        snsUser.setCurrentEvaluation(evaluations.get(idSelected));
        return mkSelected;
    }

    private static double evaluateNewsSource(double[] values) {
        double result = 0;

        for (double value : values) {
            result += value > 0 ? Math.pow(Configuration.BASE, value) : -1 * Math.pow(Configuration.BASE, Math.abs(value));
        }
        return result;
    }

    private static void report(int period, SNSUser snsUser, NewsSource newsSource, double eval) {
        reporter.Reporter.addDetailedAgentDecisionData(Simulation.ID, period, snsUser.getID(), newsSource.getName(), eval);
    }
}
