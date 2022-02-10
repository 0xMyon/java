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
	
	
	
	<U, THAT extends Set<THAT, U>> THAT convertSet(Factory<THAT, U> factory, Function<T, U> function);
	
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
