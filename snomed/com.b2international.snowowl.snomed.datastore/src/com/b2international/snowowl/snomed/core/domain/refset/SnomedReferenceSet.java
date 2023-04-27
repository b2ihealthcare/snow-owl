/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain.refset;

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.TOOLING_ID;

import java.util.Set;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.core.terminology.MapTargetTypes;
import com.b2international.snowowl.core.terminology.TerminologyComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a SNOMED&nbsp;CT Reference Set.
 * <br>
 * Reference sets returned by search requests are populated based on the expand parameters passed into the {@link BaseResourceRequestBuilder#setExpand(String)}
 * methods. The expand parameters can be nested allowing a fine control for the details returned in the resultset.  
 * 
 * The supported expand parameters are:
 * <p>
 * <ul>
 * <li>{@code members()} - returns the members of this reference set as part of the reference set</li>
 * </ul>
 * 
 * The number of expanded fields can be controlled with the {@code limit:} directive. For example:
 * {@code members(limit:Integer.MAX_VALUE)}
 * <p>
 *
 * Expand parameters can be nested to further expand or filter the details returned. 
 * 
 * @see SnomedConcept
 * @see SnomedDescription
 * @see SnomedRelationship
 * @see SnomedReferenceSetMember
 * 
 * @since 4.5
 */
@TerminologyComponent(
	id = SnomedConcept.REFSET_TYPE,
	componentCategory = ComponentCategory.SET,
	name = "SNOMED CT Reference Set",
	docType = SnomedConceptDocument.class,
	supportedRefSetTypes = { MapTargetTypes.QUERY },
	allowedAsMapTarget = false
)
public final class SnomedReferenceSet extends SnomedComponent {

	private static final long serialVersionUID = 6190078291559073421L;

	public static final SnomedReferenceSet DELETE = new SnomedReferenceSet();
	public static final SnomedReferenceSet FORCE_DELETE = new SnomedReferenceSet();
	
	public static final String SNOMED_CONCEPT_QUALIFIED_ID = TOOLING_ID + "." + SnomedConcept.TYPE;
	public static final String SNOMED_DESCRIPTION_QUALIFIED_ID = TOOLING_ID + "." + SnomedDescription.TYPE;
	public static final String SNOMED_RELATIONSHIP_QUALIFIED_ID = TOOLING_ID + "." + SnomedRelationship.TYPE;

	/**
	 * @since 6.16 
	 */
	public static final class Fields extends SnomedComponent.Fields {
		
		public static final String TYPE = "type";
		public static final String REFERENCED_COMPONENT_TYPE = "referencedComponentType";
		public static final String MAP_TARGET_COMPONENT_TYPE = "mapTargetComponentType";
		public static final String MAP_SOURCE_COMPONENT_TYPE = "mapSourceComponentType";
		
		public static final Set<String> ALL = ImmutableSet.of(
				ID,
				ACTIVE,
				EFFECTIVE_TIME,
				MODULE_ID,
				TYPE,
				REFERENCED_COMPONENT_TYPE,
				MAP_TARGET_COMPONENT_TYPE,
				MAP_SOURCE_COMPONENT_TYPE,
				RELEASED);
		
	}

	private SnomedRefSetType type;
	private String referencedComponentType;
	private String mapTargetComponentType;
	private String mapSourceComponentType;
	private SnomedReferenceSetMembers members;

	@Override
	public String getComponentType() {
		return SnomedConcept.REFSET_TYPE;
	}
	
	/**
	 * Returns the type of the reference set.
	 * 
	 * @return
	 */
	public SnomedRefSetType getType() {
		return type;
	}

	/**
	 * Returns the type of the referenced component.
	 * 
	 * @return
	 */
	public String getReferencedComponentType() {
		return referencedComponentType;
	}

	/**
	 * Returns the type of the map target if this reference set is a "map from SNOMED CT" reference set.
	 * @return
	 */
	public String getMapTargetComponentType() {
		return mapTargetComponentType;
	}
	
	/**
	 * Returns the type of the map source if this reference set is a "map to SNOMED CT" reference set.
	 * @return
	 */
	public String getMapSourceComponentType() {
		return mapSourceComponentType;
	}

	/**
	 * Returns all members of the reference set.
	 * 
	 * @return
	 */
	public SnomedReferenceSetMembers getMembers() {
		return members;
	}
	
	/**
	 * @return the {@link DataType} if this refset represents a concrete domain reference set, otherwise returns <code>null</code>.
	 */
	public DataType getDataType() {
		return getType() == SnomedRefSetType.CONCRETE_DATA_TYPE ? SnomedRefSetUtil.getDataType(getId()) : null;
	}
	
	public void setReferencedComponentType(String referencedComponent) {
		this.referencedComponentType = referencedComponent;
	}
	
	public void setMapTargetComponentType(String mapTargetComponentType) {
		this.mapTargetComponentType = mapTargetComponentType;
	}
	
	public void setMapSourceComponentType(String mapSourceComponentType) {
		this.mapSourceComponentType = mapSourceComponentType;
	}
	
	public void setType(SnomedRefSetType type) {
		this.type = type;
	}
	
	public void setMembers(SnomedReferenceSetMembers members) {
		this.members = members;
	}
	
	@Override
	public Request<TransactionContext, String> toCreateRequest(String containerId) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Request<TransactionContext, Boolean> toUpdateRequest() {
		throw new UnsupportedOperationException("Reference sets does not support update operation yet");
	}
	
	public boolean hasSnomedTypeTargetComponents() {
		return SNOMED_CONCEPT_QUALIFIED_ID.equals(getMapTargetComponentType())
				|| SNOMED_DESCRIPTION_QUALIFIED_ID.equals(getMapTargetComponentType())
				|| SNOMED_RELATIONSHIP_QUALIFIED_ID.equals(getMapTargetComponentType());
	}
}
