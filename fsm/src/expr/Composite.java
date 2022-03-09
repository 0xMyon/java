package expr;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Composite<T> extends Expression<T> {

	private final Collection<Expression<T>> elements;
	
	protected Composite(Collection<Expression<T>> elements) {
		this.elements = elements;
	}
	
	public Collection<Expression<T>> elements() {
		return elements;
	}
	
	/*
	static <T, THIS extends Composite<T>, C extends Collection<Expression<T>>> 
	Expression<T> of(
			Class<THIS> clazz, 
			C collection, 
			Function<C, THIS> constructior, 
			Stream<Expression<T>> elements
	) {
		
		for(Expression<T> element : elements.collect(Collectors.toList())) {
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
	*/
	
	static <T> Expression<T> of(@SuppressWarnings("rawtypes") Class<? extends Composite> clazz, Stream<Expression<T>> stream, Function<Collection<Expression<T>>, Expression<T>> f, Collector<Expression<T>, ?, ? extends Collection<Expression<T>>> collector) {
		return stream.map(x -> {
			if (clazz.isInstance(x)) {
				return ((Composite<T>)x).elements().stream();
			} else {
				return Stream.of(x);
			}
		}).reduce(Stream.of(), Stream::concat).collect(Collectors.collectingAndThen(collector, 
			list -> list.size() == 1 ? list.iterator().next() : f.apply(list)
		));
	}

}
