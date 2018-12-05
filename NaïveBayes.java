import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class NaïveBayes implements ClassifyData {

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
