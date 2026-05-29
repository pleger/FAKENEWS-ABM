package inputManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewsSources {
    private final static ArrayList<InnerNewsSource> innerNewsSources = new ArrayList<>();

    public static void set(HashMap<String, ArrayList<Double[]>> data, ArrayList<String> names, HashMap<String,Double> reach) {
        innerNewsSources.clear();
        for (String name : names) {
            innerNewsSources.add(new InnerNewsSource(name, reach.get(name)));
        }

        for (Map.Entry<String, ArrayList<Double[]>> entry : data.entrySet()) {
            String attributeName = entry.getKey();
            ArrayList<Double[]> values = entry.getValue();

            for (int i = 0; i < values.size(); ++i) {
                innerNewsSources.get(i).addAttribute(attributeName, values.get(i));
            }
        }
    }

    public static ArrayList<InnerNewsSource> getInnerNewsSources() {
        return innerNewsSources;
    }

    public static String attributeNames() {
        InnerNewsSource newsSource = innerNewsSources.get(0);
        StringBuilder text = new StringBuilder();
        for (String endorName : newsSource.attributeNames) {
            text.append(endorName).append(" ");
        }
        return text.toString();
    }


    public static int size() {
        return innerNewsSources.size();
    }

    public static int attributeSize() {
        return innerNewsSources.get(0).attributeNames.size();
    }

    public static String newsSourceNames() {
        StringBuilder text = new StringBuilder();
        for (InnerNewsSource newsSource : innerNewsSources) {
            text.append(newsSource.name).append(" ");
        }
        return text.toString();
    }

    public static String toStringNewsSources() {
        StringBuilder text = new StringBuilder();
        for (InnerNewsSource newsSource : innerNewsSources) {
            text.append(newsSource).append("\n");
        }
        return text.toString();
    }
}
