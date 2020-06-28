package de.sudoq.model;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;


public class ObservableModelTest {

	static ObservableModel<Void> observable;
	static Listener<Void> listener;

	@BeforeClass
	public static void initObservable() {
		observable = new ObservableModelImpl<Void>() {};
		listener = new Listener<>();
	}

	// we can't use the newest method because we're constrained to language level 7...
	@Test(expected = IllegalArgumentException.class)
	public void testRegisterNull(){
		observable.registerListener(null);
	}

	@Test
	public void testNotification() {

		observable.registerListener(listener);
		assertTrue(listener.callCount == 0);

		observable.notifyListeners(null);
		assertTrue(listener.callCount == 1);

		observable.removeListener(listener);
		observable.notifyListeners(null);
		assertTrue(listener.callCount == 1);
	}

	static class Listener<T> implements ModelChangeListener<T> {
		int callCount = 0;

		@Override
		public void onModelChanged(T obj) {
			callCount++;
		}

	}

}
