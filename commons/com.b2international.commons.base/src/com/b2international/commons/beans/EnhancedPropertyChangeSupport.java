/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * <p>This is a utility class that can be used by beans that support bound
 * properties.  You can use an instance of this class as a member field
 * of your bean and delegate various work to it.</p>
 * 
 * <p>This enhanced version will not add the same listener twice, and will
 * not do anything if there are no listeners for the fired property (the
 * original implementation calls <code>equals()</code> on the values,
 * even if there are no listeners, and this causes problems e.g. with lazily loaded
 * properties on the server side.</p>
 * 
 */
public class EnhancedPropertyChangeSupport extends PropertyChangeSupport implements Serializable {

	private static final long serialVersionUID = -5116761554941042894L;

	public EnhancedPropertyChangeSupport(Object sourceBean) {
		super(sourceBean);
	}

	/**
	 * @param listener add this listener if it is not yet added 
	 */
    public synchronized void addPropertyChangeListener(
    		PropertyChangeListener listener) {

		PropertyChangeListener[] propertyChangeListeners = getPropertyChangeListeners();
		for (PropertyChangeListener propertyChangeListener : propertyChangeListeners) {
			if (propertyChangeListener.equals(listener))
				return;
		}
		super.addPropertyChangeListener(listener);
    }
    
	/**
	 * @param propertyName name of property to listen to 
	 * @param listener add this listener if it is not yet added to this property 
	 */
    public synchronized void addPropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
		
    	PropertyChangeListener[] propertyChangeListeners = getPropertyChangeListeners();
		for (PropertyChangeListener propertyChangeListener : propertyChangeListeners) {
			if (propertyChangeListener.equals(listener))
				return;
		}
		super.addPropertyChangeListener(propertyName, listener);
    }
    
    /**
      * Fire an existing PropertyChangeEvent to any registered listeners. Nothing happens if
      * there are no listeners, not even <code>equals()</code>.
      * @param evt
      */
    public void firePropertyChange(PropertyChangeEvent evt) {
    	if(hasListeners(evt.getPropertyName())) {
    		super.firePropertyChange(evt);
    	}
    }

    /**
     * Report a bound property update to any registered listeners.
     * No event is fired if old and new are equal and non-null.
     * Nothing happens if there are no listeners, not
     * even <code>equals()</code>.
	 */
    public void firePropertyChange(String propertyName, 
			Object oldValue, Object newValue) {
    	if(hasListeners(propertyName)) {
    		super.firePropertyChange(propertyName, oldValue, newValue);
    	}
    }
}