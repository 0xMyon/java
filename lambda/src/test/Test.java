package test;

import static lambda.Reducible.Lambda;
import static org.junit.jupiter.api.Assertions.assertEquals;
//import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lambda.Reducible;
import lambda.TypeMismatch;
import lambda.reducible.Application;
import lambda.reducible.Constant;
import lambda.reducible.IAbstraction;
import lambda.reducible.Irreducible;
import lambda.reducible.Variable;

class Test {


	public Test() throws Exception {}

	Irreducible STAR = Reducible.TYPE;
	Reducible BOX = STAR.type();

	Irreducible I = new Constant("I", STAR);
	Irreducible N0 = new Constant("0", I);


	Variable T = new Variable(STAR);

	IAbstraction id = Lambda(STAR, X -> Lambda(X, x -> x));

	Reducible id_I = new Application(id, I);

	Reducible left = Lambda(I, a -> Lambda(I, b -> a));

	IAbstraction ID = Lambda(STAR, X -> X);


	@org.junit.jupiter.api.Test
	void test() throws TypeMismatch {


		assertTrue(STAR.type().isBetaEqual(BOX));

		//assertThrows(AssertionError.class, ()->BOX.type());


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



		assertTrue(id_I.isBetaEqual(Lambda(I, x->x)));

		// wrong type: expected ?:STAR
		//assertThrows(TypeMismatch.class, ()-> new Application<>(id, STAR) );
		assertThrows(Throwable.class, ()-> new Application(id, id) );
		assertThrows(Throwable.class, ()-> new Application(id, N0) );

		// wrong type: expected ?:I
		//assertThrows(TypeMismatch.class, ()-> new Application<>(id_I, STAR) );
		assertThrows(Throwable.class, ()-> new Application(id_I, id) );
		assertThrows(Throwable.class, ()-> new Application(id_I, I) );

		// none-function parameter
		assertThrows(Throwable.class, ()-> new Application(STAR, null) );
		assertThrows(Throwable.class, ()-> new Application(I, null) );
		assertThrows(Throwable.class, ()-> new Application(N0, null) );

		//System.out.println(id_I);

		//Pattern p = Pattern.compile("\\(λ\\d+:✱.λ\\d+:\\d+.\\d+ I\\)");
		//assertTrue(p.matcher(id_I.toString()).matches());

		assertFalse(ID.isBetaEqual(id.type()));
		assertFalse(ID.isAssignable(id));

		assertFalse(Lambda(I, x -> Lambda(I, y -> y)).isStructureEqual(Lambda(I, x -> Lambda(I, y -> x))));

		//assertFalse(new Application(id_I, N0).isStructureEqual(new Application(id_I, new Variable(I))));

		//assertTrue(Lambda(id.type(), f -> Lambda(STAR, X -> new Application(f, X))).apply(id).isBetaEqual(id));

		final Variable a = new Variable(I);

		assertFalse(
				new Application(new Application(left, new Variable(I)), new Variable(I))
				.isStructureEqual(new Application(new Application(left, a), a))
				);

		assertTrue(
				new Application(new Variable(left.type()), new Variable(I))
				.isBetaEqual(new Application(new Variable(left.type()), new Variable(I)))
				);

		assertTrue(ID.andThen(ID).isBetaEqual(ID));

		assertThrows(TypeMismatch.class, () -> id.andThen(ID) );



		final IAbstraction typed_id = Lambda(STAR, T -> Lambda(T, x -> T));

		assertEquals(1, I.layer());

		assertEquals(1, typed_id.layer());
		assertEquals(0, id.layer());
		assertEquals(0, id_I.layer());
		assertEquals(2, STAR.layer());
		assertEquals(3, STAR.type().layer());
		assertEquals(4, STAR.type().type().layer());


		assertTrue(typed_id.isAssignable(id_I));

		// deducing
		//assertTrue(typed_id.isAssignable(id));

	}

	@org.junit.jupiter.api.Test
	void test2() {

		// Types depending on Types
		System.out.println(ID+" : "+ID.type()+" : "+ID.type().type());

		// Types depending on Terms
		final Reducible T_i = Lambda(I, i->i.type());
		System.out.println(T_i+" : "+T_i.type());

		// Types depending on Terms
		final Reducible i_T = Lambda(STAR, T->N0);
		System.out.println(i_T+" : "+i_T.type());


		System.out.println(id+" : "+id.type());
		System.out.println(id_I+" : "+id_I.type());
		System.out.println(id_I.reduce()+" : "+id_I.reduce().type());
		System.out.println(id+" : "+id.type()+" : "+id.type().type());

		assertTrue(id_I.type().isBetaEqual(id_I.reduce().type()));

	}

}
