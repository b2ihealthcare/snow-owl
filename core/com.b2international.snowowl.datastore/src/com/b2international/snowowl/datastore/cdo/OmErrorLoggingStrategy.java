/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.eclipse.net4j.util.om.log.OMLogger;

/**
 * Logger delegating to the {@link OMLogger OM logger}.
 *
 */
public enum OmErrorLoggingStrategy implements IErrorLoggingStrategy {

	/**Shared instance.*/
	INSTANCE;
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.IErrorLoggingStrategy#logError(java.lang.Throwable)
	 */
	@Override
	@SuppressWarnings("restriction")
	public void logError(final Throwable t) {
		org.eclipse.emf.cdo.internal.server.bundle.OM.LOG.error(t);
	}

	/**Logs the given message argument via the {@link OMLogger OM logger}.*/
	@SuppressWarnings("restriction")
	public void logError(final String msg) {
		org.eclipse.emf.cdo.internal.server.bundle.OM.LOG.error(msg);
	}
	
}