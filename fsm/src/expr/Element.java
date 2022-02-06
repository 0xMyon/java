package expr;

import java.util.function.Function;

import lang.Language;

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
	
	public String toString() {
		return element.toString();
	}


}
