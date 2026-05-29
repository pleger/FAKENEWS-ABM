import agent.SNSUserFactory;
import agent.NewsSource;
import agent.NewsSourceFactory;
import endorsement.EndorsementEvalStrategies;
import inputManager.Configuration;
import inputManager.Loader;
import scenarios.ScenarioFactory;

public class TestRunner {
    private static int passed = 0;

    public static void main(String[] args) {
        testEndorsementFormulaForHighBinaryLevel();
        testLoaderReadsFakeNewsBaseline();
        testCustomizedScenarioCopiesSelectedAttributes();
        System.out.println("Tests passed: " + passed);
    }

    private static void testEndorsementFormulaForHighBinaryLevel() {
        Configuration.LEVELS = 2;
        double value = EndorsementEvalStrategies.BY_MAX(new Double[]{0.1, 0.9}, 4.0);
        assertEquals("binary high endorsement should equal snsUser weight", 4.0, value, 0.0001);
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

    private static void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(String message, int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + " but got " + actual);
        }
    }

    private static void assertEquals(String message, double expected, double actual, double epsilon) {
        if (Math.abs(expected - actual) > epsilon) {
            throw new AssertionError(message + ": expected " + expected + " but got " + actual);
        }
    }
}
