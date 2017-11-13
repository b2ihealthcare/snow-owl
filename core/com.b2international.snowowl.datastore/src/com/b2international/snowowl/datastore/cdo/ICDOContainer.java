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

import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

import com.b2international.snowowl.core.api.NsUri;

/**
 * Basic representation of a CDO container.
 * @param <T> - type of the managed service.
 */
public interface ICDOContainer<T extends ICDOManagedItem<T>> extends ILifecycle, Iterable<T> {

	/**Returns with the managed item for the {@link Class class} argument.*/
	T get(final Class<?> clazz);
	
	/**Returns with the managed item for the package.*/
	T get(final EPackage ePackage);
	
	/**Returns with the managed service for a given Ecore model given as its unique {@link NsUri namespace URI}.*/
	T get(final NsUri nsUri);
	
	/**Returns with the managed service for a given Ecore model given as its unique namespace URI.*/
	T get(final String nsUri);
	
	/**Returns with the managed service based on the {@link EClass}.*/
	T get(final EClass eClass);
	
	/**Returns with the manager item for the CDO ID.*/
	T get(final long cdoId);
	
	/**Returns with the manager item for the CDO ID.*/
	T get(final CDOID cdoId);
	
	/**Returns with the manager for the UUID argument.*/
	T getByUuid(final String uuid);
	
	/**Returns all managed item UUIDs.*/
	Set<String> uuidKeySet();
	
	/**Returns with {@code true} if the managed item is a meta item. Otherwise {@code false}.*/
	boolean isMeta(final String uuid);
	
	/**Returns with the UUID of a managed item where the current item depends on. 
	 *<br>May return with {@code null} if the managed item does not depend on any others.*/
	@Nullable String getMasterUuid(final String uuid);
	
	/**Returns with the UUID of a managed item which depends on the current item. 
	 *<br>May return with {@code null} if non of the registered managed items depends on the given one.*/
	@Nullable String getSlaveUuid(final String uuid);
	
	/**Returns with the UUID of a managed item which depends on the current item. 
	 *<br>May return with {@code null} if non of the registered managed items depends on the given one.*/
	@Nullable String getSlaveUuid(final T managedItem);
}