/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.cdo;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.datastore.cdo.ICDOConflictProcessor;
import com.b2international.snowowl.datastore.cdo.NullCDOConflictProcessor;

/**
 * Broker for terminology specific {@link ICDOConflictProcessor conflict processors}.
 */
public enum CDOConflictProcessorBroker {

	/**
	 * The singleton instance.
	 */
	INSTANCE;

	public ICDOConflictProcessor getProcessor(final String repositoryUuid) {
		checkNotNull(repositoryUuid, "Repository identifier may not be null.");

		final Collection<ICDOConflictProcessor> processors = Extensions.getExtensions(ICDOConflictProcessor.EXTENSION_ID, ICDOConflictProcessor.class);
		for (final ICDOConflictProcessor processor : processors) {
			if (repositoryUuid.equals(processor.getRepositoryUuid())) {
				return processor;
			}
		}

		return new NullCDOConflictProcessor(repositoryUuid);
	}
}
