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

	}

	<THAT extends Type<THAT, U>, U> THAT convertType(Type.Factory<THAT, U> factory, Function<T,U> function);

	default <THAT extends Type<THAT, T>> THAT convertType(final Type.Factory<THAT,T> factory) {
		return convertType(factory, Function.identity());
	}

	@Override
	default public <THAT extends lang.Set<THAT, U>, U> THAT convertSet(final lang.Set.Factory<THAT, U> factory, final Function<T, U> function) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @param types
	 * @return a partition of the given TYPE such that:
	 * 		for every two types a,b: a.intersect(b).isEmpty() and
	 * 		the union over all types equals the union over the result
	 * @see {@link Type#unite(Type)}
	 */
	public static <T,TYPE extends Type<TYPE,T>> Set<Type<?, T>> partition(final Set<Type<?, T>> types) {
		Set<Type<?, T>> result = new HashSet<>();
		for(final Type<?, T> A : types) {
			result = result.stream().map(B -> Stream.of(B.minus_Type(A), B.intersect_Type(A)))
					.reduce(Stream.of(result.stream().reduce(A, Type::minus_Type)), Stream::concat)
					.filter(X -> !X.isEmpty()).collect(Collectors.toSet());
		}
		return result;
	}

	public static <T,TYPE extends Type<TYPE,T>> Set<Type<?, T>> partition(final Stream<Type<?, T>> types) {
		return partition(types.collect(Collectors.toSet()));
	}


	@SuppressWarnings("unchecked")
	default THIS toType(final Type<?, ? extends T> that) {
		return getClass().isInstance(that) ? (THIS)that : that.convertType(factory(), x->x);
		//return that.convertType(factory(), x->x);
	}


	default THIS unite_Type(final Type<?, ? extends T> that) {
		return unite(toType(that));
	}

	default THIS minus_Type(final Type<?, ? extends T> that) {
		return minus(toType(that));
	}

	default THIS intersect_Type(final Type<?, ? extends T> that) {
		return intersect(toType(that));
	}

	default THIS removed_Type(final Type<?, ? extends T> that) {
		return removed(toType(that));
	}

	default boolean isEqual_Type(final Type<?, ? extends T> that) {
		return isEqual(toType(that));
	}

	default boolean containsAll_Type(final Type<?, ? extends T> that) {
		return containsAll(toType(that));
	}

	@Override
	Factory<THIS, T> factory();

}
