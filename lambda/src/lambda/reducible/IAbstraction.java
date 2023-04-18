package lambda.reducible;

import lambda.Reducible;
import lambda.TypeMismatch;

public interface IAbstraction extends Reducible {

	/**
	 * @param parameter
	 * @return
	 * @throws AssertionError
	 * @throws TypeMismatch
	 */
	Reducible apply(final Reducible parameter);

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

	@Override
	IAbstraction reduce();

	@Override
	IAbstraction replace(Variable variable, Reducible term);


	/**
	 * @param that
	 * @return composed function of {@code this} and {@code that}
	 * @throws TypeMismatch
	 */
	default IAbstraction andThen(final IAbstraction that) {
		//System.out.println("dom: "+that+"#"+that.domain()+" | cdom: "+this+"#"+this.codomain());
		if (!that.domain().containsType(codomain())) throw new TypeMismatch();
		return new Abstraction(domain(), x -> that.apply(apply(x)));
	}


	/**
	 * @return true, if the functions output does not depends on the parameter
	 */
	boolean isConstant();


}
