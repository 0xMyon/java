package expr;

import java.util.List;
import java.util.function.Function;

import lang.Language;
import lang.Type;

public class Complement<T, TYPE extends Type<TYPE,T>> extends Expression<T,TYPE> {

	static <T, TYPE extends Type<TYPE,T>> Expression<T,TYPE> of(final Expression<T,TYPE> that) {
		if (that instanceof Complement) {
			return ((Complement<T,TYPE>)that).complement;
		} else {
			return new Complement<>(that);
		}
	}

	private Complement(final Expression<T,TYPE> complement) {
		this.complement = complement;
	}

	private final Expression<T,TYPE> complement;

	
	@Override
	public <THAT extends Language<THAT, U, ULIST, TYPE2>, U, ULIST, TYPE2 extends Type<TYPE2, U>, FACTORY extends Language.Factory<THAT,U,ULIST,TYPE2>> 
	THAT convert(final FACTORY factory, final Function<TYPE,TYPE2> FUNCTION) {
		return complement.convert(factory, FUNCTION).complement();
	}

	@Override
	public <THAT extends Type<THAT, U>, U, FACTORY extends Type.Factory<THAT, U>> 
	THAT convert(final FACTORY factory, final Function<List<T>, U> function) {
		return complement.convert(factory, function).complement();
	}
	

	@Override
	public String toString() {
		return "!["+complement.toString()+"]";
	}

	@Override
	public <R> R accept(final Visitor<T, TYPE, R> visitor) {
		return visitor.handle(this);
	}

	@Override
	public Language.Naive.Factory<Expression<T, TYPE>, T, TYPE> factory() {
		return complement.factory();
	}

}
