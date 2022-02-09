package expr;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import fsm.Machine;
import lang.InfiniteSet;
import lang.Language;

public abstract class Expression<T> implements Language<Expression<T>,T> {

	@Override
	public Expression<T> concat(Expression<T> that) {
		return Sequence.of(this, that);
	}

	@Override
	public Expression<T> unite(Expression<T> that) {
		return Union.of(this, that);
	}

	@Override
	public Expression<T> iterate() {
		return Itteration.of(this);
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
	
	private final Machine.Factory<T, InfiniteSet<T>, Void> MACHINE = new Machine.Factory<>(new InfiniteSet.Factory<T>());
	
	
	Optional<Machine<T, InfiniteSet<T>, Void>> machine = Optional.empty();
	
	Machine<T, InfiniteSet<T>, Void> convert() {
		if (machine.isEmpty())
			machine = Optional.of(convertType(MACHINE));
		return machine.get();
	}
	
	
	@Override
	public boolean contains(List<T> that) {
		return convert().contains(that);
	}

	@Override
	public boolean containsAll(Expression<T> that) {
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
		public Expression<T> factor(T that) {
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

	
	interface Visitor<T, R> {

		R handle(Complement<T> that);
		R handle(Element<T> that);
		R handle(Itteration<T> that);
		R handle(Reverse<T> that);
		R handle(Sequence<T> that);
		R handle(Union<T> that);
		
		
	}
	
	public abstract <R> R accept(Visitor<T, R> visitor);
	
	
	
}
