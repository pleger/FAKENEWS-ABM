package agent;

import inputManager.Configuration;
import inputManager.InnerBuyer;
import inputManager.Buyers;

import java.util.ArrayList;
import java.util.List;

public class BuyerFactory {

    public static List<Buyer> createFromInput() {
        ArrayList<Buyer> buyers = new ArrayList<>();
        InnerBuyer innerBuyer =  Buyers.getOneConsumer();
        Buyer.resetCounter();

        for (int i = 0; i < Configuration.AGENTS; i++) {
            buyers.add(new Buyer(innerBuyer));
        }
        return buyers;
    }
}
