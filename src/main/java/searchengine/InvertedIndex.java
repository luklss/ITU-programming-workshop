package searchengine;

import java.util.*;


/**
 * This class implements the interface index in a inverted manner using a map. each key is a word
 * and each value is a list of the websites in which this word appears. No website is duplicated for
 * a particular word, even if the word is contained more than once in a website.
 *
 *  @author Lucas Beck
 */

public class InvertedIndex implements Index {

    private Map<String, List<Website>> map;
    private List<Website> websites; // store websites so don't have to iterate through all the map again when passing the websites
    private double averageDocumentLength;

    /**
     * The constructor of the InvertedIndex object.
     * takes the type of map to be created
     */
    public InvertedIndex(Map map) {
        this.map = map;
    }


    public void build(List<Website> listOfWebsites){
        map.clear();
        websites = listOfWebsites;

        for (Website currentWebsite: listOfWebsites ){ // iterate through the list of websites

            for (String currentWord: currentWebsite.getWords()) {// iterate through the list of words of each website

                if (!map.containsKey(currentWord)){ // if word has no websites associate with it yet, create a list of websites

                    List<Website> websitesPerWord = new ArrayList<Website>(); // create list of websites
                    map.put(currentWord, websitesPerWord);
                }

                if (!isWebsiteContained(map.get(currentWord), currentWebsite)) { // check if the current website is already in the list
                    map.get(currentWord).add(currentWebsite); // add the currentWebsite to the list for the currentWord
                }
            }
        }
        calculateAverageDocumentLength();
    }


    public List<Website> lookup(String word){

        if (map.get(word) == null){ // returns an empty list and not null in case it does not find any website
            return new ArrayList<Website>();
        }
        return map.get(word);
    }

    /**
     * A static method used to test if a website is already contained
     * in a list of websites.
     *
     * @param list    list of website's
     * @param website {@code Website} the website to be searched
     * @return true if the list contains the website.
     */
    private static boolean isWebsiteContained(List<Website> list, Website website) {
        for (Website w : list) {
            if (w.equals(website)) return true;
        }
        return false;
    }

    /**
     * This method calculates the average words in the index and assigns it to the instance variable "getAverageDocumentLength"
     */
    private void calculateAverageDocumentLength() {
        double numWebsites = 0;
        double numWords = 0;

        for (Website w : websites){ // iterate through websites in the index
            numWords += w.getWords().size(); // increases the number of words by the current website list of words size
            numWebsites++;
        }

        averageDocumentLength = (numWords/numWebsites);
    }

    @Override
    public int numWebsites() {
        return websites.size();
    }

    @Override
    public double getAverageDocumentLength() {
        return averageDocumentLength;
    }

    /**
     * This method returns a String representation of the Index
     * @return InvertedIndex string representation
     */
    @Override
    public String toString() {
        return "InvertedIndex{" +
                "map=" + map +
                '}';
    }

    /**
     * Method for returning words in the data set that start with the passed sub-query.
     * @param subQuery A prefix sub-query where the asterisk has been removed.
     * @return a list of words that start with the passed sub-query.
     */
    public List<String> getPrefixWords(String subQuery) {
        Set<String> wordList = map.keySet();
        List<String> result = new ArrayList<>();
        for (String s : wordList) { // walk through all words on all websites.
            if(s.startsWith(subQuery)) { // if a word starts with the prefix sub-query
                result.add(s); // then add it to the result list.
            }
        }
        return result; // return a list of words that start with the prefix sub-query.
    }

}
