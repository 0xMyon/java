package expr;

import java.util.List;
import java.util.function.Function;

import lang.Language;
import lang.Type;

public class Element<T, TYPE extends Type<TYPE,T>> extends Expression<T,TYPE> {

	private final TYPE element;

	private Element(final TYPE that) {
		this.element = that;
	}

	public TYPE get() {
		return this.element;
	}

	public static <T, TYPE extends Type<TYPE,T>> Expression<T,TYPE> of(final TYPE element) {
		return new Element<T,TYPE>(element);
	}

	@Override
	public <THAT extends Language<THAT, U, TYPE2>, U, TYPE2 extends Type<TYPE2, U>, FACTORY extends Language.Factory<THAT,U,TYPE2>> 
	THAT convert(final FACTORY factory, final Function<TYPE,TYPE2> FUNCTION) {
		var r =factory.letter(FUNCTION.apply(element));
		System.out.println(toString()+" -> "+r.toString());
		return r;
	}
	
	@Override
	public <THAT extends Type<THAT, U>, U, FACTORY extends Type.Factory<THAT,U>> 
	THAT convert(final FACTORY factory, final Function<List<T>, U> function) {
		return element.convert(factory, function.compose(List::of));
	}

	@Override
	public <THAT extends lang.Set<THAT, U>, U, FACTORY extends lang.Set.Factory<THAT, U>> 
	THAT convert(FACTORY factory,  Function<List<T>, U> function) throws UnsupportedOperationException {
		return element.convert(factory, function.compose(List::of));
	}
	

	@Override
	public String toString() {
		return element.toString();
	}

	@Override
	public <R> R accept(final Visitor<T, TYPE, R> visitor) {
		return visitor.handle(this);
	}
	
	@Override
	public Language.Factory<Expression<T, TYPE>, T, TYPE> factory() {
		return new Factory<>(element.factory());
	}

}
