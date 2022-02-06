package lang;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FiniteSet<T> implements InfiniteSet<T> {

	private final Set<T> elements = new HashSet<>();
	
	@SafeVarargs
	public FiniteSet(T...ts) {
		this(Stream.of(ts));
	}
	public FiniteSet(Stream<T> stream) {
		elements.addAll(stream.collect(Collectors.toSet()));
	}
	
	@Override
	public InfiniteSet<T> unite(InfiniteSet<T> that) {
		return that.accept(new Visitor<T,InfiniteSet<T>>(){
			@Override
			public InfiniteSet<T> handle(FiniteSet<T> that) {
				return unite(that);
			}
			@Override
			public InfiniteSet<T> handle(ComplementSet<T> that) {
				return that.unite(FiniteSet.this);
			}
			
		});
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
	public InfiniteSet<T> complement() {
		return new ComplementSet<>(this);
	}

	@Override
	public boolean contains(T that) {
		return elements.contains(that);
	}

	@Override
	public boolean containsAll(InfiniteSet<T> that) {
		return that.accept(new Visitor<T,Boolean>(){
			@Override
			public Boolean handle(FiniteSet<T> that) {
				return elements.containsAll(that.elements);
			}
			@Override
			public Boolean handle(ComplementSet<T> that) {
				return false;
			}
			
		});
	}

	@Override
	public <R> R accept(Visitor<T, R> visitor) {
		return visitor.handle(this);
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
	public boolean isFinite() {
		return true;
	}
	
	@Override
	public <U, THAT extends Type<THAT, U>> THAT convertType(Type.Factory<THAT, U> factory, Function<T, U> f) {
		return elements.stream().map(f).map(factory::summand).reduce(factory::union).orElse(factory.empty());
	}
	

}
