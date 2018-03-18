package searchengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class takes care of reading an input database file and
 * transforming it into {@code Website} objects.
 *
 * @author Martin Aumüller
 */

public class FileHelper {

    /**
     *  A method used to test if the entry of a website is valid,
     *  an entry is valid if url is not null, title is not null and
     *  list of words is not empty.
     *
     * @param url website's url
     * @param title website's title
     * @param words the list of words within the website
     * @return true if valid.
     *
     *
     */
    public static boolean isEntryValid(String url, String title, List<String> words) {
        if (url == null) return false;
        if (title == null) return false;
        if (words == null) return false;
        return true;
    }

    /**
     * This methods transforms a database file into a list of {@code Website}
     * objects. The list of words has the same order as the entries
     * in the database file.
     *
     * @param arg the filename of the database
     * @return the list of websites that are contained in the database file.
     */
    public static List<Website> parseFile(String arg) {
        List<Website> result = new ArrayList<Website>();
        String url = null;
        String title = null;
        List<String> wordList = null;

        // perform iterations in order to create the list of websites to be returned
        try
        { // try to read file, otherwise go to catch
            Scanner sc = new Scanner(new File(arg), "UTF-8");
            while (sc.hasNext()) {
                String line = sc.nextLine().trim();

                if (line.startsWith("*PAGE:")) {
                    // new entry starts
                    // save the old one
                    if (isEntryValid(url, title, wordList)) { // only add the entry if entry is valid
                        result.add(new Website(url, title, wordList));
                    }

                    // if reaches new website, sets line and title to null and saves url
                    title = null;
                    wordList = null;
                    url = line.substring(6); // gets a substring from index 6 onwards
                }

                else if (url != null && title == null) { // if it found an url, save the title
                    title = line;
                }

                // add the words to the list
                else if (url != null && title != null) {
                    if (wordList == null) { // checks if the word for this website was initialized
                        wordList = new ArrayList<String>();
                      }
                    wordList.add(line.toLowerCase()); // make sure all the words are lower case
                }
            }

            // prints the last entry
            if (isEntryValid(url, title, wordList)) {
                result.add(new Website(url, title, wordList)); // only add the entry if the last line read was a word
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}
