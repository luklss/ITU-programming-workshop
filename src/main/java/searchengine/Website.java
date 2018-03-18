package searchengine;

import java.util.List;

/**
 * A {@code Website} is the basic entity of the search engine.
 * It contains all the data of a website and offers some methods
 * to check whether a word is contained one a website.
 *
 * @author Martin Aumüller
 * @author Lucas Beck
 */
public class Website {
    private String url;
    private String title;
    private List<String> words;

    /**
     * The constructor of a website object. Constructs a
     * website from an URL, a title, and a list of words
     * contained on the website.
     *
     * @param url the website's url
     * @param title the website's title
     * @param words the list of words that are contained on the website
     */
    public Website(String url, String title, List<String> words) {
        this.url = url;
        this.title = title;
        this.words = words;
    }

    /**
     * Returns the website's URL.
     *
     * @return the website's URL.
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Returns the website's title.
     *
     * @return the website's title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Checks whether a given word is present on the website.
     *
     * @param word the word we are searching for.
     * @return True, if the word is contained on the website.
     */
    public boolean containsWord(String word)
    {
        return this.words.contains(word); // the contains method return true if the string contains the argument
    }

    /**
     * Return the website's list of words
     *
     * @return the website's list of words
     */
    public List<String> getWords() {
        return this.words;
    }

    /**
     * This method is used to compare two objects
     *
     * @param o any object
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Website website = (Website) o;

        if (url != null ? !url.equals(website.url) : website.url != null) return false;
        if (title != null ? !title.equals(website.title) : website.title != null) return false;
        return words != null ? words.equals(website.words) : website.words == null;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (words != null ? words.hashCode() : 0);
        return result;
    }

    /**
     * This method returns a String representation of the Website
     *
     * @return Webstie string representation
     */
    @Override
    public String toString() {
        return "Website{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", words=" + words +
                '}';
    }
}
