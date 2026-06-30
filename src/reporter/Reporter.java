package reporter;

import agent.NewsSource;
import agent.NewsSourceFactory;
import inputManager.Configuration;
import inputManager.Loader;
import utils.Console;
import utils.Error;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import endorsement.AttributesNewsSource;
import scenarios.Scenario;
import scenarios.ScenarioFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Reporter {
    private static final List<AgentDecisionData> agentDecisionData = new ArrayList<>();
    private static final List<DetailedAgentDecisionData> detailedAgentDecisionData = new ArrayList<>();
    private static final List<EndorsementData> endorsData = new ArrayList<>();
    private static final List<RepostsPerSourceData> repostsPerNewsSourceData = new ArrayList<>();
    private static final List<UniqueRepostersPerSourceData> repostsUniquePerNewsSourceData = new ArrayList<>();

    public static void write() {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Console.info("Reporter: Adding sheets");

            writeConfiguration(workbook.createSheet("Configuration"));
            addSheet(workbook, Loader.getNewsSources());
            addSheet(workbook, Loader.getSNSUsers());
            addSheet(workbook, Loader.getSourceReach());
            if (Configuration.SCENARIO != Configuration.DISABLED) addSheet(workbook, Loader.getScenario());


            writeRepostsPerNewsSource(workbook.createSheet("RepostsPerSource"), repostsPerNewsSourceData);
            writeRepostsPerNewsSource(workbook.createSheet("UniqueRepostersPerSource"), repostsUniquePerNewsSourceData);
            writeAgentDecision(workbook.createSheet("Results"));
            writeDetailedAgentDecision(workbook.createSheet("DetailedResult"));
            writeEndorsements(workbook.createSheet("Endorsements"));
            writeScenarioChanges(workbook.createSheet("ScenarioChanges"));

            Console.info("Reporter: Writing to the disk");
            writeDisk(workbook);
        } catch (IOException ex) {
            Error.trigger("Reporter.write: output workbook could not be closed\n.ERROR: " + ex, ex);
        }
    }

    public static void clear() {
        agentDecisionData.clear();
        detailedAgentDecisionData.clear();
        endorsData.clear();
        repostsPerNewsSourceData.clear();
        repostsUniquePerNewsSourceData.clear();
    }

    private static void writeScenarioChanges(XSSFSheet scenarios) {
        boolean enabled = Configuration.SCENARIO != Configuration.DISABLED;
        Console.info("Reporter: Information of Scenario Changes: " + enabled);

        if (enabled) {
            Scenario scenario = ScenarioFactory.get(Configuration.SCENARIO);
            ArrayList<NewsSource> newsSources = NewsSourceFactory.getNewsSources();

            Row headRow = scenarios.createRow(0);
            headRow.createCell(0).setCellValue("SOURCE_NAME");
            headRow.createCell(1).setCellValue("SOURCE_ID");
            headRow.createCell(2).setCellValue("SOURCE_REACH");

            int column = 3;
            for (String attribute : newsSources.get(0).getAttributes().getNames()) {
                headRow.createCell(column).setCellValue(attribute);
                ++column;
            }

            int rowIndex = 1;
            for (NewsSource mk : newsSources) {
                Row dataRow = scenarios.createRow(rowIndex);
                dataRow.createCell(0).setCellValue(mk.getName());
                dataRow.createCell(1).setCellValue(mk.getID());
                dataRow.createCell(2).setCellValue(mk.getReach());

                column = 3;
                AttributesNewsSource attributes = scenario.attributesAfterApplyingTo(mk);
                for (String attributeName : attributes.getNames()) {
                    Double[] vals = attributes.getValues(attributeName);
                    dataRow.createCell(column).setCellValue(Arrays.toString(vals));
                    ++column;
                }
                ++rowIndex;
            }

            setReadableColumnWidths(scenarios, 3 + newsSources.get(0).getAttributes().getNames().length);
        }
    }

    public static void addEndorsementData(ArrayList<EndorsementData> endors) {
        if (Configuration.SAVED_ENDORSEMENTS) endorsData.addAll(endors);
    }

    public static void addAgentDecisionData(int simulationId, int period, int snsUserId, String newsSourceName, double evaluation) {
        if (Configuration.SAVED_AGENT_DECISIONS)
            agentDecisionData.add(new AgentDecisionData(simulationId, period, snsUserId, newsSourceName, evaluation));
    }

    public static void addDetailedAgentDecisionData(int simulationId, int period, int snsUserId, String newsSourceName, double evaluation) {
        if (Configuration.SAVED_DETAILED_AGENT_DECISIONS)
            detailedAgentDecisionData.add(new DetailedAgentDecisionData(simulationId, period, snsUserId, newsSourceName, evaluation));
    }

    public static void addRepostsByNewsSourceData(int simulationId, int period, int[] reposts) {
        if (Configuration.SAVED_REPOSTS_PER_SOURCE)
            repostsPerNewsSourceData.add(new RepostsPerSourceData(simulationId, period, reposts));
    }

    public static void addRepostsUniqueByNewsSourceData(int simulationId, int period, int[] reposts) {
        if (Configuration.SAVED_REPOSTS_PER_SOURCE)
            repostsUniquePerNewsSourceData.add(new UniqueRepostersPerSourceData(simulationId,period,reposts));
    }

    private static void writeRepostsPerNewsSource(XSSFSheet repostsPerNewsSource, List<? extends RepostsPerSourceData> reposts) {
        Console.info("Reporter: Adding Reposts Per Source: " + reposts.size());
        Row headRow = repostsPerNewsSource.createRow(0);

        int column = 0;
        for (String head : RepostsPerSourceData.getHeader()) {
            Cell cell = headRow.createCell(column);
            cell.setCellValue(head);
            ++column;
        }

        int rowIndex = 1;
        for (RepostsPerSourceData oneRow : reposts) {
            Row dataRow = repostsPerNewsSource.createRow(rowIndex);
            dataRow.createCell(0).setCellValue(oneRow.simulationId);
            dataRow.createCell(1).setCellValue(oneRow.period);

            for (int i = 0; i < oneRow.reposts.length; ++i) {
                dataRow.createCell(2 + i).setCellValue(oneRow.reposts[i]);
            }
            ++rowIndex;
        }
    }

    private static void writeDetailedAgentDecision(Sheet detailedResults) {
        Console.info("Reporter: Adding Detailed Agent Decisions: " + detailedAgentDecisionData.size());
        Row headRow = detailedResults.createRow(0);

        int column = 0;
        for (String head : DetailedAgentDecisionData.getHeader()) {
            Cell cell = headRow.createCell(column);
            cell.setCellValue(head);
            ++column;
        }

        int rowIndex = 1;
        for (DetailedAgentDecisionData oneRow : detailedAgentDecisionData) {
            Row dataRow = detailedResults.createRow(rowIndex);
            dataRow.createCell(0).setCellValue(oneRow.simulationId);
            dataRow.createCell(1).setCellValue(oneRow.period);
            dataRow.createCell(2).setCellValue(oneRow.snsUserId);
            dataRow.createCell(3).setCellValue(oneRow.newsSourceName);
            dataRow.createCell(4).setCellValue(oneRow.evaluation);
            ++rowIndex;
        }
    }

    private static void addSheet(XSSFWorkbook workbook, Sheet sheet) {
        Sheet newSheet = workbook.createSheet(sheet.getSheetName());

        for (int i = 0; i <= sheet.getLastRowNum(); ++i) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Row newRow = newSheet.createRow(i);
            for (int j = 0; j < row.getLastCellNum(); ++j) {
                Cell cell = row.getCell(j);
                if (cell == null) continue;
                String cellType = cell.getCellTypeEnum().name();
                Cell newCell = newRow.createCell(j);
                if (cellType.equalsIgnoreCase("STRING")) {
                    newCell.setCellValue(cell.getRichStringCellValue());
                }
                if (cellType.equalsIgnoreCase("NUMERIC") || cellType.equalsIgnoreCase("FORMULA")) {
                    newCell.setCellValue(cell.getNumericCellValue());
                }
                if (cellType.equalsIgnoreCase("BOOLEAN")) {
                    newCell.setCellValue(cell.getBooleanCellValue());
                }
            }
        }
    }

    public static List<? extends RepostsPerSourceData> getRepostsPerSourceData() {
        return repostsUniquePerNewsSourceData;
    }

    private static void writeEndorsements(Sheet results) {
        Console.info("Reporter: Adding endorsements: " + endorsData.size());
        Row headRow = results.createRow(0);

        int column = 0;
        for (String head : EndorsementData.getHeader()) {
            Cell cell = headRow.createCell(column);
            cell.setCellValue(head);
            ++column;
        }

        int rowIndex = 1;
        for (EndorsementData oneRow : endorsData) {
            Row dataRow = results.createRow(rowIndex);
            dataRow.createCell(0).setCellValue(oneRow.simulationId);
            dataRow.createCell(1).setCellValue(oneRow.period);
            dataRow.createCell(2).setCellValue(oneRow.snsUserId);
            dataRow.createCell(3).setCellValue(oneRow.newsSourceName);
            dataRow.createCell(4).setCellValue(oneRow.attribute);
            dataRow.createCell(5).setCellValue(oneRow.value);
            ++rowIndex;
        }
    }

    private static void writeAgentDecision(Sheet results) {
        Console.info("Reporter: Adding Agent Decisions: " + agentDecisionData.size());
        Row headRow = results.createRow(0);

        int column = 0;
        for (String head : AgentDecisionData.getHeader()) {
            Cell cell = headRow.createCell(column);
            cell.setCellValue(head);
            ++column;
        }

        int rowIndex = 1;
        for (AgentDecisionData oneRow : agentDecisionData) {
            Row dataRow = results.createRow(rowIndex);
            dataRow.createCell(0).setCellValue(oneRow.simulationId);
            dataRow.createCell(1).setCellValue(oneRow.period);
            dataRow.createCell(2).setCellValue(oneRow.snsUserId);
            dataRow.createCell(3).setCellValue(oneRow.newsSourceName);
            dataRow.createCell(4).setCellValue(oneRow.evaluation);
            ++rowIndex;
        }
    }

    private static void writeConfiguration(Sheet conf) {
        Console.info("Reporter: Adding Configuration");
        Map<String, Double> dump = Configuration.toMap();

        int rowIndex = 0;
        for (String key : dump.keySet()) {
            double value = dump.get(key);
            Row row = conf.createRow(rowIndex);
            Cell keyCell = row.createCell(0);
            Cell valueCell = row.createCell(1);
            keyCell.setCellValue(key);
            valueCell.setCellValue(value);
            ++rowIndex;
        }

        setReadableColumnWidths(conf, 2);
    }

    private static void setReadableColumnWidths(Sheet sheet, int columns) {
        for (int i = 0; i < columns; ++i) {
            sheet.setColumnWidth(i, 28 * 256);
        }
    }

    private static void compressFolder() {
        if (Configuration.COMPRESSED_RESULTS) {
            File compressedFile = new File(Configuration.OUTPUT_DIRECTORY + ".zip");
            try {
                zipFolder(new File(Configuration.OUTPUT_DIRECTORY), compressedFile);
                Console.info("Reporter: Folder compressed in: " + compressedFile.getAbsolutePath());
            } catch (IOException ex) {
                Error.trigger("Output cannot be compressed: " + compressedFile.getAbsolutePath() + "\n.ERROR: " + ex, ex);
            }
        }
    }

    private static void zipFolder(File sourceFolder, File targetFile) throws IOException {
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(targetFile))) {
            zipFile(sourceFolder, sourceFolder.getName(), zip);
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zip) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zip);
                }
            }
            return;
        }
        try (FileInputStream input = new FileInputStream(fileToZip)) {
            zip.putNextEntry(new ZipEntry(fileName));
            byte[] bytes = new byte[1024];
            int length;
            while ((length = input.read(bytes)) >= 0) {
                zip.write(bytes, 0, length);
            }
        }
    }

    private static void writeDisk(XSSFWorkbook workbook) {
        System.gc(); //call garbage collector (memory leaks?)
        Console.info("Saving results in: " + (new File(Configuration.OUTPUT_DIRECTORY)).getAbsolutePath());

        String fullFileName = Configuration.OUTPUT_DIRECTORY + "/" + Configuration.FILE_NAME;
        try {
            DateFormat df = new SimpleDateFormat("dd-MM-yy(HH-mm-ss)");
            fullFileName += "_" + df.format(new Date()) + ".xlsx";

            try (FileOutputStream file = new FileOutputStream(fullFileName)) {
                workbook.write(file);
            }
            Console.info("Reporter: File saved.");
            compressFolder();
        } catch (IOException ex) {
            Error.trigger("Input cannot be created: " + fullFileName + "\n.ERROR: " + ex, ex);
        }
    }

}
