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
public interface Type<THIS extends Type<THIS, T>, T> {

	/**
	 * @return the union {@link Type} {@code this | that}
	 */
	THIS unite(final THIS that);
	
	/**
	 * @return the complement {@link Type} {@code ~this}
	 */
	THIS complement();
	
	
	/**
	 * @return the intersection {@link Type} {@code this & that}
	 */
	default THIS intersect(final THIS that) {
		return this.complement().unite(that.complement()).complement();
	}
	
	/**
	 * @return the difference {@link Type} {@code this - that}
	 */
	default THIS minus(final THIS that) {
		return this.complement().unite(that).complement();
	}
	
	/**
	 * @param that <T> to be tested
	 * @return true, if element is contained
	 */
	boolean contains(final T that);
	
	/**
	 * @param that {@link Type} to be tested
	 * @return true, if that is contained
	 */
	boolean containsAll(final THIS that);
	
	/**
	 * @param that
	 * @return true, if both types are equal
	 */
	default boolean isEqual(final THIS that) {
		return this.containsAll(that) && that.containsAll(THIS());
	}
	
	/**
	 * @return true, if the type is empty
	 * @see Factory#empty()
	 */
	boolean isEmpty();
	
	/**
	 * @return true, if the {@link Type} contains a finite set of elements
	 */
	boolean isFinite();
	
	/**
	 * @return this
	 */
	THIS THIS();
	
	/**
	 * @return {@link Factory} associated with this {@link Type}
	 */
	Factory<THIS, T> factory();
	
	
	
	interface Factory<THIS extends Type<THIS, T>, T> {
		
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
			return stream.reduce(empty(), Type::unite);
		}
		
		/**
		 * @return the {@link Type} that contains all other types
		 */
		default THIS universe() {
			return empty().complement();
		}

		/**
		 * @param that
		 * @return a {@link Type} from underlying elements
		 */
		THIS summand(T that);
	}

	
	<U, THAT extends Type<THAT, U>> THAT convertType(Type.Factory<THAT, U> factory, Function<T,U> function);

	default <THAT extends Type<THAT, T>> THAT convertType(Type.Factory<THAT, T> factory) {
		return convertType(factory, Function.identity());
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
			.reduce(Stream.of(result.stream().reduce(A, (x,y)->x.minus(y))), Stream::concat)
			.filter(X -> !X.isEmpty()).collect(Collectors.toSet());
		}
		return result;
	}
	
	public static <T,TYPE extends Type<TYPE,T>> Set<TYPE> partition(final Stream<TYPE> types) {
		return partition(types.collect(Collectors.toSet()));
	}
	
}
