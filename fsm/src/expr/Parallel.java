package expr;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lang.Language;
import lang.Type;

public class Parallel<T, TYPE extends Type<TYPE,T>> extends Composite<T,TYPE> {

	public static <T, TYPE extends Type<TYPE,T>> 
	Expression<T,TYPE> of(Type.Factory<TYPE, T> factory, Stream<Expression<T,TYPE>> elements) {
		return Composite.of(Parallel.class, elements, Parallel::new, Collectors.toList(), factory);
	}
	
	@SafeVarargs
	public static <T, TYPE extends Type<TYPE,T>> Expression<T,TYPE> of(Type.Factory<TYPE, T> factory, Expression<T,TYPE>... elements) {
		return of(factory, Stream.of(elements));
	}
	
	private Parallel(Type.Factory<TYPE, T> factory, Collection<Expression<T,TYPE>> list) {
		super(factory, list);
	}
	
	@Override
	public <THAT extends Language<THAT, U, ULIST, TYPE2>, U, ULIST, TYPE2 extends Type<TYPE2, U>, FACTORY extends Language.Factory<THAT,U,ULIST,TYPE2>> 
	THAT convert(final FACTORY factory, final Function<TYPE,TYPE2> FUNCTION) {
		return factory.parallel(elements().stream().map(x -> x.convert(factory, FUNCTION)));
	}

	
	@Override
	public <R> R accept(Visitor<T,TYPE, R> visitor) {
		return visitor.handle(this);
	}
	

}
