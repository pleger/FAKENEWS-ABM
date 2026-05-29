package endorsement;

import agent.SNSUser;
import agent.NewsSource;

import java.util.function.BiFunction;

public class EndorsementFactory {

    public static Endorsements createInitial(int period, SNSUser snsUser, NewsSource newsSource) {
        return create(period,snsUser,newsSource, EndorsementEvalStrategies::BY_MAX);
    }
    public static Endorsements createByStep(int period, SNSUser snsUser, NewsSource newsSource) {
        return create(period,snsUser,newsSource, EndorsementEvalStrategies::BY_PROBABILITY);
    }

    private static Endorsements create(int period, SNSUser snsUser, NewsSource newsSource, BiFunction<Double[], Double, Double> strategy) {
        Endorsements endors = new Endorsements();

        AttributesNewsSource aNewsSource = newsSource.getAttributes();
        AttributesSNSUser aSNSUser = snsUser.getAttribute();

        double[] results = EndorsementEvalStrategies.evaluate(aNewsSource, aSNSUser, strategy);

        for (int i = 0; i < results.length; ++i) {
            endors.add(new Endorsement(period, newsSource, aNewsSource.getName(i), results[i]));
        }

        return endors;
    }
}
