package set;

import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import lang.Set;
import lang.Type;

public class ComplementSet<SET extends Set<SET,T>, T> implements Type<ComplementSet<SET, T>, T> {

	private final SET set;
	private final boolean complement;
	
	public ComplementSet(boolean complement, SET set) {
		this.set = set;
		this.complement = complement;
	}
	
	public ComplementSet(SET set) {
		this(false, set);
	}
		
	// a u b 	= a u b
	// a u!b 	= !(b - a)
	// !a u b 	= !(a - b)
	// !a u !b 	= !(a & b)
	
	BinaryOperator<SET> op(boolean left, boolean right) {
		return left ? right ? Set::intersect : Set::minus : right ? (a,b)->b.minus(a) : Set::unite;
	}
	
	@Override
	public ComplementSet<SET, T> unite(ComplementSet<SET, T> that) {
		return new ComplementSet<>(this.complement || that.complement, op(this.complement, that.complement).apply(this.set, that.set));
	}

	@Override
	public ComplementSet<SET, T> complement() {
		return new ComplementSet<>(!complement, set);
	}

	@Override
	public boolean contains(T that) {
		return set.contains(that) != complement;
	}

	@Override
	public boolean containsAll(ComplementSet<SET, T> that) {
		if (complement) {
			if (that.complement) {
				return that.set.containsAll(this.set);
			} else {
				return this.set.isDisjunct(that.set);
			}
		} else {
			if (that.complement) {
				return false;
			} else {
				return this.set.containsAll(that.set);
			}
		}
	}
	

	public String toString() {
		return (complement?"!":"")+set.toString();
	}
	
	public int hashCode() {
		return Objects.hashCode(complement);
	}
	
	public boolean equals(Object object) {
		if (object instanceof ComplementSet) {
			ComplementSet<?, ?> that = (ComplementSet<?, ?>) object;
			return Objects.equals(this.complement, that.complement) && Objects.equals(this.set, that.set);
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return !complement && set.isEmpty();
	}

	@Override
	public boolean isFinite() {
		return !complement;
	}

	@Override
	public ComplementSet<SET, T> THIS() {
		return this;
	}

	@Override
	public boolean isEqual(ComplementSet<SET, T> that) {
		return this.complement == that.complement && this.set.isEqual(that.set);
	}

	
	public static class Factory<SET extends Set<SET,T>, T> implements Type.Factory<ComplementSet<SET, T>, T> {

		private final Set.Factory<SET, T> delegate;
		
		public Factory(Set.Factory<SET, T> delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public ComplementSet<SET, T> empty() {
			return new ComplementSet<>(false, delegate.empty());
		}

		@Override
		public ComplementSet<SET, T> summand(T that) {
			return new ComplementSet<>(false, delegate.summand(that));
		}
		
	}
	
	@Override
	public Factory<SET, T> factory() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public <U, THAT extends Type<THAT, U>> THAT convertType(lang.Type.Factory<THAT, U> factory,	Function<T, U> function) {
		return set.convertSet(factory, function).complement(complement);
	}
	
}