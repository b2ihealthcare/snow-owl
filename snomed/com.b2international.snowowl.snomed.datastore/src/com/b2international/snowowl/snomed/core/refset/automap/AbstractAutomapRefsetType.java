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
package com.b2international.snowowl.snomed.core.refset.automap;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;

/**
 * Represents automap target reference set type.
 * @since 3.1
 */
public abstract class AbstractAutomapRefsetType implements Serializable {

	private static final long serialVersionUID = 6854380124508640329L;

	private static final String SIMLE_MAP_HUMAN_READABLE = "Simple map type reference set";
	private static final String COMPLEX_MAP_HUMAN_READABLE = "Complex map type reference set";

	private SnomedRefSetType snomedRefSetType;
	
	public AbstractAutomapRefsetType(SnomedRefSetType refsetType) {
		this.snomedRefSetType = checkNotNull(refsetType, "SNOMED CT Reference Set type must be specified");
	}

	/**
	 * @return the unique ID of the automap target reference set type. Used to lookup {@link IAutomapService} by automap target type.
	 */
	public abstract String getId();

	/**
	 * @return the human readable form of the target reference set type.
	 */
	public abstract String getLabel();

	/**
	 * @return the human readable form of the <code>snomedRefSetType</code> field.
	 */
	protected String getHumanReadableRefSetType() {
		switch (snomedRefSetType) {
			case SIMPLE_MAP:
				return SIMLE_MAP_HUMAN_READABLE;
			case COMPLEX_MAP:
				return COMPLEX_MAP_HUMAN_READABLE;
			default:
				throw new IllegalArgumentException("Cannot get a human readable form for: " + snomedRefSetType);
		}
	}
	
	public SnomedRefSetType getSnomedRefSetType() {
		return snomedRefSetType;
	}
	
}