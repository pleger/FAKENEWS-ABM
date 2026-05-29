package agent;

import endorsement.AttributesBuyer;
import endorsement.Endorsement;
import endorsement.EndorsementFactory;
import endorsement.Endorsements;
import gui.DataChart;
import inputManager.Configuration;
import inputManager.InnerBuyer;
import utils.Console;
import reporter.ReportRegister;
import reporter.Reporter;
import reporter.EndorsementData;
import simulation.FlyWeight;
import simulation.Simulation;
import simulation.Step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Buyer implements Step, FlyWeight, ReportRegister {
    private static int counter = 0;

    private final int ID;
    private final AttributesBuyer attribute;
    private final List<Buyer> friends;
    private final Endorsements endors;
    private List<Market> knownMarkets;

    private final DataChart data;
    private double currentMarketEvaluation;

    Buyer(InnerBuyer ib) {
        this.ID = counter++;
        this.friends = new ArrayList<>();
        this.knownMarkets = new ArrayList<>();
        this.endors = new Endorsements();

        ArrayList<Double[]> values = new ArrayList<>();
        for (Double value : ib.attributeValues) {
            values.add(new Double[]{value});
        }

        attribute = new AttributesBuyer(ib.attributeNames, values);
        data = new DataChart(Integer.toString(ID));

        Console.info("Buyer: " + this);
    }

    public void setFriends(List<Buyer> buyers) {
        int friendCounter = 0;
        int friendSize = Math.min((int) (Configuration.CONTACTS * Configuration.FRIENDS), Math.max(0, buyers.size() - 1));

        while (friendCounter < friendSize) {
            Buyer potentialContact = buyers.get((int) (Math.random() * buyers.size()));
            if (addFriend(potentialContact)) {
                ++friendCounter;
            }
        }
    }

    private boolean addFriend(Buyer potentialContact) {
        if (potentialContact != this && !friends.contains(potentialContact)) {
            friends.add(potentialContact);
            return true;
        }
        return false;
    }

    public Endorsements getEndorsements() {
        return endors;
    }

    public AttributesBuyer getAttribute() {
        return attribute;
    }

    public int getID() {
        return ID;
    }

    static void resetCounter() {
        counter = 0;
    }

    public DataChart getDataSeries() {
        return data;
    }

    public double getCurrentMarketEvaluation() {
        return currentMarketEvaluation;
    }

    public void setInitialEndorsements() {
        knownMarkets.iterator().forEachRemaining(market -> endors.addAll(EndorsementFactory.createInitial(-1, this, market)));
    }

    public void setKnowMarkets(List<Market> markets) {
        this.knownMarkets = new ArrayList<>(markets);
    }

    @Override
    public void doStep(int period) {
        if (knownMarkets.size() > 0) { //buyer could not ignore all markets
            endors.addAll(Interaction.interact(period, this, knownMarkets));
            report(period);

            //adding data to draw (should be removed later)
            data.addData(period, endors.getSelectedMarket(period).getID());
        }
    }

    public void setCurrentEvaluation(double evaluation) {
        this.currentMarketEvaluation = evaluation;
    }

    public ArrayList<EndorsementData> getEndorsementData(int period) {
        Endorsements currentEndors = endors.filterByPeriod(period);
        ArrayList<EndorsementData> endorsData = new ArrayList<>();
        currentEndors.forEach(endor -> endorsData.add(new EndorsementData(Simulation.ID, endor.getPeriod(), ID, endor.getMarket().getName(),
                endor.getAttributeName(), endor.getValue())));

        return endorsData;
    }

    public void receiveRecommendation(int period) {
        //System.out.println("---->RECEIVED RECOMMENDATION buyer:" + getID() + " known markets:" + knownMarkets.size() + " period:" + period);

        Map<Integer, Double> currentEvaluations = new HashMap<>();
        Market recommendedMk;

        friends.iterator().forEachRemaining(friend -> {
            Market market = friend.getLastSelectMarked(period);
            if (market != null) {
                currentEvaluations.put(market.getID(), friend.getCurrentMarketEvaluation());
            }
        });

        int selectedId = MarketSelectionStrategies.BY_MAX(currentEvaluations);
        recommendedMk = MarketFactory.getMarket(knownMarkets, selectedId);
        if (recommendedMk == null) {
            //System.out.println("ADDING NOTHING:" + MarketFactory.getMarket(selectedId).getName()+ " c_eval:"+currentEvaluations.size() + " getID:"+ID+ " period:"+period);
            recommendedMk = MarketFactory.getMarket(selectedId);
            knownMarkets.add(recommendedMk);
        }

        String attName = "WORD OF MOUTH";
        double mean = attribute.getValue(attName)/(2);
        endors.add(new Endorsement(period + 1, recommendedMk, attName, mean));
    }

    public Market getLastSelectMarked(int period) {
        return endors.getSelectedMarket(period);
    }

    @Override
    public void reinit() {
        currentMarketEvaluation = Double.MAX_VALUE * -1;
        endors.clear();
        friends.clear();
        knownMarkets.clear();
    }

    @Override
    public void report(int period) {
        Reporter.addAgentDecisionData(Simulation.ID, period, getID(), getLastSelectMarked(period).getName(), this.currentMarketEvaluation);
    }

    @Override
    public String toString() {
        StringBuilder attributeValue = new StringBuilder();
        StringBuilder knowMks = new StringBuilder();

        for (int i = 0; i < attribute.size(); ++i) {
            attributeValue.append(attribute.getName(i)).append("[").append(attribute.getValue(i)).append("], ");
        }

        for (Market knownMarket : knownMarkets) {
            knowMks.append(knownMarket.getName()).append(",");
        }

        return "Buyer{" +
                "ID=" + ID +
                ", attribute=" + attributeValue +
                ", knownMarkets={" + knowMks + "}" +
                ", currentEvaluation={" + currentMarketEvaluation + "}" +
                '}';
    }
}
