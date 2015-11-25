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
package com.b2international.snowowl.snomed.datastore.server.request;

import java.util.List;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.server.converter.SnomedConverters;

/**
 * @since 4.5
 */
final class SnomedDescriptionGetRequest extends GetRequest<ISnomedDescription> {

	protected SnomedDescriptionGetRequest() {
		super(ComponentCategory.DESCRIPTION);
	}

	@Override
	protected ILookupService<String, ? extends CDOObject, CDOView> getLookupService() {
		return new SnomedDescriptionLookupService();
	}
	
	@Override
	protected ISnomedDescription process(BranchContext context, IComponent<String> component, List<String> expand) {
		return SnomedConverters.newDescriptionConverter(context, expand, locales()).convert((SnomedDescriptionIndexEntry) component);
	}
	
	@Override
	protected Class<ISnomedDescription> getReturnType() {
		return ISnomedDescription.class;
	}

}
