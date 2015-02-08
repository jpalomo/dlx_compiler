package compiler.components.lex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author John Palomo, 60206611
 *
 */
public class FileReader
{
	static Logger LOGGER = LoggerFactory.getLogger(FileReader.class);

	public static final char ERROR = (char) 0;
	public static final char EOF = (char) 255;

	private Reader streamReader;
	private PushbackReader pushBackReader;
	private boolean validState = true;  //when error or end-of-file encountered, it is set to false
	private char sym; 
	private int resetPoint;

	public FileReader(String fileName) { 
		try {
			InputStream inputStream = new FileInputStream(new File(fileName));  //file becomes an input stream
			streamReader = new BufferedReader(new InputStreamReader(inputStream));  //read the file as a buffered input stream for efficiency
			pushBackReader = new PushbackReader(streamReader);
        } catch (FileNotFoundException e) {
			error("File was not found.\n" + e.toString());
		}
	} 

	/**
	 * This method returns the next input symbol to be processed in the source file.
	 * @return
	 */
	public char getSym() {
		if(validState) {
			try {
				//sym = (char) streamReader.read(); //advance the symbol
				sym = (char) pushBackReader.read(); //advance the symbol
				checkEOF();
			} catch (IOException e) {
				error(e.toString());
			}
		} 
		return sym;
	} 

	private void checkEOF() {
		if(sym == (char) -1) {  //EOF
			sym = EOF;
			validState = false;
		}
	}

	public void error(String errorMsg) {
		System.err.println(errorMsg);
		LOGGER.error(errorMsg);
		sym = ERROR;
        validState = false;
	} 

	public void pushBackChar(char c) {
		try {
			pushBackReader.unread(c);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}