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

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.observable.value.ValueDiff;

import com.b2international.commons.ReflectionUtils;
import com.b2international.commons.functions.UncheckedCastFunction;
import com.google.common.collect.Collections2;

/**
 * An observable value which decorates other observable values.
 *
 */
public abstract class CompositeObservableValue extends CompositeObservable implements IObservableValue {
	private IValueChangeListener valueChangeListener;

	private final Collection<IObservableValue> decoratedObservableValues;

	public CompositeObservableValue(Collection<IObservableValue> decoratedObservableValues,
			boolean disposeDecoratedOnDispose) {
		super(Collections2.transform(decoratedObservableValues, 
				new UncheckedCastFunction<IObservableValue, IObservable>(IObservable.class)), disposeDecoratedOnDispose);
		this.decoratedObservableValues = decoratedObservableValues;
	}

	public synchronized void addValueChangeListener(IValueChangeListener listener) {
		Object type = ReflectionUtils.getField(ValueChangeEvent.class, null, "TYPE");
		addListener(type, listener);
	}

	public synchronized void removeValueChangeListener(IValueChangeListener listener) {
		Object type = ReflectionUtils.getField(ValueChangeEvent.class, null, "TYPE");
		removeListener(type, listener);
	}

	protected void fireValueChange(ValueDiff diff) {
		// fire general change event first
		super.fireChange();
		fireEvent(new ValueChangeEvent(this, diff));
	}

	protected void fireChange() {
		throw new RuntimeException(
				"fireChange should not be called, use fireValueChange() instead"); //$NON-NLS-1$
	}

	protected void firstListenerAdded() {
		if (valueChangeListener == null) {
			valueChangeListener = new IValueChangeListener() {
				public void handleValueChange(ValueChangeEvent event) {
					CompositeObservableValue.this.handleValueChange(event);
				}
			};
		}
		for (IObservableValue decorated : decoratedObservableValues) {
			decorated.addValueChangeListener(valueChangeListener);
		}
		super.firstListenerAdded();
	}

	protected void lastListenerRemoved() {
		super.lastListenerRemoved();
		if (valueChangeListener != null) {
			for (IObservableValue decorated : decoratedObservableValues) {
				decorated.removeValueChangeListener(valueChangeListener);
			}
			valueChangeListener = null;
		}
	}

	protected void handleValueChange(final ValueChangeEvent event) {
		fireValueChange(event.diff);
	}

	public synchronized void dispose() {
		if (valueChangeListener != null) {
			for (IObservableValue decorated : decoratedObservableValues) {
				decorated.removeValueChangeListener(valueChangeListener);
			}
		}
		decoratedObservableValues.clear();
		valueChangeListener = null;
		super.dispose();
	}
}