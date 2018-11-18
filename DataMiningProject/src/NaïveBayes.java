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

public class NaïveBayes implements Classify {
	private File trainDirectory;
	private File testDirectory;
	private Map<String, Float> mailMap;
	private Map<String, Float> spamMailMap;
	private List<List<String>> uniqueEmailWords;

	public NaïveBayes(String trainDirectoryPath, String testDirectoryPath) {
		this.trainDirectory = new File(trainDirectoryPath);
		this.testDirectory = new File(testDirectoryPath);
		mailMap = new HashMap();
		spamMailMap = new HashMap();
		uniqueEmailWords = new ArrayList();
		setUniqueEmailWords();
		setHashMaps();
	}

	@Override
	public String classifyEmails() {
		String s = "";
		int chanceSpam = 0;
		int chanceMail = 0;
		int mailCount = 0;
		int spamMailCount = 0;
		float spamAccuracy = 0;
		float mailAccuracy = 0;
		float totalMail = trainDirectory.listFiles().length/2;
		float totalSpam = trainDirectory.listFiles().length/2;
		String label = "";
		for (File file : testDirectory.listFiles()) {
			Iterator<Entry<String, Float>> itMail = mailMap.entrySet().iterator();
			Iterator<Entry<String, Float>> itSpam = spamMailMap.entrySet().iterator();
			List<String> words = getWordSet(file);
			label = (file.getName().contains("spm")) ? "Spam" : "Mail";
			
			if(label.equals("Spam")) {
				spamMailCount++;
			}
			else {
				mailCount++;	
			}
			
			while (itMail.hasNext()) {
				Map.Entry pair = (Map.Entry) itMail.next();
				if (words.contains(pair.getKey())) {
					chanceMail += 10000*Math.log10((float) pair.getValue() / totalMail);
				} else {
					chanceMail += 10000*Math.log10((totalMail - (float) pair.getValue()) / totalMail);
				}
			}
			
			while (itSpam.hasNext()) {
				Map.Entry pair = (Map.Entry) itSpam.next();
				if (words.contains(pair.getKey())) {
					chanceSpam += 10000*Math.log10((float) pair.getValue() / totalSpam);
				} else {
					chanceSpam += 10000*Math.log10((totalSpam - (float) pair.getValue()) / totalSpam);
				}
			}

			if (chanceSpam > chanceMail) {
				spamAccuracy = (label.equals("Spam")) ? spamAccuracy + 1 : spamAccuracy;
			} else {
				mailAccuracy = (label.equals("Mail")) ? mailAccuracy + 1 : mailAccuracy;
			}
			chanceSpam = 0;
			chanceMail = 0;
		}
		int mPercent = (int) ((mailAccuracy/mailCount) *100);
		int sPercent = (int) ((spamAccuracy/spamMailCount) * 100);
		int tPercent = (int) (((spamAccuracy + mailAccuracy) / (spamMailCount + mailCount)) * 100);
		
		 
		s = "Mail: " + mPercent + "% Accuracy\nSpam:  " + sPercent +"% Accuracy"+ "\nTotal: " + tPercent + "% Accuracy" ;
		return s;
	}

	private void setUniqueEmailWords() {
		Scanner input = null;

		for (File f : trainDirectory.listFiles()) {
			String line;
			try {
				input = new Scanner(f);
			} catch (FileNotFoundException e) {
			}
			try {
				ArrayList<String> uniqueWords = new ArrayList<String>();
				while ((line = input.nextLine()) != null) {
					StringTokenizer st = new StringTokenizer(line, " ");
					while (st.hasMoreTokens()) {
						String word = st.nextToken().toLowerCase();
							if(!uniqueWords.contains(word) && word.length() < 5 && hasVowels(word)) {
								uniqueWords.add(word);	
							}
					}
					if (f.getName().contains("spms")) {
						uniqueWords.add("spms");
					} else {
						uniqueWords.add("mail");
					}
					uniqueEmailWords.add(uniqueWords);
				}
				
			} catch (NoSuchElementException e) {

			}

		}
	}
	
	private boolean hasVowels(String word) {
		return word.contains("a") || word.contains("e") || word.contains("i") ||
				word.contains("o") || word.contains("u");
	}

	private void setHashMaps() {
		for(List<String> emailWords : uniqueEmailWords) {
			if (emailWords.get(emailWords.size()-1).equals("spms")) {
				for(String word : emailWords) {
					if (!spamMailMap.containsKey(word)) {
						spamMailMap.put(word, (float) 1.0);
					} else {
						spamMailMap.put(word, spamMailMap.get(word) + 1);
					}
				}
				
			} else {
				for(String word : emailWords) {
					if (!mailMap.containsKey(word)) {
						mailMap.put(word, (float) 1.0);
					} else {
						mailMap.put(word, mailMap.get(word) + 1);
					}
				}
			}
		}
	}

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
