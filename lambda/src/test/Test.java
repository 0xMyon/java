package test;

import static org.junit.jupiter.api.Assertions.*;

//import java.util.regex.Pattern;

import lambda.Reducible;
import lambda.reducible.Abstraction;
import lambda.reducible.Application;
import lambda.reducible.Constant;
import lambda.reducible.IAbstraction;
import lambda.reducible.Variable;

class Test {

	
	Constant<String> BOX = new Constant<>("◻");
	Constant<String> STAR = new Constant<>("✱", BOX);
	
	Constant<String> I = new Constant<>("I", STAR);
	Constant<String> N0 = new Constant<>("0", I);
	
	
	Variable<String> T = new Variable<>(STAR);
	
	Abstraction<String> id = new Abstraction<>(STAR, X -> new Abstraction<>(X, x -> x));
	
	Reducible<String> id_I = new Application<>(id, I);
	
	Reducible<String> left = new Abstraction<>(I, a -> new Abstraction<>(I, b -> a));
	
	IAbstraction<String> ID = new Abstraction<>(STAR, X -> X);
	
	
	@org.junit.jupiter.api.Test
	void test() {
		
		
		assertTrue(STAR.type().isBetaEqual(BOX));
		
		assertThrows(AssertionError.class, ()->BOX.type());
		
		
		assertTrue(id.isStructureEqual(id));
		assertTrue(id.isStructureEqual(id.reduce()));
		assertTrue(id.isBetaEqual(id));
				
		assertTrue(STAR.isBetaEqual(STAR));
		assertTrue(T.isBetaEqual(T));
		assertTrue(id_I.isBetaEqual(id_I));
		
		assertTrue(id_I.isStructureEqual(id_I));
		
		assertTrue(STAR.isBetaEqual(T.type()));
		
		assertFalse(id.isStructureEqual(STAR));
		assertFalse(id.isStructureEqual(T));
		assertFalse(id.isStructureEqual(id_I));
		
		assertFalse(STAR.isStructureEqual(T));
		assertFalse(STAR.isStructureEqual(id));
		assertFalse(STAR.isStructureEqual(id_I));
		
		assertFalse(T.isStructureEqual(STAR));
		assertFalse(T.isStructureEqual(id));
		assertFalse(T.isStructureEqual(id_I));
		
		assertFalse(id_I.isStructureEqual(STAR));
		assertFalse(id_I.isStructureEqual(id));
		assertFalse(id_I.isStructureEqual(T));
		
		
		
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
		
		//Pattern p = Pattern.compile("\\(λ\\d+:✱.λ\\d+:\\d+.\\d+ I\\)");
		//assertTrue(p.matcher(id_I.toString()).matches());
		
		
		assertFalse(new Abstraction<>(I, x -> new Abstraction<>(I, y -> y)).isStructureEqual(new Abstraction<>(I, x -> new Abstraction<>(I, y -> x))));
		
		assertFalse(new Application<>(id_I, N0).isStructureEqual(new Application<>(id_I, new Variable<>(I))));
		
		assertTrue(
				new Abstraction<>(id.type(), f -> new Abstraction<>(STAR, X -> new Application<>(f, X))).apply(id).isBetaEqual(id)
		);
		
		Variable<String> a = new Variable<>(I);
		
		assertFalse(
				new Application<>(new Application<>(left, new Variable<>(I)), new Variable<>(I))
				.isStructureEqual(new Application<>(new Application<>(left, a), a))
		);
		
		assertTrue(
				new Application<>(new Variable<>(left.type()), new Variable<>(I))
				.isBetaEqual(new Application<>(new Variable<>(left.type()), new Variable<>(I)))
		);
		
		assertTrue(ID.andThen(ID).isBetaEqual(ID));
		
		
	}
	
	@org.junit.jupiter.api.Test
	void test2() {
		
		// Types depending on Types
		System.out.println(ID+" : "+ID.type()+" : "+ID.type().type());
				
		// Types depending on Terms
		Reducible<String> T_i = new Abstraction<>(I, i->i.type());
		System.out.println(T_i+" : "+T_i.type());
		
		// Types depending on Terms
		Reducible<String> i_T = new Abstraction<>(STAR, T->N0);
		System.out.println(i_T+" : "+i_T.type());
				
		
		System.out.println(id+" : "+id.type());
		System.out.println(id_I+" : "+id_I.type());
		System.out.println(id_I.reduce()+" : "+id_I.reduce().type());
		System.out.println(id+" : "+id.type()+" : "+id.type().type());
		
		assertTrue(id_I.type().isBetaEqual(id_I.reduce().type()));
		
	}

}
