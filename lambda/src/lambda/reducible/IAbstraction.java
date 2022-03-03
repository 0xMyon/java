package lambda.reducible;

import lambda.Reducible;

public interface IAbstraction extends Reducible {

	Reducible apply(Reducible parameter);

	Reducible domain();
	
	IAbstraction type();
	
	default IAbstraction andThen(IAbstraction that) {
		return new Abstraction(domain(), x -> that.apply(this.apply(x)));
	}
	
}
