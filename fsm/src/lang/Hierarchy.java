package lang;

public interface Hierarchy<THIS extends Hierarchy<THIS>> extends Identifiable<THIS> {

	/**
	 * @param that {@link Type} to be tested
	 * @return true, if that is contained
	 */
	boolean containsAll(final THIS that);
	
	
	/**
	 * @param that
	 * @return true, if both types are equal
	 */
	default boolean isEqual(final THIS that) {
		return this.containsAll(that) && that.containsAll(THIS());
	}
	
}
