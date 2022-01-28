package expr;

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
	public <THAT extends Language<THAT, T>> THAT convert(lang.Language.Factory<THAT, T> factory) {
		return reverse.convert(factory).reverse();
	}

}
