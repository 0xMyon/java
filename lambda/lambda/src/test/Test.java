package test;

import static org.junit.jupiter.api.Assertions.*;

import lambda.Term;
import lambda.Type;
import lambda.term.Abstraction;
import lambda.term.Application;
import lambda.term.Variable;
import lambda.type.ArrowType;

class Test {

	@org.junit.jupiter.api.Test
	void test() {
		
		Type T = new Type() {};
		Type T_T = new ArrowType(T, T);
		Type U = new Type() {};
		
		
		
		Variable f = new Variable(T_T);
		Variable b = new Variable(T);
		
		{
			Term t = new Variable(T);
			assertTrue(t.isEqual(t));
			assertTrue(t.isEqual(new Variable(T)));
			assertThrows(AssertionError.class, () -> new Application(new Variable(T), new Variable(T)));
			assertFalse(t.isEqual(new Application(new Variable(T_T), new Variable(T))));
			assertFalse(t.isEqual(new Abstraction(T, x -> x)));
		}
		
		{
			Term t = new Abstraction(T, x -> x);
			assertTrue(t.isEqual(t));
			assertFalse(t.isEqual(new Variable(T)));
			assertFalse(t.isEqual(new Application(new Variable(T_T), new Variable(T))));
			assertTrue(t.isEqual(new Abstraction(T, x -> x)));
			assertFalse(t.isEqual(new Abstraction(T, x -> new Variable(T))));
		}
		
		{
			Term t = new Application(f, b);
			assertTrue(t.isEqual(t));
			assertFalse(t.isEqual(new Variable(T)));
			assertTrue(t.isEqual(new Application(f, new Variable(T))));
			assertTrue(t.isEqual(new Application(new Variable(T_T), b)));
			assertTrue(t.isEqual(new Application(f, b)));
			assertThrows(AssertionError.class, () -> new Application(f, f));
			assertThrows(AssertionError.class, () -> new Application(b, b));
			assertTrue(t.isEqual(new Application(new Variable(T_T), new Variable(T))));
			assertFalse(t.isEqual(new Abstraction(T, x -> x)));
			assertFalse(t.isEqual(new Abstraction(T, x -> new Variable(T))));
		}
		
		{
			Term t = new Variable(T);
			assertTrue(t.isBetaEqual(t));
			assertTrue(t.isBetaEqual(new Variable(T)));
			assertFalse(t.isBetaEqual(new Application(new Variable(T_T), new Variable(T))));
			assertFalse(t.isBetaEqual(new Abstraction(T, x -> x)));
		}
		
		{
			Term t = new Abstraction(T, x -> x);
			assertTrue(t.isBetaEqual(t));
			assertFalse(t.isBetaEqual(new Variable(T)));
			assertFalse(t.isBetaEqual(new Application(new Variable(T_T), new Variable(T))));
			assertTrue(t.isBetaEqual(new Abstraction(T, x -> x)));
			assertFalse(t.isBetaEqual(new Abstraction(T, x -> new Variable(T))));
		}
		
		{
			
			Term t = new Application(f, b);
			assertTrue(t.isBetaEqual(t));
			assertFalse(t.isBetaEqual(new Variable(T)));
			assertTrue(t.isBetaEqual(new Application(f, new Variable(T))));
			assertTrue(t.isBetaEqual(new Application(new Variable(T_T), b)));
			assertTrue(t.isBetaEqual(new Application(f, b)));
			assertTrue(t.isBetaEqual(new Application(new Variable(T_T), new Variable(T))));
			assertFalse(t.isBetaEqual(new Abstraction(T, x -> x)));
			assertFalse(t.isBetaEqual(new Abstraction(T, x -> new Variable(T))));
		}
		
		assertTrue(new Application(f,b).isBetaEqual(new Application(new Abstraction(T, x->new Application(f, b)), new Variable(T))));
		assertTrue(new Variable(T).isBetaEqual(new Application(new Abstraction(T, x->x), new Variable(T))));
		assertTrue(new Abstraction(T, x->x).isBetaEqual(new Application(new Abstraction(T, x->new Abstraction(T, y->y)), new Variable(T))));
		
		assertThrows(AssertionError.class, () -> new Abstraction(T, x -> new Application(x, x)));
		
		assertThrows(AssertionError.class, () -> new Application(new Abstraction(T, x -> x), new Variable(new Type(){})));
		
		assertEquals(new Abstraction(T, x->x).type(), new Abstraction(T, x->b).type());
		assertNotEquals(new Abstraction(U, x->x).type(), new Abstraction(T, x->x).type());
		assertNotEquals(new Abstraction(U, x->b).type(), new Abstraction(T, x->x).type());
		assertNotEquals(new Abstraction(T, x->b).type(), new Abstraction(U, x->b).type());
		
	}

}
