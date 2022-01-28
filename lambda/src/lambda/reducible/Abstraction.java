package lambda.reducible;

import java.util.Map;
import java.util.function.Function;

import lambda.Reducible;
import lambda.Type;

public class Abstraction<V,T> implements Reducible<T> {

	private final Variable<V> variable;
	private final Reducible<T> term;

	public Abstraction(Reducible<Type<V>> type, Function<Variable<V>, Reducible<T>> lambda) {
		this.variable = new Variable<>(type);
		this.term = lambda.apply(variable);
	}

	@Override
	public <X> Reducible<T> replace(Variable<X> variable, Reducible<X> term) {
		return new Abstraction<>(this.variable.type().replace(variable, term), x -> apply(x).replace(variable, term));
	}

	public Reducible<T> apply(Reducible<V> parameter) {
		return term.replace(variable, parameter);
	}

	@Override
	public Reducible<T> reduce() {
		return new Abstraction<>(variable.type().reduce(), x -> apply(x).reduce());
	}

	@Override
	public boolean isMapping(Reducible<?> term, Map<Variable<?>, Reducible<?>> map) {
		if (term instanceof Abstraction) {
			Abstraction<?,?> that = (Abstraction<?,?>) term;
			return this.variable.isMapping(that.variable, map) && this.term.isMapping(that.term, map);
		}
		return false;
	}

	@Override
	public Reducible<Type<T>> type() {
		return new Abstraction<>(domain(), x -> apply(x).type());
	}

	public Reducible<Type<V>> domain() {
		return variable.type();
	}
	
	public String toString() {
		if (term.contains(variable))
			return "λ"+variable.toString()+":"+variable.type().toString()+"."+term.toString();
		else if (variable.type() instanceof Abstraction)
			return "("+variable.type()+")->"+term;
		else
			return variable.type()+"->"+term;
	}

	@Override
	public boolean contains(Variable<?> variable) {
		return term.contains(variable);
	}

}