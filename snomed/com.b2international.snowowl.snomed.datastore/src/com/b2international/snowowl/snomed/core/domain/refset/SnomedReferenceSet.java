/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.core.terminology.TerminologyComponent;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;

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
	id = SnomedTerminologyComponentConstants.REFSET,
	shortId = SnomedTerminologyComponentConstants.REFSET_NUMBER,
	componentCategory = ComponentCategory.SET,
	name = "SNOMED CT Reference Set",
	docType = SnomedConceptDocument.class,
	supportedRefSetTypes = {
		"QUERY"
	}
)
public final class SnomedReferenceSet extends SnomedComponent {

	private static final long serialVersionUID = 6190078291559073421L;

	public static final SnomedReferenceSet DELETE = new SnomedReferenceSet();
	public static final SnomedReferenceSet FORCE_DELETE = new SnomedReferenceSet();

	private SnomedRefSetType type;
	private String referencedComponentType;
	private String mapTargetComponentType;
	private SnomedReferenceSetMembers members;

	@Override
	public short getTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.REFSET_NUMBER;
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
	 * Returns the type of the map target if this reference set is a mapping reference set.
	 * @return
	 */
	public String getMapTargetComponentType() {
		return mapTargetComponentType;
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

}
