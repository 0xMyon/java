package lang;

import java.util.function.Function;
import java.util.stream.Stream;

public interface Set<THIS extends Set<THIS, T>, T> extends Container<THIS, T>, Hierarchy<THIS> {

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
	
	
	/**
	 * Factory used to create a {@link Set}
	 * @param <THIS> type of the {@link Set}
	 * @param <T> type of the elements of the {@link Set}
	 */
	interface Factory<THIS extends Set<THIS, T>, T> {
		
		/**
		 * @return the empty {@link Set}
		 * @see Set#isEmpty()
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
		 * @return a {@link Set} from underlying elements
		 */
		THIS summand(T that);
		
		
	}
	
	
}
