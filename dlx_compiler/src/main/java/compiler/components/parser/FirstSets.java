package compiler.components.parser;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import compiler.components.lex.Token.Kind;

public class FirstSets {

	public static final Set<String> STATEMENT = ImmutableSet.<String>builder()
			.add(Kind.LET.getStaticLexeme())
			.add(Kind.CALL.getStaticLexeme())
			.add(Kind.IF.getStaticLexeme())
			.add(Kind.WHILE.getStaticLexeme())
			.add(Kind.RETURN.getStaticLexeme())
			.build();
}
