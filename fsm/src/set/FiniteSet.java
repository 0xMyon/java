package set;

import java.util.HashSet;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lang.Set;
import util.Tuple;

public class FiniteSet<T> implements Set<FiniteSet<T>, T> {

	private final HashSet<T> elements = new HashSet<>();

	public <U> FiniteSet<Tuple<T,U>> concat(final FiniteSet<U> that) {
		return new FiniteSet<Tuple<T,U>>(elements.stream()
				.map(left -> that.elements.stream().map(right -> Tuple.of(left, right)))
				.reduce(Stream.of(), Stream::concat)
				);
	}

	@SafeVarargs
	public FiniteSet(final T...ts) {
		this(Stream.of(ts));
	}
	public FiniteSet(final Stream<T> stream) {
		elements.addAll(stream.collect(Collectors.toSet()));
	}

	@Override
	public FiniteSet<T> unite(final FiniteSet<T> that) {
		return new FiniteSet<>(Stream.concat(elements.stream(), that.elements.stream()));
	}

	@Override
	public FiniteSet<T> intersect(final FiniteSet<T> that) {
		return new FiniteSet<>(elements.stream().filter(that::contains));
	}

	@Override
	public FiniteSet<T> minus(final FiniteSet<T> that) {
		return new FiniteSet<>(elements.stream().filter(e -> !that.contains(e)));
	}

	@Override
	public boolean contains(final T that) {
		return elements.contains(that);
	}

	@Override
	public String toString() {
		return elements.toString();
	}

	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	@Override
	public boolean equals(final Object object) {
		if (object instanceof FiniteSet) {
			return Objects.equals(elements, ((FiniteSet<?>)object).elements);
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	@Override
	public FiniteSet<T> THIS() {
		return this;
	}

	@Override
	public boolean containsAll(final FiniteSet<T> that) {
		return elements.containsAll(that.elements);
	}

	@Override
	public <THAT extends Set<THAT, U>, U, FACTORY extends Set.Factory<THAT,U>> THAT convert(final FACTORY factory, final Function<T, U> function) {
		return factory.union(elements.stream().map(x -> factory.summand(function.apply(x))));
	}


	public static class Factory<T> implements Set.Factory<FiniteSet<T>, T> {

		@Override
		public FiniteSet<T> empty() {
			return new FiniteSet<>();
		}

		@Override
		public FiniteSet<T> summand(final T that) {
			return new FiniteSet<>(that);
		}

	}


	@Override
	public boolean isEqual(final FiniteSet<T> that) {
		return this.elements.equals(that.elements);
	}

	@Override
	public lang.Set.Factory<FiniteSet<T>, T> factory() {
		return new Factory<>();
	}

	@SafeVarargs
	public static <T> FiniteSet<T> of(final T... that) {
		return new FiniteSet<>(that);
	}

}
