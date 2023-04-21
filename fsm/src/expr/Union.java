package expr;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lang.Container;
import lang.Language;
import lang.Type;

public class Union<T, TYPE extends Container<TYPE,T>> extends Composite<T,TYPE> {

	@SafeVarargs
	public static <T, TYPE extends Container<TYPE,T>> Expression<T,TYPE> of(Container.Factory<TYPE, T> factory, final Expression<T,TYPE>... elements) {
		return of(factory, Stream.of(elements));
	}

	public static <T, TYPE extends Container<TYPE,T>> Expression<T,TYPE> of(Container.Factory<TYPE, T> factory, final Stream<Expression<T,TYPE>> elements) {
		return Composite.of(Union.class, elements, Union::new, Collectors.toSet(), factory);
	}


	private Union(Container.Factory<TYPE, T> factory, final Collection<Expression<T,TYPE>> set) {
		super(factory, set);
	}

	@Override
	public boolean isEmpty() {
		return elements().isEmpty();
	}


	@Override
	public <THAT extends Language<THAT, U>, U, FACTORY extends Language.Factory<THAT,U>> 
	THAT convert(final FACTORY factory, final Function<T, U> function) {
		return factory.union(elements().stream().map(x -> x.convert(factory, function)));
	}

	@Override
	public <THAT extends Type<THAT, U>, U, FACTORY extends Type.Factory<THAT, U>> 
	THAT convert(final FACTORY factory, final Function<List<T>, U> function) {
		return factory.union(elements().stream().map(x -> x.convert(factory, function)));
	}
	
	@Override
	public <THAT extends lang.Set<THAT, U>, U, FACTORY extends  lang.Set.Factory<THAT, U>> 
	THAT convert(final FACTORY factory, final Function<List<T>, U> function) {
		return factory.union(elements().stream().map(x -> x.convert(factory, function)));
	}
	
	@Override
	public String toString() {
		return "{"+elements().stream().map(Object::toString).reduce((a,b) -> a+", "+b).orElse("")+"}";
	}


	@Override
	public <R> R accept(final Visitor<T, TYPE, R> visitor) {
		return visitor.handle(this);
	}

}
