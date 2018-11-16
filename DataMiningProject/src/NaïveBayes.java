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
	private Map<String, Double> mailMap;
	private Map<String, Double> spamMailMap;
	private List<List<String>> uniqueEmailWords;

	public NaïveBayes(String trainDirectoryPath, String testDirectoryPath) {
		this.trainDirectory = new File(trainDirectoryPath);
		this.testDirectory = new File(testDirectoryPath);
		mailMap = new HashMap();
		spamMailMap = new HashMap();
		uniqueEmailWords = new ArrayList();
	}

	@Override
	public String classifyEmails() {
		setUniqueEmailWords();
		setHashMaps();
		String s = "";
		double chanceSpam = 1;
		double chanceMail = 1;
		int mailCount = 0;
		int spamMailCount = 0;
		double totalMail = mailMap.size();
		double totalSpam = spamMailMap.size();
		for (File file : testDirectory.listFiles()) {
			Iterator<Entry<String, Double>> itMail = mailMap.entrySet().iterator();
			Iterator<Entry<String, Double>> itSpam = spamMailMap.entrySet().iterator();
			List<String> words = getWordSet(file);
			while (itMail.hasNext()) {
				Map.Entry pair = (Map.Entry) itMail.next();
				if (words.contains(pair.getKey())) {
					chanceMail *= (Double) pair.getValue() / totalMail;
				} else {
					chanceMail *= (totalMail - (Double) pair.getValue()) / totalMail;
				}
			}
			while (itSpam.hasNext()) {
				Map.Entry pair = (Map.Entry) itSpam.next();
				if (words.contains(pair.getKey())) {
					chanceSpam *= (Double) pair.getValue() / totalSpam;
				} else {
					chanceSpam *= (totalSpam - (Double) pair.getValue()) / totalSpam;
				}
			}

			if (chanceSpam > chanceMail) {
				spamMailCount++;
			} else {
				mailCount++;
			}
			chanceSpam = 1;
			chanceMail = 1;
		}
		
		

		s = "Mail :" + mailCount + " Spam :" + spamMailCount;
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
