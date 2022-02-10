package lang;

public interface Identifiable<THIS extends Identifiable<THIS>> {

	THIS THIS();
	
	boolean isEqual(THIS that);
	
}
