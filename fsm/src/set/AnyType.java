package set;

import java.util.List;
import java.util.function.Function;

import javax.management.openmbean.InvalidOpenTypeException;

import fsm.Machine;
import lang.Type;
import util.Choice;
import util.Tuple;

public interface AnyType<T> extends Type<AnyType<T>, T> {


	@Override
	default AnyType<T> complement() {
		return new Complement<>(this);
	}

	public class Product<T, U> implements AnyType<Tuple<T,U>> {

		private final Type<?, T> left;
		private final Type<?, U> right;

		public Product(final Type<?, T> left, final Type<?, U> right) {
			this.left = left;
			this.right = right;
		}

		@Override
		public boolean contains(final Tuple<T, U> that) {
			return left.contains(that.left) && right.contains(that.right);
		}

		@Override
		public <THAT extends Type<THAT, V>, V> THAT convertType(final Factory<THAT, V> factory, final Function<Tuple<T, U>, V> function) {

			// 1) convert to any Language of Choice<A,B> and concat left & right Choice<A,B>* => filter Tuble<A,B> => original convertType

			final var x = left.convertType(new Machine.Factory<Choice<T,U>, Void>(), a -> List.of(Choice.A(a))).concat(
					right.convertType(new Machine.Factory<Choice<T,U>, Void>(), b -> List.of(Choice.B(b)))
					);

			return x.convertType(factory, y -> {
				if (y.size() == 2) {
					return function.apply(Tuple.of(y.get(0).getA(),y.get(1).getB()));
				}
				throw new Error();
			});

		}

		//  A x  B
		//  A x !B
		// !A x  B
		// !A x !B => !(A x B)
		// Type<Tuple<A,B>> => Language<Choice<A,B>> => Type<f(Tuple<A,B>)>

	}

	class Complement<T> implements AnyType<T> {

		public Complement(final Type<?, T> that) {
			this.complement = that;
		}

		private final Type<?, T> complement;

		@Override
		public AnyType<T> complement() {
			if (complement instanceof AnyType) {
				return (AnyType<T>) complement;
			}
			return new Complement<>(this);
		}



		@Override
		public <THAT extends Type<THAT, U>, U> THAT convertType(final Factory<THAT, U> factory, final Function<T, U> function) {
			return complement.convertType(factory, function).complement();
		}


		@Override
		public boolean contains(final T that) {
			return !complement.contains(that);
		}


	}


	public class Plus<T, U> implements AnyType<Choice<T,U>> {

		private final Type<?, T> left;
		private final Type<?, U> right;

		public Plus(final Type<?, T> left, final Type<?, U> right) {
			this.left = left;
			this.right = right;
		}

		@Override
		public boolean contains(final Choice<T, U> that) {
			return that.accept(new Choice.Visitor<T, U, Boolean>(){
				@Override
				public Boolean handleA(final T a) {
					return left.contains(a);
				}
				@Override
				public Boolean handleB(final U a) {
					return right.contains(a);
				}
			});
		}

		@Override
		public <THAT extends Type<THAT, V>, V> THAT convertType(final Factory<THAT, V> factory,	final Function<Choice<T, U>, V> function) {
			return left.convertType(factory, function.compose(Choice::A)).unite(right.convertType(factory, function.compose(Choice::B)));
		}

	}

	public class Union<T> implements AnyType<T> {

		private final Type<?, T> left, right;

		public Union(final Type<?, T> left, final Type<?, T> right) {
			this.left = left;
			this.right = right;
		}

		@Override
		public boolean contains(final T that) {
			return false;
		}


		@Override
		public <THAT extends Type<THAT, U>, U> THAT convertType(final Factory<THAT, U> factory, final Function<T, U> function) {
			return left.convertType(factory, function).unite(right.convertType(factory, function));
		}

	}




	@Override
	default AnyType<T> THIS() {
		return this;
	}


	@Override
	default boolean isEqual(final AnyType<T> that) {
		return false;
	}

	@Override
	default boolean containsAll(final AnyType<T> that) {
		return false;
	}


	@Override
	default boolean isEmpty() {
		return false;
	}

	@Override
	default boolean isFinite() {
		return true;
	}

	@Override
	default Factory<AnyType<T>, T> factory() {
		throw new InvalidOpenTypeException();
	}

	@Override
	default AnyType<T> unite(final AnyType<T> that) {
		return new Union<>(THIS(), that);
	}

}
