package lambda;

public interface Term extends Reducible<Term> {

	Type type();
	
	default Term THIS() {
		return this;
	}
	
}
