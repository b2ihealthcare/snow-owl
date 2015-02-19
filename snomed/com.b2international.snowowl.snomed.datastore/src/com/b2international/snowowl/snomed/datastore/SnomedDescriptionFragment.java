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
package com.b2international.snowowl.snomed.datastore;

import java.io.Serializable;

import com.b2international.snowowl.core.api.IComponent;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Bare minimum representation of a SNOMED&nbsp;CT description.
 *
 */
public class SnomedDescriptionFragment implements Serializable {

	private static final long serialVersionUID = 470531390532636641L;

	private final String term;
	private final String conceptId;
	private final String typeId;
	private final String descriptionId;
	private final String effectiveTime;
	private final boolean preferred;

	public SnomedDescriptionFragment(final String descriptionId, final String term, final String conceptId, final String typeId, final boolean preferred, final String effectiveTime) {
		this.descriptionId = Preconditions.checkNotNull(descriptionId, "descriptionId");
		this.term = Preconditions.checkNotNull(term, "term");
		this.conceptId = Preconditions.checkNotNull(conceptId, "conceptId");
		this.typeId = Preconditions.checkNotNull(typeId, "typeId");
		this.preferred = Preconditions.checkNotNull(preferred, "preferred");
		this.effectiveTime = Preconditions.checkNotNull(effectiveTime, "effectiveTime");
	}

	/**Returns with the description term.*/
	public String getTerm() {
		return term;
	}

	/**Returns with associated concept ID.*/
	public String getConceptId() {
		return conceptId;
	}

	/**Returns with description type concept ID.*/
	public String getTypeId() {
		return typeId;
	}

	/**Returns with {@code true} if the description is preferred, otherwise {@code false}.*/
	public boolean isPreferred() {
		return preferred;
	}
	
	/**Returns with the description ID.*/
	public String getDescriptionId() {
		return descriptionId;
	}
	
	/**Returns with the description effective time.*/
	public String getEffectiveTime() {
		return effectiveTime;
	}
	
	/**Transforms the fragment into {@link IComponent} representation.*/
	public IComponent<String> toComponent() {
		
		return new IComponent<String>() {

			private static final long serialVersionUID = 4867156786910371713L;

			@Override
			public String getId() {
				return descriptionId;
			}

			@Override
			public String getLabel() {
				return term;
			}
		};
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("ID", descriptionId)
				.add("Term", term)
				.add("Concept ID", conceptId)
				.add("Type ID", typeId)
				.add("Effective time", effectiveTime)
				.add("Preferred", preferred)
				.toString();
	}
	
	
}