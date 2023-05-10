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
public interface Language<THIS extends Language<THIS, T, TYPE>, T, TYPE extends Type<TYPE,T>> extends Type<THIS, List<T>>, Sequence<THIS, T> {

	@Override
	Factory<THIS, T, TYPE> factory();

	/**
	 * @return the iterated {@link Language} {@code this+}
	 */
	THIS iterate();

	/**
	 * @return the optional {@link Language} {@code this?}
	 */
	THIS optional();


	/**
	 * @return parallel {@link Language} {@code this || that}
	 */
	THIS parallel(THIS that);

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

	default boolean contains(final Stream<T> s) {
		return contains(s.collect(Collectors.toList()));
	}


	@Override
	default boolean startsWith(final THIS that) {
		return that.concat(factory().universe()).containsAll(THIS());
	}

	@Override
	default boolean endsWith(final THIS that) {
		return factory().universe().concat(that).containsAll(THIS());
	}

	@Override
	default boolean isEnclosed(final THIS that) {
		return factory().universe().concat(that).concat(factory().universe()).containsAll(THIS());
	}


	interface Factory<THIS extends Language<THIS, T, TYPE>, T, TYPE extends Type<TYPE,T>> extends Type.Factory<THIS, List<T>>, Sequence.Factory<THIS, T> {

		@Override
		public default THIS summand(final List<T> that) {
			return sequence(that.stream().map(this::factor));
		}

		@SuppressWarnings("unchecked")
		default THIS parallel(final THIS... that) {
			return parallel(Stream.of(that));
		}

		default THIS parallel(final Stream<THIS> stream) {
			return stream.reduce(epsilon(), Language::parallel);
		}
		
		public THIS letter(TYPE type);
		
		public Type.Factory<TYPE, T> alphabet();
		
	}

	/**
	 * @param <THAT> target implementation of {@link Language}
	 * @param <U> underlying type of {@link Target} {@link Language}
	 * @param factory of target {@link Language}
	 * @return converted object in target {@link Language}
	 */
	<THAT extends Language<THAT, U, TYPE2>, U, TYPE2 extends Type<TYPE2,U>, FACTORY extends Language.Factory<THAT, U, TYPE2>> 
	THAT convert(final FACTORY factory, final Function<TYPE,TYPE2> FUNCTION);
	
	default <THAT extends Language<THAT, U, TYPE2>, U, TYPE2 extends Type<TYPE2,U>, FACTORY extends Language.Factory<THAT, U, TYPE2>> 
	THAT convertX(final FACTORY factory, final Function<TYPE,TYPE2> FUNCTION) {
		return convert(factory, FUNCTION);
	}
	
	default <THAT extends Language<THAT, T, TYPE2>, TYPE2 extends Type<TYPE2,T>, FACTORY extends Language.Factory<THAT, T, TYPE2>> 
	THAT convert(final FACTORY factory) {
		return convertX(factory, x -> x.convert(factory.alphabet()));
	}
	
	
	@Override
	default <THAT extends Type<THAT, U>, U, FACTORY extends Type.Factory<THAT, U>> 
	THAT convert(final FACTORY factory, final Function<List<T>, U> function) {
		throw new UnsupportedOperationException();
	}
	



}