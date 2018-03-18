package searchengine;

/**
 *  This interface refers to an object responsible for calculating the scores of words
 *  across a website relative to a database of websites
 *  @author Lucas Beck
 */
public interface Score {
    /**
     * This method returns the score of a particular word given an website
     * and a index (document)
     * @param word word to be assigned a score
     * @param site a website the word is contained
     * @param index the index of the database of websites
     * @return a double representing the score of the word
     */
    double getScore (String word, Website site, Index index);
}
