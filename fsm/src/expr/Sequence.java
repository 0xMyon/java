package expr;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lang.Container;
import lang.Language;

public class Sequence<T, TYPE extends Container<TYPE,T>> extends Composite<T,TYPE> {

	public static <T, TYPE extends Container<TYPE,T>> Expression<T,TYPE> of(Container.Factory<TYPE, T> factory, Stream<Expression<T,TYPE>> elements) {
		return Composite.of(Sequence.class, elements, Sequence::new, Collectors.toList(), factory);
	}
	
	@SafeVarargs
	public static <T, TYPE extends Container<TYPE,T>> Expression<T,TYPE> of(Container.Factory<TYPE, T> factory, Expression<T,TYPE>... elements) {
		return of(factory, Stream.of(elements));
	}
	
	/*
	public static <T> Expression<T> of(Expression<T>... elements) {
		return Composite.of(Sequence.class, new LinkedList<Expression<T>>(), Sequence::new, elements);
	}
	*/
	
	private Sequence(Container.Factory<TYPE, T> factory, Collection<Expression<T,TYPE>> list) {
		super(factory, list);
	}
	
	@Override
	public <THAT extends Language<THAT, U>, U, FACTORY extends Language.Factory<THAT,U>> 
	THAT convert(final FACTORY factory, final Function<T, U> function) {
		return factory.sequence(elements().stream().map(x -> x.convert(factory, function)));
	}

	
	
	public String toString() {
		return "("+elements().stream().map(Object::toString).reduce((a,b) -> a+", "+b).orElse("")+")";
	}
	
	@Override
	public <R> R accept(Visitor<T, TYPE, R> visitor) {
		return visitor.handle(this);
	}
	
}
