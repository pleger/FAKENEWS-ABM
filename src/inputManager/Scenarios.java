package inputManager;

import scenarios.Scenario;
import scenarios.ScenarioFactory;

import java.util.ArrayList;

public class Scenarios {

    private static Scenario scenario;

    public static void set(String from, String to, int start, ArrayList<String> attributesName) {
       scenario = new Scenario(ScenarioFactory.CUSTOMIZED, start, from, to, attributesName);
    }

    public static Scenario getScenario() {
        return scenario;
    }

    public static void clear() {
        scenario = null;
    }
}
