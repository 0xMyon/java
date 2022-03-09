package lambda;

import lambda.reducible.Variable;

public interface Irreducible<T> extends Reducible<T> {

	
	default Reducible<T> replace(final Variable<T> variable, final Reducible<T> term) {
		return this;
	}


	default Reducible<T> reduce() {
		return this;
	}
	
}
