package inputManager;

import java.util.ArrayList;
import java.util.Arrays;

public class InnerMarket {
    public final String name;
    public final double quota;
    public final ArrayList<String> attributeNames;
    public final ArrayList<Double[]> attributeValues;

    InnerMarket(String name, double quota) {
        this.name = name;
        this.quota = quota;
        attributeNames = new ArrayList<>();
        attributeValues = new ArrayList<>();
    }

    void addAttribute(String name, Double[] values) {
        attributeNames.add(name);
        attributeValues.add(values);
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder(name);
        text.append("{quota:").append(quota).append("}");

        for (int i = 0; i < attributeNames.size(); ++i) {
            String result = Arrays.toString(attributeValues.get(i));
            text.append("{").append(attributeNames.get(i)).append(":").append(result).append("}");
        }

        return text.toString();
    }
}
