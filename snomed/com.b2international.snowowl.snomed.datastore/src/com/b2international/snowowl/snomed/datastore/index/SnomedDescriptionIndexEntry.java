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
package com.b2international.snowowl.snomed.datastore.index;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Class for representing a SNOMED&nbsp;CT description.
 * 
 * @see SnomedIndexEntry
 */
@Immutable
public class SnomedDescriptionIndexEntry extends SnomedIndexEntry implements IComponent<String>, IIndexEntry, Serializable {
	
	private static final long serialVersionUID = 301681633674309020L;

	private final String type;
	private final String conceptId;
	private final String caseSignificance;
	
	/**
	 * Creates a index based SNOMED&nbsp;CT description.
	 * @param id the unique description ID.
	 * @param term the term.
	 * @param iconId TODO
	 * @param moduleId the module concept ID.
	 * @param score the index based score.
	 * @param storageKey the unique CDO ID.
	 * @param released flag indicating if the description has been published or not.
	 * @param active flag indicating the status of the description.
	 * @param type the description type concept ID.
	 * @param caseSignificance the description case significance concept ID
	 * @param conceptId the container SNOMED&nbsp;CT concept ID.
	 * @param effectiveTime effective time of the description. Could be {@link DateUtils#UNSET_EFFECTIVE_TIME}.
	 */
	public SnomedDescriptionIndexEntry(final String id, final String term, final String moduleId, final float score, 
			final long storageKey, final boolean released, final boolean active, final String type, String caseSignificance, final String conceptId, final long effectiveTime) {
		super(id, term, type, moduleId, score, storageKey, released, active, effectiveTime);
		this.type = Preconditions.checkNotNull(type, "Description type concept ID argument cannot be null.");
		this.conceptId = Preconditions.checkNotNull(conceptId, "Container SNOMED CT concept ID argument cannot be null.");
		this.caseSignificance = Preconditions.checkNotNull(caseSignificance, "Case significance argument ID cannot be null.");
	}
	
	/**
	 * Returns with the SNOMED&nbsp;CT description type concept ID of the current description.
	 * @return the description type concept ID.
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Returns with the unique identifier of the container SNOMED&nbsp;C T concept.
	 * @return the container concept ID.
	 */
	public String getConceptId() {
		return conceptId;
	}
	
	/**
	 * Returns with the SNOMED&nbsp;CT description case significance concept ID of the current description.
	 * @return the caseSignificance
	 */
	public String getCaseSignificance() {
		return caseSignificance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.index.AbstractIndexEntry#toString()
	 */
	@Override
	public String toString() {
		
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("label", label)
				.add("score", score)
				.add("storageKey", storageKey)
				.add("type", type)
				.add("conceptId", conceptId)
				.add("active", isActive())
				.toString();
	}
}