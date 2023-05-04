package lambda.reducible;

import lambda.Reducible;
import lambda.TypeMismatch;
import lang.Container;

public interface IAbstraction<T, TYPE extends Container<TYPE, T>> extends Reducible<T, TYPE> {

	/**
	 * @param parameter
	 * @return
	 * @throws AssertionError
	 * @throws TypeMismatch
	 */
	Reducible<T, TYPE> apply(final Reducible<T, TYPE> parameter);

	/**
	 * @return type that can be passed to {@link #apply(Reducible)}
	 */
	Reducible<T, TYPE> domain();

	/**
	 * @return type that matches all objects returned by {@link #apply(Reducible)} such that {@code this.codomain().containsAll(this.apply(x)) == true } for all {@code x}
	 */
	default Reducible<T, TYPE> codomain() {
		try {
			return type().apply(new Variable<>(domain()));
		} catch (final TypeMismatch e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	IAbstraction<T, TYPE> type();

	@Override
	IAbstraction<T, TYPE> doReduction();

	@Override
	IAbstraction<T, TYPE> replace(Variable<T, TYPE> variable, Reducible<T, TYPE> term);


	/**
	 * @param that
	 * @return composed function of {@code this} and {@code that}
	 * @throws TypeMismatch
	 */
	default IAbstraction<T, TYPE> andThen(final IAbstraction<T, TYPE> that) {
		//System.out.println("dom: "+that+"#"+that.domain()+" | cdom: "+this+"#"+this.codomain());
		if (!that.domain().containsType(codomain())) throw new TypeMismatch();
		return new Abstraction<>(domain(), x -> that.apply(apply(x)));
	}


	/**
	 * @return true, if the functions output does not depends on the parameter
	 */
	boolean isConstant();


}
