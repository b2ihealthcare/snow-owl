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
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;

/**
 * @since 4.3
 */
public class ComponentMutablePropertyUpdater extends DocumentUpdaterBase<SnomedDocumentBuilder> {

	private Component component;
	private String effectiveTimeField;

	public ComponentMutablePropertyUpdater(Component component, String effectiveTimeField) {
		super(component.getId());
		this.component = component;
		this.effectiveTimeField = effectiveTimeField;
	}
	
	@Override
	public void doUpdate(SnomedDocumentBuilder doc) {
		doc
			.removeAll(Mappings.longDocValuesField(effectiveTimeField));
		doc
			.active(component.isActive())
			.released(component.isReleased())
			.docValuesField(effectiveTimeField, EffectiveTimes.getEffectiveTime(this.component.getEffectiveTime()));
	}
	
	protected Component getComponent() {
		return component;
	}
	
}
