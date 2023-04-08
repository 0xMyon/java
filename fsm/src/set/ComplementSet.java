package set;

import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import lang.Set;
import lang.Type;
import util.BooleanOperator;

public class ComplementSet<T> implements Type<ComplementSet<T>, T> {

	private final Set<?, T> set;
	private final boolean complement;

	public ComplementSet(final boolean complement, final Set<?, T> set) {
		this.set = set;
		this.complement = complement;
	}

	public ComplementSet(final Set<?, T> set) {
		this(false, set);
	}

	@SafeVarargs
	public ComplementSet(final T...ts) {
		this(false, new FiniteSet<>(ts));
	}

	// a u b 	= a u b
	// a u!b 	= !(b - a)
	// !a u b 	= !(a - b)
	// !a u !b 	= !(a & b)

	@Override
	public ComplementSet<T> unite(final ComplementSet<T> that) {
		return op(that, (a,b)->a||b, Set::unite_Set, Set::removed_Set, Set::minus_Set, Set::intersect_Set);
	}


	@Override
	public ComplementSet<T> intersect(final ComplementSet<T> that) {
		return op(that, (a,b)->a&&b, Set::intersect_Set, Set::minus_Set, Set::removed_Set, Set::unite_Set);
	}

	// TODO
	/*
	@Override
	public ComplementSet<SET, T> minus(ComplementSet<SET, T> that) {
		return op(that, (a,b)->!a&&b, Set::minus, Set::intersect, Set::unite, Set::removed);
	}
	 */


	private ComplementSet<T> op(final ComplementSet<T> that, final BooleanOperator op, final BinaryOperator<Set<?, T>> a, final BinaryOperator<Set<?, T>> b, final BinaryOperator<Set<?, T>> c, final BinaryOperator<Set<?, T>> d) {
		return new ComplementSet<>(
				op.apply(this.complement, that.complement),
				(this.complement ? that.complement ? d : c : that.complement ? b : a).apply(this.set, that.set)
				);
	}

	@Override
	public ComplementSet<T> complement() {
		return new ComplementSet<>(!complement, set);
	}

	@Override
	public boolean contains(final T that) {
		return set.contains(that) != complement;
	}

	@Override
	public boolean containsAll(final ComplementSet<T> that) {
		if (complement) {
			if (that.complement) {
				return that.set.containsAll_Set(this.set);
			} else {
				return this.set.isDisjunct_Set(that.set);
			}
		} else {
			if (that.complement) {
				return false;
			} else {
				return this.set.containsAll_Set(that.set);
			}
		}
	}


	@Override
	public String toString() {
		return (complement?"!":"")+set.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(complement);
	}

	@Override
	public boolean equals(final Object object) {
		if (object instanceof ComplementSet) {
			final ComplementSet<?> that = (ComplementSet<?>) object;
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
	public ComplementSet<T> THIS() {
		return this;
	}

	@Override
	public boolean isEqual(final ComplementSet<T> that) {
		return this.complement == that.complement && this.set.isEqual_Set(that.set);
	}


	public static class Factory<T> implements Type.Factory<ComplementSet<T>, T> {

		private final FiniteSet.Factory<T> factory = new FiniteSet.Factory<T>();

		@Override
		public ComplementSet<T> empty() {
			return new ComplementSet<>(false, factory.empty());
		}

		@Override
		public ComplementSet<T> summand(final T that) {
			return new ComplementSet<>(false, factory.summand(that));
		}

	}

	@Override
	public Factory<T> factory() {
		return new Factory<>();
	}


	@Override
	public <THAT extends Type<THAT, U>, U> THAT convertType(final Type.Factory<THAT, U> factory,	final Function<T, U> function) {
		return set.convertSet(factory, function).complement(complement);
	}

	@Override
	public <THAT extends Set<THAT, U>, U> THAT convertSet(final Set.Factory<THAT, U> factory,	final Function<T, U> function) {
		if (!complement)
			return set.convertSet(factory, function);
		else
			throw new UnsupportedOperationException();
	}

}
