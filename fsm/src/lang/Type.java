package lang;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 *
 *
 * @author 0xMyon
 *
 * @param <THIS> underlying implementation type
 * @param <T> underlying element type
 */
public interface Type<THIS extends Type<THIS, T>, T> extends lang.Set<THIS, T>{




	/**
	 * @return the complement {@link Type} {@code ~this}
	 */
	THIS complement();

	default THIS complement(final boolean complement) {
		return complement ? complement() : THIS();
	}


	/**
	 * @return the intersection {@link Type} {@code this & that}
	 */
	@Override
	default THIS intersect(final THIS that) {
		return this.complement().unite(that.complement()).complement();
	}

	/**
	 * @return the difference {@link Type} {@code this - that}
	 */
	@Override
	default THIS minus(final THIS that) {
		return this.complement().unite(that).complement();
	}
	
	default THIS implies(final THIS that) {
		return this.complement().unite(that);
	}


	/**
	 * @return true, if the {@link Type} contains a finite set of elements
	 */
	boolean isFinite();

	
	interface Factory<THIS extends Type<THIS, T>, T> extends lang.Set.Factory<THIS, T> {

		/**
		 * @return the {@link Type} that contains all other types
		 */
		default THIS universe() {
			return empty().complement();
		}

		/*
		default <U> THIS convert(final Type<?, U> that, final Function<U,T> f) {
			return that.convert(this, f);
		}

		default <U> THIS convert(final Type<?, ? extends T> that) {
			return convert(that, x->x);
		}
		*/

	}

	<THAT extends Type<THAT, U>, U, FACTORY extends Factory<THAT, U>> 
	THAT convert(final FACTORY factory, Function<T,U> function);

	default <THAT extends Type<THAT, T>, FACTORY extends Factory<THAT,T>> 
	THAT convert(final FACTORY factory) {
		return convert(factory, Function.identity());
	}

	@Override
	default public <THAT extends lang.Set<THAT, U>, U, FACTORY extends lang.Set.Factory<THAT, U>> 
	THAT convert(final FACTORY factory, final Function<T, U> function) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @param types
	 * @return a partition of the given TYPE such that:
	 * 		for every two types a,b: a.intersect(b).isEmpty() and
	 * 		the union over all types equals the union over the result
	 * @see {@link Type#unite(Type)}
	 */
	public static <T,TYPE extends Type<TYPE,T>> Set<TYPE> partition(final Set<TYPE> types) {
		Set<TYPE> result = new HashSet<>();
		for(final TYPE A : types) {
			result = result.stream().map(B -> Stream.of(B.minus(A), B.intersect(A)))
					.reduce(Stream.of(result.stream().reduce(A, Type::minus)), Stream::concat)
					.filter(x->!x.isEmpty()).collect(Collectors.toSet());
		}
		return result;
	}


	public static <T,TYPE extends Type<TYPE,T>> Set<TYPE> partition(final Stream<TYPE> types) {
		return partition(types.collect(Collectors.toSet()));
	}


	@Override
	Factory<THIS, T> factory();

}
