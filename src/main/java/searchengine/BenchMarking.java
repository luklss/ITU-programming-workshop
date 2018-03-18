package searchengine;

import java.lang.reflect.Array;
import java.util.*;

/**
 * This class is used to test the performance of the
 *  three index implementations
 */
public class BenchMarking {

    // initialize variables to be used, since they won't chage they can be static.
    static Index SimpleIndex = new SimpleIndex();
    static Index HashMap = new InvertedIndex(new HashMap());
    static Index TreeMap = new InvertedIndex(new TreeMap());
    static List<Website> listWebsites = new ArrayList<Website>();
    static List<String> listWords = new ArrayList<String>();

    public static void main(String[] args) {

        // make sure it recieves a file with the list of words
        if (args.length < 1) {
            System.out.println("please enter the file name");
        }

        // initialize the list of websites
        Scanner input = new Scanner(System.in);
        listWebsites = FileHelper.parseFile(args[0]);

        // initialize the list of test words
        listWords = Arrays.asList("of","major","the","are","is","it","listen","and","most","in","when","most",
                "wordnotgoingtobefound","copenhagen","established");

        // Record Indexes build
        initializeIndex(SimpleIndex, "simple");
        initializeIndex(HashMap, "Inverted Hash");
        initializeIndex(TreeMap, "Inverted Tree");

        // Record Lookup methods
        lookUpMethod(SimpleIndex, "simple");
        lookUpMethod(HashMap, "Inverted Hash");
        lookUpMethod(TreeMap, "Inverted Tree");
    }

    // generic initialization
    public static void initializeIndex(Index index, String type) {
        int numOfIterations = 5;
        int numOfWarmup = 5;

        // warm-up
        for (int i = 0; i < numOfWarmup; i ++){
            index.build(listWebsites);
        }

        // record time
        long startTime =  System.nanoTime();
        for (int i = 0; i < numOfIterations; i ++){
            index.build(listWebsites);
        }
        long endTime = System.nanoTime();
        long finalTime = (endTime - startTime) / numOfIterations;
        System.out.println("Time to initialize " + type + " Index" + ": " + finalTime);
    }

    // generic lookup
    public static void lookUpMethod (Index index, String type) {
        index.build(listWebsites);
        int numOfIterations = 1000;
        int numOfWarmup = 1000;

        for (int i = 0; i < numOfWarmup; i ++){
            for (String word : listWords){
                index.lookup(word);
            }
        }

        // record time
        long startTime =  System.nanoTime();
        for (int i = 0; i < numOfIterations; i ++){
            for (String word : listWords){
                index.lookup(word);
            }
        }
        long endTime = System.nanoTime();
        long finalTime = ((endTime - startTime) / numOfIterations) / listWords.size(); // gives average per word
        System.out.println("Average lookup time per word for " + type + " Index" + ": " + finalTime);

    }
}
       