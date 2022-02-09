package expr;

import java.util.List;
import java.util.function.Function;

import lang.Language;

public class Parallel<T> extends Expression<T> {

	private final List<Expression<T>> elements;
	
	@SafeVarargs
	static <T> Expression<T> of(Expression<T>...expressions) {
		switch(expressions.length) {
			case 0:	return Sequence.of();
			case 1: return expressions[0];
			default: return new Parallel<>(List.of(expressions));
		}
	}
	
	private Parallel(List<Expression<T>> list) {
		elements = list;
	}
	
	@Override
	public <U, THAT extends Language<THAT, U>> THAT convertLanguage(Language.Factory<THAT, U> factory, Function<T, U> function) {
		return factory.parallel(elements.stream().map(x -> x.convertLanguage(factory, function)));
	}
	
	@Override
	public <R> R accept(Visitor<T, R> visitor) {
		return visitor.handle(this);
	}

}
