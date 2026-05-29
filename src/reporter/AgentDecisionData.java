package reporter;

import java.util.ArrayList;
import java.util.List;

public class AgentDecisionData {
    public final int simulationId;
    public final int period;
    public final int snsUserId;
    public final String newsSourceName;
    public final double evaluation;

    public AgentDecisionData(int simulationId, int period, int snsUserId, String newsSourceName, double evaluation) {
        this.simulationId = simulationId;
        this.period = period;
        this.snsUserId = snsUserId;
        this.newsSourceName = newsSourceName;
        this.evaluation = evaluation;
    }

    public static List<String> getHeader() {
        return new ArrayList<String>() {{
            add("SimulationId");
            add("Period");
            add("UserId");
            add("Source");
            add("Evaluation");
        }};
    }
}
