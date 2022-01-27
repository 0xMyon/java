package lambda.term;

import java.util.Map;

import lambda.Reducible;
import lambda.Term;
import lambda.Type;
import lambda.reducible.ReducibleVariable;
import lambda.type.ArrowType;

public class Application implements Term {

	private final Term function, parameter;
	
	public Application(Term function, Term parameter) {
		this.function = function;
		this.parameter = parameter;
		assert function.type() instanceof ArrowType : "function is not of ArrowType";
		assert ((ArrowType)function.type()).domain().equals(parameter.type()) : "Domain missmatch";
	}
	
	@Override
	public <X extends Reducible<X>> Term replace(ReducibleVariable<X> variable, X term) {
		return new Application(function.replace(variable, term), parameter.replace(variable, term));
	}

	@Override
	public Term reduce() {
		return (function instanceof Abstraction) 
				? ((Abstraction)function).apply(parameter).reduce()
				: new Application(function.reduce(), parameter.reduce());
	}

	@Override
	public boolean isEqual(Term term, Map<ReducibleVariable<?>, ReducibleVariable<?>> map) {
		if (term instanceof Application) {
			final Application that = (Application) term;
			return this.function.isEqual(that.function, map) && this.parameter.isEqual(that.parameter, map);
		}
		return false;
	}

	@Override
	public Type type() {
		return ((ArrowType)function.type()).codomain();
	}

}
