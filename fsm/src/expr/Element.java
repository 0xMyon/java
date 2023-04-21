package expr;

import java.util.List;
import java.util.function.Function;

import lang.Container;
import lang.Language;

public class Element<T, TYPE extends Container<TYPE,T>> extends Expression<T,TYPE> {

	private final TYPE element;

	private Element(final TYPE that) {
		this.element = that;
	}

	public TYPE get() {
		return this.element;
	}

	public static <T, TYPE extends Container<TYPE,T>> Expression<T,TYPE> of(final TYPE element) {
		return new Element<T,TYPE>(element);
	}

	@Override
	public <THAT extends Language<THAT, U>, U, FACTORY extends Language.Factory<THAT,U>> THAT convert(final FACTORY factory, final Function<T, U> function) {
		return element.convert(factory, function.andThen(List::of));
	}

	/*
	@Override
	public <THAT extends Type<THAT, U>, U> THAT convertType(final Type.Factory<THAT, U> factory, final Function<List<T>, U> function) {
		return factory.summand(function.apply(List.of(element)));
	}

	@Override
	public <THAT extends lang.Set<THAT, U>, U> THAT convertSet(final lang.Set.Factory<THAT, U> factory, final Function<List<T>, U> function) {
		return factory.summand(function.apply(List.of(element)));
	}
	*/

	@Override
	public String toString() {
		return element.toString();
	}

	@Override
	public <R> R accept(final Visitor<T, TYPE, R> visitor) {
		return visitor.handle(this);
	}
	
	@Override
	public Language.Factory<Expression<T, TYPE>, T> factory() {
		return new Factory<>(element.factory());
	}
	
	Container.Factory<TYPE,T> underlying_factory() {
		return element.factory();
	}

}
