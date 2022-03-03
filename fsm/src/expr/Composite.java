package expr;

import java.util.Collection;
import java.util.function.Function;

public abstract class Composite<T> extends Expression<T> {

	private final Collection<Expression<T>> elements;
	
	protected Composite(Collection<Expression<T>> elements) {
		this.elements = elements;
	}
	
	protected Collection<Expression<T>> elements() {
		return elements;
	}
	
	static <T, THIS extends Composite<T>, C extends Collection<Expression<T>>> 
	Expression<T> of(Class<THIS> clazz, C collection, Function<C, THIS> constructior, Expression<T>[] elements) {
		for(Expression<T> element : elements) {
			if (clazz.isInstance(element)) {
				collection.addAll(clazz.cast(element).elements());
			} else {
				collection.add(element);
			}
		}
		switch (collection.size()) {
		case 1: return collection.iterator().next();
		default: return constructior.apply(collection);
		}
	}

}
