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

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.index.DocumentUpdaterBase;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

/**
 * @since 4.3
 */
public class ComponentMutablePropertyUpdater extends DocumentUpdaterBase<SnomedDocumentBuilder> {

	private Component component;

	public ComponentMutablePropertyUpdater(Component component) {
		super(component.getId());
		this.component = component;
	}
	
	@Override
	public void doUpdate(SnomedDocumentBuilder doc) {
		doc
			.removeAll(SnomedMappings.effectiveTime());
		doc
			.active(component.isActive())
			.released(component.isReleased())
			.effectiveTime(component.isSetEffectiveTime() ? component.getEffectiveTime().getTime() : EffectiveTimes.UNSET_EFFECTIVE_TIME);
	}
	
	protected Component getComponent() {
		return component;
	}
}
