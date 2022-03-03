package lang;

import java.util.function.Function;
import java.util.stream.Stream;

import lang.Type.Factory;

public interface Set<THIS extends Set<THIS, T>, T> extends Container<THIS, T>, Hierarchy<THIS> {

	/**
	 * @return the union {@link Type} {@code this | that}
	 */
	THIS unite(final THIS that);
	
	/**
	 * @return the intersection {@link Type} {@code this & that}
	 */
	THIS intersect(final THIS that);
	
	/**
	 * @return the difference {@link Type} {@code this - that}
	 */
	THIS minus(final THIS that);
	
	
	default boolean isDisjunct(final THIS that) {
		return this.intersect(that).isEmpty();
	}
	
	/**
	 * @return true, if the type is empty
	 * @see Factory#empty()
	 */
	boolean isEmpty();
	
	
	/**
	 * @param <THAT> target type of the {@link Set}
	 * @param <U> element target type of the {@link Set}
	 * @param factory that is used for conversion between sets
	 * @param function that is used for conversion between elements
	 * @return an isomorphic {@link Set} of a different type
	 * @throws UnsupportedOperationException, if the underlying {@link Set} is not convertible
	 */
	<THAT extends Set<THAT, U>, U> THAT convertSet(final Factory<THAT, U> factory, final Function<T, U> function) throws UnsupportedOperationException;
	
	/**
	 * @see #convertSet(Factory, Function)
	 */
	default <THAT extends Set<THAT, T>> THAT convertSet(final Factory<THAT, T> factory) {
		return convertSet(factory, Function.identity());
	}
	
	
	interface Factory<THIS extends Set<THIS, T>, T> {
		
		/**
		 * @return the empty {@link Type}
		 * @see Type#isEmpty()
		 */
		THIS empty();
		
		@SuppressWarnings("unchecked")
		default THIS union(final THIS... ths) {
			return union(Stream.of(ths));
		}
		
		default THIS union(Stream<THIS> stream) {
			return stream.reduce(empty(), Set::unite);
		}
		

		/**
		 * @param that
		 * @return a {@link Type} from underlying elements
		 */
		THIS summand(T that);
		
		
	}
	
	
}
