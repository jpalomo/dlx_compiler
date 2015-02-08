package com.palomo.pl241.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import compiler.components.lex.FileReader;

public class TestFileReader {

	@Test
	public void testReaderInitializesToFirstChar() {
		FileReader fileReader = new FileReader("src/test/resources/unit_tests/abc123.txt");
		char character = fileReader.getSym();
		assertEquals('a', character);
	}

	@Test
	public void testReaderReturnsWhiteSpace() {
		FileReader fileReader = new FileReader("src/test/resources/unit_tests/whitespacewithsinglechar.txt");
		char character = fileReader.getSym();
		assertEquals(' ', character);
	}

	@Test 
	public void testReaderReturnsEOF() { 
		FileReader fileReader = new FileReader("src/test/resources/unit_tests/empty.txt");
		char character = fileReader.getSym();
		for(int i = 0; i < 10; i++) {
			character = fileReader.getSym();
		}
		assertEquals(FileReader.EOF, character);
	}

	@Test
	public void testReaderReturnsERR() {
		FileReader fileReader = new FileReader("src/test/resources/unit_tests/badFILE.txt");
		char character = fileReader.getSym();
		assertEquals(FileReader.ERROR, character); 
	} 
}