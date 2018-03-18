package searchengine;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class takes creates the object QueryEngine, which is responsible
 * for handling complex queries
 *
 * @author Stefan
 * @author Lucas
 */
public class QueryEngine {

    private Index index;
    private Score score;
    private List<String> prefixStrings = new ArrayList<>();

    /**
     *  The constructor of the QueryEngine. Takes an index as parameter
     *  in order to perform the searches in individual words
     * @param index the index as a Map, with keys as words and values as websites
     */
    public QueryEngine (Index index, Score score) {
        this.index = index;
        this.score = score;
    }

    /**
     * This method returns a list of Websites to the query entered.
     * It accepts queries using the 'OR' operator as well as the
     * ' ' operator representing the AND logic. Example "USA OR Canada Brazil"
     * will return the websites that contain either USA or Canada and Brazil.
     * It also returns the websites in order of relevance
     * @param query the query to be searched
     * @return list of websites for the specified query
     */
    public List<Website> getWebsites(String query) {
        List<String> queries;
        List<String> lowerCaseQueries = new ArrayList<>();
        Map<Website, Double> result = new HashMap<>();
        List<String> queriesToRemove = new ArrayList<>();
        String url = null;
        List<Website> approvedWebsites = new ArrayList<>();

        if (query.startsWith("site:")) { // Do for queries starting with "site:"
            url = query.substring(5, query.indexOf(" ")).toLowerCase(); // save the string to filter from. That is the string that starts after "site:" and ends before the first " ".
            query = query.substring(query.indexOf(" ") + 1); // remove everything until, and including, the first " " from the query.
        }

        queries = Arrays.asList(query.split(" OR ")); // splits queries that use OR

        // makes sure there is no distinction between lower and upper case
        for (String q : queries){
            lowerCaseQueries.add(q.toLowerCase());
        }

        // check if any query is is a prefix search (prefix query) and find all prefix search queries.
        for (String q : lowerCaseQueries) {
            if (q.contains("*")) {
                queriesToRemove.add(q); // add the current query to a remove list as to remove the queries containing "*" from lowerCaseQueries when not iterating through the list.
                prefixQueries(q); // call the method prefixQueries and pass the query to it.
            }
        }
        lowerCaseQueries.removeAll(queriesToRemove); // remove all queries containing asterisks.
        lowerCaseQueries.addAll(getPrefixStrings()); // append all all new querie strings from the prefix search.

        for (String q : lowerCaseQueries) {  // iterate through all queries split
            for (Website w: subQuery(q).keySet()) { // send the queries split by "or" to be processed by subquery and then iterate in the websites returned
                if (url != null) { // if a url-filter search has been detected do the following.
                    if (w.getUrl().toLowerCase().contains(url)) { // if the current website contains the url search.
                        if (!approvedWebsites.contains(w)) { // and it is not contained in the list of approved websites already.
                            approvedWebsites.add(w); // then add it to the list of approved websites.
                        }
                    }
                }
                if (!result.containsKey(w)){ // if the result was not added yet
                    result.put(w, subQuery(q).get(w)); // put the result with its score
                } else {
                    if (result.get(w) < subQuery(q).get(w)){ // if the result is contained but score is less, then substitute score
                        result.put(w, subQuery(q).get(w));
                    }
                }
            }
        }

        if (url != null) { // if a url-filter search has been made
            result.keySet().retainAll((approvedWebsites)); // retain only approved websites in the result list.
            approvedWebsites.clear();
        }

        return result. entrySet () . stream () . sorted (( x , y ) -> y . getValue () .
                compareTo ( x . getValue () ) ) . map ( Map . Entry :: getKey ) . collect (
                Collectors. toList () );
    }

    /**
     * This method takes a complex query that can contain
     * spaces as separators representing an 'AND' logic.
     * Example: 'USA Brazil' will return the websites containing both USA and Brazil.
     * Each website has a score associated with it according to the relevance.
     * @param query the query to be searched
     * @return Map containing websites and its scores.
     */
    private Map<Website, Double> subQuery (String query){
        List<String> subQueries;
        Map<Website, Double> scoredWebsites = new HashMap();
        Map<Website, Double> tempMap = new HashMap();

        subQueries = Arrays.asList(query.split(" ")); // splits query

        for(String subQ : subQueries){ // iterate through every split query
            if (scoredWebsites.isEmpty()) { // if the map is still empty
                for (Website w : index.lookup(subQ) ){ // iterate through the websites found for the word
                    scoredWebsites.put(w,score.getScore(subQ,w,this.index)); //populate the map the first time with websites and score
                }
            }
            else { // uses a temporary map to compare the existing websites with the ones from the next query
                for (Website w : index.lookup(subQ) ){ // iterate through the websites found for the word
                    tempMap.put(w,score.getScore(subQ,w,this.index)); // populate temporary map
                }
                scoredWebsites = intersectionMap(scoredWebsites, tempMap); // merge maps
                tempMap.clear(); // make sure to clear results to be compared
            }
        }
        return scoredWebsites;
    }

    /**
     * This method takes in a query string containing a "*" and matches words in the index that start with the string
     * before the "*" character and returns new queries from the matched words.
     * @param prefixQuery a query containing the "*" character.
     * @return a list of new queries based on the passed query containing a "*",
     * where any word ending with a "*" is replaced with a word starting with the string before the "*".
     */

    private void prefixQueries(String prefixQuery) {
        List<String> prefixSubQueries = Arrays.asList(prefixQuery.split(" ")); // splits prefixQuery into sub-queries.
        List<String> result = new ArrayList<>();

        for(String subQ : prefixSubQueries) { // iterate through the sub-queries in the query string containing a "*".
            if (subQ.endsWith("*")) { // find any word that ends with a "*".
                String trimmedSubQ = subQ.substring(0, subQ.length() - 1); // adding a trimmed subquery without the "*" from the subquery prefix word.
                for (String s : index.getPrefixWords(trimmedSubQ)) { // iterates through a list of replacement words for the subquery.
                    if (prefixQuery.startsWith(subQ)) { // if the prefix search query starts with the current subquery
                        result.add(prefixQuery.replace(subQ, s)); // then add the prefix search query while replacing the current replacement word.
                    } else { // if the current subquery is not at the start of the prefix search query
                        result.add(prefixQuery.replace(" " + subQ, " " + s)); // make sure only to replace whole words and not word-endings for words that end with the subquery.
                    }
                }
            } else if (subQ.contains("*")) {    // if a subquery contains an asterisk but not at the end of the word
                                                // then the query string should still be added after removing the faulty subquery.
                result.add(prefixQuery.replace(subQ, ""));
            }
        }
        for (String s : result) {
            if (s.contains("*")) {
                prefixQueries(s); // recursive call that clears the prefix queries of further asterisks if the original search contained more than one asterisk.
            }
        }

        if (!result.isEmpty()) {
            for (String r : result) {
                if (!r.contains("*") && r.length() >= prefixQuery.replace("*", "").length()) {
                    setPrefixStrings(r); // saves searches that are completely cleared of asterisks to a list outside of the method that is being called recursively.
                }
            }
        }
        result.clear(); // clear lists
    }

    /**
     * A way to save search queries while inside a recursive call
     * @param prefixString a prefix search query cleared of asterisks
     */
    private void setPrefixStrings(String prefixString) {
        if (!prefixStrings.contains(prefixString)) { // only add query strings that are not already contained.
            prefixStrings.add(prefixString);
        }
    }

    /**
     * A way to get all search query strings constructed from a prefix search query (after splitting by "OR".
     * @return a list of prefix searches cleared of asterisks.
     */
    private List<String> getPrefixStrings() {
        List<String> getPrefix = new ArrayList<>(prefixStrings);
        prefixStrings.clear(); // clear to avoid duplicates in further searches.
        return getPrefix;
    }


    /**
     * This function is used to merge two maps into their intersection summing the scores
     * associated with each overlaping entry.
     * @param map1 First map to be merged
     * @param map2 Second map to be merged
     * @return a merged map with websites only common to both and summed scores
     */
    private static Map<Website, Double> intersectionMap (Map<Website, Double> map1, Map<Website, Double> map2){
        Map<Website, Double> result = new HashMap<>();

        for (Website w: map1.keySet()){ // iterate through first map
            if(map2.containsKey(w)){ // if there is an overlapping result
                result.put(w, map1.get(w) + map2.get(w));// sum scores
            }
        }

        return result;
    }
}