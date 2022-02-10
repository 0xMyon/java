package set;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FiniteSet<T> implements lang.Set<FiniteSet<T>, T> {

	private final Set<T> elements = new HashSet<>();
	
	@SafeVarargs
	public FiniteSet(T...ts) {
		this(Stream.of(ts));
	}
	public FiniteSet(Stream<T> stream) {
		elements.addAll(stream.collect(Collectors.toSet()));
	}
	
	public FiniteSet<T> unite(FiniteSet<T> that) {
		return new FiniteSet<>(Stream.concat(elements.stream(), that.elements.stream()));
	}
	
	public FiniteSet<T> intersect(FiniteSet<T> that) {
		return new FiniteSet<>(elements.stream().filter(that::contains));
	}
	
	public FiniteSet<T> minus(FiniteSet<T> that) {
		return new FiniteSet<>(elements.stream().filter(e -> !that.contains(e)));
	}

	@Override
	public boolean contains(T that) {
		return elements.contains(that);
	}
	
	public String toString() {
		return elements.toString();
	}
	
	public int hashCode() {
		return elements.hashCode();
	}
	
	public boolean equals(Object object) {
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
	public boolean containsAll(FiniteSet<T> that) {
		return elements.containsAll(that.elements);
	}
	
	@Override
	public <U, THAT extends lang.Set<THAT, U>> THAT convertSet(lang.Set.Factory<THAT, U> factory, Function<T, U> function) {
		return factory.union(elements.stream().map(x -> factory.summand(function.apply(x))));
	}
	
	
	public static class Factory<T> implements lang.Set.Factory<FiniteSet<T>, T> {

		@Override
		public FiniteSet<T> empty() {
			return new FiniteSet<>();
		}

		@Override
		public FiniteSet<T> summand(T that) {
			return new FiniteSet<>(that);
		}
		
	}

}
