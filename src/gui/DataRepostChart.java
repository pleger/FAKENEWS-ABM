package gui;

import agent.NewsSource;
import reporter.Reporter;
import reporter.RepostsPerSourceData;
import simulation.Simulation;

import java.util.ArrayList;
import java.util.List;

public class DataRepostChart {
    private final String name;
    private final List<Integer> xData;
    private final List<Integer> yData;

    public DataRepostChart(String name, List<Integer> xData, List<Integer> yData) {
        this.name = name;
        this.xData = xData;
        this.yData = yData;
    }

    public List<Integer> getXData() {
        return xData;
    }

    public List<Integer> getYData() {
        return yData;
    }

    public String getName() {
        return name;
    }

    public static DataRepostChart[] createDataRepostChart(List<NewsSource> newsSources) {
        DataRepostChart[] dataRepostChart = new DataRepostChart[newsSources.size()];
        String[] name = new String[newsSources.size()];
        ArrayList<Integer>[] xData = new ArrayList[newsSources.size()];
        ArrayList<Integer>[] yData = new ArrayList[newsSources.size()];

        List<? extends RepostsPerSourceData> data = Reporter.getRepostsPerSourceData();

        for (int i = 0; i < newsSources.size(); ++i) {
            name[i] = newsSources.get(i).getName();
            xData[i] = new ArrayList<>();
            yData[i] = new ArrayList<>();
        }

        data.iterator().forEachRemaining(reposts -> {
            if (reposts.simulationId == Simulation.ID) {
                for (int i = 0; i < newsSources.size(); ++i) {
                    xData[i].add(reposts.period);
                    yData[i].add(reposts.reposts[i]);
                }
            }
        });

        for (int i = 0; i < newsSources.size(); ++i) {
            dataRepostChart[i] = new DataRepostChart(name[i], xData[i], yData[i]);
        }
        return dataRepostChart;
    }
}
