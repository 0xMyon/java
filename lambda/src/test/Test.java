package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;

import lambda.Reducible;
import lambda.Term;
import lambda.Type;
import lambda.reducible.Constant;
import lambda.reducible.Variable;
import lambda.term.Abstraction;
import lambda.term.Application;

class Test {

	
	Constant<Type<Type<Type<Term>>>> BOX = new Constant<>("◻");
	Constant<Type<Type<Term>>> STAR = new Constant<>("✱", BOX);
	
	Constant<Type<Term>> I = new Constant<>("I", STAR);
	Constant<Term> N0 = new Constant<>("0", I);
	
	
	Variable<Type<Term>> T = new Variable<>(STAR);
	
	Abstraction<Type<Term>, Term> id = new Abstraction<>(STAR, X -> new Abstraction<>(X, x -> x));
	
	Reducible<Term> id_I = new Application<>(id, I);
	
	Reducible<Term> left = new Abstraction<>(I, a -> new Abstraction<Term, Term>(I, b -> a));
	
	Reducible<Type<Term>> ID = new Abstraction<Type<Term>, Type<Term>>(STAR, X -> X);
	
	
	@org.junit.jupiter.api.Test
	void test() {
		
		
		assertTrue(STAR.type().isBetaEqual(BOX));
		
		assertThrows(AssertionError.class, ()->BOX.type());
		
		
		assertTrue(id.isEqual(id));
		assertTrue(id.isEqual(id.reduce()));
		assertTrue(id.isBetaEqual(id));
				
		assertTrue(STAR.isBetaEqual(STAR));
		assertTrue(T.isBetaEqual(T));
		assertTrue(id_I.isBetaEqual(id_I));
		
		assertTrue(id_I.isEqual(id_I));
		
		assertTrue(STAR.isBetaEqual(T.type()));
		
		assertFalse(id.isEqual(STAR));
		assertFalse(id.isEqual(T));
		assertFalse(id.isEqual(id_I));
		
		assertFalse(STAR.isEqual(T));
		assertFalse(STAR.isEqual(id));
		assertFalse(STAR.isEqual(id_I));
		
		assertFalse(T.isEqual(STAR));
		assertFalse(T.isEqual(id));
		assertFalse(T.isEqual(id_I));
		
		assertFalse(id_I.isEqual(STAR));
		assertFalse(id_I.isEqual(id));
		assertFalse(id_I.isEqual(T));
		
		
		
		assertTrue(id_I.isBetaEqual(new Abstraction<>(I, x->x)));
		
		// wrong type: expected ?:STAR
		assertThrows(AssertionError.class, ()-> new Application<>(id, STAR) );
		assertThrows(AssertionError.class, ()-> new Application<>(id, id) );
		assertThrows(AssertionError.class, ()-> new Application<>(id, N0) );
		
		// wrong type: expected ?:I
		assertThrows(AssertionError.class, ()-> new Application<>(id_I, STAR) );
		assertThrows(AssertionError.class, ()-> new Application<>(id_I, id) );
		assertThrows(AssertionError.class, ()-> new Application<>(id_I, I) );
		
		// none-function parameter
		assertThrows(AssertionError.class, ()-> new Application<>(STAR, null) );
		assertThrows(AssertionError.class, ()-> new Application<>(I, null) );
		assertThrows(AssertionError.class, ()-> new Application<>(N0, null) );
		
		//System.out.println(id_I);
		
		Pattern p = Pattern.compile("\\(λ\\d+:✱.λ\\d+:\\d+.\\d+ I\\)");
		assertTrue(p.matcher(id_I.toString()).matches());
		
		
		assertFalse(new Abstraction<Term, Term>(I, x -> new Abstraction<>(I, y -> y)).isEqual(new Abstraction<Term, Term>(I, x -> new Abstraction<>(I, y -> x))));
		
		assertFalse(new Application<>(id_I, N0).isEqual(new Application<>(id_I, new Variable<>(I))));
		
		assertTrue(
				new Abstraction<Term, Term>(id.type(), f -> new Abstraction<>(STAR, X -> new Application<>(f, X))).apply(id).isBetaEqual(id)
		);
		
		Variable<Term> a = new Variable<>(I);
		
		assertFalse(
				new Application<>(new Application<>(left, new Variable<>(I)), new Variable<>(I))
				.isEqual(new Application<>(new Application<>(left, a), a))
		);
		
		assertTrue(
				new Application<>(new Variable<>(left.type()), new Variable<>(I))
				.isBetaEqual(new Application<>(new Variable<>(left.type()), new Variable<>(I)))
		);
		
		
		
	}
	
	@org.junit.jupiter.api.Test
	void test2() {
		
		// Types depending on Types
		System.out.println(ID+" : "+ID.type()+" : "+ID.type().type());
				
		// Types depending on Terms
		new Abstraction<Term, Type<Term>>(I, i->i.type());
				
		System.out.println(id_I+" : "+id_I.type());
		System.out.println(id_I.reduce()+" : "+id_I.reduce().type());
		System.out.println(id+" : "+id.type()+" : "+id.type().type());
		
		assertTrue(id_I.type().isBetaEqual(id_I.reduce().type()));
		
	}

}
