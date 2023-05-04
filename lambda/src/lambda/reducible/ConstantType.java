package lambda.reducible;

import java.util.Map;

import lambda.Reducible;
import lang.Container;

public class ConstantType<T,TYPE extends Container<TYPE, T>> extends Irreducible<T, TYPE> {

	private final TYPE type;
	
	public ConstantType(TYPE type) {
		this.type = type;
	}
	
	@Override
	public boolean isMapping(Reducible<T, TYPE> other, Map<Variable<T, TYPE>, Reducible<T, TYPE>> context) {
		if (other instanceof ConstantType) {
			ConstantType<T, TYPE> that = (ConstantType<T,TYPE>) other;
			return this.type.isEqual(that.type);
		}
		return false;
	}

	@Override
	public Reducible<T, TYPE> type() {
		return new MetaType<>(this);
	}


	@Override
	public String toString(Map<Variable<T, TYPE>, String> names) {
		return type.toString();
	}
	
	@Override
	public int layer() {
		return 1;
	}

}
