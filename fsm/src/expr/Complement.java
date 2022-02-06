package expr;

import java.util.function.Function;

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
	public <U, THAT extends Language<THAT, U>> THAT convertLanguage(Language.Factory<THAT, U> factory, Function<T, U> function) {
		return complement.convertLanguage(factory, function).complement();
	}
	
	public String toString() {
		return "!["+complement.toString()+"]";
	}

	


	
}
