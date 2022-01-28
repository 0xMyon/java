package expr;

import lang.Language;

public class Itteration<T> extends Expression<T> {

	private final Expression<T> base;
	
	static <T> Expression<T> of(Expression<T> that) {
		if (that instanceof Itteration) {
			return that;
		} else {
			return new Itteration<>(that);
		}
	}
	
	private Itteration(Expression<T> base) {
		this.base = base;
	}
	
	@Override
	public <THAT extends Language<THAT, T>> THAT convert(Language.Factory<THAT, T> factory) {
		return base.convert(factory).iterate();
	}
	
	public String toString() {
		return base.toString()+"+";
	}
	
}
