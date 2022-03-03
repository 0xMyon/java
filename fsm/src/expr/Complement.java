package expr;

import java.util.List;
import java.util.function.Function;

import lang.Language;
import lang.Type;

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
	public <THAT extends Language<THAT, U>, U> THAT convertLanguage(Language.Factory<THAT, U> factory, Function<T, U> function) {
		return complement.convertLanguage(factory, function).complement();
	}
	
	@Override
	public <THAT extends Type<THAT, U>, U> THAT convertType(Type.Factory<THAT, U> factory, Function<List<T>, U> function) {
		return complement.convertType(factory, function).complement();
	}
	
	public String toString() {
		return "!["+complement.toString()+"]";
	}

	@Override
	public <R> R accept(Visitor<T, R> visitor) {
		return visitor.handle(this);
	}

	


	
}
