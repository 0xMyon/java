package lambda.reducible;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import lambda.Reducible;
import lambda.TypeMismatch;

public abstract class Irreducible implements Reducible {

	private final String value;

	public Irreducible(final String value) {
		this.value = value;
	}

	@Override
	public boolean isMapping(final Reducible term, final Map<Variable, Reducible> map) {
		return Objects.equals(this, term);
	}


	@Override
	public String toString() {
		return toString(new HashMap<>());
	}

	@Override
	public String toString(final Map<Variable, String> names) {
		return value;
	}

	@Override
	public boolean isDepending(final Variable variable) {
		return false;
	}

	@Override
	public Reducible replace(final Variable variable, final Reducible term) throws TypeMismatch {
		return this;
	}

	@Override
	public Reducible reduce() {
		return this;
	}

	@Override
	public Stream<Variable> freeVars() {
		return Stream.of();
	}

}
