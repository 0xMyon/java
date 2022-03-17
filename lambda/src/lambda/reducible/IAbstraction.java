package lambda.reducible;

import static lambda.ThrowingFunction.unchecked;

import lambda.Reducible;
import lambda.TypeMismatch;

public interface IAbstraction extends Reducible {

	/**
	 * @param parameter
	 * @return
	 * @throws AssertionError
	 * @throws TypeMismatch
	 */
	Reducible apply(final Reducible parameter) throws TypeMismatch;

	/**
	 * @return type that can be passed to {@link #apply(Reducible)}
	 */
	Reducible domain();

	/**
	 * @return type that matches all objects returned by {@link #apply(Reducible)} such that {@code this.codomain().containsAll(this.apply(x)) == true } for all {@code x}
	 */
	default Reducible codomain() {
		try {
			return type().apply(new Variable(domain()));
		} catch (final TypeMismatch e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	IAbstraction type();

	/**
	 * @param that
	 * @return composed function of {@code this} and {@code that}
	 * @throws TypeMismatch
	 */
	default IAbstraction andThen(final IAbstraction that) throws TypeMismatch {
		//System.out.println("dom: "+that+"#"+that.domain()+" | cdom: "+this+"#"+this.codomain());
		if (!that.domain().containsAll(codomain())) throw new TypeMismatch();
		return new Abstraction(domain(), unchecked(x -> that.apply(apply(x))));
	}


	/**
	 * @return true, if the functions output depends on the parameter
	 */
	boolean isConstant();


}
