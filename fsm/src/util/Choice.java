package util;

public interface Choice<A,B> {

	static <A,B> Choice<A,B> A(A a) {
		return new Choice<>() {
			@Override
			public <R> R accept(Visitor<A, B, R> visitor) {
				return visitor.handleA(a);
			}
		};
	}
	
	static <A,B> Choice<A,B> B(B b) {
		return new Choice<>() {
			@Override
			public <R> R accept(Visitor<A, B, R> visitor) {
				return visitor.handleB(b);
			}
		};
	}
	
	interface Visitor<A,B,R> {
		R handleA(A a);
		R handleB(B a);
	}
	
	<R> R accept(Visitor<A, B, R> visitor);
	
}
