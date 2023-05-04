package lambda.reducible;

import java.util.Map;
import java.util.Objects;
import lambda.Reducible;
import lang.Container;
import lang.Container.Factory;

public class Constant<T, TYPE extends Container<TYPE, T>> extends Irreducible<T,TYPE> {

	private final T value;
	private final Factory<TYPE, T> factory;

	public Constant(final T value, Factory<TYPE, T> factory) {
		this.value = value;
		this.factory = factory;
	}

	@Override
	public boolean isMapping(final Reducible<T,TYPE> term, final Map<Variable<T,TYPE>, Reducible<T,TYPE>> map) {
		return Objects.equals(this, term);
	}

	@Override
	public Reducible<T,TYPE> type() {
		return new ConstantType<>(factory.summand(value));
	}

	@Override
	public String toString(final Map<Variable<T,TYPE>, String> names) {
		return value.toString();
	}
	
	


	@Override
	public int layer() {
		return 0;
	}
	


}
