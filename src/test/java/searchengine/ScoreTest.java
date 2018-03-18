package searchengine;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This test assesses the validity of the implementations of the interface Score.
 */
public class ScoreTest {
    private Index index;
    private Score scoreTf;
    private Score scoreIdf;
    private Score scoreTfidf;
    private Score scoreBM25;
    private Website website1;
    private Website website2;
    private Website website3;
    private Index simpleIndex;

    @Before
    public void setUp() {
        List<String> words1 = Arrays.asList("this", "website", "is", "the", "first", "website");
        List<String> words2 = Arrays.asList("this", "website", "is", "the", "second", "website", "it", "is", "cooler");
        List<String> words3 = Arrays.asList("this", "website", "is", "the", "third", "website", "it", "is", "even", "cooler",
                " it", "is", "twice", "cooler");

        website1 = new Website("http://test.com/first", "first", words1);
        website2 = new Website("http://test.com/second", "second", words2);
        website3 = new Website("http://test.com/third", "third", words3);

        index = new InvertedIndex(new HashMap());
        index.build(Arrays.asList(website1,website2, website3));
        simpleIndex = new SimpleIndex();
        simpleIndex.build(Arrays.asList(website1,website2, website3));

        scoreTf = new TFScore();
        scoreIdf = new IDFScore();
        scoreTfidf = new TFIDFScore();
        scoreBM25 = new BM25Score();
    }

    @After
    public void tearDown(){
        index = null;
        scoreTf = null;
        scoreIdf = null;
        scoreBM25 = null;
        scoreTfidf = null;
        simpleIndex = null;
    }

    @Test
    public void testScoreTf () {
        // word that occur twice in the website
        double score = scoreTf.getScore("website", website1, index);
        Assert.assertEquals("Word website", 2, score, 0.01);

        // word that does not occur at all in the website
        score = scoreTf.getScore("banana", website1, index);
        Assert.assertEquals("Word does not exist", 0, score, 0.01);

        // word that occur once in the website
        score = scoreTf.getScore("cooler", website2, index);
        Assert.assertEquals("Word cooler", 1, score, 0.01);
    }

    @Test
    public void testScoreIdf (){
        // word that occur in one website
        double score = scoreIdf.getScore("second", website2, index);
        Assert.assertEquals("Word second", 1.58, score, 0.01);

        // word that does not occur
        score = scoreIdf.getScore("banana", website2, index);
        Assert.assertEquals("Word does not exist", 0, score, 0.01);

        // word that occur in two websites
        score = scoreIdf.getScore("cooler", website2, index);
        Assert.assertEquals("Word cooler", 0.58, score, 0.01);
    }

    @Test
    public void testScoreTfidf () {
        // word that occur in one website
        double score = scoreTfidf.getScore("second", website2, index);
        Assert.assertEquals("Word second", 1.58, score, 0.01);

        // word that does not occur
        score = scoreTfidf.getScore("banana", website2, index);
        Assert.assertEquals("Word does not exist", 0, score, 0.01);

        // word that occur in two websites
        score = scoreTfidf.getScore("cooler", website3, index);
        Assert.assertEquals("Word cooler", 1.16, score, 0.01);
    }

    @Test
    public void testScoreBM25 () {

        // word that occur in one website
        double score = scoreBM25.getScore("second", website2, index);
        Assert.assertEquals("Word second", 1.63, score, 0.01);

        // word that does not occur
        score = scoreBM25.getScore("banana", website2, index);
        Assert.assertEquals("Word does not exist", 0, score, 0.01);

        // word that occur in two websites
        score = scoreBM25.getScore("cooler", website3, index);
        Assert.assertEquals("Word cooler", 0.74, score, 0.01);

        // test one case with the simple index to make sure the getAverageDocumentLength was done correctly
        // in both indexes
        score = scoreBM25.getScore("cooler", website3, simpleIndex);
        Assert.assertEquals("simpleIndex", 0.74, score, 0.01);
    }

}
