/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.commons.databinding;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

import org.eclipse.core.databinding.observable.AbstractObservable;
import org.eclipse.core.databinding.observable.DisposeEvent;
import org.eclipse.core.databinding.observable.IDisposeListener;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.IStaleListener;
import org.eclipse.core.databinding.observable.ObservableTracker;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.StaleEvent;

/**
 * An observable which decorates other observables.
 * 
 */
public class CompositeObservable extends AbstractObservable {

	private IStaleListener staleListener;
	private boolean disposedDecoratedOnDispose;
	private final Collection<IObservable> decoratedObservables;
	private final CountDownLatch disposeCountDownLatch;

	public CompositeObservable(Collection<IObservable> decoratedObservables, boolean disposeDecoratedOnDispose) {
		super(getRealm(decoratedObservables));
		this.decoratedObservables = decoratedObservables;
		this.disposedDecoratedOnDispose = disposeDecoratedOnDispose;
		this.disposeCountDownLatch = new CountDownLatch(decoratedObservables.size());
		init();
	}

	private void init() {
		for (IObservable decorated : decoratedObservables) {
			decorated.addDisposeListener(new IDisposeListener() {
				public void handleDispose(DisposeEvent staleEvent) {
					disposeCountDownLatch.countDown();
				}
			});
		}
	}
	
	private static Realm getRealm(Collection<IObservable> decoratedObservables) {
		Realm realm = null;
		for (IObservable decorated : decoratedObservables) {
			Realm newRealm = decorated.getRealm();
			if (realm == null) {
				realm = newRealm;
			} else if (!realm.equals(newRealm)) {
				throw new IllegalArgumentException("Observables have different realms.");
			}
		}
		return realm;
	}

	public boolean isStale() {
		getterCalled();
		boolean stale = false;
		for (IObservable decorated : decoratedObservables) {
			stale |= decorated.isStale();
		}
		return stale;
	}

	protected void getterCalled() {
		ObservableTracker.getterCalled(this);
	}

	protected void firstListenerAdded() {
		if (staleListener == null) {
			staleListener = new IStaleListener() {
				public void handleStale(StaleEvent staleEvent) {
					CompositeObservable.this.handleStaleEvent(staleEvent);
				}
			};
		}
		for (IObservable decorated : decoratedObservables) {
			decorated.addStaleListener(staleListener);
		}
	}

	protected void lastListenerRemoved() {
		if (staleListener != null) {
			for (IObservable decorated : decoratedObservables) {
				decorated.removeStaleListener(staleListener);
			}
			staleListener = null;
		}
	}

	protected void handleStaleEvent(StaleEvent event) {
		fireStale();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((decoratedObservables == null) ? 0 : decoratedObservables
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompositeObservable other = (CompositeObservable) obj;
		if (decoratedObservables == null) {
			if (other.decoratedObservables != null)
				return false;
		} else if (!decoratedObservables.equals(other.decoratedObservables))
			return false;
		return true;
	}

	public synchronized void dispose() {
		if (staleListener != null) {
			for (IObservable decorated : decoratedObservables) {
				decorated.removeStaleListener(staleListener);
			}
		}
		if (disposedDecoratedOnDispose) {
			for (IObservable decorated : decoratedObservables) {
				decorated.dispose();
				disposeCountDownLatch.countDown();
			}
		}
		decoratedObservables.clear();
		staleListener = null;
		try {
			disposeCountDownLatch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		super.dispose();
	}
}