package inputManager;

import java.util.ArrayList;

public class InnerSNSUser {
    public final ArrayList<String> attributeNames;
    public final ArrayList<Double> attributeValues;

    InnerSNSUser() {
        attributeNames = new ArrayList<>();
        attributeValues = new ArrayList<>();
    }

    public void addAttribute(String name, double value) {
        attributeNames.add(name);
        attributeValues.add(value);
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder("SNSUser");

        for (int i = 0; i < attributeNames.size(); ++i) {
            text.append("{").append(attributeNames.get(i)).append(":").append(attributeValues.get(i)).append("}");
        }

        return text.toString();
    }
}