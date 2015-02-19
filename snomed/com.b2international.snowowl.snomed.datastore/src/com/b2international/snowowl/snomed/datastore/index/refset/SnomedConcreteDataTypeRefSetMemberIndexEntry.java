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

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.getTerminologyComponentIdValue;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.core.api.IStatement;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.services.SnomedConceptNameProvider;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;

/**
 * Lightweight representation of the SNOMED&nbsp;CT complex map type reference set member.
 * @see SnomedRefSetMemberIndexEntry
 */
public class SnomedConcreteDataTypeRefSetMemberIndexEntry extends SnomedRefSetMemberIndexEntry implements Serializable {

	private static final long serialVersionUID = -6630502726032961180L;

	private Object value;
	private DataType dataType;
	private String operatorComponentId;
	private String characteristicTypeId;
	private String uomComponentId;
	private String attributeLabel;

	/**
	 * (non-API)
	 * 
	 * Creates a new instance of this class based on the passed in concrete data type reference set member.
	 * @param member the concrete data type reference set member.
	 * @return the new lightweight representation of the SNOMED&nbsp;CT concrete data type reference set member.
	 */
	public static <T> SnomedConcreteDataTypeRefSetMemberIndexEntry create(final SnomedConcreteDataTypeRefSetMember member) {
		return create(member, null);
	}
	
	/**
	 * (non-API)
	 * 
	 * Creates a new member based given reference set member argument and the label.
	 * @param member the member.
	 * @param label the label. Optional, could be {@code null}.
	 * @return a new member.
	 */
	public static <T> SnomedConcreteDataTypeRefSetMemberIndexEntry create(final SnomedConcreteDataTypeRefSetMember member, @Nullable final String label) {
		checkNotNull(member, "Reference set member argument cannot be null.");
		checkNotNull(member.eContainer(), "Container reference set cannot be null");
		checkArgument(!CDOState.NEW.equals(member.cdoState()), "Reference set member CDO state should not be NEW. " +
				"Use " + SnomedComplexMapRefSetMemberIndexEntry.class + ".createForNewMember(SnomedConcreteDataTypeRefSet<T>) instead.");
		checkArgument(!CDOState.TRANSIENT.equals(member.cdoState()), "Reference set member CDO state should not be TRANSIENT. " +
				"Use " + SnomedComplexMapRefSetMemberIndexEntry.class + ".createForDetachedMember(SnomedConcreteDataTypeRefSet<T>, SnomedConcreteDataTypeRefSet) instead");
		return new SnomedConcreteDataTypeRefSetMemberIndexEntry(member, label, SnomedConstants.Concepts.ROOT_CONCEPT, CDOIDUtil.getLong(member.cdoID()), getRefSet(member));
	}
	
	/**
	 * (non-API)
	 * 
	 * Creates a new instance of this class based on the passed in unpersisted concrete data type reference set member.
	 * <br><br><b>Note:</b> the member should be an unpersisted one. The CDO state should be org.eclipse.emf.cdo.CDOState.NEW.
	 * @param member the concrete data type reference set member.
	 * @return the new lightweight representation of the SNOMED&nbsp;CT concrete data type reference set member.
	 */
	public static <T> SnomedConcreteDataTypeRefSetMemberIndexEntry createForNewMember(final SnomedConcreteDataTypeRefSetMember member) {
		return createForNewMember(member, null);
	}
	
	/**
	 * (non-API)
	 * 
	 * Creates a new member representing a new unpersisted reference set member.
	 * @param member the delegate member.
	 * @param label the label. Could be {@code null}.
	 * @return a new member representing an unpersisted reference set member.
	 */
	public static <T> SnomedConcreteDataTypeRefSetMemberIndexEntry createForNewMember(final SnomedConcreteDataTypeRefSetMember member, @Nullable final String label) {
		checkNotNull(member, "Reference set member argument cannot be null.");
		checkNotNull(member.eContainer(), "Container reference set cannot be null");
		checkArgument(CDOState.NEW.equals(member.cdoState()), "Reference set member CDO state must be NEW.");
		return new SnomedConcreteDataTypeRefSetMemberIndexEntry(member, label, SnomedConstants.Concepts.ROOT_CONCEPT, 0L, getRefSet(member));
	}
	
	/**
	 * (non-API)
	 * 
	 * Creates a new instance of this class based on the passed in detached concrete data type reference set member.
	 * <br><br><b>Note:</b> the member should be a detached one. The CDO state should be org.eclipse.emf.cdo.CDOState.TRANSIENT.
	 * @param member the concrete data type reference set member. 
	 * @param refSet the reference set from where the member has been detached.
	 * @return the new lightweight representation of the SNOMED&nbsp;CT concrete data type reference set member.
	 */
	public static <T> SnomedConcreteDataTypeRefSetMemberIndexEntry createForDetachedMember(final SnomedConcreteDataTypeRefSetMember member, final SnomedConcreteDataTypeRefSet refSet) {
		return createForDetachedMember(member, refSet, null);
	}
	
	/**
	 * (non-API)
	 * 
	 * Creates a new instance of this class representing an unpersisted reference set member.
	 * @param member the delegate detached member.
	 * @param refSet the container reference set.
	 * @param label the label. Optional. Could be {@code null}.
	 * @return a new member.
	 */
	public static <T> SnomedConcreteDataTypeRefSetMemberIndexEntry createForDetachedMember(final SnomedConcreteDataTypeRefSetMember member, final SnomedConcreteDataTypeRefSet refSet, @Nullable final String label) {
		checkNotNull(member, "Reference set member argument cannot be null.");
		checkNotNull(refSet, "Container reference set argument cannot be null.");
		checkArgument(CDOState.TRANSIENT.equals(member.cdoState()), "Reference set member CDO state must be TRANSIENT.");
		return new SnomedConcreteDataTypeRefSetMemberIndexEntry(member, label, SnomedConstants.Concepts.ROOT_CONCEPT, 0L, refSet);
	}
	
	/**
	 * (non-API)
	 * 
	 * Creates a new reference set member index entry. Initialize its base values based on the specified index entry argument.
	 * @param indexEntry the index entry.
	 * @return the new lightweight representation of the new concrete data type reference set member.
	 */
	public static SnomedConcreteDataTypeRefSetMemberIndexEntry createFromIndexEntry(@Nonnull final SnomedRefSetMemberIndexEntry indexEntry) {
		return new SnomedConcreteDataTypeRefSetMemberIndexEntry(checkNotNull(indexEntry, "SNOMED CT reference set member index entry cannot be null."));
	}

	/**
	 * (non-API)
	 * 
	 * Creates a new concrete domain reference set member index entry based on the specified source; the returned entry's storage key will be set to {@code -1L} to indicate
	 * its synthetic nature, and its previous referenced component identifier will also be replaced with the given component ID.
	 *  
	 * @param indexEntry the index entry to clone
	 * @param newReferencedComponentId the replacement component ID to use in the cloned entry
	 * @return the cloned index entry
	 */
	public static SnomedConcreteDataTypeRefSetMemberIndexEntry createFromIndexEntry(@Nonnull final SnomedConcreteDataTypeRefSetMemberIndexEntry indexEntry, 
			final String newReferencedComponentId) {
		
		final SnomedConcreteDataTypeRefSetMemberIndexEntry clone = createFromIndexEntry(indexEntry);
		clone.referencedComponentId = newReferencedComponentId;
		clone.setStorageKey(-1L);
		clone.setDataType(indexEntry.getDataType());
		clone.setValue(indexEntry.getValue());
		clone.setOperatorComponentId(indexEntry.getOperatorComponentId());
		clone.setUomComponentId(indexEntry.getUomComponentId());
		clone.setAttributeLabel(indexEntry.getAttributeLabel());
		clone.setCharacteristicTypeId(indexEntry.getCharacteristicTypeId());
		return clone;
	}

	private static SnomedConcreteDataTypeRefSet getRefSet(final SnomedConcreteDataTypeRefSetMember member) {
		return (SnomedConcreteDataTypeRefSet) new SnomedRefSetLookupService().getComponent(member.getRefSetIdentifierId(), member.cdoView());
	}
	
	/**
	 * Creates a new instance. Initialize values from the specified reference set member index entry.
	 * @param entry the reference set member index entry.
	 */
	protected SnomedConcreteDataTypeRefSetMemberIndexEntry(final SnomedRefSetMemberIndexEntry entry) {
		super(entry);
	}
	
	/**
	 * @param member the CDO object representing the concrete data type reference set member.
	 * @param iconId TODO
	 * @param cdoId the unique CDO identifier of the member.
	 * @param refSet the container concrete data type reference set member.
	 */
	protected <T> SnomedConcreteDataTypeRefSetMemberIndexEntry(final SnomedConcreteDataTypeRefSetMember member, @Nullable final String label, final String iconId, final long cdoId, final SnomedConcreteDataTypeRefSet refSet) {
		super(member, label, iconId, cdoId, refSet);
		value = SnomedRefSetUtil.deserializeValue(member.getDataType(), member.getSerializedValue());
		dataType = member.getDataType();
		operatorComponentId = member.getOperatorComponentId();
		uomComponentId = member.getUomComponentId(); //can be null
		characteristicTypeId = member.getCharacteristicTypeId();
		attributeLabel = member.getLabel();

		//since it can happen we have to extract the info via the referenced component.
		if (null == attributeLabel)
			attributeLabel = extractAttributeLabel(member);
	}
		
		
	/**
	 * Returns with the type of the encapsulated value.
	 * @return the dataType the type of the data.
	 */
	public DataType getDataType() {
		return dataType;
	}
	
	/**
	 * (non-API)
	 * 
	 * Sets the data type for a specified value.
	 * @param dataType the new data type.
	 */
	public void setDataType(final DataType dataType) {
		this.dataType = dataType;
	}
	
	/**
	 * Returns with the concrete data.
	 * @param <T> - the expected type of the data.
	 * @return the data of the current reference set member.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		return (T) value;
	}
	
	/**
	 * (non-API)
	 * 
	 * Sets the value of the data.
	 * @param value the new value of the encapsulated data.
	 */
	public <T> void setValue(final T value) {
		this.value = value;
	}
	
	/**
	 * Returns with the unique identifier of SNOMED&nbsp;CT concept representing an operator. 
	 * @return the unique identifier of the operator SNOMED&nbspCT concept.
	 */
	public String getOperatorComponentId() {
		return operatorComponentId;
	}
	
	/**
	 * (non-API)
	 * 
	 * Sets the operator concept identifier.
	 * @param operatorComponentId the operator SNOMED CT concept identifier.
	 */
	public void setOperatorComponentId(final String operatorComponentId) {
		this.operatorComponentId = operatorComponentId;
	}
	
	/**
	 * Returns with the unique identifier of SNOMED&nbsp;CT concept representing a unit of measurement. 
	 * @return the unique identifier of the unit of measurement SNOMED&nbspCT concept. Can be {@code null}.
	 */
	@Nullable
	public String getUomComponentId() {
		return uomComponentId;
	}
	
	/**
	 * (non-API)
	 * 
	 * Sets the unit of measurement concept identifier.
	 * @param uomComponentId the UOM SNOMED CT concept identifier. Can be {@code null}.
	 */
	public void setUomComponentId(@Nullable final String uomComponentId) {
		this.uomComponentId = uomComponentId;
	}
	
	/**
	 * Returns with the label of the attribute.
	 * @return the label associated with the attribute.
	 */
	public String getAttributeLabel() {
		return attributeLabel;
	}
	
	/**
	 * (non-API)
	 * 
	 * Sets the attribute label based on the specified one.
	 * @param attributeLabel the new value of the attribute label of the concrete data type.
	 */
	public void setAttributeLabel(final String attributeLabel) {
		this.attributeLabel = attributeLabel;
	}
	
	/**
	 * Returns with the characteristic type concept ID associated with the current reference set member.
	 * @return the characteristic type concept ID.
	 */
	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}
	
	/**
	 * (non-API)
	 * 
	 * Sets the characteristic type concept ID for the current reference set member.
	 * @param characteristicTypeId the characteristic concept ID.
	 */
	public void setCharacteristicTypeId(final String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}

	/*retrieves the attribute label at all cost. throw illegal argument exception if the referenced component type is not supported.*/
	private String extractAttributeLabel(final SnomedConcreteDataTypeRefSetMember member) {
		final String referencedComponentId = getReferencedComponentId();
		String conceptId = null;
		switch (getTerminologyComponentIdValue(referencedComponentId)) {
			case RELATIONSHIP_NUMBER:
				final ILookupService<String, Relationship, CDOView> service = CoreTerminologyBroker.getInstance().getLookupService(RELATIONSHIP);
				final IComponent<String> component = service.getComponent(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE), referencedComponentId);
				//look into the lightweight store
				if (component instanceof IStatement) {
					final IStatement<?> statement = castToStatement(component);
					return SnomedConceptNameProvider.INSTANCE.getText(statement.getAttributeId());
					//if we found the relationship type's preferred term
				}
				//else look into the CDO
				Relationship relationship = service.getComponent(referencedComponentId, member.cdoView());
				//get it from the underlying CDO view
				if (null == relationship)
					for (final Relationship newRelationship : ComponentUtils2.getNewObjects(member.cdoView(), Relationship.class)) {
						if (referencedComponentId.equals(newRelationship.getId())) {
							relationship = newRelationship;
							break;
						}
					}
				
				//we found the relationship type concept -> found the concept's PT
				conceptId = relationship.getType().getId();
			case CONCEPT_NUMBER:
				//the referenced component was a concept
				if (null == conceptId)
					conceptId = referencedComponentId;
				
				return SnomedConceptNameProvider.INSTANCE.getText(conceptId, member.cdoView());
			default:
				throw new IllegalArgumentException("Illegal referenced component identifier. ID: " + referencedComponentId);
		}
		
	}

	/*casts the specified component to a statement*/
	private IStatement<?> castToStatement(final IComponent<String> component) {
		return (IStatement<?>) component;
	}
}