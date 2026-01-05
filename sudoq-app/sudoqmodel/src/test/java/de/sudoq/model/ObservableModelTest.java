package de.sudoq.model;


import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class ObservableModelTest {

	static ObservableModel<Void> observable;
	static Listener<Void> listener;

    @BeforeAll
    static void initObservable() {
		observable = new ObservableModelImpl<Void>() {};
		listener = new Listener<>();
	}

    @Test
    void notification() {

		observable.registerListener(listener);
        assertEquals(0, listener.callCount);

		observable.notifyListeners(null);
        assertEquals(1, listener.callCount);

		observable.removeListener(listener);
		observable.notifyListeners(null);
        assertEquals(1, listener.callCount);
	}

	static class Listener<T> implements ModelChangeListener<T> {
		int callCount = 0;

		@Override
		public void onModelChanged(T obj) {
			callCount++;
		}

	}

}
