package lang;


import java.util.stream.Stream;

/**
 * Representation of a List
 *
 * @author 0xMyon
 *
 * @param <THIS> underlying implementation type
 * @param <T>
 */
public interface Sequence<THIS extends Sequence<THIS, T>, T> {

	/**
	 * @return this
	 */
	THIS THIS();

	/**
	 * @param that
	 * @return the concatenated {@link Sequence} this.that
	 */
	THIS concat(final THIS that);


	/**
	 * {@code this.reverse().reverse() == this}
	 * @return the reverse {@link Sequence} this^R
	 */
	THIS reverse();


	default THIS power(final int exponent) {
		if (exponent == 0) {
			return factory().epsilon();
		} else if (exponent < 0) {
			return reverse().power(-exponent);
		} else if (exponent == 1) {
			return THIS();
		} else if (exponent % 2 == 0) {
			return power(exponent/2).square();
		} else {
			return power(exponent-1).concat(THIS());
		}
	}

	/**
	 * Self concatenation {@code this.concat(this)}
	 * @return the squared {@link Sequence}
	 * @see #concat(Sequence)
	 */
	default THIS square() {
		return concat(THIS());
	}

	/**
	 * Check for empty sequence
	 * @return true, if sequence is empty
	 * @see Factory#epsilon()
	 */
	boolean isEpsilon();

	/**
	 * @return {@link Factory} associated with this {@link Sequence}
	 */
	Factory<THIS, T> factory();


	/**
	 * @param that sub-sequence
	 * @return true, if all elements of {@code that} are matching at the beginning of {@code this}
	 */
	boolean startsWith(final THIS that);

	/**
	 * @param that sub-sequence
	 * @return true, if all elements of {@code that} are matching at the end of {@code this}
	 */
	boolean endsWith(final THIS that);

	/**
	 * @param that sub-sequence
	 * @return true, if all elements of {@code that} are matching anywhere in {@code this}
	 */
	boolean isEnclosed(final THIS that);

	//T head();

	//THIS tail();


	interface Factory<THIS extends Sequence<THIS, T>, T> {

		/**
		 * @return the empty {@link Sequence}
		 * @see Sequence#isEpsilon()
		 */
		THIS epsilon();

		/**
		 * @param stream of elements
		 * @return {@link Sequence#concat(Sequence)} of stream with {@link #epsilon()} as neutral element
		 */
		default THIS sequence(final Stream<THIS> stream) {
			return stream.reduce(epsilon(), Sequence::concat);
		}

		/**
		 * @param that
		 * @return
		 */
		@SuppressWarnings("unchecked")
		default THIS sequence(final THIS... that) {
			return sequence(Stream.of(that));
		}


		THIS factor(final T that);

	}


}
