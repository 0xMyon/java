package expr;

import java.util.function.Function;

import lang.Container;
import lang.Language;

public class Iteration<T, TYPE extends Container<TYPE,T>> extends Expression<T,TYPE> {

	private final Expression<T,TYPE> base;
	
	static <T, TYPE extends Container<TYPE,T>> Expression<T,TYPE> of(Expression<T,TYPE> that) {
		if (that instanceof Iteration) {
			return that;
		} else {
			return new Iteration<>(that);
		}
	}
	
	private Iteration(Expression<T,TYPE> base) {
		this.base = base;
	}
	
	@Override
	public <THAT extends Language<THAT, U>, U, FACTORY extends Language.Factory<THAT,U>> 
	THAT convert(final FACTORY factory, final Function<T, U> function) {
		return base.convert(factory, function).iterate();
	}

	
	
	public String toString() {
		return base.toString()+"+";
	}
	
	@Override
	public <R> R accept(Visitor<T,TYPE, R> visitor) {
		return visitor.handle(this);
	}
	
	@Override
	public Language.Factory<Expression<T, TYPE>, T> factory() {
		return base.factory();
	}
	
	Container.Factory<TYPE,T> underlying_factory() {
		return base.underlying_factory();
	}
	
}
