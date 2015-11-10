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
package com.b2international.snowowl.snomed.datastore.index.refset;

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_REFERENCED_COMPONENT_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_STRUCTURAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_TYPE;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.datastore.IRefSetComponent;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Objects;

/**
 * Representation of a SNOMED CT reference set.
 */
@ThreadSafe
@Immutable
public class SnomedRefSetIndexEntry extends SnomedIndexEntry implements IRefSetComponent, IComponent<String>, Serializable {

	private static final long serialVersionUID = 2943070736359287904L;

	private final SnomedRefSetType type;
	private final short referencedComponentType;
	private final boolean structural;

	public SnomedRefSetIndexEntry(final Document doc) {
		this(doc, 0.0f);
	}
	
	public SnomedRefSetIndexEntry(final Document doc, final float score) {
		this(SnomedMappings.id().getValueAsString(doc), 
				Mappings.label().getValue(doc), 
				SnomedMappings.iconId().getValueAsString(doc),
				Mappings.longField(SnomedIndexBrowserConstants.CONCEPT_EFFECTIVE_TIME).getValue(doc),
				SnomedMappings.module().getValueAsString(doc),
				score,
				SnomedMappings.refSetStorageKey().getValue(doc), 
				SnomedMappings.released().getValue(doc) == 1,
				SnomedMappings.active().getValue(doc) == 1,
				SnomedRefSetType.get(IndexUtils.getIntValue(doc.getField(REFERENCE_SET_TYPE))),
				IndexUtils.getShortValue(doc.getField(REFERENCE_SET_REFERENCED_COMPONENT_TYPE)), 
				IndexUtils.getBooleanValue(doc.getField(REFERENCE_SET_STRUCTURAL)));
	}
	
	public SnomedRefSetIndexEntry(final String id, final String label, final String iconId, final String moduleId, final float score, 
			final long storageKey, final boolean released, final boolean active, final SnomedRefSetType type, final short referencedComponentType, final boolean structural) {
		this(id, label, iconId, EffectiveTimes.UNSET_EFFECTIVE_TIME, moduleId, score, storageKey, released, active, type, referencedComponentType, structural);
	}
	
	/**
	 * Creates a new instance of this class.
	 * @param id the unique ID of the identifier concept.
	 * @param label the label of the associated concept.
	 * @param iconId TODO
	 * @param effectiveTime 
	 * @param score the score for the index.
	 * @param storageKey unique identifier of the component in the database.
	 * @param type the type of the reference set.
	 * @param referencedComponentType the numeric ID of the referenced component's type
	 */
	public SnomedRefSetIndexEntry(final String id, final String label, final String iconId, long effectiveTime, final String moduleId, final float score, 
			final long storageKey, final boolean released, final boolean active, final SnomedRefSetType type, final short referencedComponentType, final boolean structural) {
		super(id, label, iconId, moduleId, score, storageKey, released, active, -1L); 
		this.type = checkNotNull(type, "type");
		this.referencedComponentType = referencedComponentType;
		this.structural = structural;
		setEffectiveTime(effectiveTime);
	}

	/**
	 * Returns with the {@link SnomedRefSetType type} of the reference set.
	 * @return the reference set type.
	 */
	public SnomedRefSetType getType() {
		return type;
	}
	
	/**
	 * (non-API)
	 * Returns with the application specific component identifier as a numeric value of the referenced component.
	 * @return the application specific terminology component identifier value for the referenced component.
	 */
	public short getReferencedComponentType() {
		return referencedComponentType;
	}
	
	/**
	 * @return {@code true} if reference set members need to be gathered from lists on the referenced components they
	 * refer to, {@code false} if they are retrievable from the reference set itself (a single list)
	 */
	public boolean isStructural() {
		return structural;
	}
	
	/**
	 * @return <code>true</code> if the reference set is a mapping type reference set, returns <code>false</code> otherwise.
	 */
	public boolean isMapping() {
		return SnomedRefSetUtil.isMapping(getType());
	}
	
	@Override
	public String toString() {
		
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("label", label)
				.add("score", score)
				.add("storageKey", storageKey)
				.add("type", type)
				.add("referencedComponentType", referencedComponentType)
				.add("structural", structural)
				.toString();
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SnomedRefSetIndexEntry other = (SnomedRefSetIndexEntry) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}