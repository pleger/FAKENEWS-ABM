package reporter;

import inputManager.NewsSources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RepostsPerSourceData {
    public final int simulationId;
    public final int period;
    public final int[] reposts;

    public RepostsPerSourceData(int simulationId, int period, int[] reposts) {
        this.simulationId = simulationId;
        this.period = period;
        this.reposts = reposts.clone();
    }

    public static List<String> getHeader() {
        return new ArrayList<String>() {{
            add("SimulationId");
            add("Period");
            addAll(Arrays.asList(NewsSources.newsSourceNames().split(" ")));
        }};
    }
}
