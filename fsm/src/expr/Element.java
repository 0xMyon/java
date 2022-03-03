package expr;

import java.util.List;
import java.util.function.Function;

import lang.Language;
import lang.Type;

public class Element<T> extends Expression<T> {

	private final T element;
	
	private Element(T that) {
		this.element = that;
	}
	
	
	public static <T> Expression<T> of(T element) {
		return new Element<T>(element);
	}
	
	@Override
	public <U, THAT extends Language<THAT, U>> THAT convertLanguage(Language.Factory<THAT, U> factory, Function<T, U> function) {
		return factory.factor(function.apply(element));
	}
	
	@Override
	public <U, THAT extends Type<THAT, U>> THAT convertType(Type.Factory<THAT, U> factory, Function<List<T>, U> function) {
		return factory.summand(function.apply(List.of(element)));
	}
	
	@Override
	public <U, THAT extends lang.Set<THAT, U>> THAT convertSet(lang.Set.Factory<THAT, U> factory, Function<List<T>, U> function) {
		return factory.summand(function.apply(List.of(element)));
	}
	
	public String toString() {
		return element.toString();
	}
	
	@Override
	public <R> R accept(Visitor<T, R> visitor) {
		return visitor.handle(this);
	}


}
