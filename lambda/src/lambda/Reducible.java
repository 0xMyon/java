package lambda;

import java.util.HashMap;
import java.util.Map;

import lambda.reducible.Constant;
import lambda.reducible.Variable;

/**
 * beta-reducible interface 
 * 
 * @author 0xMyon
 *
 * @param <T>
 */
public interface Reducible<T> {

	/**
	 * replaces all instances of {@code variable} with {@code term} recursively
	 * @param variable to be replaced
	 * @param term to be replaced with
	 * @return a new {@link Reducible} with replacement applied
	 * @throws AssertionError if {@code !variable.type().containsAll(term.type())}
	 */
	Reducible<T> replace(final Variable<T> variable, final Reducible<T> term) throws AssertionError;

	/**
	 * perform many step beta-reduction
	 * @return {@code this} in beta-normal form
	 */
	Reducible<T> reduce();

	/**
	 * @param that {@link Reducible} to be mapped to
	 * @param context of {@link Variable}s that are already mapped
	 * @return true, if there exists a mapping to {@code that} under the given {@code context}
	 */
	boolean isMapping(final Reducible<T> that, final Map<Variable<T>, Reducible<T>> context);

	/**
	 * @param that {@link Reducible} to be mapped to
	 * @return true, of there exists a mapping to {@code that} under an empty context
	 * @see #isMapping(Reducible, Map)
	 */
	default boolean isMapping(final Reducible<T> that) {
		return isMapping(that, new HashMap<>());
	}
	
	/**
	 * checks for structural equality
	 * @param that <T> to be compared to
	 * @return true, if {@code this} and {@code that} are structural equal
	 */
	default boolean isStructureEqual(final Reducible<T> that) {
		return isMapping(that) && that.isMapping(this);
	}
	
	/**
	 * @param that {@link Reducible}
	 * @return true, if {@code this} and {@code that} are beta-equivalent
	 */
	default boolean isBetaEqual(final Reducible<T> that) {
		return this.reduce().isStructureEqual(that.reduce());
	}
	
	/**
	 * @return type of term
	 * @throws AssertionError on none-existing type
	 * @see Constant#type()
	 */
	Reducible<T> type() throws AssertionError;
	
	/**
	 * @param variable
	 * @return true, if depending on {@code variable}}
	 */
	boolean isDepending(final Variable<T> variable);

	/**
	 * @param type
	 * @return 
	 * @see #isMapping(Reducible)
	 */
	default boolean containsAll(final Reducible<T> type) {
		return isMapping(type);
	}
	
	public String toString(final Map<Variable<T>, String> names);
	
}