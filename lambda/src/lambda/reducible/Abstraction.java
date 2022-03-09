package lambda.reducible;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lambda.Reducible;

 
public class Abstraction<T> implements IAbstraction<T> {

	private final Variable<T> variable;
	private final Reducible<T> term;

	public Abstraction(Reducible<T> type, Function<Variable<T>, Reducible<T>> lambda) {
		this.variable = new Variable<>(type);
		this.term = lambda.apply(variable);
	}

	@Override
	public Reducible<T> replace(Variable<T> variable, Reducible<T> term) {
		return new Abstraction<>(this.variable.type().replace(variable, term), x -> apply(x).replace(variable, term));
	}

	@Override
	public Reducible<T> apply(Reducible<T> parameter) {
		return term.replace(variable, parameter);
	}

	@Override
	public Reducible<T> reduce() {
		return new Abstraction<>(variable.type().reduce(), x -> apply(x).reduce());
	}

	@Override
	public boolean isMapping(Reducible<T> term, Map<Variable<T>, Reducible<T>> map) {
		if (term instanceof Abstraction) {
			Abstraction<T> that = (Abstraction<T>) term;
			return this.variable.isMapping(that.variable, map) && this.term.isMapping(that.term, map);
		}
		return false;
	}

	@Override
	public IAbstraction<T> type() {
		return new Abstraction<>(domain(), x -> apply(x).type());
	}

	public Reducible<T> domain() {
		return variable.type();
	}
	
	public boolean isConstant() {
		return !term.isDepending(variable);
	}
	
	public String toString() {
		return toString(new HashMap<>());
	}
	
	@Override
	public String toString(Map<Variable<T>, String> names) {
		if (!isConstant())
			return "λ"+variable.toString(names)+":"+variable.type().toString(names)+"."+term.toString(names);
		else if (variable.type() instanceof Abstraction)
			return "("+variable.type().toString(names)+")->"+term.toString(names);
		else
			return variable.type().toString(names)+"->"+term.toString(names);
	}

	@Override
	public boolean isDepending(Variable<T> variable) {
		return isConstant() && this.variable.isDepending(variable) || term.isDepending(variable);
	}

}