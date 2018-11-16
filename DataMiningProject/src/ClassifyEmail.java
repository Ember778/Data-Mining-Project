public class ClassifyEmail {
	
	private static final String TRAINING_EMAILS_DIRECTORY = ClassifyEmail.class.getResource("train").getPath();
	private static final String TESTING_EMAILS_DIRECTORY = ClassifyEmail.class.getResource("test").getPath();
	
	public static void main(String[] args) {
		
		//TODO Use for submission
//		System.out.println("Enter 1 for Na�veBayes, Enter 2 for KNN");
//		switch(args[0]) {
//		case "1":
//			Na�veBayes na�veBayes = new Na�veBayes(TRAINING_EMAILS_DIRECTORY, TESTING_EMAILS_DIRECTORY);
//			System.out.println(na�veBayes.classifyEmails());
//			break;
//		case "2":
//			
//			break;
//		}
		
		Na�veBayes na�veBayes = new Na�veBayes(TRAINING_EMAILS_DIRECTORY, TESTING_EMAILS_DIRECTORY);
		System.out.println(na�veBayes.classifyEmails());
	
	}
}
