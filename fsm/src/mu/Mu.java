package mu;

import java.util.List;

import lang.Language;

public interface Mu<T> extends Language<Mu<T>, T> {

	@Override
	public default Mu<T> THIS() {
		return this;
	}
	
	@Override
	public default Factory<T> factory() {
		return new Factory<>();
	}

	@Override
	public default Mu<T> iterate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public default Mu<T> optional() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public default Mu<T> parallel(Mu<T> that) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public default boolean contains(List<T> that) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public default boolean containsAll(Mu<T> that) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public default boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public default boolean isFinite() {
		// TODO Auto-generated method stub
		return false;
	}

	

	@Override
	public default Mu<T> concat(Mu<T> that) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public default Mu<T> reverse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public default boolean isEpsilon() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public default Mu<T> unite(Mu<T> that) {
		return Alternative.of(this, that);
	}
	
	public static class Factory<T> implements Language.Factory<Mu<T>, T> {
		
		@Override
		public Mu<T> empty() {
			return Alternative.of();
		}
		@Override
		public Mu<T> epsilon() {
			return universe();
		}
		@Override
		public Mu<T> factor(T that) {
			return new Possibility<>(that, epsilon());
		}
		
	}
	
}
