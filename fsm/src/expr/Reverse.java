package expr;

import java.util.function.Function;

import lang.Container;
import lang.Language;

public class Reverse<T, TYPE extends Container<TYPE,T>> extends Expression<T,TYPE> {

	private final Expression<T,TYPE> reverse;
	
	private Reverse(Expression<T,TYPE> reverse) {
		this.reverse = reverse;
	}
	
	public static <T, TYPE extends Container<TYPE,T>> Expression<T,TYPE> of(Expression<T,TYPE> that) {
		if (that instanceof Reverse) 
			return ((Reverse<T,TYPE>) that).reverse();
		else
			return new Reverse<T,TYPE>(that);
	}
	
	public Expression<T,TYPE> reverse() {
		return reverse;
	}

	@Override
	public <THAT extends Language<THAT, U>, U, FACTORY extends Language.Factory<THAT,U>> 
	THAT convert(final FACTORY factory, final Function<T, U> function) {
		return reverse.convert(factory, function).reverse();
	}
	
	@Override
	public <R> R accept(Visitor<T,TYPE, R> visitor) {
		return visitor.handle(this);
	}
	
	@Override
	public Language.Factory<Expression<T, TYPE>, T> factory() {
		return reverse.factory();
	}
	
	Container.Factory<TYPE,T> underlying_factory() {
		return reverse.underlying_factory();
	}

}
