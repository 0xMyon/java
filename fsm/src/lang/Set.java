package lang;

import java.util.function.Function;
import java.util.stream.Stream;

public interface Set<THIS extends Set<THIS, T>, T> extends Container<THIS, T>, Hierarchy<THIS, T> {

	/**
	 * @return the union {@code this | that}
	 */
	THIS unite(final THIS that);
	/**
	 * @return the intersection {@code this & that}
	 */
	THIS intersect(final THIS that);

	/**
	 * @return the difference {@code this - that}
	 */
	THIS minus(final THIS that);

	
	default THIS xor(final THIS that) {
		return this.unite(that).minus(this.intersect(that));
	}
	
	
	/**
	 * @return the difference {@code that - this}
	 */
	default THIS removed(final THIS that) {
		return that.minus(THIS());
	}

	/**
	 * @return true, if and only if there exists no element that is contained on both {@link Set}
	 * @see #contains(Object)
	 */
	default boolean isDisjunct(final THIS that) {
		return this.intersect(that).isEmpty();
	}

	/**
	 * @return true, if the type is empty
	 * @see Factory#empty()
	 */
	boolean isEmpty();


	@Override
	default <THAT extends Container<THAT, U>, U, FACTORY extends Container.Factory<THAT,U>> THAT convert(final FACTORY factory, final Function<T, U> function) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	
	
	/**
	 * @param <THAT> target type of the {@link Set}
	 * @param <U> element target type of the {@link Set}
	 * @param factory that is used for conversion between sets
	 * @param function that is used for conversion between elements
	 * @return an isomorphic {@link Set} of a different type
	 * @throws UnsupportedOperationException, if the underlying {@link Set} is not convertible
	 */
	<THAT extends Set<THAT, U>, U, FACTORY extends Factory<THAT,U>> THAT convert(final FACTORY factory, final Function<T, U> function) throws UnsupportedOperationException;

	/**
	 * @see #convertSet(Factory, Function)
	 */
	default <THAT extends Set<THAT, T>, FACTORY extends Factory<THAT,T>> THAT convert(final FACTORY factory) {
		return convert(factory, Function.identity());
	}


	/**
	 * Factory used to create a {@link Set}
	 * @param <THIS> type of the {@link Set}
	 * @param <T> type of the elements of the {@link Set}
	 */
	interface Factory<THIS extends Set<THIS, T>, T> extends Container.Factory<THIS, T> {

		/**
		 * @return the empty {@link Set}
		 * @see Set#isEmpty()
		 */
		THIS empty();

		@SuppressWarnings("unchecked")
		default THIS union(final THIS... ths) {
			return union(Stream.of(ths));
		}

		default THIS union(final Stream<THIS> stream) {
			return stream.reduce(empty(), Set::unite);
		}




	}



	/*
	default THIS unite_Set(final Set<?, T> that) {
		return unite(that.convertSet(factory()));
	}

	default THIS minus_Set(final Set<?, T> that) {
		return minus(that.convertSet(factory()));
	}

	default THIS intersect_Set(final Set<?, T> that) {
		return intersect(that.convertSet(factory()));
	}

	default THIS removed_Set(final Set<?, T> that) {
		return removed(that.convertSet(factory()));
	}

	default boolean isDisjunct_Set(final Set<?, T> that) {
		return isDisjunct(that.convertSet(factory()));
	}

	default boolean containsAll_Set(final Set<?, T> that) {
		return containsAll(that.convertSet(factory()));
	}

	default boolean isEqual_Set(final Set<?, T> that) {
		return isEqual(that.convertSet(factory()));
	}
	*/

	/**
	 * @return {@link Factory} associated with this {@link Type}
	 */
	Factory<THIS, T> factory();


}
