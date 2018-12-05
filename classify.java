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
		switch(args[0].toLowerCase()) {
		case "nb":
			dataFile = new File(args[1]);
			NaïveBayes naïveBayes = new NaïveBayes(dataFile);
			System.out.println(naïveBayes.classifyEmails());
			break;
		case "knn":
			dataFile = new File(args[1]);
			k = Integer.valueOf(args[2]);
			System.out.println(new KNN(dataFile,k).classifyEmails());
			break;
		}
		
	}
	
}



