package compiler.components.lex;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author John Palomo, 60206611
 *
 */
public class Token 
{ 

	/*
	 * This static map is responsible for holding all the reserved words in the PL241 language.
	 * It is used to determine if the current lexeme we are processing is a reserved word.
	 * If the current lexeme being processed is a reserved word, we can quickly and efficiently determine
	 * the kind of token it is.
	 */
	private static final Map<String, Kind> RESERVED_WORDS = ImmutableMap.<String, Token.Kind>builder()
			.put("main", Kind.MAIN)
			.put("var", Kind.VAR)
			.put("array", Kind.ARRAY)
			.put("function", Kind.FUNCTION)
			.put("procedure", Kind.PROCEDURE)
			.put("let", Kind.LET) 
			.put("then", Kind.THEN)
			.put("call", Kind.CALL)
			.put("do", Kind.DO)
			.put("od", Kind.OD) 
			.put("if", Kind.IF) 
			.put("fi", Kind.FI) 
			.put("else", Kind.ELSE) 
			.put("while", Kind.WHILE) 
			.put("return", Kind.RETURN) 
			.put("*", Kind.TIMES)
			.put("/", Kind.DIV)
			.put("+", Kind.PLUS)
			.put("-", Kind.MINUS)
			.put("==", Kind.EQL)
			.put("!=", Kind.NEQ)
			.put("<", Kind.LESS)
			.put("<=", Kind.LEQ)
			.put(">", Kind.GRTR)
			.put(">=", Kind.GEQ)
			.put(".", Kind.PERIOD)
			.put(",", Kind.COMMA)
			.put("[", Kind.OPN_BRACK)
			.put("]", Kind.CLS_BRACK)
			.put("(", Kind.OPN_PAREN)
			.put(")", Kind.CLS_PAREN)
			.put("<-", Kind.BECOMES)
			.put(";", Kind.SEMI_COL)
			.put("{", Kind.BEGIN)
			.put("}", Kind.END)
			.build(); 

    public static final Token EOF_TOKEN = new Token(Kind.EOF);
    public static final Token ERR_TOKEN = new Token(Kind.ERROR);

	public Kind kind;  //the name of the token representing the lexeme
	private String lexeme;  //the sequence of characters identified by the lexical scanner

	/*
	 * This is the driving private constructor for all the static factory 
	 * token methods.  It will initialized to an error token with the string:
	 * "No lexeme given". 
	 */
	private Token() {
		// if we don't match anything, signal error
		this.kind = Kind.ERROR;
		this.lexeme = "No Lexeme Given";
	}

	private Token(Kind kind){
		this.kind = kind;
	}

	/**
	 * Returns the value representing or held by this token
	 * @return  the token lexeme value represented as an int
	 */
	public int getIntValue() {
		return this.kind.getIntValue();
	}

	/**
	 * 
	 * @return the string of characters the represent the lexeme of the token
	 */
	public String getLexeme() {
		return lexeme;
	}
	
	public void setKind(Kind kind) {
		this.kind = kind;
	}
	
	public static Token INDENTIFIER_OR_KEYWORD(String lexeme) {
		Preconditions.checkNotNull(lexeme, "lexem cannot be null");
		Preconditions.checkArgument(StringUtils.isNotBlank(lexeme), "lexem cannot be blank");

		Token token = new Token();

		//assume the lexeme is an identifier
		token.lexeme = lexeme;
		token.kind = Kind.IDENTIFIER;
		
		Kind kind = findKeywordMatch(lexeme);
		if (kind != null) {
			token.lexeme = lexeme;
			token.kind = kind;
		}
		return token;
	}

	public static Token RELOP_OR_KEYWORD(String lexeme) {
		Preconditions.checkNotNull(lexeme, "lexem cannot be null");
		Preconditions.checkArgument(StringUtils.isNotBlank(lexeme), "lexeme cannot be blank");

		Token token = new Token();

		//assume the lexeme is invalid relop or keyword 
		token.lexeme = lexeme;
		token.kind = Kind.ERROR;
		
		Kind kind = findKeywordMatch(lexeme);
		if (kind != null) {
			token.lexeme = lexeme;
			token.kind = kind;
		}
		return token;
	}

	public static Token NUMBER(String num) {
		Token token = new Token();
		token.kind = Kind.NUMBER;
		token.lexeme = num;
		return token;
	}

	public static Token ERROR(String lexeme) {
		Token token = new Token();
		token.kind = Kind.ERROR;
		token.lexeme = lexeme;
		return token;
	}

	/*
	 * Determine if the lexeme matches a keyword.  If it does, return the
	 * token kind.  For example, if lexeme == "main", 
	 * we will create a token with lexeme="main" and Kind=Kind.MAIN  
	 * 
	 * @param lexeme
	 * @return
	 */
	private static Kind findKeywordMatch(String lexeme){
		/*
		 * Following loop determines if we have a keyword lexeme.  If a keyword
		 * lexeme is found, the kind needs to be updated accordingly.  Otherwise,
		 * we have an identifier which has already been set above and the values
		 * will not get updated.
		 * 
		 */
		Set<String> reservedWords = RESERVED_WORDS.keySet();

		for(String reservedWord: reservedWords) {
			if(reservedWord.equals(lexeme)) {
					return RESERVED_WORDS.get(lexeme);
			}
		}
		return null;
	} 

	/**
	 * 
	 * @author Palomo
	 *
	 */
	public static enum Kind 
	{
		/** Reserved Words **/
		ERROR(null, 0),

		TIMES("*", 1),
		DIV("/", 2),

		PLUS("+", 11),
		MINUS("-", 12),

		EQL("==", 20),
		NEQ("!=", 21),
		LESS("<", 22),
		GEQ(">=", 23),
		LEQ("<=", 24),
		GRTR(">", 25),

		PERIOD(".", 30),
		COMMA(",", 31),
		OPN_BRACK("[", 32),
		CLS_BRACK("]", 34),
		CLS_PAREN(")", 35),

		BECOMES("<-", 40),
		THEN("then", 41),
		DO("do", 42),

		OPN_PAREN("(", 50),

		NUMBER(null, 60),
		IDENTIFIER(null, 61),

		SEMI_COL(":", 70),
		
		
		END("}", 80),
		OD("od", 81),
		FI("fi", 82),

		ELSE("else", 90),

		LET("let", 100),
		CALL("call", 101),
		IF("if", 102),
		WHILE("while", 103),
		RETURN("return", 104),

		VAR("var", 110),
		ARRAY("array", 111),
		FUNCTION("function", 112),
		PROCEDURE("procedure", 113),
				
		BEGIN("{", 150),
		MAIN("main", 200),
		EOF("255", 113);

		private int value;
		private String staticLexeme;
		
		Kind(String lexeme, int value) {
			staticLexeme = lexeme;
        	this.value = value;
		} 

		public int getIntValue() {
			return value;
		}

		public String getStaticLexeme() {
			return staticLexeme;
		}
	} 
}