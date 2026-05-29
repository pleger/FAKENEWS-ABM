package agent;

import endorsement.AttributesNewsSource;
import inputManager.InnerNewsSource;
import utils.Console;
import simulation.FlyWeight;

import java.util.HashSet;
import java.util.Set;

public class NewsSource implements FlyWeight {
    private static int counter = 0;

    private final int ID;
    private final String name;
    private final double reach;
    private final InnerNewsSource innerNewsSource;
    private AttributesNewsSource attributes;
    private Set<Integer> uniqueSNSUsers;

    NewsSource(InnerNewsSource innerNewsSource) {
        this.ID = counter++;
        this.name = innerNewsSource.name;
        this.reach = innerNewsSource.reach;
        this.attributes = new AttributesNewsSource(innerNewsSource.attributeNames, innerNewsSource.attributeValues);
        this.innerNewsSource = innerNewsSource;
        reinit();
        Console.info("NewsSource: " + this);
    }

    public int getID() {
        return ID;
    }

    static void resetCounter() {
        counter = 0;
    }

    public String getName() {
        return name;
    }

    public double getReach() {
        return reach;
    }

    public AttributesNewsSource getAttributes() {
        return attributes;
    }

    public int getUniqueReposters() {
        return uniqueSNSUsers.size();
    }

    public void addSNSUsers(int idSNSUser) {
        uniqueSNSUsers.add(idSNSUser);
    }

    public void setAttributes(AttributesNewsSource attributes) {
        this.attributes = attributes;
    }

    @Override
    public void reinit() {
        this.uniqueSNSUsers = new HashSet<>();
        this.attributes = new AttributesNewsSource(innerNewsSource.attributeNames, innerNewsSource.attributeValues);
    }

    @Override
    public String toString() {
        return "NewsSource{" +
                "id=" + ID + "," +
                "name='" + name + '\'' + "," +
                "reach='" + reach + '\'' + "," +
                "attributes=" + attributes +
                '}';
    }
}
