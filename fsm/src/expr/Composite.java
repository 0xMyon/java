package expr;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lang.Container;
import lang.Language;

public abstract class Composite<T, TYPE extends Container<TYPE,T>> extends Expression<T,TYPE> {

	private final Collection<Expression<T,TYPE>> elements;
	
	Container.Factory<TYPE,T> underlying_factory() {
		return factory;
	}
	
	
	protected Composite(Container.Factory<TYPE, T> factory, Collection<Expression<T,TYPE>> elements) {
		this.elements = elements;
		this.factory = factory;
	}
	
	public Collection<Expression<T,TYPE>> elements() {
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
	
	static <T, TYPE extends Container<TYPE,T>> Expression<T,TYPE> of(@SuppressWarnings("rawtypes") 
		Class<? extends Composite> clazz, 
		Stream<Expression<T,TYPE>> stream, 
		BiFunction<Container.Factory<TYPE, T>, Collection<Expression<T,TYPE>>, Expression<T,TYPE>> f, 
		Collector<Expression<T,TYPE>, ?, ? extends Collection<Expression<T,TYPE>>> collector,
		Container.Factory<TYPE, T> factory
	) {
		return stream.map(x -> {
			if (clazz.isInstance(x)) {
				return ((Composite<T,TYPE>)x).elements().stream();
			} else {
				return Stream.of(x);
			}
		}).reduce(Stream.of(), Stream::concat).collect(Collectors.collectingAndThen(collector, 
			list -> list.size() == 1 ? list.iterator().next() : f.apply(factory, list)
		));
	}

	
	private final Container.Factory<TYPE, T> factory;
	
	@Override
	public Language.Factory<Expression<T, TYPE>, T> factory() {
		return new Factory<>(factory);
	}
	
}
