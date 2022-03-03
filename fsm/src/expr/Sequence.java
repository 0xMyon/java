package expr;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import lang.Language;

public class Sequence<T> extends Composite<T> {

	@SafeVarargs
	public static <T> Expression<T> of(Expression<T>... elements) {
		return Composite.of(Sequence.class, new LinkedList<Expression<T>>(), Sequence::new, elements);
	}
	
	private Sequence(List<Expression<T>> list) {
		super(list);
	}

	@Override
	public <U, THAT extends Language<THAT, U>> THAT convertLanguage(Language.Factory<THAT, U> factory, Function<T, U> function) {
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
