import java.io.File;

public class KNN implements Classify {
	private File trainDirectory;
	private File testDirectory;

	
	public KNN(String trainDirectoryPath, String testDirectoryPath) {
		this.trainDirectory = new File(trainDirectoryPath);
		this.testDirectory = new File(testDirectoryPath);
	}
	
	@Override
	public String classifyEmails() {
		
		return null;
	}

}
