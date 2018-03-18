package searchengine;

/**
 * This class implements the score. It calculates the score as a product
 * of the Idf implementation and the Tf implementation. Thus, the score is
 * relative not only to to the number of time the word appears in the
 * website, but also in how many websites the word appear relative to the database size.
 * @author Lucas Beck
 */
public class TFIDFScore implements Score {

    Score tf = new TFScore();
    Score idf = new IDFScore();

    @Override
    public double getScore(String word, Website site, Index index) {

        double tfScore = tf.getScore(word, site, index);
        double idfScore = idf.getScore(word, site, index);

        return tfScore * idfScore;
    }
}
