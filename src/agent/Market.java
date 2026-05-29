package agent;

import endorsement.AttributesMarket;
import inputManager.InnerMarket;
import utils.Console;
import simulation.FlyWeight;

import java.util.HashSet;
import java.util.Set;

public class Market implements FlyWeight {
    private static int counter = 0;

    private final int ID;
    private final String name;
    private final double quota;
    private final InnerMarket innerMarket;
    private AttributesMarket attributes;
    private Set<Integer> uniqueBuyers;

    Market(InnerMarket innerMarket) {
        this.ID = counter++;
        this.name = innerMarket.name;
        this.quota = innerMarket.quota;
        this.attributes = new AttributesMarket(innerMarket.attributeNames, innerMarket.attributeValues);
        this.innerMarket = innerMarket;
        reinit();
        Console.info("Market: " + this);
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

    public double getQuota() {
        return quota;
    }

    public AttributesMarket getAttributes() {
        return attributes;
    }

    public int getUniqueSales() {
        return uniqueBuyers.size();
    }

    public void addBuyers(int idBuyer) {
        uniqueBuyers.add(idBuyer);
    }

    public void setAttributes(AttributesMarket attributes) {
        this.attributes = attributes;
    }

    @Override
    public void reinit() {
        this.uniqueBuyers = new HashSet<>();
        this.attributes = new AttributesMarket(innerMarket.attributeNames, innerMarket.attributeValues);
    }

    @Override
    public String toString() {
        return "Market{" +
                "id=" + ID + "," +
                "name='" + name + '\'' + "," +
                "quota='" + quota + '\'' + "," +
                "attributes=" + attributes +
                '}';
    }
}
