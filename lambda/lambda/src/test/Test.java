package test;

import static org.junit.jupiter.api.Assertions.*;

import lambda.Reducible;
import lambda.Term;
import lambda.Type;
import lambda.reducible.Constant;
import lambda.reducible.Variable;
import lambda.term.Abstraction;
import lambda.term.Application;

class Test {

	@org.junit.jupiter.api.Test
	void test() {
		
		Reducible<Type<Type<Term>>> STAR = new Constant<>("✱");
		
		assertThrows(Error.class, ()->STAR.type());
		
		Reducible<Type<Term>> T = new Variable<>(STAR);
		
		Reducible<Term> id = new Abstraction<>(STAR, X -> new Abstraction<>(X, x -> x));
		
		assertTrue(id.isBetaEqual(id));
		assertTrue(T.isBetaEqual(T));
		assertTrue(STAR.isBetaEqual(STAR));
		
		assertTrue(STAR.isBetaEqual(T.type()));
		
		System.out.println(id+" : "+id.type());
		//System.out.println(id.type());
		System.out.println(new Abstraction<>(T, x->x));
		System.out.println(new Application<>(id, T));
		
		
		assertTrue(new Application<>(id, T).isBetaEqual(new Abstraction<>(T, x->x)));
		
		
		
		
	}

}
