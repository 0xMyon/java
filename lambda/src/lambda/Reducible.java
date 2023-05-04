package lambda;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import lambda.reducible.Abstraction;
import lambda.reducible.Irreducible;
import lambda.reducible.Variable;
import lang.Container;

/**
 * beta-reducible interface
 *
 * @author 0xMyon
 *
 */
public interface Reducible<T, TYPE extends Container<TYPE, T>> {

	/**
	 * replaces all instances of {@code variable} with {@code term} recursively
	 * @param variable to be replaced
	 * @param term to be replaced with
	 * @return a new {@link Reducible} with replacement applied
	 * @throws AssertionError if {@code !variable.type().containsAll(term.type())}
	 */
	Reducible<T,TYPE> replace(final Variable<T, TYPE> variable, final Reducible<T,TYPE> term);

	
	
	default Reducible<T,TYPE> reduce() {
		return isReducible() ? doReduction() : this;
	}
	
	
	boolean isReducible();
	
	/**
	 * perform many step beta-reduction
	 * @return {@code this} in beta-normal form
	 */
	Reducible<T,TYPE> doReduction();

	/**
	 * @param that {@link Reducible} to be mapped to
	 * @param context of {@link Variable}s that are already mapped
	 * @return true, if there exists a mapping to {@code that} under the given {@code context}
	 */
	boolean isMapping(final Reducible<T,TYPE> that, final Map<Variable<T, TYPE>, Reducible<T,TYPE>> context);

	/**
	 * @param that {@link Reducible} to be mapped to
	 * @return true, of there exists a mapping to {@code that} under an empty context
	 * @see #isMapping(Reducible, Map)
	 */
	default boolean isMapping(final Reducible<T,TYPE> that) {
		return isMapping(that, new HashMap<>());
	}

	/**
	 * checks for structural equality
	 * @param that <T> to be compared to
	 * @return true, if {@code this} and {@code that} are structural equal
	 */
	default boolean isStructureEqual(final Reducible<T,TYPE> that) {
		return this.isMapping(that) && that.isMapping(this);
	}

	/**
	 * @param that {@link Reducible}
	 * @return true, if {@code this} and {@code that} are beta-equivalent
	 */
	default boolean isBetaEqual(final Reducible<T,TYPE> that) {
		return doReduction().isStructureEqual(that.doReduction());
	}

	/**
	 * @return type of term
	 * @throws AssertionError on none-existing type
	 * @see Irreducible#type()
	 */
	Reducible<T,TYPE> type();


	int layer();

	/**
	 * @param variable
	 * @return true, if depending on {@code variable}}
	 */
	boolean isDepending(final Variable<T, TYPE> variable);

	Stream<Variable<T, TYPE>> freeVars();


	/**
	 * @param that
	 * @return true, if the value {@code that} is assignable to the type {@code this}
	 */
	default boolean containsType(final Reducible<T,TYPE> that) {
		return isMapping(that);
	}

	/**
	 * @param that
	 * @return true, if the value {@code that} is assignable to the type {@code this}
	 */
	default boolean isAssignable(final Reducible<T,TYPE> that) {
		return containsType(that.type());
	}


	public String toString(final Map<Variable<T, TYPE>, String> names);

	
	public static <T, TYPE extends Container<TYPE, T>> 
	Abstraction<T,TYPE> Lambda(final Reducible<T,TYPE> type, final Function<Variable<T, TYPE>, Reducible<T,TYPE>> lambda) {
		return new Abstraction<>(type, lambda);
	}


}