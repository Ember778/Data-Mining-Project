import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

//builds a hash Set containing every unique word in a text file
public class UniqueWordSet {

  // the hash set used to store all the unique words in the dictionary
  private HashSet<String> uniqueWordSet = new HashSet<String>();

  public UniqueWordSet(File trainDirectory) {
    addUniqueWords(trainDirectory);
  }

  // method which adds unique words to hash set
  private void addUniqueWords(File wordFile) {

    // scanner for scanning the text document to tokenize words
    Scanner input = null;

    // goes through all the files in the folder
    for (File f : wordFile.listFiles()) {
      String line;
      try {
        input = new Scanner(f);
      } catch (FileNotFoundException e) {
      }
      try {
        while ((line = input.nextLine()) != null) {
          StringTokenizer st = new StringTokenizer(line, " ");
          while (st.hasMoreTokens()) {

            // makes every word lower case and removes all nonalpha characters
            String word = st.nextToken().replaceAll("[^a-zA-Z ]", "").toLowerCase();

            // if a word doesn't exist in the hashset it is added to the hashset
            if (!uniqueWordSet.contains(word)) {
              uniqueWordSet.add(word);
            }

          }
        }

      } catch (NoSuchElementException e) {

      } catch (Exception e) {

      }

    }
  }

  public HashSet<String> getUniqueWordSet() {
    return uniqueWordSet;
  }
}
