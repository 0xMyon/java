package lambda;

import java.util.HashMap;
import java.util.Map;

import lambda.reducible.Variable;

/**
 * beta-reducible interface 
 * 
 * @author 0xMyon
 *
 * @param <T>
 */
public interface Reducible<T> {

	<X> Reducible<T> replace(final Variable<X> variable, final Reducible<X> term);

	/**
	 * perform many step beta-reduction
	 * @return T in beta-normal form
	 */
	Reducible<T> reduce();

	/**
	 * structural equality with variable mapping
	 * @param term to be compared
	 * @param map mapped variables
	 * @return true, if there is a variable mapping such that term can be created
	 */
	boolean isEqual(final Reducible<?> term, final Map<Variable<?>, Variable<?>> map);

	/**
	 * structural equality
	 * @param that T to be compared to
	 * @return true, if structural equal
	 */
	default boolean isEqual(final Reducible<?> that) {
		return isEqual(that, new HashMap<>()) && that.isEqual(this, new HashMap<>());
	}
	
	/**
	 * @param that
	 * @return true, if beta-equivalent
	 */
	default boolean isBetaEqual(final Reducible<T> that) {
		return this.reduce().isEqual(that.reduce());
	}
		
	Reducible<Type<T>> type();

}