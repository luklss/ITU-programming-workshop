package searchengine;

/**
 * This class implements the score. It calculates the score according to
 * the number of times a word occur in a particular website.
 * @author Lucas Beck
 */
public class TFScore implements Score {

    @Override
    public double getScore(String word, Website site, Index index) {
        double result = 0;
        for (String w : site.getWords()){ // iterate through all words in the website
            if (w.equals(word)) { // if encounter the word, increase score
                result ++;
            }
        }
        return result;
    }
}
