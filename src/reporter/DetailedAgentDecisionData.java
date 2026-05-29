package reporter;

public class DetailedAgentDecisionData extends AgentDecisionData {

    public DetailedAgentDecisionData(int simulationId, int period, int snsUserId, String newsSourceName, double evaluation) {
        super(simulationId, period, snsUserId, newsSourceName, evaluation);
    }
}