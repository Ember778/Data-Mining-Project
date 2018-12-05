import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

//Special class which contains a hashMap of all the words and their frequencies found
//in a text file along with a boolean which stores whether or not the text file is spam
public class EmailMap {

  // hashmap containing unique words as the key and frequencies of those words as
  // values
  private HashMap<String, Double> wordMap = new HashMap<String, Double>();

  // boolean holding whether or not the file is considered spam or not
  private boolean isSpam;

  public EmailMap(File email, HashSet<String> uniqueWordSet) {

    // determining whether the file is spam from the file name
    if (email.getName().contains("spms")) {
      setIsSpam(true);
    } else {
      setIsSpam(false);
    }

    addWords(email, uniqueWordSet);
  }

  // adds all unique words from the training dictionary and any other words found
  // in the file being read
  private void addWords(File email, HashSet<String> uniqueWordSet) {

    // iterates through the hashset containing all the unique words from the
    // training dictionary
    // and adds them to the hashmap with a zero frequency value
    Iterator<String> iter = uniqueWordSet.iterator();
    while (iter.hasNext()) {
      wordMap.put(iter.next(), 0.0);
    }
    Scanner input = null;
    String line;

    try {
      input = new Scanner(email);

    } catch (FileNotFoundException e) {
    }

    // scans through the file and counts the frequency of each word and stores it in
    // the hashmap
    try {
      while ((line = input.nextLine()) != null) {
        StringTokenizer st = new StringTokenizer(line, " ");
        while (st.hasMoreTokens()) {
          String word = st.nextToken().replaceAll("[^a-zA-Z ]", "").toLowerCase();

          if (wordMap.containsKey(word)) {
            wordMap.put(word, wordMap.get(word) + 1);
          } else {
            wordMap.put(word, 1.0);
          }
        }
      }

    } catch (NoSuchElementException e) {

    } catch (Exception e) {

    }
    wordMap.remove("");
  }

  // returns the hashmap
  public HashMap<String, Double> getWordMap() {
    return this.wordMap;
  }

  // used to set spam variable
  private void setIsSpam(boolean isSpam) {
    this.isSpam = isSpam;
  }

  // returns the spam checking boolean isSpam
  public boolean getIsSpam() {
    return isSpam;
  }

}
