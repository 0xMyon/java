package expr;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

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
	public <U, THAT extends Language<THAT, U>> THAT convertLanguage(Language.Factory<THAT, U> factory, Function<T, U> function) {
		return factory.sequence(elements.stream().map(x -> x.convertLanguage(factory, function)));
	}
	
	public String toString() {
		return "("+elements.stream().map(Object::toString).reduce((a,b) -> a+", "+b).orElse("")+")";
	}
	
}
