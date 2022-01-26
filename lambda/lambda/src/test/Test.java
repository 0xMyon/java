package test;

import static org.junit.jupiter.api.Assertions.*;

import lambda.Abstraction;
import lambda.Application;
import lambda.Term;
import lambda.Variable;

class Test {

	@org.junit.jupiter.api.Test
	void test() {
		
		Variable a = new Variable();
		Variable b = new Variable();
		
		{
			Term t = new Variable();
			assertTrue(t.isEqual(t));
			assertTrue(t.isEqual(new Variable()));
			assertFalse(t.isEqual(new Application(new Variable(), new Variable())));
			assertFalse(t.isEqual(new Abstraction(x -> x)));
		}
		
		{
			Term t = new Abstraction(x -> x);
			assertTrue(t.isEqual(t));
			assertFalse(t.isEqual(new Variable()));
			assertFalse(t.isEqual(new Application(new Variable(), new Variable())));
			assertTrue(t.isEqual(new Abstraction(x -> x)));
			assertFalse(t.isEqual(new Abstraction(x -> new Variable())));
		}
		
		{
			Term t = new Application(a, b);
			assertTrue(t.isEqual(t));
			assertFalse(t.isEqual(new Variable()));
			assertTrue(t.isEqual(new Application(a, new Variable())));
			assertTrue(t.isEqual(new Application(new Variable(), b)));
			assertTrue(t.isEqual(new Application(a, b)));
			assertFalse(t.isEqual(new Application(a, a)));
			assertFalse(t.isEqual(new Application(b, b)));
			assertTrue(t.isEqual(new Application(new Variable(), new Variable())));
			assertFalse(t.isEqual(new Abstraction(x -> x)));
			assertFalse(t.isEqual(new Abstraction(x -> new Variable())));
		}
		
		{
			Term t = new Variable();
			assertTrue(t.isBetaEqual(t));
			assertTrue(t.isBetaEqual(new Variable()));
			assertFalse(t.isBetaEqual(new Application(new Variable(), new Variable())));
			assertFalse(t.isBetaEqual(new Abstraction(x -> x)));
		}
		
		{
			Term t = new Abstraction(x -> x);
			assertTrue(t.isBetaEqual(t));
			assertFalse(t.isBetaEqual(new Variable()));
			assertFalse(t.isBetaEqual(new Application(new Variable(), new Variable())));
			assertTrue(t.isBetaEqual(new Abstraction(x -> x)));
			assertFalse(t.isBetaEqual(new Abstraction(x -> new Variable())));
		}
		
		{
			
			Term t = new Application(a, b);
			assertTrue(t.isBetaEqual(t));
			assertFalse(t.isBetaEqual(new Variable()));
			assertTrue(t.isBetaEqual(new Application(a, new Variable())));
			assertTrue(t.isBetaEqual(new Application(new Variable(), b)));
			assertTrue(t.isBetaEqual(new Application(a, b)));
			assertFalse(t.isBetaEqual(new Application(a, a)));
			assertFalse(t.isBetaEqual(new Application(b, b)));
			assertTrue(t.isBetaEqual(new Application(new Variable(), new Variable())));
			assertFalse(t.isBetaEqual(new Abstraction(x -> x)));
			assertFalse(t.isBetaEqual(new Abstraction(x -> new Variable())));
		}
		
		assertTrue(new Application(a,b).isBetaEqual(new Application(new Abstraction(x->new Application(a, b)), new Variable())));
		assertTrue(new Variable().isBetaEqual(new Application(new Abstraction(x->x), new Variable())));
		assertTrue(new Abstraction(x->x).isBetaEqual(new Application(new Abstraction(x->new Abstraction(y->y)), new Variable())));
		
		assertDoesNotThrow(() -> new Abstraction(x -> new Application(x, x)));
		
	}

}
