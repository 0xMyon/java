package lang;

import java.util.Objects;
import java.util.function.Function;


public class ComplementSet<T> implements InfiniteSet<T> {

	private final FiniteSet<T> complement;
	
	public ComplementSet(FiniteSet<T> complement) {
		this.complement = complement;
	}
	
	@Override
	public InfiniteSet<T> unite(InfiniteSet<T> that) {
		return that.accept(new Visitor<T, InfiniteSet<T>>() {

			@Override
			public InfiniteSet<T> handle(FiniteSet<T> that) {
				return complement.minus(that).complement();
			}

			@Override
			public InfiniteSet<T> handle(ComplementSet<T> that) {
				return complement.intersect(that.complement()).complement();
			}
		});
	}

	@Override
	public FiniteSet<T> complement() {
		return complement;
	}

	@Override
	public boolean contains(T that) {
		return !complement.contains(that);
	}

	@Override
	public boolean containsAll(InfiniteSet<T> that) {
		return that.accept(new Visitor<T, Boolean>() {
			@Override
			public Boolean handle(FiniteSet<T> that) {
				return complement.intersect(that).isEmpty();
			}
			@Override
			public Boolean handle(ComplementSet<T> that) {
				return that.complement().containsAll(complement());
			}
		});
	}
	
	@Override
	public <R> R accept(Visitor<T, R> visitor) {
		return visitor.handle(this);
	}

	

	public String toString() {
		return "!"+complement.toString();
	}
	
	public int hashCode() {
		return Objects.hashCode(complement);
	}
	
	public boolean equals(Object object) {
		if (object instanceof ComplementSet) {
			return Objects.equals(complement, ((ComplementSet<?>)object).complement);
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isFinite() {
		return false;
	}

	@Override
	public <U, THAT extends Type<THAT, U>> THAT convertType(lang.Type.Factory<THAT, U> factory, Function<T, U> function) {
		return complement.convertType(factory, function).complement();
	}
	
}
