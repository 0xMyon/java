package set;

import java.util.Objects;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import lang.Set;
import lang.Type;
import util.BooleanOperator;

public class ComplementSet<T, TYPE extends Set<TYPE,T>> implements Type<ComplementSet<T,TYPE>, T> {

	private final TYPE set;
	private final boolean complement;

	public ComplementSet(final boolean complement, final TYPE set) {
		this.set = set;
		this.complement = complement;
	}

	public ComplementSet(final TYPE set) {
		this(false, set);
	}
	
	@SafeVarargs
	static <T> ComplementSet<T, FiniteSet<T>> of(T... ts) {
		return new ComplementSet<>(new FiniteSet<>(ts));
	}

	// a u b 	= a u b
	// a u!b 	= !(b - a)
	// !a u b 	= !(a - b)
	// !a u !b 	= !(a & b)

	@Override
	public ComplementSet<T,TYPE> unite(final ComplementSet<T,TYPE> that) {
		return op(that, BooleanOperator.disjunction, Set::unite, Set::removed, Set::minus, Set::intersect);
	}


	@Override
	public ComplementSet<T,TYPE> intersect(final ComplementSet<T,TYPE> that) {
		return op(that, BooleanOperator.conjunction, Set::intersect, Set::minus, Set::removed, Set::unite);
	}

	// TODO
	/*
	@Override
	public ComplementSet<SET, T> minus(ComplementSet<SET, T> that) {
		return op(that, (a,b)->!a&&b, Set::minus, Set::intersect, Set::unite, Set::removed);
	}
	 */


	private ComplementSet<T,TYPE> op(final ComplementSet<T,TYPE> that, final BooleanOperator op, final BinaryOperator<TYPE> a, final BinaryOperator<TYPE> b, final BinaryOperator<TYPE> c, final BinaryOperator<TYPE> d) {
		return new ComplementSet<>(
				op.apply(this.complement, that.complement),
				(this.complement ? that.complement ? d : c : that.complement ? b : a).apply(this.set, that.set)
				);
	}

	@Override
	public ComplementSet<T,TYPE> complement() {
		return new ComplementSet<>(!complement, set);
	}

	@Override
	public boolean contains(final T that) {
		return set.contains(that) != complement;
	}

	@Override
	public boolean containsAll(final ComplementSet<T,TYPE> that) {
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


	@Override
	public String toString() {
		return (complement?"!":"")+set.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(complement, set);
	}

	@Override
	public boolean equals(final Object object) {
		if (object instanceof ComplementSet) {
			final ComplementSet<?,?> that = (ComplementSet<?,?>) object;
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
	public ComplementSet<T,TYPE> THIS() {
		return this;
	}

	@Override
	public boolean isEqual(final ComplementSet<T,TYPE> that) {
		return this.complement == that.complement && this.set.isEqual(that.set);
	}


	public static class Factory<T,TYPE extends Set<TYPE,T>> implements Type.Factory<ComplementSet<T,TYPE>, T> {

		public Factory(Set.Factory<TYPE,T> factory) {
			this.factory = factory;
		}
		
		private final Set.Factory<TYPE,T> factory;

		@Override
		public ComplementSet<T,TYPE> empty() {
			return new ComplementSet<>(false, factory.empty());
		}

		@Override
		public ComplementSet<T,TYPE> summand(final T that) {
			return new ComplementSet<>(false, factory.summand(that));
		}

	}
	
	

	@Override
	public Factory<T,TYPE> factory() {
		return new Factory<>(set.factory());
	}


	@Override
	public <THAT extends Type<THAT, U>, U, FACTORY extends Type.Factory<THAT, U>> 
	THAT convert(final FACTORY factory,	final Function<T, U> function) {
		return set.convert(factory, function).complement(complement);
	}

	@Override
	public <THAT extends Set<THAT, U>, U, FACTORY extends Set.Factory<THAT, U>> 
	THAT convert(final FACTORY factory,	final Function<T, U> function) {
		if (!complement)
			return set.convert(factory, function);
		else
			throw new UnsupportedOperationException();
	}

	@Override
	public T random(Random random) {
		if (complement) 
			throw new RuntimeException("can not give a complement random");
		else
			return set.random(random);
	}

	public static <T> ComplementSet.Factory<T, FiniteSet<T>> FACTORY() {
		return new ComplementSet.Factory<>(new FiniteSet.Factory<>());
	}

}
