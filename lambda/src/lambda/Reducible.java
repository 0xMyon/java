package lambda;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import lambda.reducible.Abstraction;
import lambda.reducible.Irreducible;
import lambda.reducible.Type;
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
	Reducible replace(final Variable variable, final Reducible term);

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
		return this.isMapping(that) && that.isMapping(this);
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
	 * @see Irreducible#type()
	 */
	Reducible type();


	int layer();

	/**
	 * @param variable
	 * @return true, if depending on {@code variable}}
	 */
	boolean isDepending(final Variable variable);

	Stream<Variable> freeVars();


	/**
	 * @param that
	 * @return true, if the value {@code that} is assignable to the type {@code this}
	 */
	default boolean containsType(final Reducible that) {
		return isMapping(that);
	}

	/**
	 * @param that
	 * @return true, if the value {@code that} is assignable to the type {@code this}
	 */
	default boolean isAssignable(final Reducible that) {
		return containsType(that.type());
	}



	public static final Irreducible TYPE = new Irreducible("Type") {
		@Override
		public Reducible type() {
			return new Type(this);
		}
		@Override
		public int layer() {
			return 2;
		}
	};


	public String toString(final Map<Variable, String> names);


	public static Abstraction Lambda(final Reducible type, final Function<Variable, Reducible> lambda) {
		return new Abstraction(type, lambda);
	}


}