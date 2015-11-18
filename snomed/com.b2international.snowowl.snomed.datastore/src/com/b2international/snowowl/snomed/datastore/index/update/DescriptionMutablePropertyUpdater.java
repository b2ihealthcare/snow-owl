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
package com.b2international.snowowl.snomed.datastore.index.update;

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_CASE_SIGNIFICANCE_ID;
import static java.lang.Long.parseLong;

import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;

/**
 * @since 4.3
 */
public class DescriptionMutablePropertyUpdater extends ComponentMutablePropertyUpdater {

	public DescriptionMutablePropertyUpdater(Description component) {
		super(component);
	}
	
	@Override
	public void doUpdate(SnomedDocumentBuilder doc) {
		super.doUpdate(doc);
		
		doc
			.update(Mappings.storedOnlyLongFieldWithDocValues(DESCRIPTION_CASE_SIGNIFICANCE_ID), getCaseSignificanceId())
			.update(Mappings.label(), getComponent().getTerm());
	}

	private long getCaseSignificanceId() {
		return parseLong(getComponent().getCaseSignificance().getId());
	}
	
	@Override
	protected Description getComponent() {
		return (Description) super.getComponent();
	}

}
