package searchengine;

/**
 * This class implements the score. It calculates the score based on a series of
 * parameters. It takes into consideration the words on the website, the average
 * number of words over all websites and some empiric parameters (k,b) that may
 * be varied when tunning for a better result. See more : https://en.wikipedia.org/wiki/Okapi_BM25
 * @author Lucas Beck
 */
public class BM25Score implements Score {

    private Score idfScore;
    private Score tfScore;
    private double k;
    private double b;
    private double averageWords;

    /**
     * This method constructs the BM25Score object.
     */
    public BM25Score() {
        idfScore = new IDFScore();
        tfScore = new TFScore();
        k = 1.75;
        b = 0.75;
    }


    @Override
    public double getScore(String word, Website site, Index index) {

        double numberOfWords = site.getWords().size();
        double tf = tfScore.getScore(word,site,index);
        double idf = idfScore.getScore(word,site,index);

        averageWords = index.getAverageDocumentLength();

        double numerator = k +1;
        double denominator = (k * ((1 - b) + (b * (numberOfWords/averageWords))) + tf);


        return (idf *(tf * (numerator/denominator)));
    }


}
