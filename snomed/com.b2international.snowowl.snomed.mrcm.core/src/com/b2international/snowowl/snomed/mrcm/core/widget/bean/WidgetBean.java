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
package com.b2international.snowowl.snomed.mrcm.core.widget.bean;

import java.io.Serializable;
import java.util.UUID;

import com.b2international.commons.beans.BeanPropertyChangeSupporter;

/**
 * An abstract backing bean for a UI element that displays a property of a
 * SNOMED CT concept; carries the minimum amount of information necessary for
 * presentation and conversion to/from the corresponding EMF model element.
 * 
 */
public abstract class WidgetBean extends BeanPropertyChangeSupporter implements Serializable {

	private static final long serialVersionUID = 8839115568215516306L;

	/** The property name for the {@link #isCloneActionEnabled() cloneActionEnabled} property. */
	public static final String PROP_CLONE_ACTION_ENABLED = "cloneActionEnabled";
	
	/** The property name for the {@link #isRetireActionEnabled() retireActionEnabled} property. */
	public static final String PROP_RETIRE_ACTION_ENABLED = "retireActionEnabled";

	/** The property name for the {@link #isCloneAndRetireActionEnabled() cloneAndRetireActionEnabled} property. */
	public static final String PROP_CLONE_AND_RETIRE_ACTION_ENABLED = "cloneAndRetireActionEnabled";

	// internal unique identifier, currently only used by equals() and hashCode()
	private final UUID id = UUID.randomUUID();
	
	/**
	 * Default constructor for serialization.
	 */
	protected WidgetBean() {
		super();
	}
	
	/**
	 * Callback method invoked when the user invokes the "clone" action, usually resulting in a cloned instance (based
	 * on the same widget model as this instance, if such a model exists) being added to the backing model.
	 * <p>
	 * The default implementation throws {@link UnsupportedOperationException}; subclasses should override.
	 * 
	 * @returns the replicated widget bean
	 */
	public WidgetBean onCloneAction() {
		throw new UnsupportedOperationException("Cloning is not supported.");
	}

	/**
	 * Callback method invoked when the user invokes the "retire" action, which removes this element from the backing
	 * model.
	 * <p>
	 * The default implementation throws {@link UnsupportedOperationException}; subclasses should override.
	 */
	public void onRetireAction() {
		throw new UnsupportedOperationException("Retiring is not supported.");
	}

	/**
	 * Checks if the "clone" action should be enabled, allowing the user to add more instances of this bean.
	 * 
	 * @return {@code true} if the "clone" action should be enabled in the context menu beside the element,
	 * {@code false} otherwise
	 */
	public boolean isCloneActionEnabled() {
		return false;
	}

	/**
	 * Checks if the "retire" action should be enabled for this instance, allowing the user to remove the selected bean
	 * from the property set.
	 * 
	 * @return {@code true} if the "retire" action should be enabled in the context menu beside the element,
	 * {@code false} otherwise
	 */
	public boolean isRetireActionEnabled() {
		return false;
	}

	/**
	 * Checks if the "clone and retire" action should be enabled for this instance.
	 * 
	 * @return {@code true} if the "clone and retire" action should be enabled in the context menu beside the element,
	 * {@code false} otherwise
	 */
	public boolean isCloneAndRetireActionEnabled() {
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		WidgetBean other = (WidgetBean) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}