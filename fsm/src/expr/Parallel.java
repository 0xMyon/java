package expr;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lang.Language;

public class Parallel<T> extends Composite<T> {

	public static <T> Expression<T> of(Stream<Expression<T>> elements) {
		return Composite.of(Parallel.class, elements, Parallel::new, Collectors.toList());
	}
	
	@SafeVarargs
	public static <T> Expression<T> of(Expression<T>... elements) {
		return of(Stream.of(elements));
	}
	
	private Parallel(Collection<Expression<T>> list) {
		super(list);
	}
	
	@Override
	public <THAT extends Language<THAT, U>, U> THAT convertLanguage(Language.Factory<THAT, U> factory, Function<T, U> function) {
		return factory.parallel(elements().stream().map(x -> x.convertLanguage(factory, function)));
	}
	
	@Override
	public <R> R accept(Visitor<T, R> visitor) {
		return visitor.handle(this);
	}
	

}
