package expr;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import lang.Language;

public class Union<T> extends Composite<T> {

	
	@SafeVarargs
	public static <T> Expression<T> of(Expression<T>... elements) {
		return Composite.of(Union.class, new HashSet<Expression<T>>(), Union::new, elements);
	}
	

	private Union(Set<Expression<T>> set) {
		super(set);
	}
	
	@Override
	public boolean isEmpty() {
		return elements().isEmpty();
	}

	@Override
	public <U, THAT extends Language<THAT, U>> THAT convertLanguage(Language.Factory<THAT, U> factory, Function<T, U> function) {
		return factory.union(elements().stream().map(x -> x.convertLanguage(factory, function)));
	}
	
	public String toString() {
		return "{"+elements().stream().map(Object::toString).reduce((a,b) -> a+", "+b).orElse("")+"}";
	}
	
	
	@Override
	public <R> R accept(Visitor<T, R> visitor) {
		return visitor.handle(this);
	}
	
}
