package lang;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link Type} of {@link Sequence}
 * @author 0xMyon
 *
 * @param <THIS>
 * @param <T>
 */
public interface Language<THIS extends Language<THIS, T>, T> extends Type<THIS, List<T>>, Sequence<THIS, T> {

	
	Factory<THIS, T> factory();

	/**
	 * @return the iterated {@link Language} this+
	 */
	THIS iterate();

	/**
	 * @return the optional {@link Language} this?
	 */
	THIS optional();



	/**
	 * @return the kleene-hull {@link Language} this*
	 */
	default THIS star() {
		return iterate().optional();
	}
	
	
	/**
	 * @return true, if {@link Factory#epsilon()} is contained
	 * @see Type#contains(Object)
	 */
	default boolean hasEpsilon() {
		return contains(List.of());
	}
	
	default boolean contains(Stream<T> s) {
		return contains(s.collect(Collectors.toList()));
	}
	
	interface Factory<THIS extends Language<THIS, T>, T> extends Type.Factory<THIS, List<T>>, Sequence.Factory<THIS, T> {
		
		default THIS apply(Language<?, T> that) {
			return that.convert(this);
		}
		
		@Override
		public default THIS summand(List<T> that) {
			return sequence(that.stream().map(this::factor));
		}
		
	}
	
	<THAT extends Language<THAT, T>> THAT convert(Language.Factory<THAT, T> factory);
	
	
	default <THAT extends Language<THAT, List<T>>> THAT toLanguage(Language.Factory<THAT, List<T>> factory) {
		return null;
	}
	
	
}