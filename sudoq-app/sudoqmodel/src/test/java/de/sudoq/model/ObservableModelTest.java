package de.sudoq.model;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.sudoq.model.ModelChangeListener;
import de.sudoq.model.ObservableModel;
import de.sudoq.model.ObservableModelImpl;

public class ObservableModelTest {

	@Test
	public void test() {
		ObservableModel<Void> observable = new ObservableModelImpl<Void>() {
		};
		Listener<Void> listener = new Listener<>();

		testNotification(observable, listener);
	}

	@SuppressWarnings("unchecked")
	public void testNotification(@SuppressWarnings("rawtypes") ObservableModel observable, @SuppressWarnings("rawtypes") Listener listener) {
		try {
			observable.registerListener(null);
			fail("No Exception thrown");
		} catch (IllegalArgumentException e) {
		}

		observable.registerListener(listener);
		assertTrue(listener.callCount == 0);

		observable.notifyListeners(null);
		assertTrue(listener.callCount == 1);

		observable.removeListener(listener);
		observable.notifyListeners(null);
		assertTrue(listener.callCount == 1);
	}

	class Listener<T> implements ModelChangeListener<T> {
		int callCount = 0;

		@Override
		public void onModelChanged(T obj) {
			callCount++;
		}

	}

}
