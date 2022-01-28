package expr;

import lang.Language;

public class Complement<T> extends Expression<T> {

	static <T> Expression<T> of(Expression<T> that) {
		if (that instanceof Complement) {
			return ((Complement<T>)that).complement;
		} else {
			return new Complement<>(that);
		}
	}
	
	private Complement(Expression<T> complement) {
		this.complement = complement;
	}
	
	private final Expression<T> complement;
	
	@Override
	public <THAT extends Language<THAT, T>> THAT convert(Language.Factory<THAT, T> factory) {
		return complement.convert(factory).complement();
	}
	
	public String toString() {
		return "!["+complement.toString()+"]";
	}

	


	
}
