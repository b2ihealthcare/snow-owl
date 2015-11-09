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
package com.b2international.snowowl.snomed.mrcm.core.widget;

import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.CaseSignificance;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexEntry;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Internal representation of a description for use with {@link WidgetBeanProvider}.
 * 
 */
public final class SnomedDescription {
	
	public enum ActivePredicate implements Predicate<SnomedDescription> {
		INSTANCE;
		
		@Override public boolean apply(SnomedDescription description) {
			return description.isActive();
		}
	}
	
	public enum CDOObjectConverterFunction implements Function<Description, SnomedDescription> {
		INSTANCE;
		
		@Override public SnomedDescription apply(Description description) {
			return new SnomedDescription(description);
		}
	}
	
	public enum IndexObjectConverterFunctions implements Function<SnomedDescriptionIndexEntry, SnomedDescription> {
		INSTANCE;
		
		@Override public SnomedDescription apply(SnomedDescriptionIndexEntry description) {
			return new SnomedDescription(description);
		}
	}
	
	private final String id;
	private final String label;
	private final String typeId;
	private final boolean active;
	private final boolean released;
	private final CaseSignificance caseSensitivity;
	
	public SnomedDescription(final Description description) {
		id = CDOUtils.getAttribute(description, SnomedPackage.eINSTANCE.getComponent_Id(), String.class);
		label = description.getTerm();
		typeId = description.getType().getId();
		active = description.isActive();
		released = description.isReleased();
		
		// XXX: The description is marked as case insensitive even when the initial character is case sensitive per requirements
		caseSensitivity = CaseSignificance.getForDescripition(description);
	}
	
	public SnomedDescription(final SnomedDescriptionIndexEntry description) {
		id = description.getId();
		label = description.getLabel();
		typeId = description.getTypeId();
		active = description.isActive();
		released = description.isReleased();
		caseSensitivity = CaseSignificance.getForDescriptionIndexEntry(description);
	}
	
	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String getTypeId() {
		return typeId;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isReleased() {
		return released;
	}
	
	/**
	 * Returns with case significance.  
	 * @return the case significance for the description.
	 */
	public CaseSignificance getCaseSensitivity() {
		return caseSensitivity;
	}
}