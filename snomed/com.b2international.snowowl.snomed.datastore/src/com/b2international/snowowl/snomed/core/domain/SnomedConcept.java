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
package com.b2international.snowowl.snomed.core.domain;

import com.b2international.snowowl.datastore.request.BaseResourceRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.google.common.collect.Multimap;

/**
 * Represents a SNOMED&nbsp;CT concept.
 * <br>
 * Concepts returned by search requests are populated based on the expand parameters passed into the {@link BaseResourceRequestBuilder#setExpand(String)}
 * methods. The expand parameters can be nested allowing a fine control for the details returned in the resultset.  
 * 
 * The supported expand parameters are:
 * <p>
 * <ul>
 * <li>{@code pt()} - returns the <i>Preferred Term</i> for the </li> locale set by {@link BaseResourceRequestBuilder#setLocales(java.util.List)} method.
 * 
 * <li>{@code fsn()} - returns the <i>Fully Specified Name (fsn)</i> for the </li> locale set by {@link BaseResourceRequestBuilder#setLocales(java.util.List)} method.
 * .setLocales(languagePreference)</li>
 * <li>{@code descriptions()} - returns the descriptions of the concept</li>
 * <li>{@code relationships()} - returns the relationships of the concept</li>
 * <li>{@code descendants(direct:true|false, form:"stated"|"inferred")} - returns the all or the only the direct descendants of the concept based on the stated or the inferred tree.</li> 
 * <li>{@code ancestors(direct:true|false, form:"stated"|"inferred")} - returns the all or the only the direct ancestors of the concept based on the stated or the inferred tree.</li>
 * <li>{@code inactivationProperties()} - returns inactivation properties like indicator and association targets.</li>
 * </ul>
 * 
 * The number of expanded fields can be controlled with the {@code limit:} directive. For example:
 * <p>ancestors(direct:false, form:"inferred", limit:Integer.MAX_VALUE)<p>
 *
 * Expand parameters can be nested to further expand or filter the details returned. 
 * For example the expand string:
 * <p>{@code descriptions(expand(type:"typeId"))}<p>
 * returns only the descriptions with the specified <i>typeId</i>
 * <p>
 * @see BaseResourceRequestBuilder#setLocales(java.util.List)
 * @see SnomedDescription
 * @see SnomedRelationship
 * @see SnomedReferenceSet
 * @see SnomedReferenceSetMember
 */
public class SnomedConcept extends BaseSnomedCoreComponent implements ISnomedConcept {

	private static final long serialVersionUID = 1L;

	private DefinitionStatus definitionStatus;
	private SubclassDefinitionStatus subclassDefinitionStatus;
	private InactivationIndicator inactivationIndicator;
	private Multimap<AssociationType, String> associationTargets;
	private ISnomedDescription fsn;
	private ISnomedDescription pt;
	private SnomedDescriptions descriptions;
	private SnomedRelationships relationships;
	private SnomedConcepts ancestors;
	private SnomedConcepts descendants;
	private long[] ancestorIds;
	private long[] parentIds;
	private long[] statedAncestorIds;
	private long[] statedParentIds;

	public SnomedConcept() {
	}
	
	public SnomedConcept(String id) {
		setId(id);
	}
	
	@Override
	public DefinitionStatus getDefinitionStatus() {
		return definitionStatus;
	}

	@Override
	public SubclassDefinitionStatus getSubclassDefinitionStatus() {
		return subclassDefinitionStatus;
	}

	@Override
	public InactivationIndicator getInactivationIndicator() {
		return inactivationIndicator;
	}

	@Override
	public Multimap<AssociationType, String> getAssociationTargets() {
		return associationTargets;
	}
	
	@Override
	public SnomedDescriptions getDescriptions() {
		return descriptions;
	}
	
	@Override
	public SnomedRelationships getRelationships() {
		return relationships;
	}

	@Override
	public ISnomedDescription getFsn() {
		return fsn;
	}
	
	@Override
	public ISnomedDescription getPt() {
		return pt;
	}
	
	@Override
	public SnomedConcepts getAncestors() {
		return ancestors;
	}
	
	@Override
	public SnomedConcepts getDescendants() {
		return descendants;
	}

	@Override
	public long[] getAncestorIds() {
		return ancestorIds;
	}
	
	@Override
	public long[] getParentIds() {
		return parentIds;
	}
	
	@Override
	public long[] getStatedAncestorIds() {
		return statedAncestorIds;
	}
	
	@Override
	public long[] getStatedParentIds() {
		return statedParentIds;
	}
	
	public void setDefinitionStatus(final DefinitionStatus definitionStatus) {
		this.definitionStatus = definitionStatus;
	}

	public void setSubclassDefinitionStatus(final SubclassDefinitionStatus subclassDefinitionStatus) {
		this.subclassDefinitionStatus = subclassDefinitionStatus;
	}

	public void setInactivationIndicator(final InactivationIndicator inactivationIndicator) {
		this.inactivationIndicator = inactivationIndicator;
	}

	public void setAssociationTargets(final Multimap<AssociationType, String> associationTargets) {
		this.associationTargets = associationTargets;
	}
	
	public void setDescriptions(SnomedDescriptions descriptions) {
		this.descriptions = descriptions;
	}
	
	public void setRelationships(SnomedRelationships relationships) {
		this.relationships = relationships;
	}
	
	public void setFsn(ISnomedDescription fsn) {
		this.fsn = fsn;
	}
	
	public void setPt(ISnomedDescription pt) {
		this.pt = pt;
	}
	
	public void setAncestors(SnomedConcepts ancestors) {
		this.ancestors = ancestors;
	}
	
	public void setDescendants(SnomedConcepts descendants) {
		this.descendants = descendants;
	}
	
	public void setAncestorIds(final long[] ancestorIds) {
		this.ancestorIds = ancestorIds;
	}
	
	public void setParentIds(final long[] parentIds) {
		this.parentIds = parentIds;
	}
	
	public void setStatedAncestorIds(final long[] statedAncestorIds) {
		this.statedAncestorIds = statedAncestorIds;
	}
	
	public void setStatedParentIds(final long[] statedParentIds) {
		this.statedParentIds = statedParentIds;
	}
	
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedConcept [isActive()=");
		builder.append(isActive());
		builder.append(", getEffectiveTime()=");
		builder.append(getEffectiveTime());
		builder.append(", getModuleId()=");
		builder.append(getModuleId());
		builder.append(", getId()=");
		builder.append(getId());
		builder.append(", isReleased()=");
		builder.append(isReleased());
		builder.append(", getDefinitionStatus()=");
		builder.append(getDefinitionStatus());
		builder.append(", getSubclassDefinitionStatus()=");
		builder.append(getSubclassDefinitionStatus());
		builder.append(", getInactivationIndicator()=");
		builder.append(getInactivationIndicator());
		builder.append(", getAssociationTargets()=");
		builder.append(getAssociationTargets());
		builder.append("]");
		return builder.toString();
	}
}
