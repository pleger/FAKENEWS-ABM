package inputManager;

import utils.Console;
import utils.Error;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Configuration {
    public final static String DEFAULT_FILE_NAME = "FAKENEWS_BASELINE_2";
    public final static int DISABLED = -1;
    public final static int MEMORY_INFINITE = -1;
    private final static int EXCEL_DISABLED = 0;
    private final static int CUSTOMIZED_SCENARIO = -2;

    private final static int D_PERIODS = 30;
    private final static int D_AGENTS = 10;
    private final static int D_CONTACTS = 17;
    private final static double D_FRIENDS = .7;
    private final static int D_LEVELS = 2; //2 or 3
    private final static int D_REPETITIONS = 0;
    private final static boolean D_GUI = false; //could be removed
    private final static double D_BASE = 1.2;
    private final static int D_MEMORY = MEMORY_INFINITE;
    private final static int D_LEARNING_PERIODS = 100;
    private final static boolean D_SOURCE_REACH = false;
    private final static boolean D_WOM = false;
    private final static int D_SCENARIO = -1;

    private final static boolean D_COMPRESSED_RESULTS = false;
    private final static boolean D_SAVED_ENDORSEMENTS = false;
    private final static boolean D_SAVED_AGENT_DECISIONS = false;
    private final static boolean D_SAVED_DETAILED_AGENT_DECISIONS = false;
    private final static boolean D_SAVED_REPOSTS_PER_SOURCE = false;
    private final static long LARGE_EXPERIMENT_OPERATIONS_WARNING_THRESHOLD = 1_000_000L;

    private final static String PERIODS_KEY = "PERIODS";
    private final static String AGENTS_KEY = "AGENTS";
    private final static String CONTACTS_KEY = "CONTACTS";
    private final static String FRIENDS_KEY = "FRIENDS";
    private final static String LEVELS_KEY = "LEVELS";
    private final static String REPETITIONS_KEY = "REPETITIONS";
    private final static String GUI_KEY = "GUI";
    private final static String BASE_KEY = "BASE";
    private final static String MEMORY_KEY = "MEMORY";
    private final static String SOURCE_REACH_KEY = "SOURCE_REACH";
    private final static String WOM_KEY = "WOM";
    private final static String SCENARIO_KEY = "SCENARIO";
    private final static String LEARNING_PERIODS_KEY = "LEARNING_PERIODS";
    private final static String COMPRESSED_RESULTS_KEY = "COMPRESSED_RESULTS";
    private final static String SAVED_ENDORSEMENTS_KEY = "SAVED_ENDORSEMENTS";
    private final static String SAVED_AGENT_DECISIONS_KEY = "SAVED_AGENT_DECISIONS";
    private final static String SAVED_DETAILED_AGENT_DECISIONS_KEY = "SAVED_DETAILED_AGENT_DECISIONS";
    private final static String SAVED_REPOSTS_PER_SOURCE_KEY = "SAVED_REPOSTS_PER_SOURCE";

    private final static String[] REQUIRED_PARAMETERS = new String[]{
            PERIODS_KEY, AGENTS_KEY, CONTACTS_KEY, FRIENDS_KEY, LEVELS_KEY, REPETITIONS_KEY, GUI_KEY,
            BASE_KEY, MEMORY_KEY, SOURCE_REACH_KEY, WOM_KEY, SCENARIO_KEY, LEARNING_PERIODS_KEY,
            SAVED_ENDORSEMENTS_KEY, SAVED_REPOSTS_PER_SOURCE_KEY, SAVED_DETAILED_AGENT_DECISIONS_KEY,
            SAVED_AGENT_DECISIONS_KEY, COMPRESSED_RESULTS_KEY
    };

    private final static Set<String> REQUIRED_PARAMETER_SET = new HashSet<>(Arrays.asList(REQUIRED_PARAMETERS));

    public static String FILE_NAME;
    public static String OUTPUT_DIRECTORY;

    public static int NEWS_SOURCES;
    public static int ATTRIBUTES_SOURCE;
    public static int ATTRIBUTES_USER;

    public static int PERIODS = D_PERIODS;
    public static int AGENTS = D_AGENTS;
    public static int CONTACTS = D_CONTACTS;
    public static double FRIENDS = D_FRIENDS;
    public static int LEVELS = D_LEVELS; //2 or 3
    public static int REPETITIONS = D_REPETITIONS;
    public static boolean GUI = D_GUI;
    public static double BASE = D_BASE;
    public static int MEMORY = D_MEMORY;
    public static boolean SOURCE_REACH = D_SOURCE_REACH;
    public static boolean WOM = D_WOM;
    public static int SCENARIO = D_SCENARIO;
    public static int LEARNING_PERIODS = D_LEARNING_PERIODS;

    //debug to save information
    public static boolean COMPRESSED_RESULTS = D_COMPRESSED_RESULTS;
    public static boolean SAVED_REPOSTS_PER_SOURCE = D_SAVED_REPOSTS_PER_SOURCE;
    public static boolean SAVED_DETAILED_AGENT_DECISIONS = D_SAVED_DETAILED_AGENT_DECISIONS;
    public static boolean SAVED_AGENT_DECISIONS = D_SAVED_AGENT_DECISIONS;
    public static boolean SAVED_ENDORSEMENTS = D_SAVED_ENDORSEMENTS;

    public static void set(HashMap<String, Double> conf) {
        checkConfigurationInput(conf);

        PERIODS = conf.get(PERIODS_KEY) != null ? conf.get(PERIODS_KEY).intValue() : D_PERIODS;
        AGENTS = conf.get(AGENTS_KEY) != null ? conf.get(AGENTS_KEY).intValue() : D_AGENTS;
        CONTACTS = conf.get(CONTACTS_KEY) != null ? conf.get(CONTACTS_KEY).intValue() : D_CONTACTS;
        FRIENDS = conf.get(FRIENDS_KEY) != null ? conf.get(FRIENDS_KEY) : D_FRIENDS;
        LEVELS = conf.get(LEVELS_KEY) != null ? conf.get(LEVELS_KEY).intValue() : D_LEVELS;
        REPETITIONS = conf.get(REPETITIONS_KEY) != null ? conf.get(REPETITIONS_KEY).intValue() : D_REPETITIONS;
        GUI = conf.get(GUI_KEY) != null ? conf.get(GUI_KEY) == 1 : D_GUI;
        BASE = conf.get(BASE_KEY) != null ? conf.get(BASE_KEY) : D_BASE;
        MEMORY = conf.get(MEMORY_KEY) != null ? conf.get(MEMORY_KEY).intValue() : D_MEMORY;
        SOURCE_REACH = conf.get(SOURCE_REACH_KEY) != null ? conf.get(SOURCE_REACH_KEY) == 1 : D_SOURCE_REACH;
        WOM = conf.get(WOM_KEY) != null ? conf.get(WOM_KEY) == 1 : D_WOM;
        SCENARIO = conf.get(SCENARIO_KEY) != null ? normalizeScenario(conf.get(SCENARIO_KEY).intValue()) : D_SCENARIO;
        LEARNING_PERIODS = conf.get(LEARNING_PERIODS_KEY) != null ? conf.get(LEARNING_PERIODS_KEY).intValue() : D_LEARNING_PERIODS;

        COMPRESSED_RESULTS = conf.get(COMPRESSED_RESULTS_KEY) != null ? conf.get(COMPRESSED_RESULTS_KEY) == 1 : D_COMPRESSED_RESULTS;
        SAVED_ENDORSEMENTS = conf.get(SAVED_ENDORSEMENTS_KEY) != null ? conf.get(SAVED_ENDORSEMENTS_KEY) == 1 : D_SAVED_ENDORSEMENTS;
        SAVED_AGENT_DECISIONS = conf.get(SAVED_AGENT_DECISIONS_KEY) != null ? conf.get(SAVED_AGENT_DECISIONS_KEY) == 1 : D_SAVED_AGENT_DECISIONS;
        SAVED_DETAILED_AGENT_DECISIONS = conf.get(SAVED_DETAILED_AGENT_DECISIONS_KEY) != null ? conf.get(SAVED_DETAILED_AGENT_DECISIONS_KEY) == 1 : D_SAVED_DETAILED_AGENT_DECISIONS;
        SAVED_REPOSTS_PER_SOURCE = conf.get(SAVED_REPOSTS_PER_SOURCE_KEY) != null ? conf.get(SAVED_REPOSTS_PER_SOURCE_KEY) == 1 : D_SAVED_REPOSTS_PER_SOURCE;

        warnIfLargeExperimentSavesDetailedResults();
    }

    private static void creatingOutputFolder(String output) {
        try {
            File dir = new File(output);
            if (!dir.exists() && !dir.mkdirs()) {
                Error.trigger("Directory cannot be created: " + output);
            }
        } catch (SecurityException se) {
            Error.trigger("Directory cannot be created: " + output + "\n ERROR: " + se, se);
        }
    }

    public static void setPath(String fileName) {
        FILE_NAME = fileName;
        DateFormat df = new SimpleDateFormat("dd-MM-yy(HH-mm-ss)");
        OUTPUT_DIRECTORY = "output/" + fileName + "_" + df.format(new Date());

        //checking and creating the output folder
        if (Files.notExists(Paths.get("output"))) {
            creatingOutputFolder("output");
        }

        //making the simulation directory
        try {
            File output = new File(OUTPUT_DIRECTORY);
            if (!output.exists() && !output.mkdirs()) {
                Error.trigger("Configuration.setPath: Directory cannot be created: " + OUTPUT_DIRECTORY);
            }
            Console.resetLogFile();
            Console.info("Configuration.setPath: Directory ready: " + OUTPUT_DIRECTORY);
        } catch (SecurityException se) {
            Error.trigger("Configuration.setPath: Directory cannot be created: " + OUTPUT_DIRECTORY +
                    "Configuration.setPath: ERROR: " + se, se);
        }
    }

    public static void setAttributes(int newsSources, int snsUsers) {
        set("ATTRIBUTES_SOURCE", newsSources);
        set("ATTRIBUTES_USER", snsUsers);
    }

    public static void setNewsSources(int newsSources) {
        set("NEWS_SOURCES", newsSources);
    }

    private static void set(String name, double value) {
        switch (name.toUpperCase()) {
            case PERIODS_KEY:
                PERIODS = (int) value;
                break;
            case AGENTS_KEY:
                AGENTS = (int) value;
                break;
            case CONTACTS_KEY:
                CONTACTS = (int) value;
                break;
            case FRIENDS_KEY:
                FRIENDS = value;
                break;
            case "ATTRIBUTES_SOURCE":
                ATTRIBUTES_SOURCE = (int) value;
                break;
            case "ATTRIBUTES_USER":
                ATTRIBUTES_USER = (int) value;
                break;
            case "NEWS_SOURCES":
                NEWS_SOURCES = (int) value;
                break;
            case REPETITIONS_KEY:
                REPETITIONS = (int) value;
                break;
            case LEVELS_KEY:
                LEVELS = (int) value;
                break;
            case GUI_KEY:
                GUI = value == 1;
                break;
            case BASE_KEY:
                BASE = value;
                break;
            case MEMORY_KEY:
                MEMORY = (int) value;
                break;
            case SOURCE_REACH_KEY:
                SOURCE_REACH = value == 1;
                break;
            case WOM_KEY:
                WOM = value == 1;
                break;
            case SCENARIO_KEY:
                SCENARIO = normalizeScenario((int) value);
                break;
            case LEARNING_PERIODS_KEY:
                LEARNING_PERIODS = (int) value;
                break;
            case COMPRESSED_RESULTS_KEY:
                COMPRESSED_RESULTS = value == 1;
                break;
            case SAVED_ENDORSEMENTS_KEY:
                SAVED_ENDORSEMENTS = value == 1;
                break;
            case SAVED_DETAILED_AGENT_DECISIONS_KEY:
                SAVED_DETAILED_AGENT_DECISIONS = value == 1;
                break;
            case SAVED_AGENT_DECISIONS_KEY:
                SAVED_AGENT_DECISIONS = value == 1;
                break;
            case SAVED_REPOSTS_PER_SOURCE_KEY:
                SAVED_REPOSTS_PER_SOURCE = value == 1;
                break;
            default:
                Console.error("CONFIGURATOR.SET: Wrong Parameter: " + name.toUpperCase());
        }
    }

    private static void checkConfigurationInput(HashMap<String, Double> conf) {
        for (String param : REQUIRED_PARAMETERS) {
            if (!conf.containsKey(param)) {
                Console.warn(param + " is missing.");
            }
        }

        for (String param : conf.keySet()) {
            if (!REQUIRED_PARAMETER_SET.contains(param)) {
                Console.warn(param + " is not a recognized configuration parameter.");
            }
        }

        validatePositiveInt(conf, PERIODS_KEY);
        validatePositiveInt(conf, AGENTS_KEY);
        validateNonNegativeInt(conf, CONTACTS_KEY);
        validateRange(conf, FRIENDS_KEY, 0.0, 1.0);
        validateLevels(conf);
        validateNonNegativeInt(conf, REPETITIONS_KEY);
        validateBoolean(conf, GUI_KEY);
        validateGreaterThan(conf, BASE_KEY, 0.0);
        validateMemory(conf);
        validateBoolean(conf, SOURCE_REACH_KEY);
        validateBoolean(conf, WOM_KEY);
        validateScenario(conf);
        validateNonNegativeInt(conf, LEARNING_PERIODS_KEY);
        validateBoolean(conf, SAVED_ENDORSEMENTS_KEY);
        validateBoolean(conf, SAVED_REPOSTS_PER_SOURCE_KEY);
        validateBoolean(conf, SAVED_DETAILED_AGENT_DECISIONS_KEY);
        validateBoolean(conf, SAVED_AGENT_DECISIONS_KEY);
        validateBoolean(conf, COMPRESSED_RESULTS_KEY);
    }

    private static void validatePositiveInt(HashMap<String, Double> conf, String param) {
        if (conf.containsKey(param)) {
            validateInteger(conf, param);
            if (conf.get(param).intValue() <= 0) {
                failConfiguration(param + " must be greater than 0.");
            }
        }
    }

    private static void validateNonNegativeInt(HashMap<String, Double> conf, String param) {
        if (conf.containsKey(param)) {
            validateInteger(conf, param);
            if (conf.get(param).intValue() < 0) {
                failConfiguration(param + " must be greater than or equal to 0.");
            }
        }
    }

    private static void validateInteger(HashMap<String, Double> conf, String param) {
        double value = conf.get(param);
        if (value != Math.rint(value)) {
            failConfiguration(param + " must be an integer.");
        }
    }

    private static void validateRange(HashMap<String, Double> conf, String param, double min, double max) {
        if (conf.containsKey(param) && (conf.get(param) < min || conf.get(param) > max)) {
            failConfiguration(param + " must be between " + min + " and " + max + ".");
        }
    }

    private static void validateGreaterThan(HashMap<String, Double> conf, String param, double min) {
        if (conf.containsKey(param) && conf.get(param) <= min) {
            failConfiguration(param + " must be greater than " + min + ".");
        }
    }

    private static void validateLevels(HashMap<String, Double> conf) {
        if (conf.containsKey(LEVELS_KEY)) {
            validateInteger(conf, LEVELS_KEY);
            int levels = conf.get(LEVELS_KEY).intValue();
            if (levels != 2 && levels != 3) {
                failConfiguration(LEVELS_KEY + " must be 2 or 3.");
            }
        }
    }

    private static void validateMemory(HashMap<String, Double> conf) {
        if (conf.containsKey(MEMORY_KEY)) {
            validateInteger(conf, MEMORY_KEY);
            int memory = conf.get(MEMORY_KEY).intValue();
            if (memory != MEMORY_INFINITE && memory < 0) {
                failConfiguration(MEMORY_KEY + " must be MEMORY_INFINITE (" + MEMORY_INFINITE +
                        ") or greater than or equal to 0.");
            }
        }
    }

    private static void validateScenario(HashMap<String, Double> conf) {
        if (conf.containsKey(SCENARIO_KEY)) {
            validateInteger(conf, SCENARIO_KEY);
            int scenario = conf.get(SCENARIO_KEY).intValue();
            if (scenario != EXCEL_DISABLED && scenario != DISABLED && scenario != CUSTOMIZED_SCENARIO) {
                failConfiguration(SCENARIO_KEY + " must be " + EXCEL_DISABLED + " (disabled), " +
                        DISABLED + " (disabled legacy), or " + CUSTOMIZED_SCENARIO + " (customized).");
            }
        }
    }

    private static int normalizeScenario(int scenario) {
        return scenario == EXCEL_DISABLED ? DISABLED : scenario;
    }

    private static void validateBoolean(HashMap<String, Double> conf, String param) {
        if (conf.containsKey(param)) {
            double value = conf.get(param);
            if (value != 0.0 && value != 1.0) {
                failConfiguration(param + " must be 0 or 1.");
            }
        }
    }

    private static void failConfiguration(String message) {
        throw new IllegalArgumentException("Invalid configuration: " + message);
    }

    private static void warnIfLargeExperimentSavesDetailedResults() {
        if (!SAVED_ENDORSEMENTS && !SAVED_AGENT_DECISIONS && !SAVED_DETAILED_AGENT_DECISIONS &&
                !SAVED_REPOSTS_PER_SOURCE) {
            return;
        }

        long simulationRuns = (long) REPETITIONS + 1L;
        long agentPeriodOperations = simulationRuns * PERIODS * AGENTS;
        if (agentPeriodOperations < LARGE_EXPERIMENT_OPERATIONS_WARNING_THRESHOLD) {
            return;
        }

        String enabledDetails = enabledDetailedResultKeys();
        Console.warn("Configuration: large experiment configured with detailed result saving enabled (" +
                enabledDetails + "). Estimated agent-period operations=" + agentPeriodOperations +
                " from runs=" + simulationRuns +
                ", periods=" + PERIODS +
                ", agents=" + AGENTS +
                ". This can create very large workbooks and slow execution; disable unneeded SAVED_* options " +
                "or enable COMPRESSED_RESULTS for large experiments.");
    }

    private static String enabledDetailedResultKeys() {
        StringBuilder keys = new StringBuilder();
        appendEnabledKey(keys, SAVED_ENDORSEMENTS, SAVED_ENDORSEMENTS_KEY);
        appendEnabledKey(keys, SAVED_AGENT_DECISIONS, SAVED_AGENT_DECISIONS_KEY);
        appendEnabledKey(keys, SAVED_DETAILED_AGENT_DECISIONS, SAVED_DETAILED_AGENT_DECISIONS_KEY);
        appendEnabledKey(keys, SAVED_REPOSTS_PER_SOURCE, SAVED_REPOSTS_PER_SOURCE_KEY);
        return keys.toString();
    }

    private static void appendEnabledKey(StringBuilder keys, boolean enabled, String key) {
        if (!enabled) {
            return;
        }

        if (keys.length() > 0) {
            keys.append(", ");
        }
        keys.append(key);
    }

    public static Map<String, Double> toMap() {
        Map<String, Double> conf = new LinkedHashMap<>();
        conf.put(PERIODS_KEY, (double) PERIODS);
        conf.put(AGENTS_KEY, (double) AGENTS);
        conf.put(CONTACTS_KEY, (double) CONTACTS);
        conf.put(FRIENDS_KEY, FRIENDS);
        conf.put(LEVELS_KEY, (double) LEVELS);
        conf.put(REPETITIONS_KEY, (double) REPETITIONS);
        conf.put(GUI_KEY, GUI ? 1.0 : 0.0);
        conf.put(BASE_KEY, BASE);
        conf.put(MEMORY_KEY, (double) MEMORY);
        conf.put(SOURCE_REACH_KEY, SOURCE_REACH ? 1.0 : 0.0);
        conf.put(WOM_KEY, WOM ? 1.0 : 0.0);
        conf.put(SCENARIO_KEY, (double) SCENARIO);
        conf.put(LEARNING_PERIODS_KEY, (double) LEARNING_PERIODS);

        conf.put(COMPRESSED_RESULTS_KEY, COMPRESSED_RESULTS ? 1.0 : 0.0);
        conf.put(SAVED_ENDORSEMENTS_KEY, SAVED_ENDORSEMENTS ? 1.0 : 0.0);
        conf.put(SAVED_DETAILED_AGENT_DECISIONS_KEY, SAVED_DETAILED_AGENT_DECISIONS ? 1.0 : 0.0);
        conf.put(SAVED_AGENT_DECISIONS_KEY, SAVED_AGENT_DECISIONS ? 1.0 : 0.0);
        conf.put(SAVED_REPOSTS_PER_SOURCE_KEY, SAVED_REPOSTS_PER_SOURCE ? 1.0 : 0.0);

        return conf;
    }

    public static String toStringConfiguration() {
        return Configuration.toMap().toString();
    }
}
