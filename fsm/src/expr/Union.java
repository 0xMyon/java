package expr;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lang.Language;
import lang.Type;

public class Union<T> extends Composite<T> {

	@SafeVarargs
	public static <T> Expression<T> of(final Expression<T>... elements) {
		return of(Stream.of(elements));
	}

	public static <T> Expression<T> of(final Stream<Expression<T>> elements) {
		return Composite.of(Union.class, elements, Union::new, Collectors.toSet());
	}


	private Union(final Collection<Expression<T>> set) {
		super(set);
	}

	@Override
	public boolean isEmpty() {
		return elements().isEmpty();
	}

	@Override
	public <THAT extends Language<THAT, U>, U> THAT convertLanguage(final Language.Factory<THAT, U> factory, final Function<T, U> function) {
		return factory.union(elements().stream().map(x -> x.convertLanguage(factory, function)));
	}

	@Override
	public <THAT extends Type<THAT, U>, U> THAT convertType(final Type.Factory<THAT, U> factory, final Function<List<T>, U> function) {
		return factory.union(elements().stream().map(x -> x.convertType(factory, function)));
	}

	@Override
	public <THAT extends lang.Set<THAT, U>, U> THAT convertSet(final lang.Set.Factory<THAT, U> factory, final Function<List<T>, U> function) {
		return factory.union(elements().stream().map(x -> x.convertSet(factory, function)));
	}

	@Override
	public String toString() {
		return "{"+elements().stream().map(Object::toString).reduce((a,b) -> a+", "+b).orElse("")+"}";
	}


	@Override
	public <R> R accept(final Visitor<T, R> visitor) {
		return visitor.handle(this);
	}

}
