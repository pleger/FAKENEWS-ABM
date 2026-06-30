package scenarios;

import inputManager.Configuration;
import inputManager.Scenarios;
import utils.Error;

import java.util.ArrayList;
import java.util.List;

public class ScenarioFactory {

    public final static int CUSTOMIZED = -2;

    private final static List<Scenario> scenarios = new ArrayList<>();

    public static Scenario get(int id) {
        if (scenarios.isEmpty()) {
            makeScenarios();
        }
        return getScenario(id);
    }

    public static void clear() {
        scenarios.clear();
    }

    private static Scenario getScenario(int id) {
        for (Scenario sc : scenarios) {
            if (sc.getId() == id) {
                return sc;
            }
        }
        Error.trigger("ScenarioFactory.getScenario: Wrong Scenario: " + Configuration.SCENARIO);
        return null;
    }


    private static void makeScenarios() {
        if (Configuration.SCENARIO == CUSTOMIZED) {
            scenarios.add(Scenarios.getScenario());
        }
    }
}
