package expr;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lang.Language;

public class Sequence<T> extends Composite<T> {

	public static <T> Expression<T> of(Stream<Expression<T>> elements) {
		return Composite.of(Sequence.class, elements, Sequence::new, Collectors.toList());
	}
	
	@SafeVarargs
	public static <T> Expression<T> of(Expression<T>... elements) {
		return of(Stream.of(elements));
	}
	
	/*
	public static <T> Expression<T> of(Expression<T>... elements) {
		return Composite.of(Sequence.class, new LinkedList<Expression<T>>(), Sequence::new, elements);
	}
	*/
	
	private Sequence(Collection<Expression<T>> list) {
		super(list);
	}

	@Override
	public <THAT extends Language<THAT, U>, U> THAT convertLanguage(Language.Factory<THAT, U> factory, Function<T, U> function) {
		return factory.sequence(elements().stream().map(x -> x.convertLanguage(factory, function)));
	}
	
	public String toString() {
		return "("+elements().stream().map(Object::toString).reduce((a,b) -> a+", "+b).orElse("")+")";
	}
	
	@Override
	public <R> R accept(Visitor<T, R> visitor) {
		return visitor.handle(this);
	}
	
}
