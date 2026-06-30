package endorsement;

import agent.NewsSource;
import inputManager.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Endorsements {
    private final List<Endorsement> endors;

    public Endorsements() {
        endors = new ArrayList<>();
    }

    public Endorsements(List<Endorsement> endors) {
        this.endors = endors;
    }

    public void add(Endorsement endor) {
        endors.add(endor);
    }

    public void clear() {
        endors.clear();
    }

    public void addAll(Endorsements endors) {
        this.endors.addAll(endors.endors);
    }

    private Endorsements filter(Predicate<Endorsement> filter) {
        return new Endorsements(endors.stream().filter(filter).collect(Collectors.toList()));
    }

    public void forEach(Consumer<Endorsement> fun) {
        endors.iterator().forEachRemaining(fun);
    }

    public Endorsements filterByMemory(int period) {
        return filter(endor -> Configuration.MEMORY == Configuration.MEMORY_INFINITE ||
                endor.getPeriod() > period - Configuration.MEMORY);
    }

    public Endorsements filterByPeriod(int period) {
        return filter(endor -> endor.getPeriod() == period);
    }

    public Endorsements filterByNewsSource(NewsSource newsSource) {
        return filter(endor -> endor.getNewsSource().getName().equals(newsSource.getName()));
    }

    public Endorsements removeByAttribute (String attName)  {
        return filter(endor -> !endor.getAttributeName().equals(attName));
    }

    public double[] toArray() {
        double[] values = new double[endors.size()];

        for (int i = 0; i < endors.size(); ++i) {
             values[i] = endors.get(i).getValue();
        }
        return values;
    }

    public NewsSource getSelectedNewsSource(int period){
        List<Endorsement> periodTransaction = filterByPeriod(period).removeByAttribute("WORD OF MOUTH").endors;
        //System.out.println("getSelectedNewsSource:"+periodTransaction.get(0).getNewsSource().getName());

        return periodTransaction.size() > 0? periodTransaction.get(0).getNewsSource(): null;
    }

    public int size() {
        return endors.size();
    }
}
