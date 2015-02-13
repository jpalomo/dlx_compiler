package com.palomo.pl241.test;

import static org.junit.Assert.*;

import org.junit.Test;

import compiler.components.lex.Scanner;
import compiler.components.lex.Token;
import compiler.components.lex.Token.Kind;


public class TestScanner
{
	@Test
	public void testDistinguishKeywordFromIdentifier() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/keywordfromidentifier.txt"); 
		Token token = scanner.nextToken();
		assertEquals(Kind.MAIN, scanner.token.kind);
		assertEquals(Kind.MAIN.getIntValue(), token.getIntValue()); 
		token = scanner.nextToken();
		assertEquals(Kind.IDENTIFIER, scanner.token.kind);
		assertEquals(Kind.IDENTIFIER.getIntValue(), token.getIntValue()); 
		assertEquals(scanner.token.getLexeme(), "main1"); 
	}
	
	@Test
	public void testMainToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_main.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.MAIN, scanner.token.kind);
		assertEquals(Kind.MAIN.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testVarToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_var.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.VAR, scanner.token.kind);
		assertEquals(Kind.VAR.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testArrayToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_array.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.ARRAY, scanner.token.kind);
		assertEquals(Kind.ARRAY.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testFunctionToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_function.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.FUNCTION, scanner.token.kind);
		assertEquals(Kind.FUNCTION.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testProcedureToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_procedure.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.PROCEDURE, scanner.token.kind);
		assertEquals(Kind.PROCEDURE.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testLetToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_let.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.LET, scanner.token.kind);
		assertEquals(Kind.LET.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testThenToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_then.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.THEN, scanner.token.kind);
		assertEquals(Kind.THEN.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testCallToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_call.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.CALL, scanner.token.kind);
		assertEquals(Kind.CALL.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testDoToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_do.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.DO, scanner.token.kind);
		assertEquals(Kind.DO.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testOdToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_od.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.OD, scanner.token.kind);
		assertEquals(Kind.OD.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testIfToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_if.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.IF, scanner.token.kind);
		assertEquals(Kind.IF.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testFiToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_fi.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.FI, scanner.token.kind);
		assertEquals(Kind.FI.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testElseToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_else.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.ELSE, scanner.token.kind);
		assertEquals(Kind.ELSE.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testWhileToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_while.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.WHILE, scanner.token.kind);
		assertEquals(Kind.WHILE.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testReturnToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_return.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.RETURN, scanner.token.kind);
		assertEquals(Kind.RETURN.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testTimesToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_times.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.TIMES, scanner.token.kind);
		assertEquals(Kind.TIMES.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testDivToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_div.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.DIV, scanner.token.kind);
		assertEquals(Kind.DIV.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testPlusToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_plus.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.PLUS, scanner.token.kind);
		assertEquals(Kind.PLUS.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testMinusToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_minus.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.MINUS, scanner.token.kind);
		assertEquals(Kind.MINUS.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testEqualToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_equal.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.EQL, scanner.token.kind);
		assertEquals(Kind.EQL.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testNotEqualToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_nequal.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.NEQ, scanner.token.kind);
		assertEquals(Kind.NEQ.getIntValue(), token.getIntValue()); 
	}

	public void testLessToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_less.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.LESS, scanner.token.kind);
		assertEquals(Kind.LESS.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testLessEqualToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_lesseq.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.LEQ, scanner.token.kind);
		assertEquals(Kind.LEQ.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testGreaterToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_greater.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.GRTR, scanner.token.kind);
		assertEquals(Kind.GRTR.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testGreaterEqualToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_greatereq.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.GEQ, scanner.token.kind);
		assertEquals(Kind.GEQ.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testPeriodToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_period.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.PERIOD, scanner.token.kind);
		assertEquals(Kind.PERIOD.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testCommanToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_comma.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.COMMA, scanner.token.kind);
		assertEquals(Kind.COMMA.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testOpenBrackToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_openbrk.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.OPN_BRACK, scanner.token.kind);
		assertEquals(Kind.OPN_BRACK.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testCloseBrackToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_closebrk.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.CLS_BRACK, scanner.token.kind);
		assertEquals(Kind.CLS_BRACK.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testOpenParenToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_openparen.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.OPN_PAREN, scanner.token.kind);
		assertEquals(Kind.OPN_PAREN.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testCloseParenToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_closeparen.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.CLS_PAREN, scanner.token.kind);
		assertEquals(Kind.CLS_PAREN.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testBecomesToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_becomes.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.BECOMES, scanner.token.kind);
		assertEquals(Kind.BECOMES.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testSemiColToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_semicol.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.SEMI_COL, scanner.token.kind);
		assertEquals(Kind.SEMI_COL.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testBeginToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_begin.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.BEGIN, scanner.token.kind);
		assertEquals(Kind.BEGIN.getIntValue(), token.getIntValue()); 
	}

	@Test
	public void testEndToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_end.txt");
		Token token = scanner.nextToken();
		assertEquals(Kind.END, scanner.token.kind);
		assertEquals(Kind.END.getIntValue(), token.getIntValue()); 
	}

    @Test(expected=IllegalArgumentException.class)
    public void testEmptyFileLocation() {
    	new Scanner("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullFileLocation() {
    	new Scanner(null);
    }
    
    @Test
    public void testErrorToken() {
    	Scanner scanner = new Scanner(String.valueOf(hashCode()));
    	Token token = scanner.nextToken();
		assertEquals(Kind.ERROR, scanner.token.kind);
		assertEquals(Kind.ERROR.getIntValue(), token.getIntValue()); 
    }

    @Test
    public void testEOFToken() {
    	Scanner scanner = new Scanner("src/test/resources/unit_tests/empty.txt");
    	Token token = scanner.nextToken();
		assertEquals(Kind.EOF, scanner.token.kind);
		assertEquals(Kind.EOF.getIntValue(), token.getIntValue()); 
    }

    @Test
    public void testNumberToken() {
    	Scanner scanner = new Scanner("src/test/resources/unit_tests/12345.txt");
    	Token token = scanner.nextToken();
		assertEquals(Kind.NUMBER, scanner.token.kind);
		assertEquals(Kind.NUMBER.getIntValue(), token.getIntValue()); 
		assertEquals("12345", scanner.token.getLexeme()); 
    }

    @Test
    public void testLineNumberCount() {
    	Scanner scanner = new Scanner("src/test/resources/unit_tests/empty_lines.txt");
    	Token token = scanner.nextToken();
    	assertEquals(3, scanner.lineNum);
    }

    @Test
    public void testCharacterCount() {
    	Scanner scanner = new Scanner("src/test/resources/unit_tests/char_count.txt");
    	Token token = scanner.nextToken();
    	assertEquals(4, scanner.charPos);
    }

    @Test
    public void testFunctionCall() {
    	Scanner scanner = new Scanner("src/test/resources/unit_tests/function_call.txt");
    	Token token = scanner.nextToken();
    	assertEquals(Kind.CALL, token.kind);
    	token = scanner.nextToken();
    	assertEquals(Kind.IDENTIFIER, token.kind);
    	assertEquals("OutputNum", token.getLexeme());
    	token = scanner.nextToken();
    	assertEquals(Kind.OPN_PAREN, token.kind);

    	token = scanner.nextToken();
    	assertEquals(Kind.IDENTIFIER, token.kind);
    	assertEquals("y", token.getLexeme());

    	token = scanner.nextToken();
    	assertEquals(Kind.CLS_PAREN, token.kind); 
    }
    
    @Test
    public void testComment() {
    	Scanner scanner = new Scanner("src/test/resources/unit_tests/comment.txt");
    	Token token = scanner.nextToken();
    	assertEquals(Kind.EOF, token.kind);   
    }
    
    @Test
    public void testCommentWithIdent() {
    	Scanner scanner = new Scanner("src/test/resources/unit_tests/comment_with_ident.txt");
    	Token token = scanner.nextToken();
    	assertEquals(Kind.DIV, token.kind);   
    	token = scanner.nextToken();
    	assertEquals(Kind.IDENTIFIER, token.kind);   
    }
}
