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
package com.b2international.snowowl.snomed.datastore.server.internal.refset;

import com.b2international.snowowl.snomed.core.domain.AbstractSnomedComponent;
import com.b2international.snowowl.snomed.core.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.5
 */
public class SnomedReferenceSetImpl extends AbstractSnomedComponent implements SnomedReferenceSet {

	private SnomedRefSetType type;
	private String referencedComponent;
	
	@Override
	public SnomedRefSetType getType() {
		return type;
	}

	@Override
	public String getReferencedComponent() {
		return referencedComponent;
	}
	
	public void setReferencedComponent(String referencedComponent) {
		this.referencedComponent = referencedComponent;
	}
	
	public void setType(SnomedRefSetType type) {
		this.type = type;
	}

}
