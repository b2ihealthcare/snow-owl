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
package com.b2international.snowowl.datastore.tasks;

import java.io.Serializable;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.ecore.EPackage;

/**
 * Cloneable and serializable representation of a task context.
 * <p>This interface is not intended to be implemented by clients. Clients should extend {@link TaskContext} instead.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITaskContext extends Cloneable, Serializable {

	/**Reserved character, used as delimiter. Cannot be used in {@link #getContextId()}.*/
	String DELIMITER = ":";
	
	/**Returns with the associated tooling ID of the task context.*/
	String getToolingId();
	
	/**Returns with the human readable name of the task context.*/
	String getLabel();
	
	/**Returns with the unique ID of the task context.
	 *<p><b>NOTE:&nbsp;</b><i>colons</i> [&#58;] are prohibited in the task context ID.
	 *@see #DELIMITER*/
	String getContextId();
	
	/**Returns with the {@link ITaskContextBranchManagementPolicy branch management policy} for the task context.*/
	ITaskContextBranchManagementPolicy getPolicy();

	/**
	 * Returns <code>true</code>, if the implementation is component scoped, <code>false</code> otherwise.
	 * @return
	 */
	boolean isComponentScoped();

	/**
	 * Returns the {@link EPackage} instance specific for this {@link TaskContext}.
	 * @param taskContext
	 * @return
	 */
	EPackage getEPackage();

	/**
	 * Returns <code>true</code> when the terminology corresponding to this {@link ITaskContext} instance provides/supports meta data properties, otherwise <code>false</code>.
	 * @return
	 */
	boolean isMetadataPropertyProvider();

	/**
	 * As task context could be excessively used via property testers, which may disrupt the UI thread, we rather clone an existing instance
	 * instead of always create a new one via {@link IConfigurationElement#createExecutableExtension(String)}.
	 * <p>
	 * {@inheritDoc}}
	 */
	ITaskContext clone() throws CloneNotSupportedException;
	
}