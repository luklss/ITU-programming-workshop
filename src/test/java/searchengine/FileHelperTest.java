package searchengine;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by maau on 05/10/16.
 *
 * @author Lucas
 * @author Stefan Wachmann
 */
public class FileHelperTest {
    private List<Website> sites_okay;
    private List<Website> sites_wrong;
    
    @Before
    public void setUp() {
        sites_okay = FileHelper.parseFile("test-resources/test-file.txt");
        sites_wrong = FileHelper.parseFile("test-resources/test-file-with-errors.txt");
    }
    
    @After
    public void tearDown() {
        sites_okay = null;
        sites_wrong = null;
    }
    
    @Test
    public void parseFileSimple() throws Exception {
        parseFileTest(sites_okay);
    }

    @Test
    public void parseFileWithErrors() throws Exception {
        parseFileTest(sites_wrong);
    }

// Actual code for testing the parsing of the files.
    private void parseFileTest(List<Website> fileInput) {
        assertEquals(2,fileInput.size());
        assertEquals("title1",fileInput.get(0).getTitle());
        assertEquals(true, fileInput.get(0).containsWord("word1"));
        assertEquals(false, fileInput.get(0).containsWord("word3"));
        assertEquals("title2", fileInput.get(1).getTitle());
        assertEquals(true, fileInput.get(1).containsWord("word1"));
        assertEquals(true, fileInput.get(1).containsWord("word3"));
    }
}