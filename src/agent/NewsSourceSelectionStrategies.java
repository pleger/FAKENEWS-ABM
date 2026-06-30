package agent;

import utils.Error;

import java.util.Map;

public class NewsSourceSelectionStrategies {
    private static final double FALLBACK_WEIGHT = 1.0;

    public static int BY_MAX(Map<Integer, Double> evaluations) {
        int selected = -1;
        double max = Double.MAX_VALUE * -1;

        for (Map.Entry<Integer, Double> entry : evaluations.entrySet()) {
            if (max < entry.getValue()) {
                max = entry.getValue();
                selected = entry.getKey();
            }
        }

        Error.setAssert(selected != -1, "NewsSourceSelectionStrategies.BY_MAX: no newsSource selected info{size:" + evaluations.size() + ",max:" + max + "}");
        return selected;
    }

    public static int BY_PROBABILITY(Map<Integer, Double> evaluations) {
        if (evaluations.isEmpty()) {
            Error.trigger("NewsSourceSelectionStrategies.BY_PROBABILITY: no evaluations available");
        }

        int selected = -1;
        double random = Math.random();
        double sum = sum(evaluations);
        double acc = 0;

        if (sum > 0 && min(evaluations) >= 0) {
            for (Map.Entry<Integer, Double> entry : evaluations.entrySet()) {
                acc += entry.getValue() / sum;

                if (acc >= random) {
                    selected = entry.getKey();
                    break;
                }
            }
        } else {
            selected = byShiftedProbability(evaluations, random);
        }

        Error.setAssert(selected != -1, "NewsSourceSelectionStrategies.BY_PROBABILITY: no newsSource selected, info{size:" + evaluations.size() + ",acc:" + acc + ",random:" + random + "}");
        return selected;
    }

    private static int byShiftedProbability(Map<Integer, Double> evaluations, double random) {
        double min = min(evaluations);
        double sum = 0;
        double acc = 0;
        int last = -1;

        for (Map.Entry<Integer, Double> entry : evaluations.entrySet()) {
            sum += entry.getValue() - min + FALLBACK_WEIGHT;
            last = entry.getKey();
        }

        for (Map.Entry<Integer, Double> entry : evaluations.entrySet()) {
            acc += (entry.getValue() - min + FALLBACK_WEIGHT) / sum;

            if (acc >= random) {
                return entry.getKey();
            }
        }

        return last;
    }

    private static double sum(Map<Integer, Double> evaluations) {
        double sum = 0;
        for (Map.Entry<Integer, Double> entry : evaluations.entrySet()) {
            sum += entry.getValue();
        }
        return sum;
    }

    private static double min(Map<Integer, Double> evaluations) {
        double min = Double.MAX_VALUE;
        for (Map.Entry<Integer, Double> entry : evaluations.entrySet()) {
            min = Math.min(min, entry.getValue());
        }
        return min;
    }
}
