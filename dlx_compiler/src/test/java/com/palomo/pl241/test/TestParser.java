package com.palomo.pl241.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import compiler.components.lex.Token;
import compiler.components.parser.Parser;
import compiler.components.parser.ParsingException;

public class TestParser {

	@Test
	public void test001() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test001.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test002() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test002.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test003() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test003.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}
	
	@Test
	public void test004() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test004.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}
	
	@Test
	public void test005() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test005.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}
	
	@Test
	public void test006() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test006.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test007() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test007.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test008() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test008.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test009() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test009.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test010() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test010.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test011() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test011.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test012() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test012.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test014() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test014.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test015() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test015.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test016() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test016.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test017() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test017.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test018() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test018.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test019() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test019.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test020() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test020.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test021() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test021.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test022() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test022.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test023() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test023.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}
	
	@Test
	public void test024() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test024.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}
	
	@Test
	public void test025() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test025.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}
	
	@Test
	public void test026() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test026.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}
	
	@Test
	public void test027() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test027.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}
	
	@Test
	public void test028() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test028.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}
	
	@Test
	public void test029() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test029.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}
	
	@Test
	public void test030() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test030.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}
	
	@Test
	public void test031() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test031.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}
}
