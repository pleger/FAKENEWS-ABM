package agent;

import inputManager.InnerNewsSource;
import inputManager.NewsSources;
import utils.Error;

import java.util.ArrayList;
import java.util.List;

public class NewsSourceFactory {

    private static ArrayList<NewsSource> newsSources;

    public static List<NewsSource> createFromInput() {
        ArrayList<InnerNewsSource> innerNewsSources =  NewsSources.getInnerNewsSources();
        newsSources = new ArrayList<>();
        NewsSource.resetCounter();
        innerNewsSources.iterator().forEachRemaining(innerNewsSource -> newsSources.add(new NewsSource(innerNewsSource)));
        return newsSources;
    }


    public static NewsSource getNewsSource(List<NewsSource> newsSources, int id) {
        for (NewsSource mk : newsSources) {
            if (mk.getID() == id) {
                return mk;
            }
        }
        return null;
    }

    public static NewsSource getNewsSource(int id) {
        return getNewsSource(newsSources, id);
    }

    public static NewsSource getNewsSource(List<NewsSource> newsSources, String name) {
        int id = -1;
        for (NewsSource mk : newsSources) {
            if (mk.getName().equals(name)) {
                id = mk.getID();
            }
        }
        Error.setAssert(id != -1, "ERROR. NewsSourceFactory: no newsSource found:"+ name);
        return getNewsSource(newsSources, id);
    }

    public static NewsSource getNewsSource(String name) {
        return getNewsSource(newsSources, name);
    }

    public static ArrayList<NewsSource> getNewsSources() {return newsSources;}
}
