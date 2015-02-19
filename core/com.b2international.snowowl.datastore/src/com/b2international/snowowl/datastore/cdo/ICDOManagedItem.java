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
package com.b2international.snowowl.datastore.cdo;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

/**
 * Representation of a managed CDO specific item. 
 * @see ILifecycle
 * @see ICDOContainer
 */
public interface ICDOManagedItem<T extends ICDOManagedItem<T>> extends ILifecycle {

	/**
	 * Sets the container for the current {@link ICDOManagedItem managed item.} Operation is not bidirectional. 
	 * @param container the appropriate container of the current managed item.
	 */
	void setContainer(final ICDOContainer<T> container);
	
	/**
	 * Returns with the unique namespace ID of the current managed item.
	 * @return the namespace ID.
	 */
	byte getNamespaceId();
	
	/**
	 * Returns with the unique ID of the current managed item.
	 * @return the unique ID of the item.
	 */
	String getUuid();

	/**
	 * Returns with the human readable name of the associated repository for the current item. 
	 * @return the name of the associated repository.
	 */
	String getRepositoryName();
	
	/**
	 * Returns with the Snow Owl specific unique ID of the associated tooling support.
	 * @return the unique application specific ID of the associated component.
	 */
	String getSnowOwlTerminologyComponentId();
	
	/**
	 * Returns with the application specific human readable name of component identified 
	 * by the {@link #getSnowOwlTerminologyComponentId() Snow Owl terminology component ID}. 
	 * @return the human readable name of the associated application specific component.
	 */
	String getSnowOwlTerminologyComponentName();
	
	/**
	 * Returns with the {@link CDOPackageRegistry package registry} for the underlying managed item.
	 * @return the package registry for the managed item. 
	 */
	CDOPackageRegistry getPackageRegistry();
	
	/**Returns with {@code true} if the item is a meta managed item. Otherwise {@code false}.*/
	boolean isMeta();
	
	/**Returns with the UUID of a managed item where the current item depends on. 
	 *<br>May return with {@code null} if the managed item does not depend on any others.*/
	@Nullable String getMasterUuid();
	
}