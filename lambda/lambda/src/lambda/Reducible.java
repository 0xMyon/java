package lambda;

import java.util.HashMap;
import java.util.Map;

import lambda.term.Variable;

/**
 * beta-reducible interface 
 * 
 * @author 0xMyon
 *
 * @param <T>
 */
public interface Reducible<T extends Reducible<T>> {

	T replace(final Variable variable, final T term);

	/**
	 * perform many step beta-reduction
	 * @return T in beta-normal form
	 */
	T reduce();

	/**
	 * structural equality with variable mapping
	 * @param term to be compared
	 * @param map mapped variables
	 * @return true, if there is a variable mapping such that term can be created
	 */
	boolean isEqual(final T term, final Map<Variable, Variable> map);

	/**
	 * structural equality
	 * @param that T to be compared to
	 * @return true, if structural equal
	 */
	default boolean isEqual(final T that) {
		return isEqual(that, new HashMap<>()) && that.isEqual(THIS(), new HashMap<>());
	}
	
	/**
	 * @param that
	 * @return true, if beta-equivalent
	 */
	default boolean isBetaEqual(final T that) {
		return this.reduce().isEqual(that.reduce());
	}
	
	/**
	 * @return this in underlying type T
	 */
	T THIS();

}