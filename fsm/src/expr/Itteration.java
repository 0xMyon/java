package expr;

import java.util.function.Function;

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
	public <U, THAT extends Language<THAT, U>> THAT convertLanguage(Language.Factory<THAT, U> factory, Function<T, U> function) {
		return base.convertLanguage(factory, function).iterate();
	}
	
	public String toString() {
		return base.toString()+"+";
	}
	
	@Override
	public <R> R accept(Visitor<T, R> visitor) {
		return visitor.handle(this);
	}
	
}
