import java.io.File;
import java.time.Clock;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class KNN implements ClassifyData {
  // training directory folder
  private File trainDirectory;

  // testing directory folder
  private File testDirectory;

  // hashset containing all the unique words in the training emails
  private HashSet<String> uniqueWordSet;
  
  //number of nearest neighbors
  int k;

  // creates KNN class and sets all variables
  public KNN(File dataFile, int k) {
    
    for(File f : dataFile.listFiles()) {
      if(f.getName().toLowerCase().contains("train")) {
        this.trainDirectory = f;
      }
      else {
        this.testDirectory = f;
      }
    }
UniqueWordSet uws = new UniqueWordSet(trainDirectory);
    this.uniqueWordSet = uws.getUniqueWordSet();
    this.k = k;
  }

  public String classifyEmails() {

    // variable to hold how many files there are in the training folder
    int trainingFileLength = 0;

    // variable to iterate through every training email file
    int trainingFileCounter = 0;

    // boolean to check whether current email is spam for accuracy checking
    boolean isSpam;

    // variable to hold the value of the actual count of spam emails
    double realSpamCounter = 0;

    // variable to hold the value of the actual count of non-spam emails
    double realMailCounter = 0;

    // variable to hold the value of algorithm determined count of spam emails
    double accurateSpam = 0;

    // variable to hold the value of algorithm determined count of spam emails
    double accurateMail = 0;

    // variable to hold the length vector for the training
    // email vector for cosine similarity calcualtions
    double trainingLength = 0;

    // variable to hold the length vector for the testing
    // email vector for cosine similarity calcualtions
    double testingLength = 0;

    // counts number of files in the training directory
    for (File file : trainDirectory.listFiles()) {
      if (file.isFile()) {
        trainingFileLength++;
      }
    }

    // creates an array empty of training email EmailMap classes for comparing
    // testing email
    // to each training email and determine the cosine similarity for them
    EmailMap[] trainingMapArray = new EmailMap[trainingFileLength];

    // fills the array with training email data
    for (File file : trainDirectory.listFiles()) {
      trainingMapArray[trainingFileCounter] = new EmailMap(file, uniqueWordSet);
      trainingFileCounter++;

    }

    // tests each testing email against every training email in the trainingMapArray
    for (File file : testDirectory.listFiles()) {
      // long time = System.currentTimeMillis();

      // counts how many spam/real emails there are in the testing folder
      if (file.getName().contains("spms")) {
        isSpam = true;
        realSpamCounter++;
      } else {
        isSpam = false;
        realMailCounter++;
      }

      // array to hold cosine similarity values which will be sorted to determine the
      // nearest neighbor
      double[] sortedCosSim = new double[trainingFileLength];

      // initializes cosine similarity to 0 at the beginning of each iteration through
      // each testing email
      double cosSim = 0;

      // creates a hashmap which will store the cosine similarity to the key and the
      // index of the training email hashmap array as the value so when the cosine
      // similarity array is sorted the index of the training EmailMap in the array
      // isn't lost
      HashMap<Double, Integer> arrayElementMap = new HashMap<Double, Integer>();

      // creates an EmailMap class of the current testing email
      EmailMap testMap = new EmailMap(file, uniqueWordSet);

      // goes through every training email and determines cosine similarity between
      // the testing email and the current training email iteration
      for (int i = 0; i < trainingFileLength; i++) {

        // iterates through each word in the testing email map and does the cosine
        // similarity calculation
        Iterator testingMapIter = testMap.getWordMap().entrySet().iterator();
        while (testingMapIter.hasNext()) {
          Map.Entry pair = (Map.Entry) testingMapIter.next();

          // if the training map contains the current word iteration then dot product of
          // word frequencies, the length vector for the training email, and the length
          // vector for the testing email are updated all of these are used to calculate
          // cosine similarity with a relationship of 
          //cosSim = (testing and training word dot product)/(testing word length vector * training word length vector)
          if (trainingMapArray[i].getWordMap().containsKey(pair.getKey())) {

            cosSim = cosSim
                + (trainingMapArray[i].getWordMap().get(pair.getKey())) * (double) pair.getValue();

            testingLength = trainingLength + (double) pair.getValue() * (double) pair.getValue();
            trainingLength = testingLength + trainingMapArray[i].getWordMap().get(pair.getKey())
                * trainingMapArray[i].getWordMap().get(pair.getKey());

            // if training map doesn't contain the current word then only testing length
            // vector needs to be updated due to multiplications of 0
          } else {

            testingLength = trainingLength + (double) pair.getValue() * (double) pair.getValue();
          }
        }

        //vector lengths are the square root of the summation of the square of each frequency
        testingLength = Math.pow(testingLength, .5);
        trainingLength = Math.pow(trainingLength, .5);

        //finishing cosine similarity calculation
        cosSim = cosSim / (testingLength * trainingLength);
        
        //stores the cosine similarity value in the key of the hashmap and the index
        //of the training emailMap
        arrayElementMap.put(cosSim, i);
        
        //stores the value of the current training email into the array to be sorted
        sortedCosSim[i] = cosSim;
        
        //resets cosine similarity variables for next training email in loop
        cosSim = 0;
        trainingLength = 0;
        testingLength = 0;
      }

      //sorts the cosine similarity array. The largest cosine similarity training emails are the
      //testing emails nearest neighbors
      Arrays.sort(sortedCosSim);

      int spamCount = 0;
      int mailCount = 0;

      //counts n nearest neighbors spam variable to determine whether the testing email should be
      //considered email or spam
      for (int i = trainingFileCounter - 1; i > trainingFileCounter - k; i--) {

        if (trainingMapArray[arrayElementMap.get(sortedCosSim[i])].getIsSpam() == true) {

          spamCount++;

        } else {
          mailCount++;

        }
      }

      
      //if there are more non-spam nearest neighbors the email is considered non-spam by the algorithm
      if (mailCount > spamCount) {
        //if the current testing email is actually non-spam and the algorithm determined
        //that it also wasn't spam then the algorithm correctly determined that the email wasn't spam
        if (isSpam == false) {
          accurateMail++;
        }

        //if there are more spam nearest neighbors the email is considered spam by the algorithm
      } else if (mailCount < spamCount) {
        
        //if the current testing email is actually spam and the algorithm detereined
        //that it also was spam then the algorithm correctly determined that the email was spam
        if (isSpam == true) {
          accurateSpam++;
        }

        //if n is an even number and there are equal number of nearest neighbors whether the testing
        //email is considered spam is determined by the nearest neighbor to the testing email
      } else if (mailCount == spamCount) {
        if (trainingMapArray[arrayElementMap.get(sortedCosSim[trainingFileCounter - 1])]
            .getIsSpam() == true) {
          if (isSpam == true) {
            accurateSpam++;
          }
        } else {
          if (isSpam == false) {
            accurateMail++;
          }
        }
      }

      // System.out.println((System.currentTimeMillis() - time) / 1000.0);

    }

    //calculates the accuracy percentage of the algorithm
    double tPercent = ((accurateMail + accurateSpam) / (realMailCounter + realSpamCounter)) * 100;

    return "Mail Accuracy: " + tPercent + "% ";
  }

}
