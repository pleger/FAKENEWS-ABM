package inputManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SNSUsers {
    private final static ArrayList<InnerSNSUser> INNER_SNS_USERS = new ArrayList<>();

    public static void set(HashMap<String, Double> data) {
        INNER_SNS_USERS.clear();
        InnerSNSUser prototypeUser = new InnerSNSUser();

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            String attributeName = entry.getKey();
            double value = entry.getValue();
            prototypeUser.addAttribute(attributeName, value);
        }
        INNER_SNS_USERS.add(prototypeUser);
    }

    public static int attributeSize() {
        return getPrototypeUser().attributeValues.size();
    }

    public static ArrayList<InnerSNSUser> getUsers() {
        return INNER_SNS_USERS;
    }

    public static InnerSNSUser getPrototypeUser() {
        return getUsers().get(0);
    }

    public static String toStringSNSUsers() {
        StringBuilder text = new StringBuilder();
        for (InnerSNSUser snsUser: INNER_SNS_USERS) {
            text.append(snsUser).append("\n");
        }
        return text.toString();
    }
}
