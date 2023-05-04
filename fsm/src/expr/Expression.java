package expr;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import fsm.Machine;
import lang.Container;
import lang.Language;
import set.ComplementSet;
import set.FiniteSet;

public abstract class Expression<T, TYPE extends Container<TYPE,T>> implements Language<Expression<T,TYPE>, T> {

	/*
	public <R,RTYPE extends Type<RTYPE,R>> Expression<R,RTYPE> map(final Function<T, R> function) {
		return convert(new Factory<>(), function);
	}
	*/


	@Override
	public Expression<T,TYPE> concat(final Expression<T,TYPE> that) {
		return Sequence.of(underlying_factory(), this, that);
	}

	@Override
	public Expression<T,TYPE> unite(final Expression<T,TYPE> that) {
		return Union.of(underlying_factory(), this, that);
	}

	@Override
	public Expression<T,TYPE> iterate() {
		return Iteration.of(this);
	}

	@Override
	public Expression<T,TYPE> optional() {
		return unite(factory().epsilon());
	}

	@Override
	public Expression<T,TYPE> complement() {
		return Complement.of(this);
	}


	@Override
	public Expression<T,TYPE> reverse() {
		return Reverse.of(this);
	}

	@Override
	public Expression<T,TYPE> parallel(final Expression<T,TYPE> that) {
		return Parallel.of(underlying_factory(), this, that);
	}

	private final Machine.Factory<T, Void, ComplementSet<T, FiniteSet<T>>> MACHINE = 
			new Machine.Factory<>(new ComplementSet.Factory<>(new FiniteSet.Factory<>()));


	Optional<Machine<T, Void, ComplementSet<T, FiniteSet<T>>>> machine = Optional.empty();

	Machine<T, Void, ComplementSet<T, FiniteSet<T>>> convert() {
		if (machine.isEmpty())
			machine = Optional.of(convert(MACHINE));
		return machine.get();
	}

	@Override
	public boolean contains(final List<T> that) {
		return convert().contains(that);
	}

	@Override
	public boolean containsAll(final Expression<T,TYPE> that) {
		return convert().containsAll(that.convert());
	}

	@Override
	public boolean isEmpty() {
		return convert().isEmpty();
	}


	@Override
	public boolean hasEpsilon() {
		return convert().hasEpsilon();
	}

	@Override
	public Expression<T,TYPE> THIS() {
		return this;
	}

	/*
	public static <T, TYPE extends Container<TYPE, T>> Expression<T,TYPE> epsilon() {
		return Sequence.of();
	}
	*/

	/*
	@Override
	public Factory<T,TYPE> factory() {
		return new Factory<>();
	}
	*/
	
	/**
	 * @param <T>
	 * @return default {@link Factory}
	 */
	public static <T> Factory<T, set.Class<T>> FACTORY() {
		return new Factory<>(new set.Class.Factory<>());
	}

	abstract Container.Factory<TYPE,T> underlying_factory();
	
	public static class Factory<T, TYPE extends Container<TYPE,T>> implements Language.Factory<Expression<T,TYPE>, T> {
		
		public Factory(Container.Factory<TYPE,T> factory) {
			this.factory = factory;
		}
		
		Container.Factory<TYPE,T> factory() {
			return factory;
		}
		
		private Container.Factory<TYPE,T> factory;
		
		@Override
		public Expression<T,TYPE> empty() {
			return Union.of(factory);
		}
		@Override
		public Expression<T,TYPE> epsilon() {
			return Sequence.of(factory);
		}
		@Override
		public Expression<T,TYPE> factor(final T that) {
			return Element.of(factory.summand(that));
		}
	}

	@Override
	public boolean isFinite() {
		return convert().isFinite();
	}

	@Override
	public boolean isEpsilon() {
		return convert().isEpsilon();
	}

	@Override
	public boolean isEqual(final Expression<T,TYPE> that) {
		return convert().isEqual(that.convert());
	}

	@Override
	public boolean startsWith(final Expression<T,TYPE> that) {
		return convert().startsWith(that.convert());
	}
	

	@Override
	public List<T> random(Random random) {
		return convert().random(random);
	}

	public interface Visitor<T, TYPE extends Container<TYPE,T>, R> {

		R handle(Complement<T,TYPE> that);
		R handle(Element<T,TYPE> that);
		R handle(Iteration<T,TYPE> that);
		R handle(Parallel<T,TYPE> that);
		R handle(Reverse<T,TYPE> that);
		R handle(Sequence<T,TYPE> that);
		R handle(Union<T,TYPE> that);


	}

	public abstract <R> R accept(Visitor<T, TYPE, R> visitor);



}
