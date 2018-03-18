package searchengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class implements the interface index using a list. Each website corresponds to
 * a instance the list.
 * *
 * @author Stefan Wachmann
 * @author Lucas Beck
 */
public class SimpleIndex implements Index {
    private List<Website> list;
    private double averageDocumentLength;

    public void build(List<Website> website){
        list = website;
        calculateAverageDocumentLength();
    }


    public List<Website> lookup(String word){
        List<Website> ListOfWebsites = new ArrayList<Website>();
        // Search for line in the list of websites
        for (Website w: list)
        {
            if (w.containsWord(word))
            {
                ListOfWebsites.add(w);
            }
        }
        return ListOfWebsites;
    }

    @Override
    public int numWebsites() {
        return list.size();
    }

    @Override
    public double getAverageDocumentLength() {
        return averageDocumentLength;
    }

    /**
     * This method calculates the average words in the index and assigns it to the instance variable "getAverageDocumentLength"
     */
    private void calculateAverageDocumentLength() {
        double numWebsites = 0;
        double numWords = 0;

        for (Website w : list){ // iterate through websites in the index
            numWords += w.getWords().size(); // increases the number of words by the current website list of words size
            numWebsites++;
        }

        averageDocumentLength = (numWords/numWebsites);
    }

    /**
     * This method returns a String representation of the Index
     *
     * @return SimpleIndex string representation
     */
    @Override
    public String toString() {
        return "SimpleIndex{" +
                "list=" + list +
                '}';
    }

    /**
     * Method for returning words in the data set that start with the passed sub-query.
     * @param subQuery A prefix sub-query where the asterisk has been removed
     * @return a list of words that start with the passed sub-query.
     */
    public List<String> getPrefixWords(String subQuery) {
        List<String> result = new ArrayList<>();
        for (Website w :list) {
            for (String s : w.getWords()) { // walk through all words on all websites.
                if(s.startsWith(subQuery)) { // if a word starts with the prefix sub-query
                    result.add(s); // then add it to the result list.
                }
            }
        }
        return result; // return a list of words that start with the prefix sub-query.
    }
}