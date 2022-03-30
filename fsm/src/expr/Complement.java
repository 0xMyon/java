package expr;

import java.util.List;
import java.util.function.Function;

import lang.Language;
import lang.Type;

public class Complement<T> extends Expression<T> {

	static <T> Expression<T> of(final Expression<T> that) {
		if (that instanceof Complement) {
			return ((Complement<T>)that).complement;
		} else {
			return new Complement<>(that);
		}
	}

	private Complement(final Expression<T> complement) {
		this.complement = complement;
	}

	private final Expression<T> complement;

	@Override
	public <THAT extends Language<THAT, U>, U> THAT convertLanguage(final Language.Factory<THAT, U> factory, final Function<T, U> function) {
		return complement.convertLanguage(factory, function).complement();
	}

	@Override
	public <THAT extends Type<THAT, U>, U> THAT convertType(final Type.Factory<THAT, U> factory, final Function<List<T>, U> function) {
		return complement.convertType(factory, function).complement();
	}

	@Override
	public String toString() {
		return "!["+complement.toString()+"]";
	}

	@Override
	public <R> R accept(final Visitor<T, R> visitor) {
		return visitor.handle(this);
	}

}
