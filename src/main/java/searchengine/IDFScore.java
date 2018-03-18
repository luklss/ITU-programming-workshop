package searchengine;

/**
 * This class implements the score. It calculates the score according to
 * the total number of websites in the Index (database) relative to the number of websites a word occur
 * @author Lucas Beck
 */
public class IDFScore implements Score {

         @Override
    public double getScore(String word, Website site, Index index) {
        double totalNum = 0;
        double siteNum = 0;

        siteNum = index.lookup(word).size(); // total number of websites that the word occur
        totalNum = index.numWebsites(); // total number of websites in the data base

        if (siteNum == 0){ // if the word is not cointained at all, avoid division by 0
            return 0;
        }
        return logBase2(totalNum/siteNum);
    }

    /**
     * This function returns the log base 2 given a double
     * @param x double number
     * @return the log base 2 in a double number
     */
    private static double logBase2 (double x){
        return (Math.log(x)/Math.log(2));
    }
}
