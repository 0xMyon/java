package expr;

import java.util.HashSet;
import java.util.Set;

import lang.Language;

public class Union<T> extends Expression<T> {

	private final Set<Expression<T>> elements;
	
	@SafeVarargs
	static <T> Expression<T> of(Expression<T>... elements) {
		Set<Expression<T>> set = new HashSet<>();
		for(Expression<T> e : elements) {
			if (e instanceof Union) {
				set.addAll(((Union<T>)e).elements);
			} else {
				set.add(e);
			}
		}
		if (set.size() == 1)
			return set.iterator().next();
		else 
			return new Union<>(set);
	}
	

	private Union(Set<Expression<T>> set) {
		this.elements = set;
	}
	
	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	@Override
	public <THAT extends Language<THAT, T>> THAT convert(Language.Factory<THAT, T> factory) {
		return factory.union(elements.stream().map(factory::apply));
	}
	
	public String toString() {
		return "{"+elements.stream().map(Object::toString).reduce((a,b) -> a+", "+b).orElse("")+"}";
	}
	
}
