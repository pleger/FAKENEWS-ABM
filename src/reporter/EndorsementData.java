package reporter;

import java.util.ArrayList;
import java.util.List;

public class EndorsementData {
    public final int simulationId;
    public final int period;
    public final int snsUserId;
    public final String newsSourceName;
    public final String attribute;
    public final double value;

    public EndorsementData(int simulationId, int period, int snsUserId, String newsSourceName, String attribute, double value) {
        this.simulationId = simulationId;
        this.period = period;
        this.snsUserId = snsUserId;
        this.newsSourceName = newsSourceName;
        this.attribute = attribute;
        this.value = value;
    }

    public static List<String> getHeader() {
        return new ArrayList<String>() {{
            add("SimulationId");
            add("Period");
            add("UserId");
            add("Source");
            add("Attribute");
            add("Value");
        }};
    }
}
