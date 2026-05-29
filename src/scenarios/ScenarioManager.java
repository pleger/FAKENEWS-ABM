package scenarios;

import inputManager.Configuration;

public class ScenarioManager {

    public static void apply(int period) {
        if (Configuration.SCENARIO != Configuration.DISABLED) {
            Scenario sc = ScenarioFactory.get(Configuration.SCENARIO);
            sc.apply(period);
        }
    }
}
