import java.io.File;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;
public class classify {
	
	private static File dataFile; 
	private static int k;
	
	
	public static void main(String[] args) {
		
		//TODO Use for submission
		switch(args[0]) {
		case "NB":
			dataFile = new File(args[1]);
			NaïveBayes naïveBayes = new NaïveBayes(dataFile);
			System.out.println(naïveBayes.classifyEmails());
			break;
		case "knn":
			dataFile = new File(args[1]);
			k = Integer.valueOf(args[2]);
			//TODO have the KNN class have the a constructor similar to what I have for Naive Bayes
			//but have an additional parameter for k. This is need for the final submission of the 
			//project so it could be ran through the command line
			break;
		}
		
	}
	
	public static class NaïveBayes implements ClassifyData {

		// The file that contains all the files used for building the model
		private File trainDirectory;

		// The file that contains the files used to test the model
		private File testDirectory;

		// The section of the table created by the training files that are classified as
		// mail.
		//Contains the word and frequency of it in a class in the table
		private Map<String, Float> mailMap;

		// The section of the table created by the testing files, that are classified as
		// spam
		//Contains the word and frequency of it in a class in the table
		private Map<String, Float> spamMailMap;

		// The constructor that builds the table from the training files
		public NaïveBayes(File dataFile) {
			
			//Assumes that the data files has sub directories for training and testing sets
			for(File f : dataFile.listFiles()) {
				if(f.getName().toLowerCase().contains("train")) {
					this.trainDirectory = f;
				}
				else {
					this.testDirectory = f;
				}
			}
			
			mailMap = new HashMap();
			spamMailMap = new HashMap();
			setUniqueEmailWords();
		}

		// Classifies the emails
		@Override
		public String classifyEmails() {
			String s = "";

			// The probability that an email is spam
			float chanceSpam = 0;

			// The probability that an email is mail
			float chanceMail = 0;

			// The amount of emails that are correctly classified as spam
			float spamAccuracy = 0;

			// The amount of emails that are correctly classified as mail
			float mailAccuracy = 0;

			//The amount of emails that are labeled mail in the test file
			float totalTestMail = 0;
			
			//The amount of emails that are labeled spam in the test file
			float totalTestSpam = 0;
			
			//The amount of emails that are labeled mail in the train file
			float totalTrainMail = 0;
			
			//The amount of emails that are labeled mail in the train file
			float totalTrainSpam = 0;
			
			//Used to display the message 
			String label = "";
			
			//Gets the amount of files that are labeled spam or mail in the test file
			for (File file : testDirectory.listFiles()) {
				label = (file.getName().contains("spm")) ? "Spam" : "Mail";

				if (label.equals("Spam")) {
					totalTestSpam++;
				} else {
					totalTestMail++;
				}
			}
			
			//Get the total amount of spam and mail files in the training set
			for(File file : trainDirectory.listFiles()) {
				if(file.getName().contains("spm")) {
					totalTrainSpam++;
				}
				else{
					totalTrainMail++;
				}
			}

			//Calculate the probability of each email in the test file by iterating on the number of files in the
			//test file
			for (File file : testDirectory.listFiles()) {
				
				//Gets the set of unique words for an email in the test file
				List<String> words = getWordSet(file);
				
				//Used to determine the correctness of the classificatoin of the Naive Bayes
				label = (file.getName().contains("spm")) ? "Spam" : "Mail";
				
				//Goes through the unique words contained in an email contained in the testing set
				for(String word : words) {
					
					//If the word is contained in the hash for the mail classification, it calculates the 
					//probability of the of the word in the class or calculate the m-estimate if the word
					// is not in the class then adds the probability to the overall probability of it being 
					//mail
					if(mailMap.containsKey(word)) {
						chanceMail +=  Math.log10( mailMap.get(word) / totalTrainMail);
					}
					else {
						chanceMail +=  Math.log10( 1 / (totalTrainMail+1));
					}
					
					
					//If the word is contained in the hash for the mail classification, it calculates the 
					//probability of the of the word in the class or calculate the m-estimate if the word
					// is not in the class then adds the probability to the overall probability of it being 
					//spam
					if(spamMailMap.containsKey(word)) {
						chanceSpam +=  Math.log10( spamMailMap.get(word) / totalTrainSpam);
					}
					else {
						chanceSpam +=  Math.log10( 1 / (totalTrainSpam+1));
					}
				}
				
				//Adds the probabilities of the spam and mail in the overall training set to the 
				//respective probabilities of the test being spam or mail
				chanceSpam +=  Math.log10( totalTrainSpam / trainDirectory.listFiles().length);
				chanceMail +=  Math.log10( totalTrainMail / trainDirectory.listFiles().length);
				
				//Compares the probabilities and confirms the accuracy based on the name of the file in the test file
				if (chanceSpam > chanceMail) {
					spamAccuracy = (label.equals("Spam")) ? spamAccuracy + 1 : spamAccuracy;
				} else {
					mailAccuracy = (label.equals("Mail")) ? mailAccuracy + 1 : mailAccuracy;
				}
				chanceSpam = 0;
				chanceMail = 0;
			}
			
			//Used to calculate the accuracies
			int mPercent = (int) ((mailAccuracy / totalTestMail) * 100);
			int sPercent = (int) ((spamAccuracy / totalTestSpam) * 100);
			int tPercent = (int) (((spamAccuracy + mailAccuracy) / (totalTestSpam + totalTestMail)) * 100);

			s = "------Naive Bayes-----\nMail: " + mPercent + "% Accuracy\nSpam:  " + sPercent + "% Accuracy" + "\nOverall: " + tPercent
					+ "% Accuracy";
			return s;
		}

		
		//Finds the unique words in an email in the training file places them in a hashmap for spam
		// or mail based on the name of a file
		private void setUniqueEmailWords() {
			
			//used to scan the file
			Scanner input = null;
			
			//used to get the unique words from an email in the training file
			ArrayList<String> uniqueWords = null;
			for (File f : trainDirectory.listFiles()) {
				String line;
				try {
					input = new Scanner(f);
				} catch (FileNotFoundException e) {
				}
				
				//Goes through and file and get words from it
				try {
					uniqueWords = new ArrayList<String>();
					while ((line = input.nextLine()) != null) {
						StringTokenizer st = new StringTokenizer(line, " ");
						while (st.hasMoreTokens()) {
							String word = st.nextToken().toLowerCase();
							
					
							 if (!uniqueWords.contains(word)) {
								uniqueWords.add(word);
							}
						}
					}

				} catch (NoSuchElementException e) {
					
					//Places words found in the array into a particular hashmap
					//based on the file name
					if (f.getName().contains("spms")) {
						setHashMaps(uniqueWords,"spms");
					} else {
						setHashMaps(uniqueWords,"mail");
					}
				}

			}
		}


		//Place the words found in the list of strings and update the hashmaps
		//with the word and frequency of it based on the file name the list of 
		//strings is tied to.
		private void setHashMaps(List<String> emailWords, String classifer) {
			for (String word : emailWords) {
				if (classifer.equals("spms")) {
					if (!spamMailMap.containsKey(word)) {
						spamMailMap.put(word, (float) 1.0);
					} else {
						spamMailMap.put(word, spamMailMap.get(word) + 1);
					}
				} else {
					if (!mailMap.containsKey(word)) {
						mailMap.put(word, (float) 1.0);
					} else {
						mailMap.put(word, mailMap.get(word) + 1);
					}
				}
			}
		}

		//Gets the set of unique word of a email in the test file
		private ArrayList<String> getWordSet(File f) {
			List<String> words = new ArrayList();
			Scanner input = null;
			String line;
			try {
				input = new Scanner(f);
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}
			try {
				while ((line = input.nextLine()) != null) {
					StringTokenizer st = new StringTokenizer(line, " ");
					while (st.hasMoreTokens()) {
						String word = st.nextToken().toLowerCase();
						if (!words.contains(word)) {
							words.add(word);
						}
					}

				}

			} catch (Exception e) {
			}

			return (ArrayList<String>) words;
		}

	}
	
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

	
	
	public static interface ClassifyData {
		
		//Prints message about accuracy of the classification algorithm
		public String classifyEmails();
	}
	
}



