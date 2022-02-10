package lang;

public interface Container<THIS extends Container<THIS, T>, T> extends Identifiable<THIS> {

	boolean contains(T that);
	
}
