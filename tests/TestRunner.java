import agent.SNSUserFactory;
import agent.NewsSource;
import agent.NewsSourceFactory;
import agent.NewsSourceSelectionStrategies;
import agent.SNSUser;
import endorsement.AttributesNewsSource;
import endorsement.Endorsement;
import endorsement.EndorsementEvalStrategies;
import endorsement.Endorsements;
import inputManager.Configuration;
import inputManager.Loader;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import reporter.Reporter;
import reporter.RepostsPerSourceData;
import scenarios.Scenario;
import scenarios.ScenarioFactory;
import simulation.Simulation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class TestRunner {
    private static int passed = 0;

    public static void main(String[] args) {
        testEndorsementFormulaForHighBinaryLevel();
        testConfigurationAppliesPluralSavedEndorsementsKey();
        testConfigurationOutputOrderIsStable();
        testConfigurationRejectsInvalidValues();
        testConfigurationAcceptsExcelDisabledScenario();
        testConfigurationAcceptsInfiniteMemoryConstant();
        testConfigurationRejectsInvalidMemoryValues();
        testLargeConfigurationWithDetailedSavingIsAcceptedWithWarning();
        testLoaderReadsFakeNewsBaseline();
        testCustomizedScenarioCopiesSelectedAttributes();
        testScenarioReportPreviewDoesNotMutateSource();
        testProbabilitySelectionHandlesNonPositiveEvaluations();
        testRepeatedLoaderClearsScenarioCache();
        testReporterClearRemovesAccumulatedRows();
        testReporterSplitsLargeEndorsementSheets();
        testEmptyWordOfMouthRecommendationsAreIgnored();
        testUserWithNoKnownSourcesCanStep();
        testMainWritesReporterWorkbookWithExpectedSheets();
        testAttributeReplacementLeavesOriginalUntouched();
        testEndorsementsFilterByMemoryAndSelectedSource();
        testEndorsementsInfiniteMemoryKeepsAllPeriods();
        testNewsSourceSelectionByMax();
        testFactoriesResetIdsAcrossCreations();
        testRepostsDataClonesInputArray();
        System.out.println("Tests passed: " + passed);
    }

    private static void testEndorsementFormulaForHighBinaryLevel() {
        Configuration.LEVELS = 2;
        double value = EndorsementEvalStrategies.BY_MAX(new Double[]{0.1, 0.9}, 4.0);
        assertEquals("binary high endorsement should equal snsUser weight", 4.0, value, 0.0001);
        passed++;
    }

    private static void testConfigurationAppliesPluralSavedEndorsementsKey() {
        HashMap<String, Double> conf = validConfiguration();
        conf.put("SAVED_ENDORSEMENTS", 1.0);

        Configuration.set(conf);

        assertTrue("plural SAVED_ENDORSEMENTS key should enable endorsement reporting",
                Configuration.SAVED_ENDORSEMENTS);
        passed++;
    }

    private static void testConfigurationOutputOrderIsStable() {
        Configuration.set(validConfiguration());
        List<String> keys = new ArrayList<>(Configuration.toMap().keySet());

        assertEquals("first configuration key", "PERIODS", keys.get(0));
        assertEquals("second configuration key", "AGENTS", keys.get(1));
        assertEquals("last configuration key", "SAVED_REPOSTS_PER_SOURCE", keys.get(keys.size() - 1));
        passed++;
    }

    private static void testConfigurationRejectsInvalidValues() {
        assertThrows("LEVELS should only accept supported endorsement levels", () -> {
            HashMap<String, Double> conf = validConfiguration();
            conf.put("LEVELS", 4.0);
            Configuration.set(conf);
        });

        assertThrows("integer fields should reject decimals", () -> {
            HashMap<String, Double> conf = validConfiguration();
            conf.put("AGENTS", 2.5);
            Configuration.set(conf);
        });

        assertThrows("boolean fields should only accept 0 or 1", () -> {
            HashMap<String, Double> conf = validConfiguration();
            conf.put("GUI", 2.0);
            Configuration.set(conf);
        });

        assertThrows("FRIENDS should stay within probability range", () -> {
            HashMap<String, Double> conf = validConfiguration();
            conf.put("FRIENDS", 1.5);
            Configuration.set(conf);
        });

        passed++;
    }

    private static void testConfigurationAcceptsExcelDisabledScenario() {
        HashMap<String, Double> conf = validConfiguration();
        conf.put("SCENARIO", 0.0);

        Configuration.set(conf);

        assertEquals("Excel SCENARIO=0 should map to internal disabled scenario",
                Configuration.DISABLED, Configuration.SCENARIO);
        assertEquals("configuration dump should keep internal disabled scenario",
                Configuration.DISABLED, Configuration.toMap().get("SCENARIO"), 0.0001);
        passed++;
    }

    private static void testConfigurationAcceptsInfiniteMemoryConstant() {
        HashMap<String, Double> conf = validConfiguration();
        conf.put("MEMORY", (double) Configuration.MEMORY_INFINITE);

        Configuration.set(conf);

        assertEquals("MEMORY_INFINITE should be accepted as configured memory",
                Configuration.MEMORY_INFINITE, Configuration.MEMORY);
        assertEquals("configuration dump should preserve MEMORY_INFINITE",
                Configuration.MEMORY_INFINITE, Configuration.toMap().get("MEMORY"), 0.0001);
        passed++;
    }

    private static void testConfigurationRejectsInvalidMemoryValues() {
        assertThrows("MEMORY should reject negative values other than MEMORY_INFINITE", () -> {
            HashMap<String, Double> conf = validConfiguration();
            conf.put("MEMORY", (double) Configuration.MEMORY_INFINITE - 1);
            Configuration.set(conf);
        });

        assertThrows("MEMORY should reject decimal values", () -> {
            HashMap<String, Double> conf = validConfiguration();
            conf.put("MEMORY", 1.5);
            Configuration.set(conf);
        });

        passed++;
    }

    private static void testLargeConfigurationWithDetailedSavingIsAcceptedWithWarning() {
        HashMap<String, Double> conf = validConfiguration();
        conf.put("PERIODS", 1_000.0);
        conf.put("AGENTS", 1_000.0);
        conf.put("REPETITIONS", 1.0);
        conf.put("SAVED_ENDORSEMENTS", 1.0);

        Configuration.set(conf);

        assertEquals("large warning path should preserve configured periods", 1_000, Configuration.PERIODS);
        assertEquals("large warning path should preserve configured agents", 1_000, Configuration.AGENTS);
        assertEquals("large warning path should preserve configured repetitions", 1, Configuration.REPETITIONS);
        assertTrue("large warning path should preserve detailed saving flag", Configuration.SAVED_ENDORSEMENTS);
        passed++;
    }

    private static void testLoaderReadsFakeNewsBaseline() {
        Loader.load("FAKENEWS_BASELINE");
        assertEquals("configured agent count", 120, Configuration.AGENTS);
        Configuration.AGENTS = 3;
        assertEquals("source count", 4, NewsSourceFactory.createFromInput().size());
        assertEquals("agent factory override", 3, SNSUserFactory.createFromInput().size());
        assertEquals("attribute count", 13, Configuration.ATTRIBUTES_SOURCE);
        passed++;
    }

    private static void testCustomizedScenarioCopiesSelectedAttributes() {
        Loader.load("FAKENEWS_COORDINATED_PUSH");
        NewsSourceFactory.createFromInput();
        NewsSource fake = NewsSourceFactory.getNewsSource("FAKE_NEWS_SOURCE");
        NewsSource unknown = NewsSourceFactory.getNewsSource("UNKNOWN_MEDIA");
        Double[] fakeSensationalism = fake.getAttributes().getValues("SENSACIONALISMO DE LA NOTICIA");
        Double[] oldUnknownSensationalism = unknown.getAttributes().getValues("SENSACIONALISMO DE LA NOTICIA");

        ScenarioFactory.get(Configuration.SCENARIO).apply(15);
        Double[] newUnknownSensationalism = unknown.getAttributes().getValues("SENSACIONALISMO DE LA NOTICIA");

        assertTrue("scenario should change unknown-media sensationalism",
                oldUnknownSensationalism[0].doubleValue() != newUnknownSensationalism[0].doubleValue());
        assertEquals("scenario should copy fake-news low probability",
                fakeSensationalism[0], newUnknownSensationalism[0], 0.0001);
        assertEquals("scenario should copy fake-news high probability",
                fakeSensationalism[1], newUnknownSensationalism[1], 0.0001);
        passed++;
    }

    private static void testScenarioReportPreviewDoesNotMutateSource() {
        Loader.load("FAKENEWS_COORDINATED_PUSH");
        NewsSourceFactory.createFromInput();
        NewsSource fake = NewsSourceFactory.getNewsSource("FAKE_NEWS_SOURCE");
        NewsSource unknown = NewsSourceFactory.getNewsSource("UNKNOWN_MEDIA");
        String attribute = "SENSACIONALISMO DE LA NOTICIA";

        Double[] originalUnknown = unknown.getAttributes().getValues(attribute);
        Double[] fakeValues = fake.getAttributes().getValues(attribute);
        Scenario scenario = ScenarioFactory.get(Configuration.SCENARIO);
        AttributesNewsSource preview = scenario.attributesAfterApplyingTo(unknown);

        assertEquals("preview should copy fake-news low probability",
                fakeValues[0], preview.getValues(attribute)[0], 0.0001);
        assertEquals("preview should copy fake-news high probability",
                fakeValues[1], preview.getValues(attribute)[1], 0.0001);
        assertEquals("scenario preview should not mutate unknown-media low probability",
                originalUnknown[0], unknown.getAttributes().getValues(attribute)[0], 0.0001);
        assertEquals("scenario preview should not mutate unknown-media high probability",
                originalUnknown[1], unknown.getAttributes().getValues(attribute)[1], 0.0001);
        passed++;
    }

    private static void testProbabilitySelectionHandlesNonPositiveEvaluations() {
        LinkedHashMap<Integer, Double> evaluations = new LinkedHashMap<>();
        evaluations.put(10, -2.0);
        evaluations.put(11, 0.0);
        evaluations.put(12, 2.0);

        int selected = NewsSourceSelectionStrategies.BY_PROBABILITY(evaluations);

        assertTrue("probability selection should return one of the evaluated sources",
                evaluations.containsKey(selected));
        passed++;
    }

    private static void testRepeatedLoaderClearsScenarioCache() {
        Loader.load("FAKENEWS_COORDINATED_PUSH");
        Scenario first = ScenarioFactory.get(Configuration.SCENARIO);

        Loader.load("FAKENEWS_BASELINE");
        Loader.load("FAKENEWS_COORDINATED_PUSH");
        Scenario second = ScenarioFactory.get(Configuration.SCENARIO);

        assertTrue("loader should rebuild scenario cache for each workbook load", first != second);
        passed++;
    }

    private static void testReporterClearRemovesAccumulatedRows() {
        Configuration.SAVED_REPOSTS_PER_SOURCE = true;
        Reporter.clear();
        Reporter.addRepostsUniqueByNewsSourceData(1, 1, new int[]{1, 2});
        assertEquals("reporter should contain one unique-repost row", 1, Reporter.getRepostsPerSourceData().size());

        Reporter.clear();

        assertEquals("reporter clear should remove unique-repost rows", 0, Reporter.getRepostsPerSourceData().size());
        Configuration.SAVED_REPOSTS_PER_SOURCE = false;
        passed++;
    }

    private static void testReporterSplitsLargeEndorsementSheets() {
        Loader.load("FAKENEWS_BASELINE");
        Configuration.SAVED_ENDORSEMENTS = true;
        Configuration.SAVED_AGENT_DECISIONS = false;
        Configuration.SAVED_DETAILED_AGENT_DECISIONS = false;
        Configuration.SAVED_REPOSTS_PER_SOURCE = false;
        Configuration.SCENARIO = Configuration.DISABLED;
        Reporter.clear();

        ArrayList<reporter.EndorsementData> data = new ArrayList<>();
        data.add(new reporter.EndorsementData(1, 1, 1, "SOURCE", "A", 1.0));
        data.add(new reporter.EndorsementData(1, 1, 1, "SOURCE", "B", 2.0));
        data.add(new reporter.EndorsementData(1, 1, 1, "SOURCE", "C", 3.0));
        Reporter.addEndorsementData(data);

        System.setProperty("reporter.maxRowsPerSheet", "3");
        try {
            Reporter.write();
        } finally {
            System.clearProperty("reporter.maxRowsPerSheet");
        }

        File workbookFile = newestWorkbookInOutputDirectory(new File(Configuration.OUTPUT_DIRECTORY));
        try (Workbook workbook = WorkbookFactory.create(workbookFile)) {
            assertTrue("first endorsement sheet should exist", workbook.getSheet("Endorsements") != null);
            assertTrue("second endorsement sheet should exist after row rollover", workbook.getSheet("Endorsements_2") != null);
            assertEquals("first endorsement sheet should contain header plus two rows",
                    2, workbook.getSheet("Endorsements").getLastRowNum());
            assertEquals("second endorsement sheet should contain header plus one row",
                    1, workbook.getSheet("Endorsements_2").getLastRowNum());
        } catch (Exception ex) {
            throw new AssertionError("split endorsement workbook should be readable: " + ex);
        } finally {
            Reporter.clear();
        }

        passed++;
    }

    private static void testEmptyWordOfMouthRecommendationsAreIgnored() {
        Loader.load("FAKENEWS_BASELINE");
        Configuration.AGENTS = 3;
        Configuration.CONTACTS = 0;
        Configuration.FRIENDS = 0;
        Configuration.WOM = true;
        Configuration.PERIODS = 1;
        Configuration.LEARNING_PERIODS = 100;
        Reporter.clear();

        Simulation simulation = new Simulation(SNSUserFactory.createFromInput(), NewsSourceFactory.createFromInput(),
                Configuration.PERIODS);
        simulation.run();

        passed++;
    }

    private static void testUserWithNoKnownSourcesCanStep() {
        Loader.load("FAKENEWS_BASELINE");
        Configuration.AGENTS = 1;
        SNSUser user = SNSUserFactory.createFromInput().get(0);
        user.setKnowNewsSources(new ArrayList<>());
        user.doStep(1);

        assertTrue("user with no known sources should not select a source", user.getLastSelectMarked(1) == null);
        passed++;
    }

    private static void testMainWritesReporterWorkbookWithExpectedSheets() {
        long startedAt = System.currentTimeMillis();
        String output = runMain("--input", "FAKENEWS_BASELINE", "--periods", "2", "--agents", "3",
                "--repetitions", "0", "--learning-periods", "0", "--no-gui");
        assertTrue("main process should finish and write report", output.contains("Reporter: File saved."));

        File workbookFile = newestWorkbookInNewestOutputDirectory("FAKENEWS_BASELINE", startedAt);
        try (Workbook workbook = WorkbookFactory.create(workbookFile)) {
            assertTrue("output workbook should contain Configuration", workbook.getSheet("Configuration") != null);
            assertTrue("output workbook should contain copied NewsSources", workbook.getSheet("NewsSources") != null);
            assertTrue("output workbook should contain Results", workbook.getSheet("Results") != null);
            assertTrue("output workbook should contain DetailedResult", workbook.getSheet("DetailedResult") != null);
            assertTrue("output workbook should contain Endorsements", workbook.getSheet("Endorsements") != null);
            Sheet reposts = workbook.getSheet("RepostsPerSource");
            assertTrue("output workbook should contain RepostsPerSource", reposts != null);
            assertTrue("repost sheet should include data rows", reposts.getLastRowNum() > 0);
        } catch (Exception ex) {
            throw new AssertionError("output workbook should be readable: " + ex);
        }

        passed++;
    }

    private static void testAttributeReplacementLeavesOriginalUntouched() {
        ArrayList<String> names = new ArrayList<>();
        names.add("A");
        names.add("B");
        ArrayList<Double[]> values = new ArrayList<>();
        values.add(new Double[]{0.2, 0.8});
        values.add(new Double[]{0.4, 0.6});
        AttributesNewsSource attributes = new AttributesNewsSource(names, values);

        AttributesNewsSource replaced = attributes.replace("A", new Double[]{0.9, 0.1});

        assertEquals("replacement should update copied low value", 0.9, replaced.getValues("A")[0], 0.0001);
        assertEquals("original low value should remain unchanged", 0.2, attributes.getValues("A")[0], 0.0001);
        assertEquals("unreplaced value should be preserved", 0.4, replaced.getValues("B")[0], 0.0001);
        passed++;
    }

    private static void testEndorsementsFilterByMemoryAndSelectedSource() {
        Loader.load("FAKENEWS_BASELINE");
        NewsSourceFactory.createFromInput();
        NewsSource traditional = NewsSourceFactory.getNewsSource("TRADITIONAL_MEDIA");
        NewsSource fake = NewsSourceFactory.getNewsSource("FAKE_NEWS_SOURCE");
        Endorsements endorsements = new Endorsements();
        endorsements.add(new Endorsement(1, traditional, "QUALITY", 1.0));
        endorsements.add(new Endorsement(3, fake, "QUALITY", 2.0));
        endorsements.add(new Endorsement(3, fake, "WORD OF MOUTH", 3.0));

        Configuration.MEMORY = 1;
        Endorsements recent = endorsements.filterByMemory(3);

        assertEquals("memory filter should keep period 3 endorsements only", 2, recent.size());
        assertEquals("selected source should ignore WOM endorsement", "FAKE_NEWS_SOURCE",
                endorsements.getSelectedNewsSource(3).getName());
        passed++;
    }

    private static void testEndorsementsInfiniteMemoryKeepsAllPeriods() {
        Endorsements endorsements = new Endorsements();
        endorsements.add(new Endorsement(1, null, "QUALITY", 1.0));
        endorsements.add(new Endorsement(2, null, "QUALITY", 2.0));
        endorsements.add(new Endorsement(3, null, "QUALITY", 3.0));

        Configuration.MEMORY = Configuration.MEMORY_INFINITE;
        Endorsements all = endorsements.filterByMemory(3);

        assertEquals("infinite memory should keep all endorsement periods", 3, all.size());
        passed++;
    }

    private static void testNewsSourceSelectionByMax() {
        LinkedHashMap<Integer, Double> evaluations = new LinkedHashMap<>();
        evaluations.put(1, -3.0);
        evaluations.put(2, 5.0);
        evaluations.put(3, 4.0);

        assertEquals("BY_MAX should select highest evaluation", 2,
                NewsSourceSelectionStrategies.BY_MAX(evaluations));
        passed++;
    }

    private static void testFactoriesResetIdsAcrossCreations() {
        Loader.load("FAKENEWS_BASELINE");
        Configuration.AGENTS = 2;
        List<SNSUser> firstUsers = SNSUserFactory.createFromInput();
        List<SNSUser> secondUsers = SNSUserFactory.createFromInput();
        List<NewsSource> firstSources = NewsSourceFactory.createFromInput();
        List<NewsSource> secondSources = NewsSourceFactory.createFromInput();

        assertEquals("first user factory run should start at id 0", 0, firstUsers.get(0).getID());
        assertEquals("second user factory run should reset to id 0", 0, secondUsers.get(0).getID());
        assertEquals("first source factory run should start at id 0", 0, firstSources.get(0).getID());
        assertEquals("second source factory run should reset to id 0", 0, secondSources.get(0).getID());
        passed++;
    }

    private static void testRepostsDataClonesInputArray() {
        int[] reposts = new int[]{1, 2, 3};
        RepostsPerSourceData row = new RepostsPerSourceData(1, 2, reposts);
        reposts[0] = 99;

        assertEquals("repost row should clone input array", 1, row.reposts[0]);
        passed++;
    }

    private static void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertThrows(String message, Runnable runnable) {
        try {
            runnable.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError(message + ": expected IllegalArgumentException");
    }

    private static void assertEquals(String message, int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + " but got " + actual);
        }
    }

    private static void assertEquals(String message, String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ": expected " + expected + " but got " + actual);
        }
    }

    private static void assertEquals(String message, double expected, double actual, double epsilon) {
        if (Math.abs(expected - actual) > epsilon) {
            throw new AssertionError(message + ": expected " + expected + " but got " + actual);
        }
    }

    private static String runMain(String... args) {
        ArrayList<String> command = new ArrayList<>();
        command.add("java");
        command.add("-cp");
        command.add("build/classes:lib/*");
        command.add("Main");
        for (String arg : args) {
            command.add(arg);
        }

        try {
            Process process = new ProcessBuilder(command)
                    .directory(new File("."))
                    .redirectErrorStream(true)
                    .start();
            String output = new String(process.getInputStream().readAllBytes());
            int exit = process.waitFor();
            if (exit != 0) {
                throw new AssertionError("Main process failed with exit " + exit + "\n" + output);
            }
            return output;
        } catch (Exception ex) {
            throw new AssertionError("Main process should run successfully: " + ex);
        }
    }

    private static File newestWorkbookInNewestOutputDirectory(String prefix, long startedAt) {
        File outputRoot = new File("output");
        File[] directories = outputRoot.listFiles((dir, name) -> name.startsWith(prefix + "_"));
        assertTrue("output root should contain matching report directory", directories != null && directories.length > 0);

        File newestDirectory = null;
        for (File directory : directories) {
            if (directory.isDirectory() && directory.lastModified() >= startedAt &&
                    (newestDirectory == null || directory.lastModified() > newestDirectory.lastModified())) {
                newestDirectory = directory;
            }
        }
        assertTrue("main process should create a fresh matching report directory", newestDirectory != null);
        return newestWorkbookInOutputDirectory(newestDirectory);
    }

    private static File newestWorkbookInOutputDirectory(File outputDirectory) {
        File[] files = outputDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".xlsx"));
        assertTrue("output directory should contain an xlsx report", files != null && files.length > 0);

        File newest = files[0];
        for (File file : files) {
            if (file.lastModified() > newest.lastModified()) {
                newest = file;
            }
        }
        return newest;
    }

    private static HashMap<String, Double> validConfiguration() {
        HashMap<String, Double> conf = new HashMap<>();
        conf.put("PERIODS", 30.0);
        conf.put("AGENTS", 10.0);
        conf.put("CONTACTS", 17.0);
        conf.put("FRIENDS", 0.7);
        conf.put("LEVELS", 2.0);
        conf.put("REPETITIONS", 0.0);
        conf.put("GUI", 0.0);
        conf.put("BASE", 1.2);
        conf.put("MEMORY", (double) Configuration.MEMORY_INFINITE);
        conf.put("SOURCE_REACH", 0.0);
        conf.put("WOM", 0.0);
        conf.put("SCENARIO", -1.0);
        conf.put("LEARNING_PERIODS", 100.0);
        conf.put("SAVED_ENDORSEMENTS", 0.0);
        conf.put("SAVED_REPOSTS_PER_SOURCE", 0.0);
        conf.put("SAVED_DETAILED_AGENT_DECISIONS", 0.0);
        conf.put("SAVED_AGENT_DECISIONS", 0.0);
        conf.put("COMPRESSED_RESULTS", 0.0);
        return conf;
    }
}
