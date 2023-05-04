package lambda.reducible;

import java.util.HashMap;
import java.util.stream.Stream;

import lambda.Reducible;
import lambda.TypeMismatch;
import lang.Container;

public abstract class Irreducible<T,TYPE extends Container<TYPE,T>> implements Reducible<T,TYPE> {

	@Override
	public String toString() {
		return toString(new HashMap<>());
	}

	@Override
	public boolean isDepending(final Variable<T,TYPE> variable) {
		return false;
	}

	@Override
	public Reducible<T,TYPE> replace(final Variable<T,TYPE> variable, final Reducible<T,TYPE> term) throws TypeMismatch {
		return this;
	}

	@Override
	public Reducible<T,TYPE> doReduction() {
		return this;
	}

	@Override
	public Stream<Variable<T,TYPE>> freeVars() {
		return Stream.of();
	}
	
	@Override
	public boolean isReducible() {
		return false;
	}

}
