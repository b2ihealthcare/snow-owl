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
package com.b2international.snowowl.snomed.datastore;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.FSMUtil;

import com.b2international.commons.StringUtils;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentNameProvider;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkResponse;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CdoViewComponentTextProvider;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.b2international.snowowl.snomed.datastore.services.SnomedModuleDependencyRefSetService;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
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
			SnomedRefSetType.COMPLEX_MAP, 
			SnomedRefSetType.EXTENDED_MAP,
			SnomedRefSetType.QUERY);
	
	private static final EnumSet<SnomedRefSetType> RELATIONSHIP_REFERRING_MEMBER_TYPES = EnumSet.of(
			SnomedRefSetType.ATTRIBUTE_VALUE,
			SnomedRefSetType.ASSOCIATION, 
			SnomedRefSetType.CONCRETE_DATA_TYPE);
	
	private static final EnumSet<SnomedRefSetType> DESCRIPTION_REFERRING_MEMBER_TYPES = EnumSet.of(
			SnomedRefSetType.ATTRIBUTE_VALUE,
			SnomedRefSetType.ASSOCIATION,
			SnomedRefSetType.LANGUAGE,
			SnomedRefSetType.SIMPLE_MAP);
	
	protected final SnomedEditingContext snomedEditingContext;

	private final CdoViewComponentTextProvider transactionTextProvider;

	/**
	 * Creates and returns a reference set based on the given values.
	 * 
	 * @param parentIdentifierConceptId concept ID the reference set identifier concept's parent. 
	 * @param label the preferred term for the reference set.
	 * @param referencedComponentType the referenced component type.
	 * @param mapTargetType the map target component type. Ignored in case of *NON* mapping {@link SnomedRefSetType reference set type}s.
	 * @param type the reference set type.
	 * @return the created reference set
	 */
	public SnomedRefSet createReferenceSet(final String parentIdentifierConceptId, final String label, final String referencedComponentType, final short mapTargetType, final SnomedRefSetType type) throws CommitException {
		switch (type) {
			case SIMPLE:
				return createSnomedSimpleTypeRefSet(label, referencedComponentType, parentIdentifierConceptId);
			case ATTRIBUTE_VALUE:
				return createSnomedAttributeRefSet(label, referencedComponentType);
			case SIMPLE_MAP:
				final SnomedMappingRefSet simpleMap = createSnomedSimpleMapRefSet(label, referencedComponentType, parentIdentifierConceptId);
				simpleMap.setMapTargetComponentType(mapTargetType);
				return simpleMap;
			case EXTENDED_MAP: //$FALL-THROUGH$
			case COMPLEX_MAP:
				final SnomedMappingRefSet complexMap = createSnomedComplexMapRefSet(label, referencedComponentType, type);
				complexMap.setMapTargetComponentType(mapTargetType);
				return complexMap;
			case ASSOCIATION: //$FALL-THROUGH$
			case CONCRETE_DATA_TYPE: //$FALL-THROUGH$
			case DESCRIPTION_TYPE: //$FALL-THROUGH$
			case QUERY: //$FALL-THROUGH$ 
			case LANGUAGE: //$FALL-THROUGH$
				throw new UnsupportedOperationException("Creating " + type + " reference set is currently not supported.");
			default:
				throw new IllegalArgumentException("Unknown SNOMED CT reference set type: " + type);
			
		} 
	}
	
	@Override
	protected <T> ILookupService<String, T, CDOView> getComponentLookupService(Class<T> type) {
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
	
	public static ComponentIdentifierPair<String> createDescriptionTypePair(final String descriptionId) {
		return ComponentIdentifierPair.<String>create(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, descriptionId);
	}

	public static ComponentIdentifierPair<String> createRefSetTypePair(final String refSetId) {
		return ComponentIdentifierPair.<String>create(SnomedTerminologyComponentConstants.REFSET_NUMBER, refSetId);
	}

	public static ComponentIdentifierPair<String> createConceptTypePair(final String conceptId) {
		return ComponentIdentifierPair.<String>create(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, conceptId);
	}
	
	public static ComponentIdentifierPair<String> createRelationshipTypePair(final String relationshipId) {
		return ComponentIdentifierPair.<String>create(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationshipId);
	}
	
	public static ComponentIdentifierPair<String> createUnspecifiedTypePair(final String id) {
		return ComponentIdentifierPair.<String>create(CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT, id);
	}

	// should be only called from SnomedEditingContext constructor
	/*default*/ SnomedRefSetEditingContext(final SnomedEditingContext snomedEditingContext) {
		super(snomedEditingContext.getTransaction());
		this.snomedEditingContext = snomedEditingContext;
		this.transactionTextProvider = new CdoViewComponentTextProvider(ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class), snomedEditingContext.getTransaction());
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
	
	private SnomedStructuralRefSet createSnomedStructuralRefSet(final short referencedComponentType, final SnomedRefSetType type) {
		final SnomedStructuralRefSet snomedRefSet = SnomedRefSetFactory.eINSTANCE.createSnomedStructuralRefSet();
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
	public SnomedRegularRefSet createSnomedSimpleTypeRefSet(final String fullySpecifiedName, final String referencedComponentType) {
		return createSnomedSimpleTypeRefSet(fullySpecifiedName, referencedComponentType, Concepts.REFSET_SIMPLE_TYPE);
	}
	
	/**
	 * Creates a SNOMED&nbsp;CT simple type reference set with the given identifier concept.
	 * 
	 * @param identifierConcept the identifier concept of the reference set.
	 * @param referencedComponentType the unique reference component type as a string literal.
	 * @return the new SNOMED&nbsp;CT simple type reference set.
	 */
	public SnomedRegularRefSet createSnomedSimpleTypeRefSet(final Concept identifierConcept, final String referencedComponentType) {
		final SnomedRegularRefSet snomedRefSet = createSnomedRegularRefSet(getTerminologyComponentTypeAsShort(referencedComponentType), SnomedRefSetType.SIMPLE);
		snomedRefSet.setIdentifierId(identifierConcept.getId());
		add(snomedRefSet);
		return snomedRefSet;
	}
	
	/**
	 * Creates a SNOMED CT simple type reference set as a child of the given parent concept.
	 * @param fullySpecifiedName the fully specified name of the reference set identifier concept.
	 * @param referencedComponentType the unique referenced component type as a string literal.
	 * @param parentConcept the parent concept of the new concept.
	 * @return the brand new reference set.
	 */
	public SnomedRegularRefSet createSnomedSimpleTypeRefSet(final String fullySpecifiedName, final String referencedComponentType, final String parentConceptId) {
		final SnomedRegularRefSet snomedRefSet = createSnomedRegularRefSet(getTerminologyComponentTypeAsShort(referencedComponentType), SnomedRefSetType.SIMPLE);
		createIdentifierAndAddRefSet(snomedRefSet, parentConceptId, fullySpecifiedName);
		return snomedRefSet;
	}
	
	/**
	 * Creates a SNOMED CT concrete domain type reference set.
	 * @param fullySpecifiedName the fully specified name reference set identifier concept.
	 * @return the new SNOMED CT simple map type reference set.
	 * @deprecated - unused, will be removed in 4.4
	 */
	public SnomedRefSet createSnomedConcreteDataTypeTypeRefSet(final String fullySpecifiedName, final String referencedComponentType) {
		final SnomedRefSet snomedRefSet = createSnomedRegularRefSet(getTerminologyComponentTypeAsShort(referencedComponentType), SnomedRefSetType.CONCRETE_DATA_TYPE);
		createIdentifierAndAddRefSet(snomedRefSet, Concepts.REFSET_SIMPLE_TYPE, fullySpecifiedName);
		return snomedRefSet;
	}

	/**
	 * Creates a SNOMED CT simple map type reference set.
	 * @param fullySpecifiedName the fully specified name reference set identifier concept.
	 * @return the new SNOMED CT simple map type reference set.
	 */
	public SnomedMappingRefSet createSnomedSimpleMapRefSet(final String fullySpecifiedName, final String referencedComponentType) {
		return createSnomedSimpleMapRefSet(fullySpecifiedName, referencedComponentType, Concepts.REFSET_SIMPLE_MAP_TYPE);
	}
	
	/**
	 * Creates a SNOMED&nbsp;CT simple map type reference set.
	 * 
	 * @param identifierConcept the identifier concept of the simple map.
	 * @param referencedComponentType the referenced component type of the simple map.
	 * @return the new SNOMED&nbsp;CT simple map type reference set.
	 */
	public SnomedMappingRefSet createSnomedSimpleMapRefSet(final Concept identifierConcept, final String referencedComponentType) {
		final SnomedMappingRefSet mappingRefSet = SnomedRefSetFactory.eINSTANCE.createSnomedMappingRefSet();
		mappingRefSet.setType(SnomedRefSetType.SIMPLE_MAP);
		mappingRefSet.setReferencedComponentType(getTerminologyComponentTypeAsShort(referencedComponentType));
		mappingRefSet.setIdentifierId(identifierConcept.getId());
		add(mappingRefSet);
		
		return mappingRefSet;
	}
	
	/**
	 * Creates a SNOMED CT simple map type reference set with the given typeId.
	 * @param fullySpecifiedName the fully specified name reference set identifier concept.
	 * @param referencedComponentType
	 * @param typeId
	 * @return the new SNOMED CT simple map type reference set.
	 */
	public SnomedMappingRefSet createSnomedSimpleMapRefSet(final String fullySpecifiedName, final String referencedComponentType, final String typeId) {
		final SnomedMappingRefSet mappingRefSet = SnomedRefSetFactory.eINSTANCE.createSnomedMappingRefSet();
		mappingRefSet.setType(SnomedRefSetType.SIMPLE_MAP);
		mappingRefSet.setReferencedComponentType(getTerminologyComponentTypeAsShort(referencedComponentType));
		createIdentifierAndAddRefSet(mappingRefSet, typeId, fullySpecifiedName);
		return mappingRefSet;
	}

	/**
	 * Creates a SNOMED CT language type reference set.
	 * @param fullySpecifiedName the fully specified name of the reference set identifier concept.
	 * @return the new language type SNOMED CT reference set.
	 */
	public SnomedStructuralRefSet createSnomedLanguageRefSet(final String fullySpecifiedName) {
		final SnomedStructuralRefSet snomedRefSet = createSnomedStructuralRefSet(getTerminologyComponentTypeAsShort(SnomedTerminologyComponentConstants.DESCRIPTION), SnomedRefSetType.LANGUAGE); 
		createIdentifierAndAddRefSet(snomedRefSet, Concepts.REFSET_LANGUAGE_TYPE, fullySpecifiedName);
		return snomedRefSet;
	}

	/**
	 * Creates a new SNOMED CT query type reference set.
	 * @param fullySpecifiedName the fully specified name of the reference set identifier concept.
	 * @return the new query type reference set.
	 */
	public SnomedRegularRefSet createSnomedQueryRefSet(final String fullySpecifiedName) {
		final SnomedRegularRefSet snomedRefSet = createSnomedRegularRefSet(getTerminologyComponentTypeAsShort(SnomedTerminologyComponentConstants.REFSET), SnomedRefSetType.QUERY);
		createIdentifierAndAddRefSet(snomedRefSet, Concepts.REFSET_QUERY_SPECIFICATION_TYPE, fullySpecifiedName);
		return snomedRefSet;
	}

	/**
	 * Creates a new SNOMED CT attribute value type reference set.
	 * @param fullySpecifiedName the fully specified name of the reference set identifier concept.
	 * @param referencedComponentType the type of the desired 
	 * @return the refset with an identifier concept, and which is locally saved
	 */
	public SnomedRegularRefSet createSnomedAttributeRefSet(final String fullySpecifiedName, final String referencedComponentType) {
		final SnomedRegularRefSet snomedRefSet = createSnomedRegularRefSet(getTerminologyComponentTypeAsShort(referencedComponentType), SnomedRefSetType.ATTRIBUTE_VALUE);
		createIdentifierAndAddRefSet(snomedRefSet, Concepts.REFSET_ATTRIBUTE_VALUE_TYPE, fullySpecifiedName);
		return snomedRefSet;
	}
	
	/**
	 * Creates a SNOMED CT complex map type reference set.
	 * @param fullySpecifiedName the fully specified name reference set identifier concept.
	 * @return the new SNOMED CT complex map type reference set.
	 */
	public SnomedMappingRefSet createSnomedComplexMapRefSet(final String fullySpecifiedName, final String referencedComponentType, final SnomedRefSetType type) {
		checkArgument(SnomedRefSetUtil.isComplexMapping(type));
		final SnomedMappingRefSet mappingRefSet = SnomedRefSetFactory.eINSTANCE.createSnomedMappingRefSet();
		mappingRefSet.setType(type);
		mappingRefSet.setReferencedComponentType(getTerminologyComponentTypeAsShort(referencedComponentType));
		createIdentifierAndAddRefSet(mappingRefSet, SnomedRefSetUtil.getConceptId(type), fullySpecifiedName);
		return mappingRefSet;
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
	public SnomedRefSetMember createSimpleTypeRefSetMember(final ComponentIdentifierPair<String> referencedComponentPair, 
			final String moduleId, 
			final SnomedRegularRefSet regularRefSet) {
		
		final SnomedRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedRefSetMember();
		initializeRefSetMember(member, referencedComponentPair, moduleId, regularRefSet);
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
	public SnomedSimpleMapRefSetMember createSimpleMapRefSetMember(final ComponentIdentifierPair<String> referencedComponentPair, 
			final String mapTargetComponentId,
			final String moduleId,
			final SnomedMappingRefSet refSet) {
		return createSimpleMapRefSetMember(referencedComponentPair, mapTargetComponentId, null, moduleId, refSet);
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
	private SnomedSimpleMapRefSetMember createSimpleMapRefSetMember(final ComponentIdentifierPair<String> referencedComponentPair, 
			final String mapTargetComponentId,
			@Nullable final String mapTargetDescription,
			final String moduleId,
			final SnomedMappingRefSet refSet) {
		final SnomedSimpleMapRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedSimpleMapRefSetMember();
		initializeRefSetMember(member, referencedComponentPair, moduleId, refSet);
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
	 * @param referencedComponentPair the component identifier - terminology identifier pair for the referenced component
	 * @param mapTargetPair the component identifier - terminology identifier pair for the map target
	 * @param moduleId the module ID for the reference set member
	 * @param refSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 */
	public SnomedComplexMapRefSetMember createComplexMapRefSetMember(final ComponentIdentifierPair<String> referencedComponentPair, 
			final String mapTargetComponentId, 
			final String moduleId,
			final SnomedMappingRefSet mappingRefSet) {
		
		final SnomedComplexMapRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedComplexMapRefSetMember();
		initializeRefSetMember(member, referencedComponentPair, moduleId, mappingRefSet);
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
	 * @param referencedComponentPair the component identifier - terminology identifier pair for the referenced component
	 * @param type the concrete domain of the reference set member
	 * @param value the value of the reference set member
	 * @param label the label of the reference set member
	 * @param moduleId the module ID for the reference set member
	 * @param concreteDataTypeRefSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 */
	public SnomedConcreteDataTypeRefSetMember createConcreteDataTypeRefSetMember(final ComponentIdentifierPair<String> referencedComponentPair, 
			DataType type, 
			final Object value,
			final String characteristicTypeId,
			final String label, 
			final String moduleId,
			final SnomedConcreteDataTypeRefSet concreteDataTypeRefSet) {
		
		final SnomedConcreteDataTypeRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedConcreteDataTypeRefSetMember();
		initializeRefSetMember(member, referencedComponentPair, moduleId, concreteDataTypeRefSet);
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
	 * @param referencedComponentPair the component identifier - terminology identifier pair for the referenced component
	 * @param uomComponentId the unit of measurement component identifier
	 * @param operatorComponentId the comparison operator component identifier
	 * @param value the value of the reference set member
	 * @param attrLabel the label of the concrete domain. Can be {@code null}. If {@code null}, the label is specified by the referenced component.
	 * @param moduleId the module ID for the reference set member
	 * @param concreteDataTypeRefSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 */
	public SnomedConcreteDataTypeRefSetMember createConcreteDataTypeRefSetMember(final ComponentIdentifierPair<String> referencedComponentPair, 
			final String uomComponentId, 
			final String operatorComponentId, 
			final Object value,
			final String characteristicTypeId,
			@Nullable final String attrLabel, 
			final String moduleId,
			final SnomedConcreteDataTypeRefSet concreteDataTypeRefSet) {
		
		final SnomedConcreteDataTypeRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedConcreteDataTypeRefSetMember();
		initializeRefSetMember(member, referencedComponentPair, moduleId, concreteDataTypeRefSet);
		member.setSerializedValue(SnomedRefSetUtil.serializeValue(SnomedRefSetUtil.getByClass(value.getClass()), value));
		member.setUomComponentId(uomComponentId);
		member.setCharacteristicTypeId(characteristicTypeId);
		member.setOperatorComponentId(operatorComponentId);
		member.setLabel(getConcreteDomainLabel(referencedComponentPair, attrLabel));
		
		return member; 
	}

	private String getConcreteDomainLabel(final ComponentIdentifierPair<String> referencedComponentPair, final String attrLabel) {
		
		if (null != attrLabel) {
			return attrLabel;
		}
		
		// No label given up front, extract label from referenced component. Use regular name providers if the referenced component is not a relationship
		if (!SnomedTerminologyComponentConstants.RELATIONSHIP.equals(referencedComponentPair.getTerminologyComponentId())) {
			final IComponentNameProvider nameProvider = getNameProvider(referencedComponentPair);
			return nameProvider.getComponentLabel(BranchPathUtils.createPath(transaction), referencedComponentPair.getComponentId());
		}			
			
		// Look up relationship
		final String relationshipId = referencedComponentPair.getComponentId();
		
		// Try to retrieve from the lightweight store first
		final SnomedRelationship relationshipMini = Iterables.getOnlyElement(SnomedRequests.prepareSearchRelationship()
				.setLimit(1)
				.filterById(relationshipId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync(), null);
		
		if (null == relationshipMini) {
			// Retrieve from CDO if it does not exist in local lightweight store
			Relationship relationship = getSnomedEditingContext().lookup(relationshipId, Relationship.class);
			
			// If not persisted yet, try to get it from the transaction
			if (null == relationship) {
				for (final Relationship newRelationship : ComponentUtils2.getNewObjects(transaction, Relationship.class)) {
					if (newRelationship.getId().equals(relationshipId)) {
						relationship = newRelationship;
						break;
					}
				}
			}
			
			return transactionTextProvider.getText(relationship.getType().getId());
		}
			
		return transactionTextProvider.getText(relationshipMini.getTypeId());
	}
	
	/**
	 * Creates a new SNOMED CT <i>description type</i> reference set member with the specified arguments.
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
	public SnomedDescriptionTypeRefSetMember createDescriptionTypeRefSetMember(final ComponentIdentifierPair<String> referencedComponentPair, 
			final String moduleId, 
			final SnomedRegularRefSet regularRefSet) {
		
		final SnomedDescriptionTypeRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedDescriptionTypeRefSetMember();
		initializeRefSetMember(member, referencedComponentPair, moduleId, regularRefSet);
		return member;
	}
	
	/**
	 * Creates a new SNOMED CT <i>query type</i> reference set member with the specified arguments.
	 * <p>
	 * Note that the member's parent reference set feature will be initialized, but the member itself will <i>not</i> be
	 * added to the reference set's members list.
	 * 
	 * @param referencedComponentPair the component identifier - terminology identifier pair for the referenced component
	 * @param query the query this reference set member represents
	 * @param moduleId the module ID for the reference set member
	 * @param regularRefSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 */
	public SnomedQueryRefSetMember createQueryRefSetMember(final ComponentIdentifierPair<String> referencedComponentPair, 
			@Nullable final String query, 
			final String moduleId,
			final SnomedRegularRefSet regularRefSet) {
		
		final SnomedQueryRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedQueryRefSetMember();
		initializeRefSetMember(member, referencedComponentPair, moduleId, regularRefSet);
		
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
	 * @param referencedComponentPair the component identifier - terminology identifier pair for the referenced component
	 * @param acceptabilityPair the component identifier - terminology identifier pair for the acceptability of this reference set member
	 * @param moduleId the module ID for the reference set member
	 * @param languageRefSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 * @deprecated - use {@link SnomedComponents#newLanguageMember()} instead
	 */
	public SnomedLanguageRefSetMember createLanguageRefSetMember(final ComponentIdentifierPair<String> referencedComponentPair, 
			@Nullable final ComponentIdentifierPair<String> acceptabilityPair, 
			final String moduleId,
			final SnomedStructuralRefSet languageRefSet) {
		
		final SnomedLanguageRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedLanguageRefSetMember();
		initializeRefSetMember(member, referencedComponentPair, moduleId, languageRefSet);
		
		if (null != acceptabilityPair) {
			member.setAcceptabilityId(acceptabilityPair.getComponentId());
		}
		
		return member;
	}

	/**
	 * Creates a new SNOMED CT <i>attribute value type</i> reference set member with the specified arguments.
	 * <p>
	 * Note that the member's parent reference set feature will be initialized, but the member itself will <i>not</i> be
	 * added to the reference set's members list.
	 * 
	 * @param referencedComponentPair the component identifier - terminology identifier pair for the referenced component
	 * @param valueComponentPair the component identifier - terminology identifier pair for the value of this reference set member
	 * @param moduleId the module ID for the reference set member
	 * @param refSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 * @deprecated - use {@link SnomedComponents#newAttributeValueMember()} instead
	 */
	public SnomedAttributeValueRefSetMember createAttributeValueRefSetMember(final ComponentIdentifierPair<String> referencedComponentPair, 
			@Nullable final ComponentIdentifierPair<String> valueComponentPair, 
			final String moduleId,
			final SnomedRefSet refSet) {
		
		final SnomedAttributeValueRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedAttributeValueRefSetMember();
		initializeRefSetMember(member, referencedComponentPair, moduleId, refSet);
		
		if (null != valueComponentPair) {
			member.setValueId(valueComponentPair.getComponentId());
		}
		
		return member;
	}
	
	/**
	 * Creates a new SNOMED CT <i>association type</i> reference set member with the specified arguments.
	 * <p>
	 * Note that the member's parent reference set feature will be initialized, but the member itself will <i>not</i> be
	 * added to the reference set's members list.
	 * 
	 * @param referencedComponentPair the component identifier - terminology identifier pair for the referenced component
	 * @param targetComponentPair the component identifier - terminology identifier pair for the association target of this reference set member
	 * @param moduleId the module ID for the reference set member
	 * @param structuralRefSet the parent reference set
	 * 
	 * @return the populated reference set member instance
	 * @deprecated - use {@link SnomedComponents#newAssociationMember()} instead
	 */
	public SnomedAssociationRefSetMember createAssociationRefSetMember(final ComponentIdentifierPair<String> referencedComponentPair, 
			@Nullable final ComponentIdentifierPair<String> targetComponentPair, 
			final String moduleId,
			final SnomedStructuralRefSet structuralRefSet) {
		
		final SnomedAssociationRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedAssociationRefSetMember();
		initializeRefSetMember(member, referencedComponentPair, moduleId, structuralRefSet);

		if (null != targetComponentPair) {
			member.setTargetComponentId(targetComponentPair.getComponentId());
		}
		
		return member;
	}

	private SnomedRefSetMember initializeRefSetMember(final SnomedRefSetMember member, ComponentIdentifierPair<String> referencedComponentPair, final String moduleId, final SnomedRefSet refSet) {
		
		// Set all common fields for a reference set member. Effective time is left unset, released flag defaults to false
		member.setActive(true);
		member.setModuleId(moduleId);
		member.setUuid(UUID.randomUUID().toString());
		member.setRefSet(refSet);
		member.setReferencedComponentId(referencedComponentPair.getComponentId());
		
		return member;
	}

	/* 
	 * Reference set member builder methods end here
	 */
	 
	/**
	 * Retrieves the reference set which has an identifier concept with the
	 * given ID.
	 * 
	 * @param identifierConceptId
	 *            the unique identifier of the reference set's identifying
	 *            concept (may not be {@code null}, empty, or blank)
	 * 
	 * @return the reference set with the given identifier concept, or
	 *         {@code null} if no such reference set exists
	 *         
	 * @deprecated Use com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService.getComponent(String, CDOView) instead.
	 */
	@Deprecated
	public SnomedRefSet findRefSetByIdentifierConceptId(final String identifierConceptId) {
		
		
		checkArgument(!StringUtils.isEmpty(identifierConceptId), "Identifier SNOMED CT concept ID cannot be null.");

		return new SnomedRefSetLookupService().getComponent(identifierConceptId, transaction);
	}
	
	@Override
	public void preCommit() {
		/*
		 * Updates the module dependency refset members based on the changes. Source or target
		 * effective time is set to null if the changed component module id has dependency in
		 * the refset.
		 */
		SnomedModuleDependencyRefSetService dependencyRefSetService = new SnomedModuleDependencyRefSetService();
		dependencyRefSetService.updateModuleDependenciesDuringPreCommit(getTransaction());
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
				// member is referencing to the investigated component. mark for deletion.
				if (id.equals(member.getReferencedComponentId())) {
					referringMembers.add(member);
				}
				
				final String specialFieldId = getSpecialFieldId(member);
				if (id.equals(specialFieldId)) {
					referringMembers.add(member);
				}
			}
		// persistent component. check for referring members in index
		} else {
			for (SnomedReferenceSetMember member : getAllReferringMembersStorageKey(id, getReferringMemberTypes(component))) {
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
			for (SnomedReferenceSetMember member : getMemberStorageKeys(id)) {
				referringMembers.add((SnomedRefSetMember) getSnomedEditingContext().lookupIfExists(member.getStorageKey()));
			}
		}
		
		return referringMembers;
	}
	
	private SnomedReferenceSetMembers getMemberStorageKeys(String id) {
		return SnomedRequests.prepareSearchMember()
				.all()
				.filterByRefSet(id)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
	}

	private SnomedReferenceSetMembers getAllReferringMembersStorageKey(String id, EnumSet<SnomedRefSetType> types) {
		// construct bulk requests with many sub queries to search for any member that references the given ID in any RF2 member component field
		return SnomedRequests.prepareBulkRead()
				.setBody(BulkRequest.<BranchContext>create()
						.add(getReferringMembers(id, types))
						.add(getReferringMembersByProps(id, types, Fields.ACCEPTABILITY_ID))
						.add(getReferringMembersByProps(id, types, Fields.CHARACTERISTIC_TYPE_ID))
						.add(getReferringMembersByProps(id, types, Fields.CORRELATION_ID))
						.add(getReferringMembersByProps(id, types, Fields.DESCRIPTION_FORMAT))
						.add(getReferringMembersByProps(id, types, Fields.MAP_CATEGORY_ID))
						.add(getReferringMembersByProps(id, types, Fields.OPERATOR_ID))
						.add(getReferringMembersByProps(id, types, Fields.TARGET_COMPONENT))
						.add(getReferringMembersByProps(id, types, Fields.UNIT_ID))
						.add(getReferringMembersByProps(id, types, Fields.VALUE_ID))
						)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<BulkResponse, SnomedReferenceSetMembers>() {
					@Override
					public SnomedReferenceSetMembers apply(BulkResponse input) {
						final List<SnomedReferenceSetMember> items = ImmutableList.copyOf(Iterables.concat(input.getResponses(SnomedReferenceSetMembers.class)));
						return new SnomedReferenceSetMembers(items, 0, items.size(), items.size());
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
		return SnomedCDORootResourceNameProvider.REFSET_ROOT_RESOURCE_NAME;
	}
	
	/*returns with the 'special field' ID of a reference set member:
	 * - value ID in case of attribute value member
	 * - acceptability ID in case of language member
	 * - map target ID in case of mapping member if the map target is a SNOMED CT component, otherwise null.
	 * - null in case of query member
	 * - null in case of CDT reference set member
	 * - null if simple type member
	 * - description format in case of description type member
	 * - type ID in case of association reference set member*/
	@Nullable private String getSpecialFieldId(final SnomedRefSetMember member) {
		
		if (member instanceof SnomedAttributeValueRefSetMember) {
			
			return ((SnomedAttributeValueRefSetMember) member).getValueId();
			
		} else if (member instanceof SnomedLanguageRefSetMember) {
			
			return ((SnomedLanguageRefSetMember) member).getAcceptabilityId();
			
		} else if (member instanceof SnomedSimpleMapRefSetMember) { //includes complex map as well
			
			final short type = ((SnomedSimpleMapRefSetMember) member).getMapTargetComponentType();
			
			switch (type) {
				
				case SnomedTerminologyComponentConstants.CONCEPT_NUMBER: //$FALL-THROUGH$
				case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER: //$FALL-THROUGH$
				case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER: //$FALL-THROUGH$
					return ((SnomedSimpleMapRefSetMember) member).getMapTargetComponentId();
					
				default:
					return null;
				
			}
			
			
		} else if (member instanceof SnomedQueryRefSetMember) {
			
			return null; //query is not an ID
			
		} else if (member instanceof SnomedConcreteDataTypeRefSetMember) {
			
			return null; //does not have 'special field ID'
			
		} else if (member instanceof SnomedAssociationRefSetMember) {
			
			return ((SnomedAssociationRefSetMember) member).getTargetComponentId();
			
		} else if (member instanceof SnomedDescriptionTypeRefSetMember) {
			
			return ((SnomedDescriptionTypeRefSetMember) member).getDescriptionFormat(); //XXX would be nice to have #getDescriptionFormatId
			
		} else { //simple type member
			
			return null;
			
		}
		
	} 

	private void createIdentifierAndAddRefSet(final SnomedRefSet snomedRefSet, final String parentConceptId, final String name) {
		createIdentifierAndAddRefSet(snomedRefSet, getSnomedEditingContext().generateComponentId(ComponentCategory.CONCEPT), parentConceptId, name);
	}
	
	// create identifier concept with the given arguments, save it locally
	private void createIdentifierAndAddRefSet(final SnomedRefSet snomedRefSet, final String conceptId, final String parentConceptId, final String name) {
		final Concept identifier = createIdentifierConcept(conceptId, parentConceptId, name);
		snomedRefSet.setIdentifierId(identifier.getId());
		add(snomedRefSet);
	}

	/**
	 * non-API - will be refactored later
	 * @param parentConceptId
	 * @param name
	 * @return
	 */
	public Concept createIdentifierConcept(final String conceptId, final String parentConceptId, final String name) {
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
		final SnomedStructuralRefSet languageRefSet = getLanguageRefSet();
		for (final Description description : identifier.getDescriptions()) {
			if (description.isActive()) { //this point all description should be active
				final ComponentIdentifierPair<String> acceptabilityPair = createConceptTypePair(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED);
				//create language reference set membership
				final ComponentIdentifierPair<String> referencedComponentPair = SnomedRefSetEditingContext.createDescriptionTypePair(description.getId());
				final SnomedLanguageRefSetMember member = createLanguageRefSetMember(referencedComponentPair, acceptabilityPair, context.getDefaultModuleConcept().getId(), languageRefSet);
				description.getLanguageRefSetMembers().add(member);
			}
		}
		return identifier;
	}
	
	/*returns with the currently used language type reference set*/
	private SnomedStructuralRefSet getLanguageRefSet() {
		return snomedEditingContext.getLanguageRefSet();
	}

	private short getTerminologyComponentTypeAsShort(final String terminologyComponentId) {
		return CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsShort(terminologyComponentId);
	}

	private IComponentNameProvider getNameProvider(final ComponentIdentifierPair<String> referencedComponentPair) {
		return CoreTerminologyBroker.getInstance().getNameProviderFactory(referencedComponentPair.getTerminologyComponentId()).getNameProvider();
	}
	
}