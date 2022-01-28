package expr;

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
	public <THAT extends Language<THAT, T>> THAT convert(Language.Factory<THAT, T> factory) {
		return factory.factor(element);
	}
	
	public String toString() {
		return element.toString();
	}


}
