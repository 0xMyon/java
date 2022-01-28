package util;

import java.util.Objects;
import java.util.function.BiConsumer;

public class Tuple<A,B> {

	public final A a;
	public final B b;
	
	public static <A,B> Tuple<A,B> of(A a, B b) {
		return new Tuple<>(a,b);
	}
		
	protected Tuple(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	public String toString() {
		return a+"."+b;
	}
	
	public int hashCode() {
		return Objects.hash(a, b);
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Tuple) {
			Tuple<?,?> that = (Tuple<?,?>) obj;
			return Objects.equals(this.a, that.a) && Objects.equals(this.b, that.b);
		}
		return false;
	}
	
	public void apply(BiConsumer<A, B> f) {
		f.accept(a, b);
	}
	
	public static <A,B> Tuple<A,B> left(A a) {
		return of(a, null);
	}
	public static <A,B> Tuple<A,B> right(B b) {
		return of(null, b);
	}
	
}
