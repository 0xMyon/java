package fsm;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import lang.InfiniteSet;
import lang.Language;

class Test {
	
	
	@org.junit.jupiter.api.Test
	void testEqualence() {
		
		System.out.println("testEqualence()");

		
		CharMachine a = new CharMachine('a');
		CharMachine b = new CharMachine('b');
		CharMachine c = new CharMachine('c');
		
		Machine<Character, InfiniteSet<Character>, Void> a_b = a.unite(b);
		Machine<Character, InfiniteSet<Character>, Void> ab = a.concat(b);
		Machine<Character, InfiniteSet<Character>, Void> ba = b.concat(a);
		
		//Machine<Character, InfiniteSet<Character>, Void> a_plus = a.iterate();
		//Machine<Character, InfiniteSet<Character>, Void> a_plus_2 = a.iterate().iterate();
		
		
		
		assertTrue(a.iterate().containsAll(a));
		assertFalse(a.containsAll(a.iterate()));
		
		assertTrue(a.unite(b).containsAll(a));
		assertTrue(a.unite(b).containsAll(b));
		
		assertFalse(a.containsAll(a.unite(b)));
		assertFalse(b.containsAll(a.unite(b)));
		
		assertTrue(a_b.concat(a_b).containsAll(ab.unite(ba)));
		assertFalse(ab.unite(ba).containsAll(a_b.concat(a_b)));
		
		Machine<Character, InfiniteSet<Character>, Void> A = a.complement();
		
		System.out.println("!a := "+A);
		
		assertTrue(a.complement().containsAll(b));
		assertFalse(a.complement().containsAll(a));
		
		System.out.println("!!a := "+a.complement().complement());
		
		assertTrue(a.complement().complement().isEqual(a));
		
		
		assertFalse(ab.containsAll(a));
		assertFalse(a.containsAll(a.optional()));
		
		System.out.println("!a+b:= "+a.unite(b).complement());
		//System.out.println("!!a+b:= "+a.unite(b).complement().complement());
		
		System.out.println("a+b & a+c := "+a.unite(b).intersect(a.unite(c)));
		
		
		
		System.out.println("a+b-b := "+a.unite(b).minus(b));
		
		System.out.println("a-a := "+a.minus(a));
				
		System.out.println("a+ := "+a.iterate());
		System.out.println("a++ := "+a.iterate().iterate());
		
		// (a++)++ == a++
		assertTrue(a.iterate().isEqual(a.iterate().iterate()));
		
		// !(a||b) == (!a)&&(!b)
		assertTrue(a.unite(b).complement().isEqual(a.complement().intersect(b.complement())));
		
		System.out.println("a && !a := "+a.intersect(a.complement()));
		System.out.println("a || !a := "+a.complement().unite(a));
		System.out.println("(a || !a)! := "+a.complement().unite(a).complement());
		
		assertTrue(a.complement().unite(a).complement().isEqual(a.intersect(a.complement())));
		
		// a && !a == 0
		assertTrue(a.complement().unite(a).complement().isEmpty());
		assertTrue(a.intersect(a.complement()).isEmpty());
		
		
		System.out.println("a || b -- b := "+a.unite(b).minus(b));
		System.out.println("a || b -- a := "+a.unite(b).minus(a));
		
		assertTrue(a.unite(b).minus(b).isEqual(a));
		assertTrue(a.unite(b).minus(a).isEqual(b));
		assertTrue(a.minus(a).isEmpty());
	
	}
	
	@org.junit.jupiter.api.Test
	void testRevrerse() {
	
		CharMachine empty = new CharMachine();
		CharMachine epsilon = new CharMachine(true);
		
		CharMachine a = new CharMachine('a');
		CharMachine b = new CharMachine('b');
		
		Machine<Character, InfiniteSet<Character>, Void> ab = a.concat(b);
		Machine<Character, InfiniteSet<Character>, Void> ba = b.concat(a);
		
		Machine<Character, InfiniteSet<Character>, Void> abR = ab.reverse();
		
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
		
		
	}

	@org.junit.jupiter.api.Test
	void test() {
		
		System.out.println("test()");

		Machine<Character, InfiniteSet<Character>, Void> e = new CharMachine(true);
		
		CharMachine a = new CharMachine('a');
		CharMachine b = new CharMachine('b');
		
		System.out.println("a+b := "+a.unite(b));
		
		assertTrue(a.unite(e).isEqual(e.unite(a)));
				
		Machine<Character, InfiniteSet<Character>, Void> ab = a.concat(b);
		Machine<Character, InfiniteSet<Character>, Void> a_b = a.unite(b);
		
		Machine<Character, InfiniteSet<Character>, Void> a_ = a.optional();
		Machine<Character, InfiniteSet<Character>, Void> aa = a.iterate();
		
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

		
		
		assertTrue(a.initial().toString().startsWith("<I"));
		a.finals().forEach(s-> assertTrue(s.toString().endsWith("F>")));
		
	}
	
	void testAll(Language<?, Character> fsm, Stream<String> s, boolean exp) {
		//System.out.println(fsm);
		s.forEach(x -> {
			assertEquals(exp, fsm.contains(x.chars().<Character>mapToObj(c->(char)c)), fsm+" does "+(exp?"not ":"")+"contain "+x);
		});
	}

}