package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.stream.Stream;

import expr.Expression;
import fsm.CharMachine;
import fsm.Machine;
import lang.Language;
import lang.Type;

class TestMachine {

	@org.junit.jupiter.api.Test
	void testIsolated() {
		
		
		
		final CharMachine a = new CharMachine('a');
		checkConversion(a.complement());
	}

	@org.junit.jupiter.api.Test
	void testEqualence() {

		//final Type<?, Number> M = new ComplementSet<>();
		//final Type<?, Integer> N = new ComplementSet<>();

		//final var O = M.unite_Type(N);

		System.out.println("testEqualence()");


		final CharMachine a = new CharMachine('a');
		final CharMachine b = new CharMachine('b');
		final CharMachine c = new CharMachine('c');

		final CharMachine abc = new CharMachine("abc");
		assertTrue(CharMachine.toString(abc.iterate().random(new Random())).matches("(abc)*"));
		
		final var a_b = a.unite(b);
		final var ab = a.concat(b);
		final var ba = b.concat(a);
		final var e = new CharMachine(true);

		//Machine<Character, InfiniteSet<Character>, Void> a_plus = a.iterate();
		//Machine<Character, InfiniteSet<Character>, Void> a_plus_2 = a.iterate().iterate();



		assertTrue(a.iterate().containsAll(a));
		assertFalse(a.containsAll(a.iterate()));

		//assertTrue(a.iterate().iterate().isEqual(a.iterate()));


		assertTrue(a.unite(b).containsAll(a));
		assertTrue(a.unite(b).containsAll(b));

		assertFalse(a.containsAll(a.unite(b)));
		assertFalse(b.containsAll(a.unite(b)));

		assertTrue(a_b.concat(a_b).containsAll(ab.unite(ba)));
		assertFalse(ab.unite(ba).containsAll(a_b.concat(a_b)));

		final var A = a.complement();

		checkConversion(a);
		
		System.out.println("!a := "+A);
		checkConversion(A);

		assertTrue(a.complement().containsAll(b));
		assertFalse(a.complement().containsAll(a));

		checkConversion(a.complement().complement());

		assertTrue(a.complement().complement().isEqual(a));


		assertFalse(ab.containsAll(a));
		assertFalse(a.containsAll(a.optional()));

		checkConversion(a.unite(b).complement());
		checkConversion(a.unite(b).complement().complement());
		checkConversion(a.unite(b).intersect(a.unite(c)));
		
		checkConversion(a.unite(b).minus(b));
		checkConversion(a.minus(a));
		checkConversion(a.iterate());
		checkConversion(a.iterate().iterate());
		

		// (a++)++ == a++
		assertTrue(a.iterate().isEqual(a.iterate().iterate()));

		// !(a||b) == (!a)&&(!b)
		assertTrue(a.unite(b).complement().isEqual(a.complement().intersect(b.complement())));

		checkConversion(a.intersect(a.complement()));
		checkConversion(a.complement().unite(a));
		checkConversion(a.complement().unite(a).complement());
		
		assertTrue(a.complement().unite(a).complement().isEqual(a.intersect(a.complement())));

		// a && !a == 0
		assertTrue(a.complement().unite(a).complement().isEmpty());
		assertTrue(a.intersect(a.complement()).isEmpty());
	
		checkConversion(a.unite(b).minus(b));
		checkConversion(a.unite(b).minus(a));
		

		assertTrue(a.unite(b).minus(b).isEqual(a));
		assertTrue(a.unite(b).minus(a).isEqual(b));
		assertTrue(a.minus(a).isEmpty());

		assertTrue(a.isEqual(a.parallel(e)));
		assertTrue(a.parallel(e).isEqual(a));
		assertTrue(e.parallel(e).isEqual(e));


	}
	
	
	<T, R, TYPE extends Type<TYPE, T>> void  checkConversion(Machine<T, R, TYPE> m) {
		System.out.println(m);
		var ex = m.convert(Expression.<T>FACTORY());
		var M = ex.convert(m.factory());
		assertTrue(M.isEqual(m), M+" <"+ex+"> "+m);
	}

	@org.junit.jupiter.api.Test
	void testRevrerse() {

		final CharMachine empty = new CharMachine();
		final CharMachine epsilon = new CharMachine(true);

		final CharMachine a = new CharMachine('a');
		final CharMachine b = new CharMachine('b');

		final var ab = a.concat(b);
		final var ba = b.concat(a);
		final var bab = ba.concat(b);

		final var abR = ab.reverse();


		assertTrue(empty.isEmpty());
		assertFalse(empty.isEpsilon());
		assertFalse(epsilon.isEmpty());
		assertTrue(epsilon.isEpsilon());
		assertFalse(a.isEmpty());
		assertFalse(a.isEpsilon());


		assertTrue(abR.isEqual(ba));

		assertTrue(abR.reverse().isEqual(ab));

		assertTrue(a.reverse().isEqual(a));

		assertTrue(a.reverse().isEqual(a));

		assertTrue(empty.reverse().isEqual(empty));
		assertTrue(epsilon.reverse().isEqual(epsilon));

		System.out.println("ab^R := "+abR);

		assertTrue(a.isFinite());
		assertTrue(b.isFinite());
		assertTrue(ab.isFinite());
		assertTrue(abR.isFinite());
		assertTrue(empty.isFinite());
		assertTrue(epsilon.isFinite());

		assertTrue(a.optional().isFinite());
		assertFalse(a.iterate().isFinite());
		assertFalse(a.complement().isFinite());

		assertFalse(empty.isEpsilon());

		assertTrue(a.power(-3).isEqual(a.power(3)));
		assertTrue(a.power(0).isEpsilon());


		assertTrue(a.startsWith(a));
		assertTrue(ab.startsWith(a));
		assertTrue(a.power(5).startsWith(a));
		assertFalse(ba.startsWith(a));
		assertFalse(bab.startsWith(a));
		assertFalse(b.startsWith(a));

		assertTrue(a.endsWith(a));
		assertFalse(ab.endsWith(a));
		assertTrue(a.power(5).endsWith(a));
		assertTrue(ba.endsWith(a));
		assertFalse(bab.endsWith(a));
		assertFalse(b.endsWith(a));

		assertTrue(a.isEnclosed(a));
		assertTrue(ab.isEnclosed(a));
		assertTrue(a.power(5).isEnclosed(a));
		assertTrue(ba.isEnclosed(a));
		assertTrue(bab.isEnclosed(a));
		assertFalse(b.isEnclosed(a));


	}

	@org.junit.jupiter.api.Test
	void test() {

		System.out.println("test()");

		final var e = new CharMachine(true);

		final CharMachine a = new CharMachine('a');
		final CharMachine b = new CharMachine('b');

		System.out.println("a+b := "+a.unite(b));

		assertTrue(a.unite(e).isEqual(e.unite(a)));

		final var ab = a.concat(b);
		final var a_b = a.unite(b);

		final var a_ = a.optional();
		final var aa = a.iterate();

		testAll(a, Stream.of("a"), true);
		testAll(a, Stream.of("", "b", "ab", "ba", "aa", "bb"), false);

		testAll(e, Stream.of(""), true);
		testAll(e, Stream.of("a", "b", "ab", "ba", "aa", "bb"), false);

		testAll(e.concat(e), Stream.of(""), true);
		testAll(e.concat(e), Stream.of("a", "b", "ab", "ba", "aa", "bb"), false);

		testAll(a.concat(e), Stream.of("a"), true);
		testAll(a.concat(e), Stream.of("", "b", "ab", "ba", "aa", "bb"), false);

		testAll(e.concat(a), Stream.of("a"), true);
		testAll(e.concat(a), Stream.of("", "b", "ab", "ba", "aa", "bb"), false);


		testAll(b, Stream.of("b"), true);
		testAll(b, Stream.of("", "a", "ab", "ba", "aa", "bb"), false);

		testAll(ab, Stream.of("ab"), true);
		testAll(ab, Stream.of("", "a", "b", "ba", "aa", "bb"), false);

		testAll(a_b, Stream.of("a", "b"), true);
		testAll(a_b, Stream.of("", "ab", "ba", "aa", "bb"), false);

		testAll(a_b.xor(a), Stream.of("b"), true);
		testAll(a_b.xor(a), Stream.of("", "a", "ab", "ba", "aa", "bb"), false);


		testAll(a_, Stream.of("", "a"), true);
		testAll(a_, Stream.of("b", "ab", "ba", "aa", "bb"), false);

		System.out.println("a?ab"+a_.concat(ab));

		testAll(a_.concat(ab), Stream.of("ab", "aab"), true);
		testAll(a_.concat(ab), Stream.of("", "a", "b", "ba", "aa", "bb"), false);


		testAll(aa, Stream.of("a", "aa", "aaa"), true);
		testAll(aa, Stream.of("", "b", "ab", "ba", "bb"), false);

		testAll(a.star(), Stream.of("", "a", "aa", "aaa"), true);
		testAll(a.star(), Stream.of("b", "ab", "ba", "bb"), false);


		System.out.println("a||b"+a.parallel(b));

		testAll(a.parallel(b), Stream.of("ab", "ba"), true);
		testAll(a.parallel(b), Stream.of("", "a", "b", "aa", "bb"), false);

		System.out.println("ab||ab"+ab.parallel(ab));
		testAll(ab.parallel(ab), Stream.of("abab", "aabb"), true);
		testAll(ab.parallel(ab), Stream.of("", "a", "b", "aa", "bb"), false);

		var a_na = a.concat(a.factory().letter(a.factory().alphabet().empty().complement()));
		System.out.println("a.![] = "+a_na);
		testAll(a_na, Stream.of("aa", "ab"), true);
		testAll(a_na, Stream.of("", "a", "b", "bb", "abc"), false);
		
		
		
		assertTrue(a.initial().toString().startsWith("<I"));
		a.finals().forEach(s-> assertTrue(s.toString().endsWith("F>")));

	}

	void testAll(final Language.Naive<?, Character, ?> fsm, final Stream<String> s, final boolean exp) {
		//System.out.println(fsm);
		s.forEach(x -> {
			assertEquals(exp, fsm.contains(x.chars().<Character>mapToObj(c->(char)c)), fsm+" does "+(exp?"not ":"")+"contain "+x);
		});
	}

}
