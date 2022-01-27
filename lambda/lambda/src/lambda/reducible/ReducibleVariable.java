package lambda.reducible;

import java.util.Map;

import lambda.Reducible;

public abstract class ReducibleVariable<T extends Reducible<T>> implements Reducible<T> {


	@Override
	public T replace(ReducibleVariable<? extends T> variable, T term) {
		return equals(variable) ? term : THIS();
	}

	@Override
	public T reduce() {
		return THIS();
	}

	@Override
	public boolean isEqual(T term, Map<ReducibleVariable<?>, ReducibleVariable<?>> map) {
		if (term instanceof ReducibleVariable) {
			if (map.containsKey(this)) {
				return map.get(this).equals(term);
			} else {
				map.put(this, (ReducibleVariable<?>)term);
				return true;
			}
		}
		return false;
	}

}