package lambda.reducible;

import lambda.Reducible;

public interface IAbstraction extends Reducible {

	Reducible apply(Reducible parameter);

	Reducible domain();
	
	IAbstraction type();
	
}
