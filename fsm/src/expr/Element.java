package expr;

import java.util.List;
import java.util.function.Function;

import lang.Language;
import lang.Type;

public class Element<T> extends Expression<T> {

	private final T element;

	private Element(final T that) {
		this.element = that;
	}

	public T get() {
		return this.element;
	}

	public static <T> Expression<T> of(final T element) {
		return new Element<T>(element);
	}

	@Override
	public <THAT extends Language<THAT, U>, U> THAT convertLanguage(final Language.Factory<THAT, U> factory, final Function<T, U> function) {
		return factory.factor(function.apply(element));
	}

	@Override
	public <THAT extends Type<THAT, U>, U> THAT convertType(final Type.Factory<THAT, U> factory, final Function<List<T>, U> function) {
		return factory.summand(function.apply(List.of(element)));
	}

	@Override
	public <THAT extends lang.Set<THAT, U>, U> THAT convertSet(final lang.Set.Factory<THAT, U> factory, final Function<List<T>, U> function) {
		return factory.summand(function.apply(List.of(element)));
	}

	@Override
	public String toString() {
		return element.toString();
	}

	@Override
	public <R> R accept(final Visitor<T, R> visitor) {
		return visitor.handle(this);
	}

}
