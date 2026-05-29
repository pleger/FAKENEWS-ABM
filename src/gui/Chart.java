package gui;

import agent.SNSUser;
import agent.NewsSource;
import inputManager.Configuration;
import utils.Console;
import utils.Error;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;
import simulation.Simulation;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chart {
    private static XYChart chart;

    public static void displayReposts(List<NewsSource> newsSources) {
        Console.info("Chart: Displaying Reposts");
        createXChartDriverReposts();

        DataRepostChart[] reposts = DataRepostChart.createDataRepostChart(newsSources);
        for (DataRepostChart repost : reposts) {
            registerSeries2(repost);
        }

        if (Configuration.REPETITIONS == 0) drawChart();
        saveChart();
    }

    public static void displaySelection(List<SNSUser> snsUsers, List<NewsSource> newsSources) {
        Console.info("Chart: Displaying Selection");
        createXChartDriverSelection(newsSources);
        snsUsers.iterator().forEachRemaining(snsUser -> registerSeries(snsUser.getDataSeries()));
        if (Configuration.REPETITIONS == 0) drawChart();
        saveChart();
    }

    private static void createXChartDriverReposts() {
        chart = new XYChartBuilder().width(800).height(600).title("simulation")
                .xAxisTitle("Period").yAxisTitle("Reposts").build();
        chart.getStyler().setYAxisDecimalPattern("#0").setXAxisDecimalPattern("#0").setLegendPosition(Styler.LegendPosition.InsideNE);
    }

    private static void createXChartDriverSelection(List<NewsSource> newsSources) {
        chart = new XYChartBuilder().width(800).height(600).title("simulation")
                .xAxisTitle("Period").yAxisTitle("NewsSource").build();

        chart.getStyler().setYAxisDecimalPattern("#0").setXAxisDecimalPattern("#0").setYAxisMax(newsSources.size() * 1.0).setLegendPosition(Styler.LegendPosition.InsideNE);
        Map<Double, Object> customYAxisTickLabelsMap = new HashMap<>();
        for (NewsSource newsSource : newsSources) {
            customYAxisTickLabelsMap.put(newsSource.getID() * 1.0, newsSource.getName());
        }
        chart.setYAxisLabelOverrideMap(customYAxisTickLabelsMap);
    }

    private static void registerSeries2(DataRepostChart dataChart) {
        chart.addSeries(dataChart.getName(), dataChart.getXData(), dataChart.getYData());
    }

    private static void registerSeries(DataChart dataChart) {
        chart.addSeries(dataChart.getName(), dataChart.getXData(), dataChart.getYData());
    }

    private static void drawChart() {
        (new SwingWrapper<>(chart)).displayChart();
    }

    private static void saveChart() {
        String fileName = Configuration.OUTPUT_DIRECTORY + "/Simulation_" + Simulation.ID + "_";
        DateFormat df = new SimpleDateFormat("dd-MM-yy(HH-mm-ss)");
        fileName += df.format(new Date()) + ".png";

        try {
            Console.info("Chart: Saving chart");
            BitmapEncoder.saveBitmap(chart, fileName, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException ex) {
            Error.trigger("Image cannot be saved: " + fileName, ex);
        }
    }
}
