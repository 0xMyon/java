package lang;


public interface InfiniteSet<T> extends Type<InfiniteSet<T>, T> {

	public interface Visitor<T,R> {
		R handle(FiniteSet<T> that);
		R handle(ComplementSet<T> that);
	}
	
	abstract <R> R accept(Visitor<T, R> visitor);
	
	@Override
	public default InfiniteSet<T> THIS() {
		return this;
	}

	public static class Factory<T> implements Type.Factory<InfiniteSet<T>, T> {
		@Override
		public InfiniteSet<T> empty() {
			return new FiniteSet<>();
		}

		@Override
		public InfiniteSet<T> summand(T that) {
			return new FiniteSet<>(that);
		}
	}
	
	
	@Override
	public default Factory<T> factory() {
		return new Factory<>();
	}
		
}
