package reporter;

import java.util.ArrayList;
import java.util.List;

public class AgentDecisionData {
    public final int simulationId;
    public final int period;
    public final int buyerId;
    public final String marketName;
    public final double evaluation;

    public AgentDecisionData(int simulationId, int period, int buyerId, String marketName, double evaluation) {
        this.simulationId = simulationId;
        this.period = period;
        this.buyerId = buyerId;
        this.marketName = marketName;
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
