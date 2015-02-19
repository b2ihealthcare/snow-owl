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
package com.b2international.snowowl.snomed.mrcm.core.validator;

import org.eclipse.core.runtime.IStatus;

import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ModeledWidgetBean;
import com.google.common.collect.Multimap;

/**
 * Client side {@link ConceptWidgetBean} validator.
 * 
 */
public class ClientWidgetBeanValidator implements IClientWidgetBeanValidator {

	private final IWidgetBeanValidator wrappedService;

	public ClientWidgetBeanValidator(IWidgetBeanValidator wrappedService) {
		this.wrappedService = wrappedService;
	}
	
	/**
	 * Validates a {@link ConceptWidgetBean} and returns with a multimap of status informations.
	 * <br>Keys are the {@link ModeledWidgetBean} instances.
	 * <br>Values are the associated validation status. 
	 * @param concept the {@link ConceptWidgetBean} to validate. Represents a SNOMED&nbsp;CT concept.
	 * @return a multimap of validation status. Can be empty.
	 */
	public Multimap<ModeledWidgetBean, IStatus> validate(final ConceptWidgetBean conceptWidgetBean) {
		return wrappedService.validate(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE), conceptWidgetBean);
	}
}