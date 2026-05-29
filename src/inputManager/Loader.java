package inputManager;

import utils.Console;
import utils.Error;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Loader {
    private static Sheet configuration;
    private static Sheet newsSources;
    private static Sheet snsUsers;
    private static Sheet sourceReach;
    private static Sheet scenario;

    public static void load(String file) {
        File input = determineInputFile(file);
        Configuration.setPath(stripExtension(input.getName()));
        Console.info("Loader: Reading input from: " + input.getPath());
        read(input);
    }

    private static void read(File file) {
        try {
            FileInputStream fileStream = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(fileStream);
            showAvailableSheets(workbook);

            configuration = workbook.getSheet("Configuration");
            Configuration.set(readConfiguration(getConfiguration()));

            newsSources = workbook.getSheet("NewsSources");
            snsUsers = workbook.getSheet("SNSUsers");
            sourceReach = workbook.getSheet("SourceReach");
            scenario =  (Configuration.SCENARIO != Configuration.DISABLED)? workbook.getSheet("Scenario"): null;



            NewsSources.set(readNewsSourceAttributes(getNewsSources(), Configuration.LEVELS),
                    readNewsSourceNames(getNewsSources(), Configuration.LEVELS),
                    readSourceReach(getSourceReach()));

            SNSUsers.set(readSNSUsers(getSNSUsers()));
            if (Configuration.SCENARIO != Configuration.DISABLED) readScenario(getScenario());

            Configuration.setAttributes(NewsSources.attributeSize(), SNSUsers.attributeSize());
            Configuration.setNewsSources(NewsSources.getInnerNewsSources().size());
        } catch (Exception ex) {
            Console.error("Loader.read: Input cannot be open: " + file.getAbsolutePath());
            Console.error("Loader.read: ERROR: " + ex);
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static void showAvailableSheets(Workbook workbook) {
        StringBuilder names = new StringBuilder();
        for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
            names.append(workbook.getSheetName(i)).append(",");
        }
        
        String text = names.length() > 0 ? names.substring(0, names.length() - 1) : "";
        Console.info("Loader: Sheets available in the input file: " + text);
    }

    private static void readScenario(Sheet scenario) {
        Console.info("Loader: Reading Scenario");
        String from;
        String to;
        int start;
        ArrayList<String> attNames = new ArrayList<>();

        Row row = scenario.getRow(0);
        from = row.getCell(0).getStringCellValue().toUpperCase();
        to = row.getCell(1).getStringCellValue().toUpperCase();
        start = (int) row.getCell(2).getNumericCellValue();

        for (int i = 3; i < row.getLastCellNum(); ++i) {
            attNames.add(row.getCell(i).getStringCellValue().toUpperCase());
        }

        Scenarios.set(from, to, start, attNames);
    }


    private static HashMap<String, Double> readSourceReach(Sheet sourceReach) {
        Console.info("Loader: Reading NewsSource Reach");
        HashMap<String, Double> reach = new HashMap<>();

        for (Row row : sourceReach) {
            reach.put(row.getCell(0).getStringCellValue().toUpperCase(), row.getCell(1).getNumericCellValue() / 100.0);
        }
        return reach;
    }

    private static HashMap<String, Double> readConfiguration(Sheet conf) {
        Console.info("Loader: Reading Configuration");
        HashMap<String, Double> confs = new HashMap<>();
        for (Row row : conf) {
            confs.put(row.getCell(0).getStringCellValue().toUpperCase(), row.getCell(1).getNumericCellValue());
        }
        return confs;
    }

    private static ArrayList<String> readNewsSourceNames(Sheet newsSource, int levels) {
        ArrayList<String> newsSourceNames = new ArrayList<>();

        for (Row row : newsSource) {
            if (row.getRowNum() == 1) {
                for (Cell cell : row) {
                    if (cell.getColumnIndex() > 0 && (cell.getColumnIndex() + 1) % levels == 0) {
                        newsSourceNames.add(cell.getStringCellValue().toUpperCase());
                    }
                }
            }
        }
        return newsSourceNames;
    }

    private static HashMap<String, ArrayList<Double[]>> readNewsSourceAttributes(Sheet newsSource, int levels) {
        Console.info("Loader: Reading NewsSource Attributes");
        HashMap<String, ArrayList<Double[]>> datas = new HashMap<>();
        ArrayList<Double> endor = new ArrayList<>();
        ArrayList<Double[]> endors = new ArrayList<>();

        for (Row row : newsSource) {
            String name = "NO NAME";

            //get data from attributes
            if (row.getRowNum() > 2) {  // where starts attributes
                for (Cell cell : row) {
                    if (cell.getColumnIndex() == 0) {  //adding attributeName
                        name = cell.getStringCellValue().toUpperCase();
                    } else {
                        endor.add(cell.getNumericCellValue());
                        if (cell.getColumnIndex() % levels == 0) {
                            Double[] oneEndorsement = new Double[levels];
                            endors.add(endor.toArray(oneEndorsement));
                            endor.clear();
                        }
                    }
                }

                datas.put(name, new ArrayList<>(endors));
                endors.clear();
            }
        }
        return datas;
    }

    private static HashMap<String, Double> readSNSUsers(Sheet snsUser) {
        Console.info("Loader: Reading SNSUsers");
        HashMap<String, Double> snsUsers = new HashMap<>();
        for (Row row : snsUser) {
            snsUsers.put(row.getCell(0).getStringCellValue().toUpperCase(), row.getCell(1).getNumericCellValue());
        }
        return snsUsers;
    }

    private static File determineInputFile(String inputFileName) {
        inputFileName = inputFileName.equals("") ? "" : inputFileName;

        if (inputFileName.equals("")) {
            try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
                inputFileName = br.readLine();
            } catch (Exception e) {
                //First error without a initialize console
                System.out.println("MAIN: input.txt not found");
            }
        }

        inputFileName = inputFileName.equals("") ? Configuration.DEFAULT_FILE_NAME : inputFileName;
        ArrayList<File> candidates = new ArrayList<>();
        File given = new File(inputFileName);
        candidates.add(given);
        if (!inputFileName.toLowerCase().endsWith(".xlsx")) {
            candidates.add(new File(inputFileName + ".xlsx"));
            candidates.add(new File("input", inputFileName + ".xlsx"));
            candidates.add(new File("inputs", inputFileName + ".xlsx"));
        } else {
            candidates.add(new File("input", inputFileName));
            candidates.add(new File("inputs", inputFileName));
        }

        for (File candidate : candidates) {
            if (candidate.exists()) {
                return candidate;
            }
        }
        return given;
    }

    private static String stripExtension(String name) {
        return name.toLowerCase().endsWith(".xlsx") ? name.substring(0, name.length() - 5) : name;
    }

    private static void verifyLoadedSheet(Sheet sheet, String name) {
        if (sheet == null) Error.trigger("Sheet '"+name+"' has not been loaded");
    }

    public static Sheet getSNSUsers() {
        verifyLoadedSheet(snsUsers, "SNSUsers");
        return snsUsers;
    }

    public static Sheet getNewsSources() {
        verifyLoadedSheet(newsSources, "NewsSources");
        return newsSources;
    }

    public static Sheet getSourceReach() {
        verifyLoadedSheet(sourceReach, "SourceReach");
        return sourceReach;
    }

    public static Sheet getScenario() {
        verifyLoadedSheet(scenario, "Scenario");
        return scenario;
    }

    private static Sheet getConfiguration() {
        verifyLoadedSheet(configuration, "Configuration");
        return configuration;
    }
}
