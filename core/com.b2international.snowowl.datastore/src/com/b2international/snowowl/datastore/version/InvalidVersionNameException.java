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

import org.eclipse.core.runtime.IStatus;

import com.b2international.commons.status.SerializableStatus;
import com.b2international.snowowl.datastore.DatastoreActivator;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Exception indicating invalid version name.
 */
public class InvalidVersionNameException extends Exception {

	private static final long serialVersionUID = -5135908605847039118L;
	private IStatus status;
	
	/**Creates a new exception with the given status argument.*/
	public InvalidVersionNameException(final IStatus status) {
		super(Preconditions.checkNotNull(status).getMessage());
		this.status = status instanceof Serializable ? status : new SerializableStatus(status); 
	}
	
	/**Creates a new exception with the given message.*/
	public InvalidVersionNameException(final String message) {
		super(message);
		this.status = new SerializableStatus(IStatus.ERROR, DatastoreActivator.PLUGIN_ID, Strings.nullToEmpty(message));
	}

	/**
	 * Returns with the {@link IStatus status} for the exception.
	 */
	public IStatus getStatus() {
		return status;
	}
	
}