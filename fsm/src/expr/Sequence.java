package expr;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lang.Language;
import lang.Type;

public class Sequence<T, TYPE extends Type<TYPE,T>> extends Composite<T,TYPE> {

	public static <T, TYPE extends Type<TYPE,T>> Expression<T,TYPE> of(Type.Factory<TYPE, T> factory, Stream<Expression<T,TYPE>> elements) {
		return Composite.of(Sequence.class, elements, Sequence::new, Collectors.toList(), factory);
	}
	
	@SafeVarargs
	public static <T, TYPE extends Type<TYPE,T>> Expression<T,TYPE> of(Type.Factory<TYPE, T> factory, Expression<T,TYPE>... elements) {
		return of(factory, Stream.of(elements));
	}
	
	/*
	public static <T> Expression<T> of(Expression<T>... elements) {
		return Composite.of(Sequence.class, new LinkedList<Expression<T>>(), Sequence::new, elements);
	}
	*/
	
	private Sequence(Type.Factory<TYPE, T> factory, Collection<Expression<T,TYPE>> list) {
		super(factory, list);
	}
	
	@Override
	public <THAT extends Language<THAT, U, ULIST, TYPE2>, U, ULIST, TYPE2 extends Type<TYPE2, U>, FACTORY extends Language.Factory<THAT,U,ULIST,TYPE2>> 
	THAT convert(final FACTORY factory, final Function<TYPE,TYPE2> FUNCTION) {
		var r = factory.sequence(elements().stream().map(x -> x.convert(factory, FUNCTION)));
		System.out.println(toString()+" -> "+r.toString());
		return r;
	}

	
	
	public String toString() {
		return "("+elements().stream().map(Object::toString).reduce((a,b) -> a+", "+b).orElse("")+")";
	}
	
	
	public Expression<T, TYPE> star() {
		return elements().isEmpty() ? this : super.star();
	}
	
	public Expression<T, TYPE> optional() {
		return elements().isEmpty() ? this : super.optional();
	}
	
	public Expression<T, TYPE> iterate() {
		return elements().isEmpty() ? this : super.iterate();
	}
	
	
	@Override
	public <R> R accept(Visitor<T, TYPE, R> visitor) {
		return visitor.handle(this);
	}
	

	@Override
	public Expression<T, TYPE> reverse() {
		List<Expression<T,TYPE>> x = elements().stream().collect(Collectors.toList());
		Collections.reverse(x);
		return of(underlying_factory(), x.stream());
	}

	
}
