package agent;

import inputManager.Configuration;
import inputManager.InnerSNSUser;
import inputManager.SNSUsers;

import java.util.ArrayList;
import java.util.List;

public class SNSUserFactory {

    public static List<SNSUser> createFromInput() {
        ArrayList<SNSUser> snsUsers = new ArrayList<>();
        InnerSNSUser innerSNSUser =  SNSUsers.getPrototypeUser();
        SNSUser.resetCounter();

        for (int i = 0; i < Configuration.AGENTS; i++) {
            snsUsers.add(new SNSUser(innerSNSUser));
        }
        return snsUsers;
    }
}
