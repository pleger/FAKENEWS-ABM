package endorsement;

import agent.NewsSource;

public class Endorsement {

    private final int period;
    private final NewsSource newsSource;
    private final String attributeName;
    private final double value;

    public Endorsement(int period, NewsSource newsSource, String attributeName, double value) {
        this.period = period;
        this.newsSource = newsSource;
        this.attributeName = attributeName;
        this.value = value;
    }

    public int getPeriod() {
        return period;
    }

    public NewsSource getNewsSource() {
        return newsSource;
    }

    public double getValue() {
        return value;
    }

    public String getAttributeName() {
        return attributeName;
    }
}
