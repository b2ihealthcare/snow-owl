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
package com.b2international.snowowl.core;

import java.io.Serializable;

import com.b2international.snowowl.core.api.IComponent;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * This class uniquely identifies a terminology independent component. 
 * @param <K> type of the terminology independent component's identifier.
 * @see IComponent
 * @see CoreTerminologyBroker#getComponent(ComponentIdentifierPair)
 */
public class ComponentIdentifierPair<K> implements Serializable {

	private static final long serialVersionUID = -8872004877194511407L;

	private static final NullComponentIdentifierPair NULL_IMPL = new NullComponentIdentifierPair();

	private final String terminologyComponentId;
	private final K componentId;
	
	/**
	 * Returns with a component identifier pair where the terminology component identifier is 
	 * UNSPECIFIED and the component identifier is {@code null}.
	 * @return a special component identifier pair with {@code null} component identifier.
	 * @see CoreTerminologyBroker#UNSPECIFIED
	 */
	@SuppressWarnings("unchecked")
	public static final <T> ComponentIdentifierPair<T> emptyPair() {
		return (ComponentIdentifierPair<T>) NULL_IMPL;
	}  

	/**
	 * Creates a new {@link ComponentIdentifierPair component identifier pair} instance.
	 * @param component the terminology independent component.
	 * @return a new component identifier pair.
	 */
	public static <K> ComponentIdentifierPair<K> create(final IComponent<K> component) {
		Preconditions.checkNotNull(component, "Component argument cannot be null.");
		return new ComponentIdentifierPair<K>(
				CoreTerminologyBroker.getInstance().getTerminologyComponentId(component), 
				component.getId());
	}
	
	/**
	 * Creates a new {@link ComponentIdentifierPair component identifier pair} instance.
	 * @param component the terminology independent component.
	 * @return a new component identifier pair.
	 */
	public static <K> ComponentIdentifierPair<K> create(final Object component) {
		Preconditions.checkNotNull(component, "Component argument cannot be null.");
		@SuppressWarnings("unchecked")
		final K id = (K) CoreTerminologyBroker.getInstance().adapt(component).getId();
		return new ComponentIdentifierPair<K>(
				CoreTerminologyBroker.getInstance().getTerminologyComponentId(component), 
				id);
	}
	
	/**
	 * Creates a new {@link ComponentIdentifierPair component identifier pair} instance.
	 * @param terminologyComponentId the unique terminology component identifier.
	 * @param componentId unique identifier of the component.
	 * @return a new component identifier pair.
	 */
	public static <K> ComponentIdentifierPair<K> create(final String terminologyComponentId, final K componentId) {
		Preconditions.checkNotNull(terminologyComponentId, "Terminology component identifier argument cannot be null.");
		Preconditions.checkNotNull(componentId, "Component identifier argument cannot be null.");
		return new ComponentIdentifierPair<K>(terminologyComponentId, componentId);
	}
	
	/**
	 * Creates a new {@link ComponentIdentifierPair component identifier pair} instance, where 
	 * the component identifier is allowed to be <code>null</code>.
	 * @param terminologyComponentId the unique terminology component identifier.
	 * @param componentId unique identifier of the component.
	 * @return a new component identifier pair.
	 */
	public static <K> ComponentIdentifierPair<K> createWithUncheckedComponentId(final String terminologyComponentId, final K componentId) {
		Preconditions.checkNotNull(terminologyComponentId, "Terminology component identifier argument cannot be null.");
		return new ComponentIdentifierPair<K>(terminologyComponentId, componentId);
	}
	
	/**
	 * Creates a new {@link ComponentIdentifierPair component identifier pair} instance.
	 * @param terminologyComponentIdValue the unique terminology component identifier as a <b>short</b>.
	 * @param componentId unique identifier of the component.
	 * @return a new component identifier pair.
	 */
	public static <K> ComponentIdentifierPair<K> create(final short terminologyComponentIdValue, final K componentId) {
		Preconditions.checkNotNull(componentId, "Component identifier argument cannot be null.");
		return new ComponentIdentifierPair<K>(CoreTerminologyBroker.getInstance().getTerminologyComponentId(terminologyComponentIdValue), componentId);
	}
	
	private ComponentIdentifierPair(final String terminologyComponentId, final K componentId) {
		this.terminologyComponentId = terminologyComponentId;
		this.componentId = componentId;
	}
	
	/**
	 * Returns with the unique identifier of the component.
	 * @return unique identifier of the component.
	 */
	public K getComponentId() {
		return componentId;
	}
	
	/**
	 * Returns with the unique terminology component identifier.
	 * @return unique terminology component identifier.
	 */
	public String getTerminologyComponentId() {
		return terminologyComponentId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("Terminology component ID", terminologyComponentId).add("ID", componentId).toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((componentId == null) ? 0 : componentId.hashCode());
		result = prime * result + ((terminologyComponentId == null) ? 0 : terminologyComponentId.hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ComponentIdentifierPair<?> other = (ComponentIdentifierPair<?>) obj;
		if (componentId == null) {
			if (other.componentId != null)
				return false;
		} else if (!componentId.equals(other.componentId))
			return false;
		if (terminologyComponentId == null) {
			if (other.terminologyComponentId != null)
				return false;
		} else if (!terminologyComponentId.equals(other.terminologyComponentId))
			return false;
		return true;
	}
	
	private static final class NullComponentIdentifierPair extends ComponentIdentifierPair<String> implements Serializable {
		private static final long serialVersionUID = 3533657601536175412L;

		public NullComponentIdentifierPair() {
			super(CoreTerminologyBroker.UNSPECIFIED, null);
		}
	}
	
}