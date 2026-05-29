package endorsement;

import inputManager.Configuration;
import utils.Error;

import java.util.function.BiFunction;

public class EndorsementEvalStrategies {

    public static double[] evaluate(AttributesMarket amarkets, AttributesBuyer abuyer, BiFunction<Double[], Double, Double> strategy) {
        int attributesNumber = amarkets.size();
        double[] results = new double[attributesNumber];

        Error.setAssert(Configuration.ATTRIBUTES_M == attributesNumber, "EndorsementEvaluation: Wrong number of attributes of market");

        for (int i = 0; i < attributesNumber; ++i) {
            String nameAtt = amarkets.getName(i);
            Double[] valuesMarket = amarkets.getValues(nameAtt);
            Double valueBuyer = abuyer.getValue(nameAtt);

            results[i] = strategy.apply(valuesMarket, valueBuyer);
        }
        return results;
    }


    public static Double BY_MAX(Double[] attributes, Double mean) {
        int index = -1;
        double max = Double.MAX_VALUE*-1;

        for (int i = 0; i < Configuration.LEVELS; ++i) {
            if (max < attributes[i]) {
                max = attributes[i];
                index = i;
            }
        }

        Error.setAssert(index != -1, "Endorsement Evaluation: MAX index not found");
        return calculateEndorsementFormula(index + 1, mean, Configuration.LEVELS);
    }

    public static Double BY_PROBABILITY(Double[] attributes, Double mean) {
        double random = Math.random();
        double acc = 0;
        int index = -1;

        for (int i = 0; i < Configuration.LEVELS; ++i) {
            acc += attributes[i];
            if (acc >= random) {
                index = i;
                break;
            }
        }

        Error.setAssert(index != -1, "Endorsement: Evaluation BY_PROBABILITY index not found");
        return calculateEndorsementFormula(index + 1, mean, Configuration.LEVELS);
    }

    /**
     * @param index  index of levels
     * @param mean   mean of buyers
     * @param levels maximum of levels
     * @return Formula of @Oswaldo
     */
    private static Double calculateEndorsementFormula(int index, Double mean, int levels) {
        int k = (int) Math.floor(index - levels / 2.0);
        k = levels % 2 == 0 && k <= 0 ? k - 1 : k;
        double div = levels % 2 == 0 ? levels : levels - 1;

        double result = 0;
        if (k > 0) {
            result = mean * k * (2.0 / div);
        }

        if (k < 0) {
            result = mean * k * (1.0 / div);
        }

        return result;
    }
}
