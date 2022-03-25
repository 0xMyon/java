package util;

public interface Choice<A,B> {

	static <A,B> Choice<A,B> A(final A a) {
		return new Choice<>() {
			@Override
			public <R> R accept(final Visitor<A, B, R> visitor) {
				return visitor.handleA(a);
			}

			@Override
			public A getA() {
				return a;
			}

			@Override
			public B getB() {
				throw new Error();
			}
		};
	}

	static <A,B> Choice<A,B> B(final B b) {
		return new Choice<>() {
			@Override
			public <R> R accept(final Visitor<A, B, R> visitor) {
				return visitor.handleB(b);
			}
			@Override
			public A getA() {
				throw new Error();
			}

			@Override
			public B getB() {
				return b;
			}
		};
	}

	A getA();

	B getB();

	interface Visitor<A,B,R> {
		R handleA(A a);
		R handleB(B a);
	}

	<R> R accept(Visitor<A, B, R> visitor);

}
