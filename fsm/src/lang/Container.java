package lang;

import java.util.Random;
import java.util.function.Function;

public interface Container<THIS extends Container<THIS, T>, T> extends Identifiable<THIS> {

	boolean contains(T that);
	
	T random(Random random);
	
	/**
	 * @param <THAT> target type of the {@link Container}
	 * @param <U> element target type of the {@link Container}
	 * @param factory that is used for conversion between sets
	 * @param function that is used for conversion between elements
	 * @return an isomorphic {@link Container} of a different type
	 * @throws UnsupportedOperationException, if the underlying {@link Container} is not convertible
	 */
	<THAT extends Container<THAT, U>, U, FACTORY extends Factory<THAT, U>> THAT convert(final FACTORY factory, final Function<T, U> function) throws UnsupportedOperationException;

	default <THAT extends Container<THAT, T>, FACTORY extends Factory<THAT, T>> THAT convert(final FACTORY factory) {
		return convert(factory, Function.identity());
	}
	
	Factory<THIS, T> factory();
	
	public interface Factory<THIS extends Container<THIS, T>, T> {

		/**
		 * @param that
		 * @return a {@link Set} from underlying elements
		 */
		THIS summand(T that);
		
	}
	
}
