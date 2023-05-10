package expr;

import java.util.function.Function;

import lang.Language;
import lang.Type;

public class Reverse<T, TYPE extends Type<TYPE,T>> extends Expression<T,TYPE> {

	private final Expression<T,TYPE> reverse;
	
	private Reverse(Expression<T,TYPE> reverse) {
		this.reverse = reverse;
	}
	
	public static <T, TYPE extends Type<TYPE,T>> Expression<T,TYPE> of(Expression<T,TYPE> that) {
		if (that instanceof Reverse) 
			return ((Reverse<T,TYPE>) that).reverse();
		else
			return new Reverse<T,TYPE>(that);
	}
	
	public Expression<T,TYPE> reverse() {
		return reverse;
	}

	@Override
	public <THAT extends Language<THAT, U, TYPE2>, U, TYPE2 extends Type<TYPE2, U>, FACTORY extends Language.Factory<THAT,U,TYPE2>> 
	THAT convert(final FACTORY factory, final Function<TYPE,TYPE2> FUNCTION) {
		return reverse.convert(factory, FUNCTION).reverse();
	}
	
	@Override
	public <R> R accept(Visitor<T,TYPE, R> visitor) {
		return visitor.handle(this);
	}
	
	@Override
	public Language.Factory<Expression<T, TYPE>, T, TYPE> factory() {
		return reverse.factory();
	}

}
