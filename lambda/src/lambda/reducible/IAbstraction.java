package lambda.reducible;

import lambda.Reducible;

public interface IAbstraction<T> extends Reducible<T> {

	/**
	 * @param parameter
	 * @return
	 * @throws AssertionError
	 */
	Reducible<T> apply(final Reducible<T> parameter) throws AssertionError;

	/**
	 * @return type that can be passed to {@link #apply(Reducible)}
	 */
	Reducible<T> domain();
	
	@Override
	IAbstraction<T> type();
	
	/**
	 * @param that
	 * @return composed function of {@code this} and {@code that}
	 */
	default IAbstraction<T> andThen(final IAbstraction<T> that) {
		return new Abstraction<>(domain(), x -> that.apply(this.apply(x)));
	}
	
}
