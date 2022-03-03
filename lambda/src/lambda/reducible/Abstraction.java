package lambda.reducible;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lambda.Reducible;

 
public class Abstraction implements IAbstraction {

	private final Variable variable;
	private final Reducible term;

	public Abstraction(Reducible type, Function<Variable, Reducible> lambda) {
		this.variable = new Variable(type);
		this.term = lambda.apply(variable);
	}

	@Override
	public Reducible replace(Variable variable, Reducible term) {
		return new Abstraction(this.variable.type().replace(variable, term), x -> apply(x).replace(variable, term));
	}

	public Reducible apply(Reducible parameter) {
		assert domain().containsAll(parameter.type());
		// TODO check if Var type conatains parameter
		return term.replace(variable, parameter);
	}

	@Override
	public Reducible reduce() {
		return new Abstraction(variable.type().reduce(), x -> apply(x).reduce());
	}

	@Override
	public boolean isMapping(Reducible term, Map<Variable, Reducible> map) {
		if (term instanceof Abstraction) {
			Abstraction that = (Abstraction) term;
			return this.variable.isMapping(that.variable, map) && this.term.isMapping(that.term, map);
		}
		return false;
	}

	@Override
	public IAbstraction type() {
		return new Abstraction(domain(), x -> apply(x).type());
	}

	public Reducible domain() {
		return variable.type();
	}
	
	public boolean isConstant() {
		return !term.contains(variable);
	}
	
	public String toString() {
		return toString(new HashMap<>());
	}
	
	@Override
	public String toString(Map<Variable, String> names) {
		if (!isConstant())
			return "λ"+variable.toString(names)+":"+variable.type().toString(names)+"."+term.toString(names);
		else if (variable.type() instanceof Abstraction)
			return "("+variable.type().toString(names)+")->"+term.toString(names);
		else
			return variable.type().toString(names)+"->"+term.toString(names);
	}

	@Override
	public boolean contains(Variable variable) {
		return isConstant() && this.variable.contains(variable) || term.contains(variable);
	}

}