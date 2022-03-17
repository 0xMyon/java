package expr;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import fsm.Machine;
import lang.Language;
import set.ComplementSet;
import set.FiniteSet;

public abstract class Expression<T> implements Language<Expression<T>, T> {

	public <R> Expression<R> map(final Function<T, R> function) {
		return convertLanguage(new Factory<>(), function);
	}


	@Override
	public Expression<T> concat(final Expression<T> that) {
		return Sequence.of(this, that);
	}

	@Override
	public Expression<T> unite(final Expression<T> that) {
		return Union.of(this, that);
	}

	@Override
	public Expression<T> iterate() {
		return Iteration.of(this);
	}

	@Override
	public Expression<T> optional() {
		return unite(epsilon());
	}

	@Override
	public Expression<T> complement() {
		return Complement.of(this);
	}


	@Override
	public Expression<T> reverse() {
		return Reverse.of(this);
	}

	@Override
	public Expression<T> parallel(final Expression<T> that) {
		return Parallel.of(this, that);
	}

	private final Machine.Factory<T, ComplementSet<FiniteSet<T>, T>, Void> MACHINE = new Machine.Factory<>(new ComplementSet.Factory<>(new FiniteSet.Factory<>()));


	Optional<Machine<T, ComplementSet<FiniteSet<T>, T>, Void>> machine = Optional.empty();

	Machine<T, ComplementSet<FiniteSet<T>, T>, Void> convert() {
		if (machine.isEmpty())
			machine = Optional.of(convertLanguage(MACHINE));
		return machine.get();
	}

	@Override
	public boolean contains(final List<T> that) {
		return convert().contains(that);
	}

	@Override
	public boolean containsAll(final Expression<T> that) {
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
	public Expression<T> THIS() {
		return this;
	}


	public static <T> Expression<T> epsilon() {
		return Sequence.of();
	}

	@Override
	public Factory<T> factory() {
		return new Factory<>();
	}

	public static class Factory<T> implements Language.Factory<Expression<T>, T> {
		@Override
		public Expression<T> empty() {
			return Union.of();
		}
		@Override
		public Expression<T> epsilon() {
			return Expression.epsilon();
		}
		@Override
		public Expression<T> factor(final T that) {
			return Element.of(that);
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
	public boolean isEqual(final Expression<T> that) {
		return convert().isEqual(that.convert());
	}

	@Override
	public boolean startsWith(final Expression<T> that) {
		return convert().startsWith(that.convert());
	}

	public interface Visitor<T, R> {

		R handle(Complement<T> that);
		R handle(Element<T> that);
		R handle(Iteration<T> that);
		R handle(Parallel<T> that);
		R handle(Reverse<T> that);
		R handle(Sequence<T> that);
		R handle(Union<T> that);


	}

	public abstract <R> R accept(Visitor<T, R> visitor);



}
