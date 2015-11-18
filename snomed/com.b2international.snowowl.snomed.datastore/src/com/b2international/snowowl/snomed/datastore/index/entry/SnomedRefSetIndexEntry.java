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
package com.b2international.snowowl.snomed.datastore.index.entry;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import org.apache.lucene.document.Document;

import com.b2international.commons.BooleanUtils;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.datastore.IRefSetComponent;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * A transfer object representing a SNOMED CT reference set.
 */
public class SnomedRefSetIndexEntry extends SnomedIndexEntry implements IRefSetComponent, IComponent<String>, Serializable {

	private static final long serialVersionUID = 2943070736359287904L;

	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(final Document doc) {
		return builder()
				.id(SnomedMappings.id().getValueAsString(doc))
				.moduleId(SnomedMappings.module().getValueAsString(doc))
				.storageKey(SnomedMappings.refSetStorageKey().getValue(doc)) // XXX: This is different than the concept's storage key
				.active(BooleanUtils.valueOf(SnomedMappings.active().getValue(doc).intValue())) 
				.released(BooleanUtils.valueOf(SnomedMappings.released().getValue(doc).intValue()))
				.effectiveTimeLong(SnomedMappings.effectiveTime().getValue(doc))
				.type(SnomedRefSetType.get(Mappings.intField(SnomedIndexBrowserConstants.REFERENCE_SET_TYPE).getValue(doc)))
				.referencedComponentType(Mappings.intField(SnomedIndexBrowserConstants.REFERENCE_SET_REFERENCED_COMPONENT_TYPE).getShortValue(doc));
				// TODO: .mapTargetComponentType(...) is not indexed yet
	}

	public static class Builder extends AbstractBuilder<Builder> {

		private SnomedRefSetType type;
		private short referencedComponentType;
		private short mapTargetComponentType = CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT;
		private boolean structural;

		private Builder() {
			// Disallow instantiation outside static method
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		public Builder type(final SnomedRefSetType type) {
			this.type = type;
			return getSelf();
		}

		public Builder referencedComponentType(final short referencedComponentType) {
			this.referencedComponentType = referencedComponentType;
			return getSelf();
		}

		public Builder mapTargetComponentType(final short mapTargetComponentType) {
			this.mapTargetComponentType = mapTargetComponentType;
			return getSelf();
		}

		public Builder structural(final boolean structural) {
			this.structural = structural;
			return getSelf();
		}

		public SnomedRefSetIndexEntry build() {
			return new SnomedRefSetIndexEntry(id, 
					score, 
					storageKey, 
					moduleId, 
					released, 
					active, 
					effectiveTimeLong, 
					type, 
					referencedComponentType, 
					mapTargetComponentType, 
					structural);
		}
	}

	private final SnomedRefSetType type;
	private final short referencedComponentType;
	private final short mapTargetComponentType;
	private final boolean structural;

	private SnomedRefSetIndexEntry(final String id, 
			final float score, 
			final long storageKey, 
			final String moduleId, 
			final boolean released,
			final boolean active, 
			final long effectiveTimeLong, 
			final SnomedRefSetType type, 
			final short referencedComponentType,
			final short mapTargetComponentType, 
			final boolean structural) {

		super(id, 
				id, // XXX: iconId is the same as the identifier ID; reference sets might have a specialized icon
				score, 
				storageKey, 
				moduleId, 
				released, 
				active, 
				effectiveTimeLong);

		checkArgument(referencedComponentType >= CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT, "Referenced component type '%s' is invalid.", referencedComponentType);
		checkArgument(mapTargetComponentType >= CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT, "Map target component type '%s' is invalid.", referencedComponentType);

		this.type = checkNotNull(type, "Reference set type may not be null.");
		this.referencedComponentType = referencedComponentType;
		this.mapTargetComponentType = mapTargetComponentType;
		this.structural = structural;
	}

	/**
	 * @return the reference set type
	 */
	public SnomedRefSetType getType() {
		return type;
	}

	/**
	 * @return the terminology component identifier value for the referenced component
	 */
	public short getReferencedComponentType() {
		return referencedComponentType;
	}

	/**
	 * @return the terminology component identifier value for the map target, or
	 *         {@link CoreTerminologyBroker#UNSPECIFIED_NUMBER_SHORT} if not known (or the reference set is not a map)
	 * 
	 * @see #isMapping()
	 */
	public short getMapTargetComponentType() {
		return mapTargetComponentType;
	}

	/**
	 * @return {@code true} if reference set members are contained in lists on the components they refer to, {@code false} if they
	 *         can be retrieved from the reference set itself
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
		return toStringHelper()
				.add("type", type)
				.add("referencedComponentType", referencedComponentType)
				.add("mapTargetComponentType", mapTargetComponentType)
				.add("structural", structural)
				.toString();
	}
}
