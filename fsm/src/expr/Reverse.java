package expr;

import java.util.function.Function;

import lang.Language;

public class Reverse<T> extends Expression<T> {

	private final Expression<T> reverse;
	
	private Reverse(Expression<T> reverse) {
		this.reverse = reverse;
	}
	
	public static <T> Expression<T> of(Expression<T> that) {
		if (that instanceof Reverse) 
			return ((Reverse<T>) that).reverse();
		else
			return new Reverse<T>(that);
	}
	
	public Expression<T> reverse() {
		return reverse;
	}
	
	@Override
	public <U, THAT extends Language<THAT, U>> THAT convertLanguage(Language.Factory<THAT, U> factory, Function<T, U> function) {
		return reverse.convertLanguage(factory, function).reverse();
	}

}
