package searchengine;

/**
 * This is a naive implementation of the score, used for testing purposes.
 */
public class SimpleScore implements Score {
    @Override
    public double getScore(String word, Website site, Index index) {
        return 0;
    }
}
