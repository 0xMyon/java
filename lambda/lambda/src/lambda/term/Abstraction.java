package lambda.term;

import java.util.Map;
import java.util.function.Function;

import lambda.Reducible;
import lambda.Type;
import lambda.reducible.Variable;

public class Abstraction<V,T> implements Reducible<T> {

	private final Variable<V> variable;
	private final Reducible<T> term;

	public Abstraction(Reducible<Type<V>> type, Function<Variable<V>, Reducible<T>> lambda) {
		this.variable = new Variable<V>(type);
		this.term = lambda.apply(variable);
	}

	@Override
	public <X> Reducible<T> replace(Variable<X> variable, Reducible<X> term) {
		return new Abstraction<V,T>(this.variable.type(), x -> apply(x).replace(variable, term));
	}

	public Reducible<T> apply(Reducible<V> parameter) {
		return term.replace(variable, parameter);
	}

	@Override
	public Reducible<T> reduce() {
		return new Abstraction<V,T>(variable.type(), x -> apply(x).reduce());
	}

	@Override
	public boolean isEqual(Reducible<?> term, Map<Variable<?>, Variable<?>> map) {
		if (term instanceof Abstraction) {
			Abstraction<?,?> that = (Abstraction<?,?>) term;
			return this.variable.isEqual(that.variable, map) && this.term.isEqual(that.term, map);
		}
		return false;
	}

	@Override
	public Reducible<Type<T>> type() {
		return new Abstraction<V, Type<T>>(domain(), x -> term.type());
	}

	public Reducible<Type<V>> domain() {
		return variable.type();
	}
	
	public String toString() {
		return "λ"+variable.toString()+":"+variable.type().toString()+"."+term.toString();
	}

}