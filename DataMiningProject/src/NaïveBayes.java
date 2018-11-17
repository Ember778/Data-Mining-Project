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

public class Na�veBayes implements Classify {
	private File trainDirectory;
	private File testDirectory;
	private Map<String, Double> mailMap;
	private Map<String, Double> spamMailMap;
	private List<List<String>> uniqueEmailWords;

	public Na�veBayes(String trainDirectoryPath, String testDirectoryPath) {
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
		double chanceSpam = 1;
		double chanceMail = 1;
		int mailCount = 0;
		int spamMailCount = 0;
		double spamAccuracy = 0;
		double mailAccuracy = 0;
		double totalMail = mailMap.size();
		double totalSpam = spamMailMap.size();
		String label = "";
		for (File file : testDirectory.listFiles()) {
			Iterator<Entry<String, Double>> itMail = mailMap.entrySet().iterator();
			Iterator<Entry<String, Double>> itSpam = spamMailMap.entrySet().iterator();
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
					chanceMail += Math.log10((Double) pair.getValue() / totalMail);
				} else {
					chanceMail += Math.log10((totalMail - (Double) pair.getValue()) / totalMail);
				}
			}
			
			while (itSpam.hasNext()) {
				Map.Entry pair = (Map.Entry) itSpam.next();
				if (words.contains(pair.getKey())) {
					chanceSpam += Math.log10((Double) pair.getValue() / totalSpam);
				} else {
					chanceSpam += Math.log10((totalSpam - (Double) pair.getValue()) / totalSpam);
				}
			}

			if (chanceSpam >= chanceMail) {
				spamAccuracy = (label.equals("Spam")) ? spamAccuracy + 1 : spamAccuracy;
			} else {
				mailAccuracy = (label.equals("Mail")) ? mailAccuracy + 1 : mailAccuracy;
			}
			chanceSpam = 1;
			chanceMail = 1;
		}
		int mPercent = (int) ((mailAccuracy/mailCount) *100);
		int sPercent = (int) ((spamAccuracy/spamMailCount) * 100);
		 
		s = "Mail: " + mPercent + "% Accuracy\nSpam:  " + sPercent +"% Accuracy";
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
							if(!uniqueWords.contains(word)) {
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

	private void setHashMaps() {
		for(List<String> emailWords : uniqueEmailWords) {
			if (emailWords.get(emailWords.size()-1).equals("spms")) {
				for(String word : emailWords) {
					if (!spamMailMap.containsKey(word)) {
						spamMailMap.put(word, 1.0);
					} else {
						spamMailMap.put(word, spamMailMap.get(word) + 1);
					}
				}
				
			} else {
				for(String word : emailWords) {
					if (!mailMap.containsKey(word)) {
						mailMap.put(word, 1.0);
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
