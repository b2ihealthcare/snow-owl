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
package com.b2international.snowowl.snomed.mrcm.core.extensions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * Represents an abstract {@link IConceptExtension} which accepts concepts based on reference set membership. 
 * 
 */
public abstract class AbstractRefSetBasedExtension implements IConceptExtension, IExecutableExtension {

	protected String refSetId;

	@Override
	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data) throws CoreException {
		refSetId = ClassUtils.checkAndCast(data, String.class);
	}

	@Override
	public boolean handlesConcept(final String branch, final String conceptId) {
		return isReferenced(branch, refSetId, conceptId);
	}

	private boolean isReferenced(String branch, String refSetId, String conceptId) {
		return SnomedRequests.prepareSearchMember()
				.setLimit(0)
				.filterByRefSet(refSetId)
				.filterByReferencedComponent(conceptId)
				.build(branch)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync().getTotal() > 0;
	}

}