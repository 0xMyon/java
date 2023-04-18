package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import expr.Expression;
import expr.Expression.Factory;
import fsm.CharMachine;
import fsm.Machine;
import lang.Language;

class TestExpression {

	
	@Test
	void test() {
		
		testLang(new Machine.Factory<>(CharMachine.FACTORY));
		testLang(new Expression.Factory<>());
		
	}
		

	private static final String[] ALL = {"", "a", "b", "aa", "ab", "ba", "bb"};

	<THIS extends Language<THIS, Character>> void testLang(Language.Factory<THIS, Character> factory) {
		
		System.out.println("testLang("+factory.getClass().getName()+")");

		final THIS e = factory.epsilon();
		
		assertTrue(e.isEpsilon());
		assertTrue(e.hasEpsilon());
		
		assertTrue(e.isEqual(factory.epsilon()));
		
		
		final THIS a = factory.factor('a');
		final THIS b = factory.factor('b');
		
		
		assertTrue(a.unite(e).isEqual(e.unite(a)));
				
		final THIS ab = a.concat(b);
		final THIS a_b = a.unite(b);
		
		final THIS a_ = a.optional();
		final THIS aa = a.iterate();
		
		testContains(a, "a");
		
		testContains(e, "");
		
		testContains(e.concat(e),"");

		testContains(a.concat(e), "a");

		testContains(e.concat(a), "a");
		
		testContains(b, "b");

		testContains(ab, "ab");

		testContains(a_b, "a", "b");
	
		testContains(a_, "", "a");

		testContains(a_.concat(ab), "ab", "aab");
		
		testContains(aa, "a", "aa", "aaa");

		testContains(a.star(), "", "a", "aa", "aaa");
		
	}
	
	void testContains(Language<?, Character> lang, String...in) {
		testAll(lang, Stream.of(in), true);
		testAll(lang, Stream.of(ALL).filter(c -> !Arrays.asList(in).contains(c)), false);
		
	}
	
	void testAll(Language<?, Character> fsm, Stream<String> s, boolean exp) {
		s.forEach(x -> {
			assertEquals(exp, fsm.contains(x.chars().<Character>mapToObj(c->(char)c)), fsm+" does "+(exp?"not ":"")+"contain '"+x+"'");
		});
	}

}
