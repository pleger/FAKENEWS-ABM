package scenarios;

import agent.NewsSource;
import agent.NewsSourceFactory;
import endorsement.AttributesNewsSource;
import inputManager.Configuration;
import utils.Console;

import java.util.ArrayList;
import java.util.Arrays;

public class Scenario {
    private final int id;
    private final int start;
    private final String from;
    private final String to;
    private final String[] atts;


    public Scenario(int id, int start, String from, String to, ArrayList<String> atts) {
        this.id = id;
        this.start = start;
        this.from = from;
        this.to = to;
        this.atts = atts.toArray(new String[0]);
    }

    public void apply(int period) {
        if (period == this.start || period == -1) {
            Console.info("ScenarioManager: Applying Scenario " + Configuration.SCENARIO +"  [" + this + "]");
            copyAttributes(NewsSourceFactory.getNewsSource(from), NewsSourceFactory.getNewsSource(to), atts);
        }
    }

    public int getId() {
        return id;
    }

    private static void copyAttributes(NewsSource from, NewsSource to, String[] names) {
        AttributesNewsSource attFrom = from.getAttributes();
        AttributesNewsSource attTo = to.getAttributes();
        checkAttributes(attFrom, names);

        AttributesNewsSource newAttTo = attTo.replaceAll(names, attFrom);
        checkDifference(attTo, newAttTo, names);
        to.setAttributes(newAttTo);
    }

    private static void checkAttributes(AttributesNewsSource attm, String[] names) {
        if (!attm.contains(names)) {
            Console.error("SCENARIO X: some attributes not found");
            System.exit(1);
        }
    }

    private static void checkDifference(AttributesNewsSource oldAtt, AttributesNewsSource newAtt, String[] names) {
        for (String name : names) {
            Double[] oldValues = oldAtt.getValues(name);
            Double[] newValues = newAtt.getValues(name);

            for (int j = 0; j < oldValues.length; ++j) {
                if (oldValues[j].equals(newValues[j])) {
                    String oldTextValues = Arrays.toString(oldValues);
                    String newTextValues = Arrays.toString(newValues);
                    Console.warn("No difference for " + name + " => Old values:["+ oldTextValues + "]  &  New values:[" + newTextValues + "] - Value's Index:"+j);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        for (String att : atts) {
            text.append(att).append(",");
        }

        return "Scenario{" +
                "ID=" + this.id +
                ", start=" + this.start +
                ", from=" + this.from +
                ", to=" + this.to +
                ", attributes= {" + text + "}" +
                '}';
    }
}
