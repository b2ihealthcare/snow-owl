/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.FSMUtil;

import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkResponse;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMModuleScopeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * SNOMED CT reference set editing context. Delegates to {@link SnomedEditingContext} to persist the identifier concept when persisting a
 * new reference set. On the other hand, removing a reference set doesn't automatically remove its identifier concept.
 * <p>
 * This editing context differs in construction from all other editing context classes &ndash; it is mandatory to call
 * {@link #createInstance()} or {@link #createInstance(IBranchPath)} instead of its constructor to obtain an instance, if a
 * {@link SnomedEditingContext} instance is not already present for use.
 * </p>
 * 
 * @see SnomedEditingContext#getRefSetEditingContext()
 * 
 */
public class SnomedRefSetEditingContext extends BaseSnomedEditingContext {
	
	private static final EnumSet<SnomedRefSetType> CONCEPT_REFERRING_MEMBER_TYPES = EnumSet.of(
			SnomedRefSetType.ATTRIBUTE_VALUE,
			SnomedRefSetType.ASSOCIATION, 
			SnomedRefSetType.CONCRETE_DATA_TYPE, 
			SnomedRefSetType.SIMPLE, 
			SnomedRefSetType.SIMPLE_MAP,
			SnomedRefSetType.SIMPLE_MAP_WITH_DESCRIPTION,
			SnomedRefSetType.COMPLEX_MAP, 
			SnomedRefSetType.EXTENDED_MAP,
			SnomedRefSetType.QUERY,
			SnomedRefSetType.OWL_AXIOM,
			SnomedRefSetType.MRCM_DOMAIN,
			SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN,
			SnomedRefSetType.MRCM_ATTRIBUTE_RANGE,
			SnomedRefSetType.MRCM_MODULE_SCOPE);
	
	private static final EnumSet<SnomedRefSetType> RELATIONSHIP_REFERRING_MEMBER_TYPES = EnumSet.of(
			SnomedRefSetType.ATTRIBUTE_VALUE,
			SnomedRefSetType.ASSOCIATION, 
			SnomedRefSetType.CONCRETE_DATA_TYPE); // MRMC types?
	
	private static final EnumSet<SnomedRefSetType> DESCRIPTION_REFERRING_MEMBER_TYPES = EnumSet.of(
			SnomedRefSetType.ATTRIBUTE_VALUE,
			SnomedRefSetType.ASSOCIATION,
			SnomedRefSetType.LANGUAGE,
			SnomedRefSetType.SIMPLE_MAP,
			SnomedRefSetType.SIMPLE_MAP_WITH_DESCRIPTION);
	
	protected final SnomedEditingContext snomedEditingContext;

	@Override
	protected <T> ILookupService<T, CDOView> getComponentLookupService(Class<T> type) {
		return getSnomedEditingContext().getComponentLookupService(type);
	}
	
	/**
	 * Creates a new SNOMED CT reference set editing context on the specified branch of the SNOMED CT repository.
	 * 
	 * @param branchPath the branch path to use
	 */
	public static SnomedRefSetEditingContext createInstance(final IBranchPath branchPath) {
		return new SnomedEditingContext(branchPath).getRefSetEditingContext();
	}
	
	public static ComponentIdentifier createDescriptionTypePair(final String descriptionId) {
		return ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, descriptionId);
	}

	// should be only called from SnomedEditingContext constructor
	/*default*/ SnomedRefSetEditingContext(final SnomedEditingContext snomedEditingContext) {
		super(snomedEditingContext.getTransaction());
		this.snomedEditingContext = snomedEditingContext;
	}
	
	@Override
	protected String getId(CDOObject component) {
		return getSnomedEditingContext().getId(component);
	}
	
	@Override
	protected <T extends CDOObject> Iterable<? extends IComponent> fetchComponents(Collection<String> componentIds, Class<T> type) {
		return getSnomedEditingContext().fetchComponents(componentIds, type);
	}

	public SnomedEditingContext getSnomedEditingContext() {
		return snomedEditingContext;
	}
	
	@Override
	public void delete(EObject object, boolean force) throws ConflictException {
		snomedEditingContext.delete(object, force);
	}
	
	/*
	 * Simple instance builder method for building SnomedRefSet instances with the given arguments.
	 */
	private SnomedRegularRefSet createSnomedRegularRefSet(final short referencedComponentType, final SnomedRefSetType type) {
		final SnomedRegularRefSet snomedRefSet = SnomedRefSetFactory.eINSTANCE.createSnomedRegularRefSet();
		snomedRefSet.setType(type);
		snomedRefSet.setReferencedComponentType(referencedComponentType);
		return snomedRefSet;
	}
	
	/**
	 * Creates a SNOMED CT simple type reference set.
	 * @param fullySpecifiedName the fully specified name of the reference set identifier concept.
	 * @param referencedComponentType the unique referenced component type as a string literal.
	 * @return the brand new reference set.
	 */
	public SnomedRegularRefSet createSnomedSimpleTypeRefSet(final String fullySpecifiedName, final String referencedComponentType, final String languageReferenceSetId) {
		return createSnomedSimpleTypeRefSet(fullySpecifiedName, referencedComponentType, Concepts.REFSET_SIMPLE_TYPE, languageReferenceSetId);
	}
	
	/**
	 * Creates a SNOMED CT simple type reference set as a child of the given parent concept.
	 * @param fullySpecifiedName the fully specified name of the reference set identifier concept.
	 * @param referencedComponentType the unique referenced component type as a string literal.
	 * @param parentConcept the parent concept of the new concept.
	 * @return the brand new reference set.
	 */
	public SnomedRegularRefSet createSnomedSimpleTypeRefSet(final String fullySpecifiedName, final String referencedComponentType, final String parentConceptId, final String languageReferenceSetId) {
		final SnomedRegularRefSet snomedRefSet = createSnomedRegularRefSet(getTerminologyComponentTypeAsShort(referencedComponentType), SnomedRefSetType.SIMPLE);
		createIdentifierAndAddRefSet(snomedRefSet, parentConceptId, fullySpecifiedName, languageReferenceSetId);
		return snomedRefSet;
	}
	
	/**
	 * Creates a new SNOMED CT <i>simple type</i> reference set member with the specified arguments.
	 * <p>
	 * Note that the member's parent reference set feature will be initialized, but the member itself will <i>not</i> be
	 * added to the reference set's members list.
	 * 
	 * @param referencedComponentPair the component identifier - terminology identifier pair for the referenced component
	 * @param moduleId the module ID for the reference set member
	 * @param regularRefSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 */
	public SnomedRefSetMember createSimpleTypeRefSetMember(final String referencedComponentId, 
			final String moduleId, 
			final SnomedRegularRefSet regularRefSet) {
		
		final SnomedRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedRefSetMember();
		initializeRefSetMember(member, referencedComponentId, moduleId, regularRefSet);
		return member;
	}

	/**
	 * Creates a new SNOMED CT <i>simple map type</i> reference set member with the specified arguments.
	 * <p>
	 * Note that the member's parent reference set feature will be initialized, but the member itself will <b>NOT</b> be
	 * added to the reference set's members list.
	 * 
	 * @param referencedComponentPair the component identifier - terminology identifier pair for the referenced component
	 * @param mapTargetPair the component identifier - terminology identifier pair for the map target
	 * @param moduleId the module ID for the reference set member
	 * @param refSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 */
	public SnomedSimpleMapRefSetMember createSimpleMapRefSetMember(final String referencedComponentId, 
			final String mapTargetComponentId,
			final String moduleId,
			final SnomedMappingRefSet refSet) {
		return createSimpleMapRefSetMember(referencedComponentId, mapTargetComponentId, null, moduleId, refSet);
	}

	/**
	 * Creates a new SNOMED CT <i>simple map type</i> reference set member with the specified arguments.
	 * <p>
	 * Note that the member's parent reference set feature will be initialized, but the member itself will <b>NOT</b> be
	 * added to the reference set's members list.
	 * 
	 * @param referencedComponentPair the component identifier - terminology identifier pair for the referenced component
	 * @param mapTargetPair the component identifier - terminology identifier pair for the map target
	 * @param mapTargetDescription optional map target description for the member 
	 * @param moduleId the module ID for the reference set member
	 * @param refSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 */
	private SnomedSimpleMapRefSetMember createSimpleMapRefSetMember(final String referencedComponentId, 
			final String mapTargetComponentId,
			@Nullable final String mapTargetDescription,
			final String moduleId,
			final SnomedMappingRefSet refSet) {
		final SnomedSimpleMapRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedSimpleMapRefSetMember();
		initializeRefSetMember(member, referencedComponentId, moduleId, refSet);
		if (mapTargetDescription != null) {
			member.setMapTargetComponentDescription(mapTargetDescription);
		}
		initializeMapTarget(member, mapTargetComponentId);
		return member;
	}

	/**
	 * Creates a new SNOMED CT <i>complex map type</i> reference set member with the specified arguments.
	 * <p>
	 * Note that the member's parent reference set feature will be initialized, but the member itself will <i>not</i> be
	 * added to the reference set's members list.
	 * <p>
	 * The correlation ID set for this reference set member is {@link Concepts#REFSET_CORRELATION_NOT_SPECIFIED}.
	 * 
	 * @param referencedComponentId the component identifier - terminology identifier pair for the referenced component
	 * @param mapTargetPair the component identifier - terminology identifier pair for the map target
	 * @param moduleId the module ID for the reference set member
	 * @param refSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 */
	public SnomedComplexMapRefSetMember createComplexMapRefSetMember(final String referencedComponentId, 
			final String mapTargetComponentId, 
			final String moduleId,
			final SnomedMappingRefSet mappingRefSet) {
		
		final SnomedComplexMapRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedComplexMapRefSetMember();
		initializeRefSetMember(member, referencedComponentId, moduleId, mappingRefSet);
		initializeMapTarget(member, mapTargetComponentId);
		member.setCorrelationId(Concepts.REFSET_CORRELATION_NOT_SPECIFIED);
		return member;
	}

	private void initializeMapTarget(final SnomedSimpleMapRefSetMember member, final String mapTargetComponentId) {
		if (mapTargetComponentId != null) {
			member.setMapTargetComponentId(mapTargetComponentId);
		}
	}
	
	/**
	 * Creates a new SNOMED CT <i>concrete domain</i> reference set member with the specified arguments.
	 * <p>
	 * Note that the member's parent reference set feature will be initialized, but the member itself will <i>not</i> be
	 * added to the reference set's members list.
	 * 
	 * @param referencedComponentId the component identifier - terminology identifier pair for the referenced component
	 * @param type the concrete domain of the reference set member
	 * @param value the value of the reference set member
	 * @param label the label of the reference set member
	 * @param moduleId the module ID for the reference set member
	 * @param concreteDataTypeRefSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 */
	public SnomedConcreteDataTypeRefSetMember createConcreteDataTypeRefSetMember(final String referencedComponentId, 
			DataType type, 
			final Object value,
			final String characteristicTypeId,
			final String label, 
			final String moduleId,
			final SnomedConcreteDataTypeRefSet concreteDataTypeRefSet) {
		
		final SnomedConcreteDataTypeRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedConcreteDataTypeRefSetMember();
		initializeRefSetMember(member, referencedComponentId, moduleId, concreteDataTypeRefSet);
		member.setSerializedValue(SnomedRefSetUtil.serializeValue(type, value));
		member.setLabel(label);
		member.setOperatorComponentId(Concepts.CD_EQUAL);
		member.setCharacteristicTypeId(characteristicTypeId);
		return member;
	}
	
	/**
	 * Creates a new SNOMED CT <i>concrete domain</i> reference set member with the specified arguments.
	 * <p>
	 * Note that the member's parent reference set feature will be initialized, but the member itself will <i>not</i> be
	 * added to the reference set's members list.
	 * 
	 * @param referencedComponentId the component identifier - terminology identifier pair for the referenced component
	 * @param uomComponentId the unit of measurement component identifier
	 * @param operatorComponentId the comparison operator component identifier
	 * @param value the value of the reference set member
	 * @param attrLabel the label of the concrete domain.
	 * @param moduleId the module ID for the reference set member
	 * @param concreteDataTypeRefSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 */
	public SnomedConcreteDataTypeRefSetMember createConcreteDataTypeRefSetMember(final String referencedComponentId, 
			final String uomComponentId, 
			final String operatorComponentId, 
			final Object value,
			final String characteristicTypeId,
			final String attrLabel, 
			final String moduleId,
			final SnomedConcreteDataTypeRefSet concreteDataTypeRefSet) {
		
		final SnomedConcreteDataTypeRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedConcreteDataTypeRefSetMember();
		initializeRefSetMember(member, referencedComponentId, moduleId, concreteDataTypeRefSet);
		member.setSerializedValue(SnomedRefSetUtil.serializeValue(SnomedRefSetUtil.getByClass(value.getClass()), value));
		member.setUomComponentId(uomComponentId);
		member.setCharacteristicTypeId(characteristicTypeId);
		member.setOperatorComponentId(operatorComponentId);
		member.setLabel(checkNotNull(attrLabel, "Attribute Label must be specified."));
		
		return member; 
	}

	/**
	 * Creates a new SNOMED CT <i>query type</i> reference set member with the specified arguments.
	 * <p>
	 * Note that the member's parent reference set feature will be initialized, but the member itself will <i>not</i> be
	 * added to the reference set's members list.
	 * 
	 * @param referencedComponentId the component identifier - terminology identifier pair for the referenced component
	 * @param query the query this reference set member represents
	 * @param moduleId the module ID for the reference set member
	 * @param regularRefSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 */
	public SnomedQueryRefSetMember createQueryRefSetMember(final String referencedComponentId, 
			@Nullable final String query, 
			final String moduleId,
			final SnomedRegularRefSet regularRefSet) {
		
		final SnomedQueryRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedQueryRefSetMember();
		initializeRefSetMember(member, referencedComponentId, moduleId, regularRefSet);
		
		if (null != query) {
			member.setQuery(query);
		}
		
		return member;
	}
	
	/**
	 * Creates a new SNOMED CT <i>language type</i> reference set member with the specified arguments.
	 * <p>
	 * Note that the member's parent reference set feature will be initialized, but the member itself will <i>not</i> be
	 * added to the reference set's members list.
	 * 
	 * @param referencedComponentId the component identifier - terminology identifier pair for the referenced component
	 * @param acceptabilityPair the component identifier - terminology identifier pair for the acceptability of this reference set member
	 * @param moduleId the module ID for the reference set member
	 * @param languageRefSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 * @deprecated - use {@link SnomedComponents#newLanguageMember()} instead
	 */
	public SnomedLanguageRefSetMember createLanguageRefSetMember(final String referencedComponentId, 
			@Nullable final String acceptabilityId, 
			final String moduleId,
			final SnomedStructuralRefSet languageRefSet) {
		
		final SnomedLanguageRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedLanguageRefSetMember();
		initializeRefSetMember(member, referencedComponentId, moduleId, languageRefSet);
		
		if (null != acceptabilityId) {
			member.setAcceptabilityId(acceptabilityId);
		}
		
		return member;
	}

	/**
	 * Creates a new SNOMED CT <i>attribute value type</i> reference set member with the specified arguments.
	 * <p>
	 * Note that the member's parent reference set feature will be initialized, but the member itself will <i>not</i> be
	 * added to the reference set's members list.
	 * 
	 * @param referencedComponentId the component identifier - terminology identifier pair for the referenced component
	 * @param valueComponentPair the component identifier - terminology identifier pair for the value of this reference set member
	 * @param moduleId the module ID for the reference set member
	 * @param refSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 * @deprecated - use {@link SnomedComponents#newAttributeValueMember()} instead
	 */
	public SnomedAttributeValueRefSetMember createAttributeValueRefSetMember(final String referencedComponentId, 
			@Nullable final String valueId, 
			final String moduleId,
			final SnomedRefSet refSet) {
		
		final SnomedAttributeValueRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedAttributeValueRefSetMember();
		initializeRefSetMember(member, referencedComponentId, moduleId, refSet);
		
		if (null != valueId) {
			member.setValueId(valueId);
		}
		
		return member;
	}
	
	/**
	 * Creates a new SNOMED CT <i>association type</i> reference set member with the specified arguments.
	 * <p>
	 * Note that the member's parent reference set feature will be initialized, but the member itself will <i>not</i> be
	 * added to the reference set's members list.
	 * 
	 * @param referencedComponentId the component identifier - terminology identifier pair for the referenced component
	 * @param targetComponentId the component identifier - terminology identifier pair for the association target of this reference set member
	 * @param moduleId the module ID for the reference set member
	 * @param structuralRefSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 * @deprecated - use {@link SnomedComponents#newAssociationMember()} instead
	 */
	public SnomedAssociationRefSetMember createAssociationRefSetMember(final String referencedComponentId, 
			@Nullable final String targetComponentId, 
			final String moduleId,
			final SnomedStructuralRefSet structuralRefSet) {
		
		final SnomedAssociationRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedAssociationRefSetMember();
		initializeRefSetMember(member, referencedComponentId, moduleId, structuralRefSet);

		if (null != targetComponentId) {
			member.setTargetComponentId(targetComponentId);
		}
		
		return member;
	}

	private SnomedRefSetMember initializeRefSetMember(final SnomedRefSetMember member, String referencedComponentId, final String moduleId, final SnomedRefSet refSet) {
		
		// Set all common fields for a reference set member. Effective time is left unset, released flag defaults to false
		member.setActive(true);
		member.setModuleId(moduleId);
		member.setUuid(UUID.randomUUID().toString());
		member.setRefSet(refSet);
		member.setReferencedComponentId(referencedComponentId);
		
		return member;
	}

	@Override
	public void close() {
		// Disposes of the transaction used here, no need to call super.dispose()
		snomedEditingContext.close();
	}

	protected List<SnomedRefSetMember> getReferringMembers(final Component component) {
		
		checkNotNull(component, "Component argument cannot be null.");
		
		// already detached. nothing to do
		if (FSMUtil.isTransient(component)) {
			return Collections.emptyList();
		}
		
		final List<SnomedRefSetMember> referringMembers = Lists.newArrayList();

		final String id = checkNotNull(component.getId(), "Component ID was null for component. [" + component + "]");
		
		// process new referring members from the transaction.
		if (CDOState.NEW.equals(component.cdoState())) {
			final Iterable<SnomedRefSetMember> newMembers = ComponentUtils2.getNewObjects(transaction, SnomedRefSetMember.class);
			
			for (final SnomedRefSetMember member : newMembers) {
				
				if (id.equals(member.getReferencedComponentId())) {
					referringMembers.add(member);
				}
				
				if (isReferredBy(member, id)) {
					referringMembers.add(member);
				}
			}
		// persistent component. check for referring members in index
		} else {
			for (SnomedReferenceSetMember member : getAllReferringMembers(id, getReferringMemberTypes(component))) {
				referringMembers.add((SnomedRefSetMember) getSnomedEditingContext().lookupIfExists(member.getStorageKey()));
			}
		}
		
		return referringMembers;
	}
	
	protected List<SnomedRefSetMember> getMembers(final SnomedRefSet refSet) {
		checkNotNull(refSet, "Component argument cannot be null.");
		
		if (FSMUtil.isTransient(refSet)) {
			return Collections.emptyList();
		}
		
		final List<SnomedRefSetMember> referringMembers = Lists.newArrayList();
		final String id = checkNotNull(refSet.getIdentifierId(), "Identifier was null for reference set. [" + refSet + "]");
		
		if (CDOState.NEW.equals(refSet.cdoState())) {
			final Iterable<SnomedRefSetMember> newMembers = ComponentUtils2.getNewObjects(transaction, SnomedRefSetMember.class);
			
			for (final SnomedRefSetMember member : newMembers) {
				if (id.equals(member.getRefSetIdentifierId())) {
					referringMembers.add(member);
				}
			}
		} else {
			for (SnomedReferenceSetMember member : getMembers(id)) {
				referringMembers.add((SnomedRefSetMember) getSnomedEditingContext().lookupIfExists(member.getStorageKey()));
			}
		}
		
		return referringMembers;
	}
	
	private SnomedReferenceSetMembers getMembers(String id) {
		return SnomedRequests.prepareSearchMember()
				.all()
				.filterByRefSet(id)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
	}

	private SnomedReferenceSetMembers getAllReferringMembers(String id, EnumSet<SnomedRefSetType> types) {
		// construct bulk requests with many sub queries to search for any member that references the given ID in any RF2 member component field
		return RepositoryRequests.prepareBulkRead()
				.setBody(BulkRequest.<BranchContext>create()
						.add(getReferringMembers(id, types))
						.add(getReferringMembersByProps(id, types, Fields.TARGET_COMPONENT)) // association
						.add(getReferringMembersByProps(id, types, Fields.VALUE_ID)) // attribute value
						.add(getReferringMembersByProps(id, types, Fields.UNIT_ID)) // cd
						.add(getReferringMembersByProps(id, types, Fields.CHARACTERISTIC_TYPE_ID)) // cd
						.add(getReferringMembersByProps(id, types, Fields.OPERATOR_ID)) // cd
						.add(getReferringMembersByProps(id, types, Fields.DESCRIPTION_FORMAT))
						.add(getReferringMembersByProps(id, types, Fields.ACCEPTABILITY_ID)) // language
						.add(getReferringMembersByProps(id, types, Fields.MAP_TARGET)) // simple map
						.add(getReferringMembersByProps(id, types, Fields.CORRELATION_ID)) // complex map
						.add(getReferringMembersByProps(id, types, Fields.MAP_CATEGORY_ID)) // extended map
						.add(getReferringMembersByProps(id, types, Fields.MRCM_DOMAIN_ID)) // MRCM attr domain
						.add(getReferringMembersByProps(id, types, Fields.MRCM_RULE_STRENGTH_ID)) // MRCM attr domain/range
						.add(getReferringMembersByProps(id, types, Fields.MRCM_CONTENT_TYPE_ID)) // MRCM attr domain/range
						.add(getReferringMembersByProps(id, types, Fields.MRCM_RULE_REFSET_ID)) // MRCM module scope
						)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<BulkResponse, SnomedReferenceSetMembers>() {
					@Override
					public SnomedReferenceSetMembers apply(BulkResponse input) {
						final List<SnomedReferenceSetMember> items = ImmutableList.copyOf(Iterables.concat(input.getResponses(SnomedReferenceSetMembers.class)));
						return new SnomedReferenceSetMembers(items, null, null, items.size(), items.size());
					}
				})
				.getSync();
	}

	private SnomedRefSetMemberSearchRequestBuilder getReferringMembers(String id, EnumSet<SnomedRefSetType> types) {
		return SnomedRequests.prepareSearchMember().all().filterByRefSetType(types).filterByReferencedComponent(id);
	}
	
	private SnomedRefSetMemberSearchRequestBuilder getReferringMembersByProps(String id, EnumSet<SnomedRefSetType> types, final String propField) {
		return SnomedRequests.prepareSearchMember().all().filterByRefSetType(types).filterByProps(OptionsBuilder.newBuilder()
				.put(propField, id)
				.build());
	}

	private EnumSet<SnomedRefSetType> getReferringMemberTypes(final Component component) {
		if (component instanceof Concept) {
			return CONCEPT_REFERRING_MEMBER_TYPES;
		} else if (component instanceof Relationship) {
			return RELATIONSHIP_REFERRING_MEMBER_TYPES;
		} else if (component instanceof Description) {
			return DESCRIPTION_REFERRING_MEMBER_TYPES;
		}
		throw new IllegalArgumentException("Invalid component type: " + component.getClass());
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.CDOEditingContext#getRootResourceName()
	 */
	@Override
	protected String getRootResourceName() {
		return SnomedDatastoreActivator.REFSET_ROOT_RESOURCE_NAME;
	}
	
	@Nullable
	private boolean isReferredBy(final SnomedRefSetMember member, String componentId) {
		
		if (member instanceof SnomedAttributeValueRefSetMember) {
			
			return componentId.equals(((SnomedAttributeValueRefSetMember) member).getValueId());
			
		} else if (member instanceof SnomedAssociationRefSetMember) {
			
			return componentId.equals(((SnomedAssociationRefSetMember) member).getTargetComponentId());
			
		} else if (member instanceof SnomedConcreteDataTypeRefSetMember) {
			
			SnomedConcreteDataTypeRefSetMember cdMember = (SnomedConcreteDataTypeRefSetMember) member;
			return componentId.equals(cdMember.getUomComponentId()) 
					|| componentId.equals(cdMember.getOperatorComponentId())
					|| componentId.equals(cdMember.getCharacteristicTypeId());
			
		} else if (member instanceof SnomedDescriptionTypeRefSetMember) {
			
			return componentId.equals(((SnomedDescriptionTypeRefSetMember) member).getDescriptionFormat());
			
		} else if (member instanceof SnomedLanguageRefSetMember) {
			
			return componentId.equals(((SnomedLanguageRefSetMember) member).getAcceptabilityId());
			
		} else if (member instanceof SnomedSimpleMapRefSetMember) {
			
			short mapTargetComponentType = ((SnomedSimpleMapRefSetMember) member).getMapTargetComponentType();
			
			if (SnomedTerminologyComponentConstants.isCoreComponentType(mapTargetComponentType)) {
				
				if (member instanceof SnomedComplexMapRefSetMember) {
					SnomedComplexMapRefSetMember complexMember = (SnomedComplexMapRefSetMember) member;
					return componentId.equals(complexMember.getMapTargetComponentId())
							|| componentId.equals(complexMember.getCorrelationId())
							|| componentId.equals(complexMember.getMapCategoryId());
				} else {
					return componentId.equals(((SnomedSimpleMapRefSetMember) member).getMapTargetComponentId());
				}
				
			}
			
		} else if (member instanceof SnomedMRCMAttributeDomainRefSetMember) {
			
			SnomedMRCMAttributeDomainRefSetMember mrcmMember = (SnomedMRCMAttributeDomainRefSetMember) member;
			return componentId.equals(mrcmMember.getDomainId())
					|| componentId.equals(mrcmMember.getRuleStrengthId())
					|| componentId.equals(mrcmMember.getContentTypeId());
			
		} else if (member instanceof SnomedMRCMAttributeRangeRefSetMember) {
			
			SnomedMRCMAttributeRangeRefSetMember mrcmMember = (SnomedMRCMAttributeRangeRefSetMember) member;
			return componentId.equals(mrcmMember.getRuleStrengthId())
					|| componentId.equals(mrcmMember.getContentTypeId());
			
		} else if (member instanceof SnomedMRCMModuleScopeRefSetMember) {
			
			return componentId.equals(((SnomedMRCMModuleScopeRefSetMember) member).getMrcmRuleRefsetId());
			
		}
		
		return false;
	} 

	private void createIdentifierAndAddRefSet(final SnomedRefSet snomedRefSet, final String parentConceptId, final String name, final String languageReferenceSetId) {
		createIdentifierAndAddRefSet(snomedRefSet, getSnomedEditingContext().generateComponentId(ComponentCategory.CONCEPT), parentConceptId, name, languageReferenceSetId);
	}
	
	// create identifier concept with the given arguments, save it locally
	private void createIdentifierAndAddRefSet(final SnomedRefSet snomedRefSet, final String conceptId, final String parentConceptId, final String name, final String languageReferenceSetId) {
		final Concept identifier = createIdentifierConcept(conceptId, parentConceptId, name, languageReferenceSetId);
		snomedRefSet.setIdentifierId(identifier.getId());
		add(snomedRefSet);
	}

	/**
	 * non-API - will be refactored later
	 * @param parentConceptId
	 * @param name
	 * @return
	 */
	public Concept createIdentifierConcept(final String conceptId, final String parentConceptId, final String name, final String languageReferenceSetId) {
		final SnomedEditingContext context = getSnomedEditingContext();
		
		// FIXME replace with proper builder, 
		// create identifier concept with one FSN
		final Concept identifier = context.buildDefaultConcept(conceptId, name, parentConceptId);
		final Description synonym = context.buildDefaultDescription(name, Concepts.SYNONYM);
		synonym.setConcept(identifier);
		
		final Relationship inferredIsa = context.buildDefaultRelationship(identifier, context.findConceptById(Concepts.IS_A),
				context.findConceptById(parentConceptId), context.findConceptById(Concepts.INFERRED_RELATIONSHIP));
		identifier.getOutboundRelationships().add(inferredIsa);
		
		// create language reference set members for the descriptions, one FSN and PT both should be preferred
		final SnomedStructuralRefSet languageRefSet = getLanguageRefSet(languageReferenceSetId);
		for (final Description description : identifier.getDescriptions()) {
			if (description.isActive()) { //this point all description should be active
				//create language reference set membership
				final ComponentIdentifier referencedComponentPair = SnomedRefSetEditingContext.createDescriptionTypePair(description.getId());
				final SnomedLanguageRefSetMember member = createLanguageRefSetMember(referencedComponentPair.getComponentId(), Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED, context.getDefaultModuleConcept().getId(), languageRefSet);
				description.getLanguageRefSetMembers().add(member);
			}
		}
		return identifier;
	}
	
	private SnomedStructuralRefSet getLanguageRefSet(String languageRefSetId) {
		return snomedEditingContext.getLanguageRefSet(languageRefSetId);
	}

	private short getTerminologyComponentTypeAsShort(final String terminologyComponentId) {
		return CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsShort(terminologyComponentId);
	}

}