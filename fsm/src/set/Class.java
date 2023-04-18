package set;

import java.util.Objects;

import lang.Container;

public class Class<T> implements Container<Class<T>, T> {

	private final T representatnt;

	public Class(final T representant) {
		this.representatnt = representant;
	}

	@Override
	public Class<T> THIS() {
		return this;
	}

	@Override
	public boolean isEqual(final Class<T> that) {
		return Objects.equals(this.representatnt, that.representatnt);
	}

	@Override
	public boolean contains(final T that) {
		return Objects.equals(this.representatnt, that);
	}

	@Override
	public int hashCode() {
		return representatnt.hashCode();
	}

	@Override
	public String toString() {
		return "["+representatnt.toString()+"]";
	}

	@Override
	public boolean equals(final Object other) {
		if (other instanceof Class) {
			final Class<?> that = (Class<?>) other;
			return Objects.equals(this.representatnt, that.representatnt);
		}
		return false;
	}

}
