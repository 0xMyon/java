package lambda;

import java.util.Map;

public class Application implements Term {

	private final Term function, parameter;
	
	public Application(Term function, Term parameter) {
		this.function = function;
		this.parameter = parameter;
	}
	
	@Override
	public Term replace(Variable variable, Term term) {
		return new Application(function.replace(variable, term), parameter.replace(variable, term));
	}

	@Override
	public Term reduce() {
		return (function instanceof Abstraction) 
				? ((Abstraction)function).apply(parameter).reduce()
				: new Application(function.reduce(), parameter.reduce());
	}

	@Override
	public boolean isEqual(Term term, Map<Variable, Variable> map) {
		if (term instanceof Application) {
			final Application that = (Application) term;
			return this.function.isEqual(that.function, map) && this.parameter.isEqual(that.parameter, map);
		}
		return false;
	}

}
