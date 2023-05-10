package expr;

import java.util.function.Function;

import lang.Language;
import lang.Type;

public class Iteration<T, TYPE extends Type<TYPE,T>> extends Expression<T,TYPE> {

	enum Version {
		Optional("?"),
		Itterate("+"),
		Star("*");
		
		private final String str;
		Version(String str) {
			this.str = str;
		}
		
		public String toString() {
			return str;
		}

		Version and(Version that) {
			return this == that ? this : Star;
		}
		
	}
	
	private final Version type;
	
	private final Expression<T,TYPE> base;
	
	static <T, TYPE extends Type<TYPE,T>> Expression<T,TYPE> of(Expression<T,TYPE> that) {
		return of(that, Version.Itterate);
	}
	
	static <T, TYPE extends Type<TYPE,T>> Expression<T,TYPE> of(Expression<T,TYPE> other, Version type) {
		if (other instanceof Iteration) {
			Iteration<T,TYPE> that = (Iteration<T,TYPE>) other;
			return new Iteration<>(that.base, that.type.and(type));
		} else {
			return new Iteration<>(other, type);
		}
	}
	
	private Iteration(Expression<T,TYPE> base, Version type) {
		this.base = base;
		this.type = type;
	}
	
	@Override
	public <THAT extends Language<THAT, U, TYPE2>, U, TYPE2 extends Type<TYPE2, U>, FACTORY extends Language.Factory<THAT,U,TYPE2>> 
	THAT convert(final FACTORY factory, final Function<TYPE,TYPE2> FUNCTION) {
		switch (type) {
			case Optional: return base.convert(factory, FUNCTION).optional();
			case Itterate: return base.convert(factory, FUNCTION).iterate();
			case Star: return base.convert(factory, FUNCTION).star();
		}
		throw new Error();
	}

	
	
	public String toString() {
		return base.toString()+type.toString();
	}
	
	@Override
	public <R> R accept(Visitor<T,TYPE, R> visitor) {
		return visitor.handle(this);
	}
	
	@Override
	public Language.Factory<Expression<T, TYPE>, T, TYPE> factory() {
		return base.factory();
	}
	
}
