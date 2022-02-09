package lang;

import java.util.List;
import java.util.function.Function;
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

	@Override
	Factory<THIS, T> factory();

	/**
	 * @return the iterated {@link Language} {@code this+}
	 */
	THIS iterate();

	/**
	 * @return the optional {@link Language} {@code this?}
	 */
	THIS optional();



	/**
	 * @return the kleene-hull {@link Language} {@code this*}
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
				
		@Override
		public default THIS summand(List<T> that) {
			return sequence(that.stream().map(this::factor));
		}
		
	}
	
	/**
	 * convert between two {@link Language}s
	 * @param <THAT>
	 * @param factory
	 * @return
	 */
	<U, THAT extends Language<THAT, U>> THAT convertLanguage(Language.Factory<THAT, U> factory, Function<T,U> function);
		
	default <THAT extends Language<THAT, T>> THAT convertLanguage(Language.Factory<THAT, T> factory) {
		return convertLanguage(factory, Function.identity());
	}
	
	
	
	@Override
	default <U, THAT extends Type<THAT, U>> THAT convertType(Type.Factory<THAT, U> factory, Function<List<T>, U> function) {
		throw new UnsupportedOperationException();
	}
	
	
	
}