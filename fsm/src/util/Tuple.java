package util;

import java.util.Objects;
import java.util.function.BiConsumer;

public class Tuple<A,B> {

	public final A left;
	public final B right;

	public static <A,B> Tuple<A,B> of(final A a, final B b) {
		return new Tuple<>(a,b);
	}


	protected Tuple(final A a, final B b) {
		this.left = a;
		this.right = b;
	}

	@Override
	public String toString() {
		return left+"."+right;
	}

	@Override
	public int hashCode() {
		return Objects.hash(left, right);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Tuple) {
			final Tuple<?,?> that = (Tuple<?,?>) obj;
			return Objects.equals(this.left, that.left) && Objects.equals(this.right, that.right);
		}
		return false;
	}

	public void apply(final BiConsumer<A, B> f) {
		f.accept(left, right);
	}

	public static <A,B> Tuple<A,B> left(final A a) {
		return of(a, null);
	}
	public static <A,B> Tuple<A,B> right(final B b) {
		return of(null, b);
	}

}
