package inputManager;

import java.util.ArrayList;
import java.util.Arrays;

public class InnerNewsSource {
    public final String name;
    public final double reach;
    public final ArrayList<String> attributeNames;
    public final ArrayList<Double[]> attributeValues;

    InnerNewsSource(String name, double reach) {
        this.name = name;
        this.reach = reach;
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
        text.append("{reach:").append(reach).append("}");

        for (int i = 0; i < attributeNames.size(); ++i) {
            String result = Arrays.toString(attributeValues.get(i));
            text.append("{").append(attributeNames.get(i)).append(":").append(result).append("}");
        }

        return text.toString();
    }
}
