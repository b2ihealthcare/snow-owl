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

import static com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil.getSpecialFieldComponentTypeId;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil.isMapping;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedIconProvider;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;
import com.google.common.base.Preconditions;

/**
 * Lightweight representation of a SNOMED CT reference set member.
 */
public class SnomedRefSetMemberIndexEntry extends SnomedIndexEntry implements IComponent<String>, Serializable {
	
	private static final long serialVersionUID = 3504576207161692354L;
	
	private long refSetIdentifierId;	// SCT ID
	private final short refComponentType;
	
	/* 
	 * XXX (apeteri): write access of referencedComponentId is required in 
	 * SnomedConcreteDataTypeRefSetMemberIndexEntry.createFromIndexEntry(SnomedConcreteDataTypeRefSetMemberIndexEntry, String), but is generally
	 * discouraged.
	 */
	protected /* final */ String referencedComponentId;
	private SnomedRefSetType refSetType;
	private long storageKey;
	private short specialFieldComponentType;
	private String specialFieldLabel;
	private String specialFieldId;
	private String label;
	private boolean active;
	@Nullable private String mapTargetDescription;
	
	/**
	 * (non-API)
	 * Creates a reference set member from a CDOish object.
	 */
	public static SnomedRefSetMemberIndexEntry create(final SnomedRefSetMember member) {
		return create(member, null);
	}
	
	/**
	 * (non-API)
	 * Creates a reference set member from a CDOish object with the given label of the referenced component.
	 */
	public static SnomedRefSetMemberIndexEntry create(final SnomedRefSetMember member, @Nullable final String label) {
		Preconditions.checkNotNull(member, "Reference set member argument cannot be null.");
		Preconditions.checkArgument(!CDOState.NEW.equals(member.cdoState()), "Reference set member CDO state should not be NEW. " +
				"Use com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetLuceneIndexDTO.createForNewMember(SnomedRefSetMember<?>) instead.");
		Preconditions.checkArgument(!CDOState.TRANSIENT.equals(member.cdoState()), "Reference set member CDO state should not be TRANSIENT. " +
				"Use com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetLuceneIndexDTO.createForDetachedMember(SnomedRefSetMember<?>, SnomedRefSet<?>) instead");
		return new SnomedRefSetMemberIndexEntry(member, label, tryGetIconId(member), CDOIDUtil.getLong(member.cdoID()), member.getRefSet());
	}
	
	/**
	 * (non-API)
	 * Creates a reference set member from a new CDOish reference set member.
	 */
	public static SnomedRefSetMemberIndexEntry createForNewMember(final SnomedRefSetMember member) {
		return createForNewMember(member, null);
	}
	
	/**
	 * (non-API)
	 * Creates a reference set member from a new CDOish reference set member with the given label of the referenced component.
	 */
	public static SnomedRefSetMemberIndexEntry createForNewMember(final SnomedRefSetMember member, @Nullable final String label) {
		Preconditions.checkNotNull(member, "Reference set member argument cannot be null.");
		Preconditions.checkArgument(CDOState.NEW.equals(member.cdoState()), "Reference set member CDO state must be NEW.");
		return new SnomedRefSetMemberIndexEntry(member, label, tryGetIconId(member), 0L, member.getRefSet());
	}
	
	/**
	 * (non-API)
	 * Creates a reference set member representing a detached member.
	 */
	public static SnomedRefSetMemberIndexEntry createForDetachedMember(final SnomedRefSetMember member, final SnomedRefSet refSet) {
		return createForDetachedMember(member, refSet, null);
	}

	/**
	 * (non-API)
	 * Creates a reference set member representing a detached member.
	 */
	public static SnomedRefSetMemberIndexEntry createForDetachedMember(final SnomedRefSetMember member, final SnomedRefSet refSet, @Nullable final String label) {
		Preconditions.checkNotNull(member, "Reference set member argument cannot be null.");
		Preconditions.checkNotNull(refSet, "Container reference set argument cannot be null.");
		Preconditions.checkArgument(CDOState.TRANSIENT.equals(member.cdoState()), "Reference set member CDO state must be TRANSIENT.");
		return new SnomedRefSetMemberIndexEntry(member, label, SnomedConstants.Concepts.ROOT_CONCEPT, 0L, refSet);
	}
	
	/**
	 * (non-API)
	 * Creates a mock reference set member.
	 */
	public static SnomedRefSetMemberIndexEntry createMockMember(final IComponent<String> component) {
		return new SnomedRefSetMemberIndexEntry(component, SnomedIconProvider.getInstance().getIconComponentId(component.getId()));
	}
	
	protected static String tryGetIconId(final SnomedRefSetMember member) {
		
		if (CDOUtils.checkObject(member)) {
			
			if (member instanceof SnomedQueryRefSetMember) {
				return Concepts.REFSET_ROOT_CONCEPT;
			}
			
			final short referencedComponentType = member.getReferencedComponentType();
			final String referencedComponentId = member.getReferencedComponentId();
			Object iconIdAsobject = CoreTerminologyBroker.getInstance().getComponentIconIdProvider(referencedComponentType).getIconId(BranchPathUtils.createPath(member.cdoView()), referencedComponentId);
			if (null != iconIdAsobject) {
				return String.valueOf(iconIdAsobject);
			}
			
		}
		
		return Concepts.ROOT_CONCEPT;
	}
	
	/**
	 * (non-API)
	 * Creates a new reference set member based on the given index document.
	 */
	public static SnomedRefSetMemberIndexEntry create(final Document doc, @Nullable final IBranchPath branchPath) {
		Preconditions.checkNotNull(doc, "Document argument cannot be null.");
		
		final SnomedRefSetType type = SnomedRefSetType.get(SnomedMappings.memberRefSetType().getValue(doc));
		final String uuid = doc.get(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UUID);
		final String moduleId = SnomedMappings.module().getValueAsString(doc);
		final long storageKey = Mappings.storageKey().getValue(doc);
		final boolean active = SnomedMappings.active().getValue(doc) == 1;
		final boolean released = IndexUtils.getBooleanValue(doc.getField(SnomedIndexBrowserConstants.COMPONENT_RELEASED));
		final long refSetId = SnomedMappings.memberRefSetId().getValue(doc);
		final short referencedComponentType = SnomedMappings.memberReferencedComponentType().getShortValue(doc);
		final String referencedComponentId = SnomedMappings.memberReferencedComponentId().getValueAsString(doc);
		final long effectiveTimeLong = doc.getField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_EFFECTIVE_TIME).numericValue().longValue();
		final short specialFieldComponentType = isMapping(type) ? getSpecialFieldComponentTypeId(doc) : getSpecialFieldComponentTypeId(type);
		final String specialFieldId = doc.get(SnomedRefSetUtil.getSpecialComponentIdIndexField(type));
		final String specialFieldLabel = doc.get(SnomedRefSetUtil.getSpecialComponentLabelIndexField(type));
		final String mapTargetDescription = doc.get(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION);
		
		String iconId = null;
		if (null != branchPath) {
			Object iconIdAsObjact = CoreTerminologyBroker.getInstance().getComponentIconIdProvider(referencedComponentType).getIconId(branchPath, referencedComponentId);
			if (null != iconIdAsObjact) {
				iconId = String.valueOf(iconIdAsObjact);
			}
		}
		
		return new SnomedRefSetMemberIndexEntry(
				uuid, 
				uuid, 
				iconId, 
				moduleId, 
				0.0F, 
				storageKey, 
				released, 
				active, 
				refSetId, 
				referencedComponentType, 
				referencedComponentId, 
				type, 
				effectiveTimeLong, 
				specialFieldComponentType, 
				specialFieldLabel,
				specialFieldId,
				mapTargetDescription);
		
	}
	

	/**
	 * @param id
	 * @param label
	 * @param iconId TODO
	 * @param moduleId
	 * @param score
	 * @param storageKey
	 * @param storageKey
	 * @param released
	 * @param active
	 * @param refSetIdentifierId
	 * @param refComponentType
	 * @param referencedComponentId
	 * @param refSetType
	 * @param effectiveTime
	 * @param specialFieldComponentType
	 * @param specialFieldLabel
	 * @param specialFieldId
	 * @param mapTargetDescription2 
	 */
	private SnomedRefSetMemberIndexEntry(final String id, final String label, String iconId, final String moduleId, 
			final float score, final long storageKey, final boolean released, final boolean active,
			final long refSetIdentifierId, final short refComponentType, final String referencedComponentId, 
			final SnomedRefSetType refSetType, final long effectiveTime, final short specialFieldComponentType, final String specialFieldLabel, final String specialFieldId, final String mapTargetDescription) {
		
		super(id, label, iconId, moduleId, score, storageKey, released, active, effectiveTime);
		this.refSetIdentifierId = refSetIdentifierId;
		this.refComponentType = refComponentType;
		this.referencedComponentId = referencedComponentId;
		this.refSetType = refSetType;
		this.storageKey = storageKey;
		this.specialFieldComponentType = specialFieldComponentType;
		this.specialFieldLabel = specialFieldLabel;
		this.specialFieldId = specialFieldId;
		this.label = label;
		this.active = active;
		this.mapTargetDescription = mapTargetDescription;
	}

	protected SnomedRefSetMemberIndexEntry(final IComponent<String> component, String iconId) {
		super(Preconditions.checkNotNull(component, "Component argument cannot be null.").getId(), component.getLabel(), iconId, Concepts.MODULE_SCT_CORE, 0.0F, -1L, false, true, -1L);
		referencedComponentId = component.getId();
		refComponentType = CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsShort(component);
		label = component.getLabel();
		storageKey = -1L;
	}
	
	/**
	 * Copy constructor.
	 * @param entry
	 */
	protected SnomedRefSetMemberIndexEntry(final SnomedRefSetMemberIndexEntry entry) {
		super(entry.getId(), entry.getLabel(), entry.getIconId(), entry.getModuleId(), 0.0F, entry.getStorageKey(), entry.isReleased(), entry.isActive(), entry.getEffectiveTimeAsLong());
		referencedComponentId = entry.getReferencedComponentId();
		refComponentType = CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsShort(entry.getReferencedComponentType());
		specialFieldComponentType = CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsShort(entry.getSpecialFieldComponentType());
		specialFieldId = entry.getSpecialFieldId();
		specialFieldLabel = entry.getSpecialFieldLabel();
		refSetIdentifierId = Long.parseLong(entry.getRefSetIdentifierId());
		refSetType = entry.getRefSetType();
		active = entry.isActive();
		storageKey = entry.getStorageKey();
		label = entry.getLabel();
		mapTargetDescription = entry.mapTargetDescription;
	}
	
	protected SnomedRefSetMemberIndexEntry(final SnomedRefSetMember member, @Nullable final String label, String iconId, final long cdoId, final SnomedRefSet refSet) {
		super(member.getUuid(), null == label ? getComponent(member, refSet).getLabel() : label, iconId, member.getModuleId(), 0.0F, cdoId, member.isReleased(), 
				member.isActive(), member.isSetEffectiveTime() ? member.getEffectiveTime().getTime() : EffectiveTimes.UNSET_EFFECTIVE_TIME);
		refSetIdentifierId = Long.parseLong(refSet.getIdentifierId());
		referencedComponentId = member.getReferencedComponentId();
		refComponentType = member.getReferencedComponentType();
		active = member.isActive();
		refSetType = refSet.getType();
		this.label = super.label; //required as we are managing quite different reference for the label
		storageKey = cdoId;
		
		switch (refSetType) {
			case SIMPLE: 
				specialFieldComponentType = CoreTerminologyBroker.UNSPECIFIED_NUMBER;
				specialFieldId = null;
				specialFieldLabel = null;
				break;
			case QUERY:
				specialFieldComponentType = CoreTerminologyBroker.UNSPECIFIED_NUMBER;
				specialFieldId = ((SnomedQueryRefSetMember) member).getQuery(); //query string should be set as ID
				specialFieldLabel = ((SnomedQueryRefSetMember) member).getQuery();
				break;
			case EXTENDED_MAP: //$FALL-THROUGH$
			case COMPLEX_MAP: //$FALL-THROUGH$
			case SIMPLE_MAP:
				specialFieldComponentType = ((SnomedMappingRefSet) refSet).getMapTargetComponentType();
				specialFieldId = ((SnomedSimpleMapRefSetMember) member).getMapTargetComponentId();
				mapTargetDescription = ((SnomedSimpleMapRefSetMember) member).getMapTargetComponentDescription();
				if (null == specialFieldId) {
					specialFieldId = "";
				} else {
					if (!isUnspecified()) {
						final IComponent<String> concept = getTerminologyBrowser().getConcept(specialFieldId);
						if (null == concept) {
							specialFieldLabel = specialFieldId;
						} else {
							specialFieldLabel =  concept.getLabel();
						}
					} else {
						specialFieldLabel = specialFieldId;
					}
				}
				break;
			case DESCRIPTION_TYPE:
				final SnomedDescriptionTypeRefSetMember descrMember = (SnomedDescriptionTypeRefSetMember) member;
				specialFieldId = descrMember.getDescriptionFormat();
				specialFieldComponentType = SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
				specialFieldLabel = CoreTerminologyBroker.getInstance().getComponent(
						createPair(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, descrMember.getDescriptionFormat())).getLabel();
				break;
			case ATTRIBUTE_VALUE:
				final SnomedAttributeValueRefSetMember attrMember = (SnomedAttributeValueRefSetMember) member;
				specialFieldId = attrMember.getValueId();
				specialFieldComponentType = SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
				specialFieldLabel = CoreTerminologyBroker.getInstance().getComponent(
						createPair(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, attrMember.getValueId())).getLabel();
				break;
			case LANGUAGE:
				final SnomedLanguageRefSetMember langMember = (SnomedLanguageRefSetMember) member;
				specialFieldComponentType = SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER;
				specialFieldId = langMember.getAcceptabilityId();
				specialFieldLabel = CoreTerminologyBroker.getInstance().getComponent(
						createPair(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, specialFieldId)).getLabel();
				break;
			case CONCRETE_DATA_TYPE:
				specialFieldComponentType = CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT;
				specialFieldId = String.valueOf(((SnomedConcreteDataTypeRefSetMember) member).getDataType().getValue());
				specialFieldLabel = ((SnomedConcreteDataTypeRefSetMember) member).getSerializedValue();
				break;
			default: throw new IllegalArgumentException("Unknown reference set type: " + refSet.getType());
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.SnomedIndexEntry#isActive()
	 */
	@Override
	public boolean isActive() {
		return active;
	}
	
	/**
	 * @return the storageKey
	 */
	@Override
	public long getStorageKey() {
		return storageKey;
	}
	
	/**
	 * @return the label
	 */
	@Override
	public String getLabel() {
		return label;
	}
	
	/**
	 * (non-API)
	 * Sets the unique storage key on the component.
	 * @param storageKey the storageKey to set
	 */
	public void setStorageKey(final long storageKey) {
		this.storageKey = storageKey;
	}
	
	/**
	 * (non-API)
	 * Sets the label on the reference set member.
	 * @param label for the member.
	 */
	public void setLabel(final String label) {
		this.label = label;
	}

	/**
	 * Returns with the container reference set ID.
	 * @return the reference set ID.
	 */
	public String getRefSetIdentifierId() {
		return Long.toString(refSetIdentifierId);
	}

	/**
	 * Returns with the unique ID of the referenced component.
	 * @return the referenced component's ID.
	 */
	public String getReferencedComponentId() {
		return referencedComponentId;
	}

	/**
	 * (non-API)
	 * Returns with the application specific component identifier of the referenced component.
	 * @return the application specific terminology component identifier for the referenced component.
	 */
	public String getReferencedComponentType() {
		return CoreTerminologyBroker.getInstance().getTerminologyComponentId(refComponentType);
	}

	/**
	 * (non-API)
	 * Sets the status of the current reference set member. {@code true} if active, otherwise {@code false}.
	 */
	public void setActive(final boolean active) {
		this.active = active;
	}

	/**
	 * Returns with the referenced component type.
	 * @return the referenced component type.
	 */
	public SnomedRefSetType getRefSetType() {
		return refSetType;
	}

	/**
	 * Returns with the human readable label of the target component.
	 * Target component could be map target for mapping type reference sets, 
	 * acceptability for language reference set members
	 * value for attribute value reference set members, etc. 
	 */
	public String getSpecialFieldLabel() {
		return specialFieldLabel;
	}
	/**
	 * Returns with the unique component ID of the target component.
	 * Target component could be map target for mapping type reference sets, 
	 * acceptability for language reference set members
	 * value for attribute value reference set members, etc. 
	 */
	public String getSpecialFieldId() {
		return specialFieldId;
	}
	
	/**
	 * (non-API)
	 * Returns with the application specific terminology component ID of the target component.
	 * Target component could be map target for mapping type reference sets, 
	 * acceptability for language reference set members
	 * value for attribute value reference set members, etc. 
	 */
	public String getSpecialFieldComponentType() {
		return CoreTerminologyBroker.getInstance().getTerminologyComponentId(specialFieldComponentType);
	}

	/**
	 * (non-API)
	 * Returns with the map target description if available and applicable.
	 * May return with {@code null} if missing. It is the client's responsibility to check 
	 * its availability before referencing it.
	 */
	public String getMapTargetDescription() {
		return mapTargetDescription;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SnomedRefSetMemberIndexEntry other = (SnomedRefSetMemberIndexEntry) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SnomedRefSetMemberIndexEntry [uuid=" + getId()
				+ ", refComponentLabel=" + getLabel() + "]";
	}

	private boolean isUnspecified() {
		return CoreTerminologyBroker.UNSPECIFIED_NUMBER == specialFieldComponentType;
	}

	private IClientTerminologyBrowser<IComponent<String>, String> getTerminologyBrowser() {
		return CoreTerminologyBroker.getInstance().getTerminologyBrowserFactory(CoreTerminologyBroker.getInstance().getTerminologyComponentId(specialFieldComponentType)).getTerminologyBrowser();
	}

	/*extracts the reference set member label from the fully detailed reference set and the member*/
	private static IComponent<?> getComponent(final SnomedRefSetMember member, final SnomedRefSet refSet) {
		final short referencedComponentType = getReferencedComponentType(member, refSet);
		IComponent<?> component = CoreTerminologyBroker.getInstance().getComponent(createPair(referencedComponentType, member.getReferencedComponentId()));
		
		//get concept from the transaction. can happen when adding new sibling/child reference set member from reference set editor
		//and also creating and revealing referenced component concept in details page
		if (null == component) {
			if (SnomedTerminologyComponentConstants.CONCEPT_NUMBER == referencedComponentType) {
				for (final Concept concept : ComponentUtils2.getNewObjects(refSet.cdoView(), Concept.class)) {
					if (concept.getId().equals(member.getReferencedComponentId())) {
						component = CoreTerminologyBroker.getInstance().adapt(concept);
					}
				}
			}
			if (SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER == referencedComponentType) {
				for (final Description description : ComponentUtils2.getNewObjects(refSet.cdoView(), Description.class)) {
					if (description.getId().equals(member.getReferencedComponentId())) {
						component = CoreTerminologyBroker.getInstance().adapt(description);
					}
				}
			}
			if (SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER == referencedComponentType) {
				for (final Relationship relationship : ComponentUtils2.getNewObjects(refSet.cdoView(), Relationship.class)) {
					if (relationship.getId().equals(member.getReferencedComponentId())) {
						component = CoreTerminologyBroker.getInstance().adapt(relationship);
					}
				}
			}
		}
		
		//can happen when adding SNOMED CT query type reference set member
		//by creating the query type member a simple type reference set should be created 
		//which is not persisted yet, so we should get it from the underlying CDO view
		if (null == component) {
			for (final SnomedRefSet newRefSet : ComponentUtils2.getNewObjects(refSet.cdoView(), SnomedRefSet.class)) {
				if (member.getReferencedComponentId().equals(newRefSet.getIdentifierId())) {
					component = CoreTerminologyBroker.getInstance().adapt(newRefSet);
				}
			}
		}
		
		return Preconditions.checkNotNull(component, "Component was null for " + refSet + " reference set with " + member + " reference set member");
	}

	private static short getReferencedComponentType(final SnomedRefSetMember member, final SnomedRefSet refSet) {
		if (member.getRefSet() != null) {
			return member.getReferencedComponentType();
		}
		return refSet.getReferencedComponentType();
	}

	/*returns with the short value of the passed in unique terminology component identifier*/
	private static String getTerminologyComponentId(final short terminologyComponentIdValue) {
		return CoreTerminologyBroker.getInstance().getTerminologyComponentId(terminologyComponentIdValue);
	}
	
	/*creates and returns with a new component identifier pair based on the passed in component identifier and the unique identifier of the component*/
	private static ComponentIdentifierPair<String> createPair(final short terminologyComponentIdValue, final String componentId) {
		return ComponentIdentifierPair.<String>create(getTerminologyComponentId(terminologyComponentIdValue), componentId);
	}

}