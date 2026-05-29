package inputManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Markets {
    private final static ArrayList<InnerMarket> innerMarkets = new ArrayList<>();

    public static void set(HashMap<String, ArrayList<Double[]>> data, ArrayList<String> names, HashMap<String,Double> quota) {
        innerMarkets.clear();
        for (String name : names) {
            innerMarkets.add(new InnerMarket(name, quota.get(name)));
        }

        for (Map.Entry<String, ArrayList<Double[]>> entry : data.entrySet()) {
            String attributeName = entry.getKey();
            ArrayList<Double[]> values = entry.getValue();

            for (int i = 0; i < values.size(); ++i) {
                innerMarkets.get(i).addAttribute(attributeName, values.get(i));
            }
        }
    }

    public static ArrayList<InnerMarket> getInnerMarkets() {
        return innerMarkets;
    }

    public static String attributeNames() {
        InnerMarket market = innerMarkets.get(0);
        StringBuilder text = new StringBuilder();
        for (String endorName : market.attributeNames) {
            text.append(endorName).append(" ");
        }
        return text.toString();
    }


    public static int size() {
        return innerMarkets.size();
    }

    public static int attributeSize() {
        return innerMarkets.get(0).attributeNames.size();
    }

    public static String marketNames() {
        StringBuilder text = new StringBuilder();
        for (InnerMarket market : innerMarkets) {
            text.append(market.name).append(" ");
        }
        return text.toString();
    }

    public static String toStringMarkets() {
        StringBuilder text = new StringBuilder();
        for (InnerMarket market : innerMarkets) {
            text.append(market).append("\n");
        }
        return text.toString();
    }
}
