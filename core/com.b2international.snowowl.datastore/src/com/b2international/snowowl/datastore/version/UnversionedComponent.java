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
package com.b2international.snowowl.datastore.version;

import java.io.Serializable;

import com.b2international.snowowl.core.ComponentTypeNameCache;
import com.b2international.snowowl.core.api.ExtendedComponent;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.IComponentPropertyProvider;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Represents an unversioned component.
 */
public class UnversionedComponent implements Serializable {

	private static final long serialVersionUID = 4205147201759233528L;

	private final long storageKey;
	private final String id;
	private final String label;
	private final String componentType;

	/**Creates a new unversioned component with the given storage key and the {@link ExtendedComponent extended component}.*/
	public UnversionedComponent(final long storageKey, final ExtendedComponent extendedComponent) {
		this(
				storageKey,
				Preconditions.checkNotNull(extendedComponent).getId(),
				extendedComponent.getLabel(),
				ComponentTypeNameCache.INSTANCE.getComponentName(extendedComponent)
				);
	}
	
	/**Creates a new unversioned component with the given storage key and the {@link IComponentPropertyProvider property provider}.*/
	public UnversionedComponent(final long storageKey, final IComponentPropertyProvider propertyProvider) {
		this(
				storageKey,
				Preconditions.checkNotNull(propertyProvider).getId(),
				propertyProvider.getLabel(),
				propertyProvider.getArtefactType()
				);
	}
	
	/**
	 * Creates a new instance representing a unversioned component.
	 * @param storageKey the unique storage key of the component.
	 * @param id the component ID.
	 * @param label the label of the component.
	 * @param componentType the component artefact type as string.
	 */
	public UnversionedComponent(final long storageKey, final String id, final String label, final String componentType) {
		Preconditions.checkArgument(storageKey > CDOUtils.NO_STORAGE_KEY);
		this.storageKey = storageKey;
		this.id = Preconditions.checkNotNull(id);
		this.label = Preconditions.checkNotNull(label);
		this.componentType = Preconditions.checkNotNull(componentType);
	}
	
	/**
	 * Returns with the unique storage key.
	 */
	public long getStorageKey() {
		return storageKey;
	}

	/**
	 * Returns with the terminology specific ID for the component.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns with the component label.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Returns with the component type as a string.
	 */
	public String getComponentType() {
		return componentType;
	}

	/**Transforms the current instance into an {@link IComponent component}.*/
	public IComponent<String> toComponent() {
		return new IComponent<String>() {
			private static final long serialVersionUID = 7838381264155800928L;
			@Override public String getId() { return id; }
			@Override public String getLabel() { return label; }
		}; 
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (storageKey ^ (storageKey >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UnversionedComponent))
			return false;
		final UnversionedComponent other = (UnversionedComponent) obj;
		if (storageKey != other.storageKey)
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("ID", id)
				.add("Label", label)
				.add("Storage key", storageKey)
				.add("Type", componentType)
				.toString();
	}
	
}