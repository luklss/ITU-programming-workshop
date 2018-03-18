package searchengine;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Stefan on 5/11/16.
 *
 * @author Stefan Wachmann
 * @author Lucas Beck
 */
public class QueryEngineTest {

    private Index index;
    private QueryEngine naiveQuery;
    private QueryEngine orderedQuery;
    private Score simpleScore;
    private Score testScore;
    private List<Website> result;

    @Before
    public void setUp() {
        index = new InvertedIndex(new HashMap());
        List<String> words1 = Arrays.asList("first","website", "for", "testing", "the", "query");
        List<String> words2 = Arrays.asList("second", "website", "for", "testing", "with", "some", "different", "words",
                "like", "with");
        List<String> words3 = Arrays.asList("third", "website", "for", "testing", "using", "some", "other", "words", "for",
                "testing", "more");
        List<String> words4 = Arrays.asList("This", "ITU", "site", "is", "used", "for", "testing", "the", "URL", "Filter", "now",
                "these", "are", "just", "more", "words", "to", "have", "this", "come", "last");

        Website website1 = new Website("http://example.com/first", "first", words1);
        Website website2 = new Website("http://example.com/second", "second", words2);
        Website website3 = new Website("http://example.com/third", "third", words3);
        Website website4 = new Website("http://wikipedia.com/IT_University_Copenhagen", "ITU", words4);

        index.build(Arrays.asList(website1, website2, website3, website4));
        simpleScore = new SimpleScore();
        naiveQuery = new QueryEngine(index, simpleScore);

        // created anonymous class to return a simple value
        testScore = new Score(){
            @Override
            public double getScore(String word, Website site, Index index) {
                return word.length();
            }
        };
        orderedQuery = new QueryEngine(index, testScore);

    }
    @After
    public void tearDown() {
        index = null;
        simpleScore = null;
        naiveQuery = null;

    }

    @Test
    public void queryTest() {
        // simple naiveQuery
        result = naiveQuery.getWebsites("query");
        Assert.assertEquals("simpe naiveQuery", "http://example.com/first",result.get(0).getUrl());

        // tests OR
        result = naiveQuery.getWebsites("first OR second");
        Assert.assertEquals("OR simple query",2, result.size());
        result = naiveQuery.getWebsites("with OR the");
        Assert.assertEquals("OR simple query second time", 3, result.size());

        // tests AND
        result = naiveQuery.getWebsites("website for");
        Assert.assertEquals("AND simple query", 3, result.size());
        result = naiveQuery.getWebsites("website some other");
        Assert.assertEquals("AND simple query second time", "http://example.com/third",result.get(0).getUrl());

        // tests AND and OR
        result = naiveQuery.getWebsites("website second OR other");
        Assert.assertEquals("Complex query size", 2, result.size());

        // we were having a bug when searching for another simple naiveQuery the second time, therefore this test again.
        result = naiveQuery.getWebsites("query");
        Assert.assertEquals("Simple query second time", "http://example.com/first",result.get(0).getUrl());

        // makes sure that there is no distinction between lower and upper case when searching
        result = naiveQuery.getWebsites("Website");
        Assert.assertEquals("Case distinction", 3, result.size());
    }

    @Test
    public void queryOrderTest(){
        // test OR
        result = orderedQuery.getWebsites("query OR with OR first");
        Assert.assertEquals("Or logic", "http://example.com/first", result.get(0).getUrl());

        // tests AND and OR
        result = orderedQuery.getWebsites("query first for OR with");
        Assert.assertEquals("Complex logic", "http://example.com/first", result.get(0).getUrl());
    }

    @Test
    public void urlFilterTest(){
        // test "site:" plus com
        result = orderedQuery.getWebsites("site:com for");
        Assert.assertEquals("URL filter com", "[Website{url='http://wikipedia.com/IT_University_Copenhagen', title='ITU', words=[This, ITU, site, is, used, for, testing, the, URL, Filter, now, these, are, just, more, words, to, have, this, come, last]}, Website{url='http://example.com/third', title='third', words=[third, website, for, testing, using, some, other, words, for, testing, more]}, Website{url='http://example.com/first', title='first', words=[first, website, for, testing, the, query]}, Website{url='http://example.com/second', title='second', words=[second, website, for, testing, with, some, different, words, like, with]}]", result.toString());

        // test space after "site:"
        result = orderedQuery.getWebsites("site: for");
        Assert.assertEquals("URL Filter space", "[Website{url='http://wikipedia.com/IT_University_Copenhagen', title='ITU', words=[This, ITU, site, is, used, for, testing, the, URL, Filter, now, these, are, just, more, words, to, have, this, come, last]}, Website{url='http://example.com/third', title='third', words=[third, website, for, testing, using, some, other, words, for, testing, more]}, Website{url='http://example.com/first', title='first', words=[first, website, for, testing, the, query]}, Website{url='http://example.com/second', title='second', words=[second, website, for, testing, with, some, different, words, like, with]}]", result.toString());

        // test full website URL
        result = orderedQuery.getWebsites("site:http://example.com/third for");
        Assert.assertEquals("URL Filter full website", "[Website{url='http://example.com/third', title='third', words=[third, website, for, testing, using, some, other, words, for, testing, more]}]", result.toString());

        // test part of website URL
        result = orderedQuery.getWebsites("site:University for");
        Assert.assertEquals("URL Filter for of URL", "[Website{url='http://wikipedia.com/IT_University_Copenhagen', title='ITU', words=[This, ITU, site, is, used, for, testing, the, URL, Filter, now, these, are, just, more, words, to, have, this, come, last]}]", result.toString());

        // test wrong website substring
        result = orderedQuery.getWebsites("site:wrong for");
        Assert.assertEquals("URL Filter wrong substring", 0, result.size());
    }

    @Test
    public void prefixSearchTest(){
        // test simple prefix search with only one asterisk
        result = orderedQuery.getWebsites("th*");
        Assert.assertEquals("Prefix search - one asterisk", "[Website{url='http://wikipedia.com/IT_University_Copenhagen', title='ITU', words=[This, ITU, site, is, used, for, testing, the, URL, Filter, now, these, are, just, more, words, to, have, this, come, last]}, Website{url='http://example.com/third', title='third', words=[third, website, for, testing, using, some, other, words, for, testing, more]}, Website{url='http://example.com/first', title='first', words=[first, website, for, testing, the, query]}]", result.toString());

        // test simple prefix search with multiple asterisks
        result = orderedQuery.getWebsites("th* u*");
        Assert.assertEquals("Prefix search - multiple asterisks", "[Website{url='http://example.com/third', title='third', words=[third, website, for, testing, using, some, other, words, for, testing, more]}, Website{url='http://wikipedia.com/IT_University_Copenhagen', title='ITU', words=[This, ITU, site, is, used, for, testing, the, URL, Filter, now, these, are, just, more, words, to, have, this, come, last]}]", result.toString());

        // test simple prefix search with an added normal search word
        result = orderedQuery.getWebsites("fo* now");
        Assert.assertEquals("Prefix search - asterisk plus normal word", "[Website{url='http://wikipedia.com/IT_University_Copenhagen', title='ITU', words=[This, ITU, site, is, used, for, testing, the, URL, Filter, now, these, are, just, more, words, to, have, this, come, last]}]", result.toString());

        // test complex prefix search with one asterisk on either side of the OR
        result = orderedQuery.getWebsites("fo* OR th*");
        Assert.assertEquals("Prefix search - complex one asterisk", "[Website{url='http://wikipedia.com/IT_University_Copenhagen', title='ITU', words=[This, ITU, site, is, used, for, testing, the, URL, Filter, now, these, are, just, more, words, to, have, this, come, last]}, Website{url='http://example.com/third', title='third', words=[third, website, for, testing, using, some, other, words, for, testing, more]}, Website{url='http://example.com/first', title='first', words=[first, website, for, testing, the, query]}, Website{url='http://example.com/second', title='second', words=[second, website, for, testing, with, some, different, words, like, with]}]", result.toString());

        // test complex prefix search with multiple asterisks on either side of the OR
        result = orderedQuery.getWebsites("fo* t* OR th* f*");
        Assert.assertEquals("Prefix search - complex multiple asterisks", "[Website{url='http://wikipedia.com/IT_University_Copenhagen', title='ITU', words=[This, ITU, site, is, used, for, testing, the, URL, Filter, now, these, are, just, more, words, to, have, this, come, last]}, Website{url='http://example.com/third', title='third', words=[third, website, for, testing, using, some, other, words, for, testing, more]}, Website{url='http://example.com/first', title='first', words=[first, website, for, testing, the, query]}, Website{url='http://example.com/second', title='second', words=[second, website, for, testing, with, some, different, words, like, with]}]", result.toString());

        // test complex prefix search with an added search word
        result = orderedQuery.getWebsites("for t* OR th* for");
        Assert.assertEquals("Prefix search - complex added normal word", "[Website{url='http://wikipedia.com/IT_University_Copenhagen', title='ITU', words=[This, ITU, site, is, used, for, testing, the, URL, Filter, now, these, are, just, more, words, to, have, this, come, last]}, Website{url='http://example.com/third', title='third', words=[third, website, for, testing, using, some, other, words, for, testing, more]}, Website{url='http://example.com/first', title='first', words=[first, website, for, testing, the, query]}, Website{url='http://example.com/second', title='second', words=[second, website, for, testing, with, some, different, words, like, with]}]", result.toString());

        // test single word query with asterisk inside the word (not at the end)
        result = orderedQuery.getWebsites("fo*r");
        Assert.assertEquals("Prefix search - asterisk inside word", 0, result.size());

        // test multiple word query with asterisk inside the word
        result = orderedQuery.getWebsites("fo*r f* *a words");
        Assert.assertEquals("Prefix search - asterisk inside word (multiple)", "[Website{url='http://wikipedia.com/IT_University_Copenhagen', title='ITU', words=[This, ITU, site, is, used, for, testing, the, URL, Filter, now, these, are, just, more, words, to, have, this, come, last]}, Website{url='http://example.com/third', title='third', words=[third, website, for, testing, using, some, other, words, for, testing, more]}, Website{url='http://example.com/second', title='second', words=[second, website, for, testing, with, some, different, words, like, with]}]", result.toString());
    }

    @Test
    public void urlFilterAndPrefixSearchTest() {
        // URL Filter and simple prefix search
        result = orderedQuery.getWebsites("site:University th*");
        Assert.assertEquals("URL & prefix - one asterisk", "[Website{url='http://wikipedia.com/IT_University_Copenhagen', title='ITU', words=[This, ITU, site, is, used, for, testing, the, URL, Filter, now, these, are, just, more, words, to, have, this, come, last]}]", result.toString());

        // URL Filter (in lowercase) and complex prefix search
        result = orderedQuery.getWebsites("site:university th* OR m* now");
        Assert.assertEquals("URL & prefix - one asterisk", "[Website{url='http://wikipedia.com/IT_University_Copenhagen', title='ITU', words=[This, ITU, site, is, used, for, testing, the, URL, Filter, now, these, are, just, more, words, to, have, this, come, last]}]", result.toString());
    }
}
