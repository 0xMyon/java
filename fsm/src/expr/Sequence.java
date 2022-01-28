package expr;

import java.util.LinkedList;
import java.util.List;

import lang.Language;

public class Sequence<T> extends Expression<T> {

	
	@SafeVarargs
	static <T> Expression<T> of(Expression<T>... elements) {
		List<Expression<T>> list = new LinkedList<>();
		for(Expression<T> e : elements) {
			if (e instanceof Sequence) {
				list.addAll(((Sequence<T>)e).elements);
			} else {
				list.add(e);
			}
		}
		if (list.size() == 1)
			return list.get(0);
		else 
			return new Sequence<>(list);
	}
	
	private Sequence(List<Expression<T>> list) {
		elements = list;
	}
	

	private final List<Expression<T>> elements;


	@Override
	public <THAT extends Language<THAT, T>> THAT convert(Language.Factory<THAT, T> factory) {
		return factory.sequence(elements.stream().map(factory::apply));
	}
	
	public String toString() {
		return "("+elements.stream().map(Object::toString).reduce((a,b) -> a+", "+b).orElse("")+")";
	}
	
}
