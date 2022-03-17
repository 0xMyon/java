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
 */
public interface Reducible {

	/**
	 * replaces all instances of {@code variable} with {@code term} recursively
	 * @param variable to be replaced
	 * @param term to be replaced with
	 * @return a new {@link Reducible} with replacement applied
	 * @throws AssertionError if {@code !variable.type().containsAll(term.type())}
	 */
	Reducible replace(final Variable variable, final Reducible term) throws TypeMismatch;

	/**
	 * perform many step beta-reduction
	 * @return {@code this} in beta-normal form
	 */
	Reducible reduce();

	/**
	 * @param that {@link Reducible} to be mapped to
	 * @param context of {@link Variable}s that are already mapped
	 * @return true, if there exists a mapping to {@code that} under the given {@code context}
	 */
	boolean isMapping(final Reducible that, final Map<Variable, Reducible> context);

	/**
	 * @param that {@link Reducible} to be mapped to
	 * @return true, of there exists a mapping to {@code that} under an empty context
	 * @see #isMapping(Reducible, Map)
	 */
	default boolean isMapping(final Reducible that) {
		return isMapping(that, new HashMap<>());
	}

	/**
	 * checks for structural equality
	 * @param that <T> to be compared to
	 * @return true, if {@code this} and {@code that} are structural equal
	 */
	default boolean isStructureEqual(final Reducible that) {
		return isMapping(that) && that.isMapping(this);
	}

	/**
	 * @param that {@link Reducible}
	 * @return true, if {@code this} and {@code that} are beta-equivalent
	 */
	default boolean isBetaEqual(final Reducible that) {
		return reduce().isStructureEqual(that.reduce());
	}

	/**
	 * @return type of term
	 * @throws AssertionError on none-existing type
	 * @see Constant#type()
	 */
	Reducible type() throws AssertionError;


	//boolean isType();

	/**
	 * @param variable
	 * @return true, if depending on {@code variable}}
	 */
	boolean isDepending(final Variable variable);

	/**
	 * @param type
	 * @return
	 * @see #isMapping(Reducible)
	 */
	default boolean containsAll(final Reducible type) {
		return isMapping(type);
	}

	default boolean contains(final Reducible that) {
		return containsAll(that.type());
	}

	public String toString(final Map<Variable, String> names);

}