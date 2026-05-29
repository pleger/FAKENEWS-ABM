package agent;

import inputManager.InnerMarket;
import inputManager.Markets;
import utils.Error;

import java.util.ArrayList;
import java.util.List;

public class MarketFactory {

    private static ArrayList<Market> markets;

    public static List<Market> createFromInput() {
        ArrayList<InnerMarket> innerMarkets =  Markets.getInnerMarkets();
        markets = new ArrayList<>();
        Market.resetCounter();
        innerMarkets.iterator().forEachRemaining(innerMarket -> markets.add(new Market(innerMarket)));
        return markets;
    }


    public static Market getMarket(List<Market> markets, int id) {
        for (Market mk : markets) {
            if (mk.getID() == id) {
                return mk;
            }
        }
        return null;
    }

    public static Market getMarket(int id) {
        return getMarket(markets, id);
    }

    public static Market getMarket(List<Market> markets, String name) {
        int id = -1;
        for (Market mk : markets) {
            if (mk.getName().equals(name)) {
                id = mk.getID();
            }
        }
        Error.setAssert(id != -1, "ERROR. MarketFactory: no market found:"+ name);
        return getMarket(markets, id);
    }

    public static Market getMarket(String name) {
        return getMarket(markets, name);
    }

    public static ArrayList<Market> getMarkets() {return markets;}
}
