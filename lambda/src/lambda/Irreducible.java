package lambda;

import lambda.reducible.Variable;

public interface Irreducible extends Reducible {

	
	default Reducible replace(final Variable variable, final Reducible term) {
		return this;
	}


	default Reducible reduce() {
		return this;
	}
	
}
