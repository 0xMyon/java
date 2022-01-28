package fsm;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import lang.Type;

class Transition<T,TYPE extends Type<TYPE,T>,R> {
	
	private final State<T,TYPE,R> source, target;
	
	private final TYPE value;
	
	private final List<R> result = new LinkedList<>();
		
	@SafeVarargs
	Transition(State<T,TYPE,R> source, TYPE value, State<T,TYPE,R> target, R... rs) {
		this.source = source;
		this.value = value;
		this.target = target;
		for (R r : rs) this.result.add(r);
	}
	
	Transition(State<T,TYPE,R> source, TYPE value, State<T,TYPE,R> target, List<R> rs) {
		this.source = source;
		this.value = value;
		this.target = target;
		this.result.addAll(rs);
	}
	
	State<T,TYPE,R> source() { return source; }
	
	State<T,TYPE,R> target() { return target; }
	
	TYPE value() { return value; }
	
	public boolean equals(Object object) {
		if (object instanceof Transition) {
			Transition<?,?,?> that = (Transition<?,?,?>) object;
			return Objects.equals(source, that.source) && 
					Objects.equals(target, that.target) &&
					Objects.equals(value, that.value);
		}
		return false;
	}
	
	public int hashCode() {
		return Objects.hash(source(), target(), value());
	}
	
	public String toString() {
		return source().toString()+"["+value().toString()+"]"+target().toString();
	}

	public List<R> result() {
		return result;
	}
	
}