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
package com.b2international.snowowl.snomed.datastore;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.spi.cdo.FSMUtil;

import bak.pcj.LongIterator;
import bak.pcj.set.LongSet;

import com.b2international.commons.Pair;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentNameProvider;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.services.IClientSnomedComponentService;
import com.b2international.snowowl.snomed.datastore.services.SnomedConceptNameProvider;
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
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
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
	
	private static final Map<String, String> CMT_NAME_ID_PAIRS = ImmutableMap.<String, String>builder()
			.put("Cardiology", Concepts.CARDIOLOGY_REFERENCE_SET)
			.put("Endocrinology Urology Nephrology", Concepts.ENDOCRINOLOGY_UROLOGY_NEPHROLOGY_REFERENCE_SET)
			.put("Hematology Oncology", Concepts.HEMATOLOGY_ONCOLOGY_REFERENCE_SET)
			.put("Mental Health", Concepts.MENTAL_HEALTH_REFERENCE_SET)
			.put("Musculoskeletal", Concepts.MUSCULOSKELETAL_REFERENCE_SET)
			.put("Neurology", Concepts.NEUROLOGY_REFERENCE_SET)
			.put("Ophthalmology", Concepts.OPHTHALMOLOGY_REFERENCE_SET)
			.put("ENT Gastrointestinal Infectious Diseases", Concepts.ENT_GASTROINTESTINAL_INFECTIOUS_DISEASES_REFERENCE_SET)
			.put("Hx of and FHx of", Concepts.HX_OF_AND_FHX_OF_REFERENCE_SET)
			.put("Injuries [Part 1]", Concepts.INJURIES_PART_1_REFERENCE_SET)
			.put("Obstetrics and Gynecology", Concepts.OBSTETRICS_AND_GYNECOLOGY_REFERENCE_SET)
			.put("Orthopedics Extremity Fractures", Concepts.ORTHOPEDICS_EXTREMITY_FRACTURES_REFERENCE_SET)
			.put("Orthopedics Non-Extremity Fractures", Concepts.ORTHOPEDICS_NON_EXTREMITY_FRACTURES_REFERENCE_SET)
			.put("Primary Care", Concepts.PRIMARY_CARE_REFERENCE_SET)
			.put("Skin Respiratory", Concepts.SKIN_RESPIRATORY_REFERENCE_SET)
			.put("KP Problem List", Concepts.KP_PROBLEM_LIST_REFERENCE_SET)
			.build();
	
	protected final SnomedEditingContext snomedEditingContext;

	/**
	 * Creates a reference set on the specified editing context. After creating the object graph changes, the change set will be committed to the returning file.
	 * The returning pair consists of the identifier concept ID of the brand new concept and the file containing the changes.
	 * <p>Clients should take care of disposing the specified context.
	 * 
	 * <p> The {@link File} in the returned pair, is scheduled for removal on VM shutdown, thus clients may want to persist it to avoid information loss.
	 * 
	 * @param context the editing context. Should be clean.
	 * @param parentIdentifierConceptId concept ID the reference set identifier concept's parent. 
	 * @param label the preferred term for the reference set.
	 * @param referencedComponentType the referenced component type.
	 * @param mapTargetType the map target component type. Ignored in case of *NON* mapping {@link SnomedRefSetType reference set type}s.
	 * @param type the reference set type.
	 * @return a file containing the object graph changes as a commit.
	 * @throws CommitException if the commit failed.
	 */
	public static Pair<String, File> createReferenceSet(final SnomedRefSetEditingContext context, final String parentIdentifierConceptId, final String label, 
			final String referencedComponentType, final short mapTargetType, final SnomedRefSetType type) throws CommitException {
		
		Preconditions.checkNotNull(context, "Reference set editing context argument cannot be null.");
		Preconditions.checkArgument(!context.transaction.isDirty(), "Editing context for SNOMED CT reference sets cannot be dirty.");
		Preconditions.checkNotNull(parentIdentifierConceptId, "Parent identifier concept ID argument cannot be null.");
		Preconditions.checkNotNull(type, "SNOMED CT reference set type argument cannot be null.");
		Preconditions.checkNotNull(label, "Label argument cannot be null.");
		Preconditions.checkNotNull(referencedComponentType, "Referenced component type argument cannot be null.");
		
		final Concept parentConcept = new SnomedConceptLookupService().getComponent(parentIdentifierConceptId, context.transaction);
		Preconditions.checkNotNull(parentConcept, "Concept cannot be found in store. ID: " + parentIdentifierConceptId + ". [" + BranchPathUtils.createPath(context.transaction) + "]");
		
		String identifierConceptId = null;
		
		switch (type) {
			
			case SIMPLE:
				final SnomedRegularRefSet refSet = context.createSnomedSimpleTypeRefSet(label, referencedComponentType, parentConcept);
				identifierConceptId = refSet.getIdentifierId();
				break;
			case ATTRIBUTE_VALUE:
				final SnomedRegularRefSet attributeRefSet = context.createSnomedAttributeRefSet(label, referencedComponentType);
				identifierConceptId = attributeRefSet.getIdentifierId();
				break;
			case SIMPLE_MAP:
				final SnomedMappingRefSet simpleMap = context.createSnomedSimpleMapRefSet(label, referencedComponentType, parentIdentifierConceptId);
				simpleMap.setMapTargetComponentType(mapTargetType);
				identifierConceptId = simpleMap.getIdentifierId();
				break;
			case EXTENDED_MAP: //$FALL-THROUGH$
			case COMPLEX_MAP:
				final SnomedMappingRefSet complexMap = context.createSnomedComplexMapRefSet(label, referencedComponentType, type);
				identifierConceptId = complexMap.getIdentifierId();
				complexMap.setMapTargetComponentType(mapTargetType);
				break;
			case ASSOCIATION: //$FALL-THROUGH$
			case CONCRETE_DATA_TYPE: //$FALL-THROUGH$
			case DESCRIPTION_TYPE: //$FALL-THROUGH$
			case QUERY: //$FALL-THROUGH$ 
			case LANGUAGE: //$FALL-THROUGH$
				throw new UnsupportedOperationException("Creating " + type + " reference set is currently not supported.");
				
			default:
				throw new IllegalArgumentException("Unknown SNOMED CT reference set type: " + type);
			
		} 

		return new Pair<String, File>(identifierConceptId, commitToFile(context));
	}
	
	/**
	 * Creates a new SNOMED CT reference set editing context on the currently active branch of the SNOMED CT repository.
	 * 
	 * @see BranchPathUtils#createActivePath(EPackage)
	 * @see SnomedRefSetEditingContext#getPackage()
	 */
	public static SnomedRefSetEditingContext createInstance() {
		return new SnomedEditingContext().getRefSetEditingContext();
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
	}

	public SnomedEditingContext getSnomedEditingContext() {
		return snomedEditingContext;
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
		final SnomedRegularRefSet snomedRefSet = createSnomedRegularRefSet(getTerminologyComponentTypeAsShort(referencedComponentType), SnomedRefSetType.SIMPLE);
		createIdentifierAndAddRefSet(snomedRefSet, Concepts.REFSET_SIMPLE_TYPE, fullySpecifiedName);
		return snomedRefSet;
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
	public SnomedRegularRefSet createSnomedSimpleTypeRefSet(final String fullySpecifiedName, final String referencedComponentType, final Concept parentConcept) {
		final SnomedRegularRefSet snomedRefSet = createSnomedRegularRefSet(getTerminologyComponentTypeAsShort(referencedComponentType), SnomedRefSetType.SIMPLE);
		createIdentifierWithParentAndAddRefSet(snomedRefSet, Concepts.REFSET_SIMPLE_TYPE, fullySpecifiedName, parentConcept);
		return snomedRefSet;
	}
	
	/**
	 * 
	 * @param label
	 * @param terminologyComponentId - referenced component type (e.g. CONCEPT for example)
	 * @param namespace
	 * @param module
	 * @param parent
	 * @return
	 */
	public SnomedRegularRefSet createSnomedSimpleTypeRefSet(final String label, final short terminologyComponentId, final String namespace, final Concept module, final Concept parent) {
		final SnomedRegularRefSet refSet = createSnomedRegularRefSet(terminologyComponentId, SnomedRefSetType.SIMPLE);
		final Concept concept = getSnomedEditingContext().buildDefaultConcept(label, namespace, module, parent);
		updateIdIfCMTConcept(label, concept);
		refSet.setIdentifierId(concept.getId());
		add(refSet);
		return refSet;
	}
	
	/**
	 * Creates a SNOMED CT concrete domain type reference set.
	 * @param fullySpecifiedName the fully specified name reference set identifier concept.
	 * @return the new SNOMED CT simple map type reference set.
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

	/*
	 * Reference set member builder methods start here
	 * TODO: move to separate class, this one is getting too big
	 */
	
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
			@Nullable final ComponentIdentifierPair<String> mapTargetPair, 
			final String moduleId,
			final SnomedMappingRefSet refSet) {
		return createSimpleMapRefSetMember(referencedComponentPair, mapTargetPair, null, moduleId, refSet);
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
	public SnomedSimpleMapRefSetMember createSimpleMapRefSetMember(final ComponentIdentifierPair<String> referencedComponentPair, 
			@Nullable final ComponentIdentifierPair<String> mapTargetPair,
			@Nullable final String mapTargetDescription,
			final String moduleId,
			final SnomedMappingRefSet refSet) {
		final SnomedSimpleMapRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedSimpleMapRefSetMember();
		initializeRefSetMember(member, referencedComponentPair, moduleId, refSet);
		if (mapTargetDescription != null) {
			member.setMapTargetComponentDescription(mapTargetDescription);
		}
		initializeMapTarget(member, mapTargetPair);
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
			@Nullable final ComponentIdentifierPair<String> mapTargetPair, 
			final String moduleId,
			final SnomedMappingRefSet mappingRefSet) {
		
		final SnomedComplexMapRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedComplexMapRefSetMember();
		initializeRefSetMember(member, referencedComponentPair, moduleId, mappingRefSet);
		initializeMapTarget(member, mapTargetPair);
		member.setCorrelationId(Concepts.REFSET_CORRELATION_NOT_SPECIFIED);
		return member;
	}

	private void initializeMapTarget(final SnomedSimpleMapRefSetMember member, final ComponentIdentifierPair<String> mapTargetPair) {
		
		if (null != mapTargetPair) {
			member.setMapTargetComponentId(mapTargetPair.getComponentId());
		}
	}
	
	/**
	 * Creates a new SNOMED CT <i>concrete domain</i> reference set member with the specified arguments.
	 * <p>
	 * Note that the member's parent reference set feature will be initialized, but the member itself will <i>not</i> be
	 * added to the reference set's members list.
	 * 
	 * @param referencedComponentPair the component identifier - terminology identifier pair for the referenced component
	 * @param type the concrete data type of the reference set member
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
	 * @param attrLabel the label of the concrete data type. Can be {@code null}. If {@code null}, the label is specified by the referenced component.
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
			return nameProvider.getText(referencedComponentPair.getComponentId());
		}			
			
		// Look up relationship
		final String relationshipId = referencedComponentPair.getComponentId();
		
		// Try to retrieve from the lightweight store first
		final SnomedRelationshipIndexEntry relationshipMini = ApplicationContext.getInstance().getService(SnomedClientStatementBrowser.class).getStatement(relationshipId);
		
		if (null == relationshipMini) {
			
			// Retrieve from CDO if it does not exist in local lightweight store
			final ILookupService<String, Relationship, CDOView> lookupService = CoreTerminologyBroker.getInstance().getLookupService(SnomedTerminologyComponentConstants.RELATIONSHIP);
			Relationship relationship = lookupService.getComponent(relationshipId, transaction);
			
			// If not persisted yet, try to get it from the transaction
			if (null == relationship) {
				for (final Relationship newRelationship : ComponentUtils2.getNewObjects(transaction, Relationship.class)) {
					if (newRelationship.getId().equals(relationshipId)) {
						relationship = newRelationship;
						break;
					}
				}
			}
			
			return SnomedConceptNameProvider.INSTANCE.getText(relationship.getType().getId(), transaction);
		}
			
		return SnomedConceptNameProvider.INSTANCE.getText(relationshipMini.getAttributeId(), transaction);
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
		
		
		Preconditions.checkArgument(!StringUtils.isEmpty(identifierConceptId), "Identifier SNOMED CT concept ID cannot be null.");

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

	protected SnomedDeletionPlan deleteRefSet(final SnomedRefSet refSet, SnomedDeletionPlan deletionPlan) {
		if (deletionPlan == null) {
			deletionPlan = new SnomedDeletionPlan();
		}
		deletionPlan.markForDeletion(refSet);
		return deletionPlan;
	}

	protected List<SnomedRefSetMember> getReferringMembers(final Component component, final SnomedRefSetType type, final SnomedRefSetType... others) {
		
		Preconditions.checkNotNull(component, "Component argument cannot be null.");
		
		//already detached. nothing to do
		if (FSMUtil.isTransient(component)) {
			return Collections.emptyList();
		}
		
		final List<SnomedRefSetMember> $ = Lists.newArrayList();

		final String id = Preconditions.checkNotNull(component.getId(), "Component ID was null for component. [" + component + "]");
		
		//process new referring members from the transaction.
		if (CDOState.NEW.equals(component.cdoState())) {
			
			
			final Iterable<SnomedRefSetMember> newMembers = ComponentUtils2.getNewObjects(transaction, SnomedRefSetMember.class);
			
			for (final SnomedRefSetMember member : newMembers) {
				
				//member is referencing to the investigated component. mark for deletion.
				if (id.equals(member.getReferencedComponentId())) {
					
					$.add(member);
					
				}
				
				final String specialFieldId = getSpecialFieldId(member);
				
				if (id.equals(specialFieldId)) {
					
					$.add(member);
					
				}
				
			}
			
		//persistent component. check for referring members in index
		} else {
			
			final IClientSnomedComponentService componentService = ApplicationContext.getInstance().getService(IClientSnomedComponentService.class);
			
			final int[] ordinals = new int[others.length];
			for (int i = 0; i < others.length; i++) {
				ordinals[i] = others[i].ordinal();
			}
			
			final LongSet ids = componentService.getAllReferringMembersStorageKey(id, type.ordinal(), ordinals);
			
			for (final LongIterator itr = ids.iterator(); itr.hasNext(); /* */) {
				
				final CDOObject object = CDOUtils.getObjectIfExists(transaction, itr.next());
				
				if (object instanceof SnomedRefSetMember) {
					
					$.add((SnomedRefSetMember) object);
					
				}
				
			}
			
		}
		
		
		return $;
		
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

	// create identifier concept with the given arguments, save it locally
	private void createIdentifierAndAddRefSet(final SnomedRefSet snomedRefSet, final String typeId, final String name) {
		final Concept identifierParent = new SnomedConceptLookupService().getComponent(typeId, transaction);
		final Concept identifier = snomedEditingContext.buildDefaultConcept(name, identifierParent);
		identifier.getDescriptions().add(snomedEditingContext.buildDefaultDescription(name, new SnomedConceptLookupService().getComponent(Concepts.SYNONYM, transaction)));
		
		//create language reference set members for the descriptions.
		final SnomedStructuralRefSet languageRefSet = getLanguageRefSet();
		for (final Description description : identifier.getDescriptions()) {
			if (description.isActive()) { //this point all description should be active
				final String descriptionTypeId = description.getType().getId();
				final ComponentIdentifierPair<String> acceptabilityPair;
				if (Concepts.FULLY_SPECIFIED_NAME.equals(descriptionTypeId)) { //FSN should be associated with preferred acceptability 
					acceptabilityPair = createConceptTypePair(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED);
				} else {
					acceptabilityPair = createConceptTypePair(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE); //all the others got acceptable acceptability
				}
				
				//create language reference set membership
				final ComponentIdentifierPair<String> referencedComponentPair = SnomedRefSetEditingContext.createDescriptionTypePair(description.getId());
				final SnomedLanguageRefSetMember member = createLanguageRefSetMember(referencedComponentPair, acceptabilityPair, getSnomedEditingContext().getDefaultModuleConcept().getId(), languageRefSet);
				description.getLanguageRefSetMembers().add(member);
			}
		}
		
		snomedRefSet.setIdentifierId(identifier.getId());
		add(snomedRefSet);
	}
	
	// create identifier concept with the given arguments and parent concept, save it locally
	private void createIdentifierWithParentAndAddRefSet(SnomedRefSet snomedRefSet, String refsetSimpleType, String fullySpecifiedName, Concept parentConcept) {
		final Concept identifier = snomedEditingContext.buildDefaultConcept(fullySpecifiedName, parentConcept);
		identifier.getDescriptions().add(snomedEditingContext.buildDefaultDescription(fullySpecifiedName, new SnomedConceptLookupService().getComponent(Concepts.SYNONYM, transaction)));
		snomedRefSet.setIdentifierId(identifier.getId());
		add(snomedRefSet);
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
	
	// update the concept Id to default constant id if the concept is CMT concept
	private void updateIdIfCMTConcept(String label, Concept concept) {
		String conceptId = CMT_NAME_ID_PAIRS.get(label.replaceAll(" reference set", ""));
		if (null != conceptId) {
			concept.setId(conceptId);
		}
	}
}