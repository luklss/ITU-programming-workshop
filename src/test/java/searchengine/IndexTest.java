package searchengine;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
//import sun.jvm.hotspot.jdi.ArrayReferenceImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * This class tests all the methods from all the indexes.
 *
 * @author Lucas Beck
 */
public class IndexTest {

    private Index simpleIndex;
    private Index invertedIndexHash;
    private Index invertedIndexTree;

    @Before
    public void setUp() {
        simpleIndex = new SimpleIndex();
        invertedIndexHash = new InvertedIndex(new HashMap<>());
        invertedIndexTree = new InvertedIndex(new TreeMap<>());

        List<String> words1 = Arrays.asList("This", "is", "a", "first", "website", "just", "a", "test");
        List<String> words2 = Arrays.asList("This", "is", "a", "second", "website");

        Website website1 = new Website("http://example.com/first", "first", words1);
        Website website2 = new Website("http://example.com/seconbd", "second", words2);

        simpleIndex.build(Arrays.asList(website1, website2));
        invertedIndexHash.build(Arrays.asList(website1, website2));
        invertedIndexTree.build(Arrays.asList(website1,website2));
    }

    @After
    public void tearDown() {
        simpleIndex = null;
        invertedIndexHash = null;
        invertedIndexTree = null;
    }

    // Build Methods
    @Test
    public void buildTestSimpleIndex(){
        Assert.assertEquals("SimpleIndex{list=[Website{url='http://example.com/first', title='first', words=[This, is, a, first, website, just, a, test]}, Website{url='http://example.com/seconbd', title='second', words=[This, is, a, second, website]}]}", simpleIndex.toString());

    }

    @Test
    public void buildTestInvertedIndexHash(){
        Assert.assertEquals("InvertedIndex{map={a=[Website{url='http://example.com/first', title='first', words=[This, is, a, first, website, just, a, test]}, Website{url='http://example.com/seconbd', title='second', words=[This, is, a, second, website]}], website=[Website{url='http://example.com/first', title='first', words=[This, is, a, first, website, just, a, test]}, Website{url='http://example.com/seconbd', title='second', words=[This, is, a, second, website]}], test=[Website{url='http://example.com/first', title='first', words=[This, is, a, first, website, just, a, test]}], This=[Website{url='http://example.com/first', title='first', words=[This, is, a, first, website, just, a, test]}, Website{url='http://example.com/seconbd', title='second', words=[This, is, a, second, website]}], is=[Website{url='http://example.com/first', title='first', words=[This, is, a, first, website, just, a, test]}, Website{url='http://example.com/seconbd', title='second', words=[This, is, a, second, website]}], just=[Website{url='http://example.com/first', title='first', words=[This, is, a, first, website, just, a, test]}], first=[Website{url='http://example.com/first', title='first', words=[This, is, a, first, website, just, a, test]}], second=[Website{url='http://example.com/seconbd', title='second', words=[This, is, a, second, website]}]}}",invertedIndexHash.toString());
    }

    @Test
    public void buildTestInvertedIndexTree() {
        Assert.assertEquals("InvertedIndex{map={This=[Website{url='http://example.com/first', title='first', words=[This, is, a, first, website, just, a, test]}, Website{url='http://example.com/seconbd', title='second', words=[This, is, a, second, website]}], a=[Website{url='http://example.com/first', title='first', words=[This, is, a, first, website, just, a, test]}, Website{url='http://example.com/seconbd', title='second', words=[This, is, a, second, website]}], first=[Website{url='http://example.com/first', title='first', words=[This, is, a, first, website, just, a, test]}], is=[Website{url='http://example.com/first', title='first', words=[This, is, a, first, website, just, a, test]}, Website{url='http://example.com/seconbd', title='second', words=[This, is, a, second, website]}], just=[Website{url='http://example.com/first', title='first', words=[This, is, a, first, website, just, a, test]}], second=[Website{url='http://example.com/seconbd', title='second', words=[This, is, a, second, website]}], test=[Website{url='http://example.com/first', title='first', words=[This, is, a, first, website, just, a, test]}], website=[Website{url='http://example.com/first', title='first', words=[This, is, a, first, website, just, a, test]}, Website{url='http://example.com/seconbd', title='second', words=[This, is, a, second, website]}]}}",invertedIndexTree.toString());
    }

    // Lookup methods
    @Test
    public void simpleIndexLookupTest() {
        lookupTest(simpleIndex);
    }

    @Test
    public void invetedIndexHashLookupTest() {
        lookupTest(invertedIndexHash);
    }

    @Test
    public void invetedIndexTreeLookupTest() {
        lookupTest(invertedIndexTree);
    }

    // Create test for whether the invertedIndex creates several lists of websites when the same word is found several times in a website.

    // actual code for all the lookups
    private void lookupTest(Index index) {
        List<Website> result = index.lookup("This");
        Assert.assertEquals(2, result.size());

        result = index.lookup("just");
        Assert.assertEquals(1, result.size());

        Assert.assertEquals("http://example.com/first", result.get(0).getUrl());

        result = index.lookup("itu");
        Assert.assertNotEquals(null, result);
        Assert.assertEquals(0, result.size());

    }

}