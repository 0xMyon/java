package lambda.reducible;

import lambda.Reducible;
import lambda.Type;

public interface IAbstraction<V, T> extends Reducible<T> {

	Reducible<T> apply(Reducible<V> parameter);

	Reducible<Type<V>> domain();
	
	IAbstraction<?,Type<T>> type();
}
