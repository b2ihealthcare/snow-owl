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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.getAttribute;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.getObjectIfExists;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ENTIRE_TERM_CASE_INSENSITIVE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ENTIRE_TERM_CASE_SENSITIVE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.EXISTENTIAL_RESTRICTION_MODIFIER;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.INFERRED_RELATIONSHIP;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.IS_A;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.PRIMITIVE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.QUALIFIER_VALUE_TOPLEVEL_CONCEPT;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.QUALIFYING_RELATIONSHIP;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_COMPLEX_MAP_TYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_DESCRIPTION_TYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_SIMPLE_TYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.STATED_RELATIONSHIP;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.SYNONYM;
import static com.b2international.snowowl.snomed.datastore.SnomedDeletionPlanMessages.COMPONENT_IS_RELEASED_MESSAGE;
import static com.b2international.snowowl.snomed.datastore.SnomedDeletionPlanMessages.UNABLE_TO_DELETE_CONCEPT_MESSAGE;
import static com.b2international.snowowl.snomed.datastore.SnomedDeletionPlanMessages.UNABLE_TO_DELETE_ONLY_FSN_DESCRIPTION_MESSAGE;
import static com.b2international.snowowl.snomed.datastore.SnomedDeletionPlanMessages.UNABLE_TO_DELETE_REFERENCE_SET_MESSAGE;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;
import static org.eclipse.emf.cdo.common.id.CDOID.NULL;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.Pair;
import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Annotatable;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Concepts;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.preference.ModulePreference;
import com.b2international.snowowl.snomed.core.store.SnomedComponentBuilder;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.NormalFormWrapper.AttributeConceptGroupWrapper;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.b2international.snowowl.snomed.datastore.services.ISnomedRelationshipNameProvider;
import com.b2international.snowowl.snomed.datastore.services.SnomedModuleDependencyRefSetService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * SNOMED CT RF2 specific editing context subclass of {@link CDOEditingContext}
 * providing retrieval, builder and other utility methods.
 * 
 */
public class SnomedEditingContext extends BaseSnomedEditingContext {

	private SnomedRefSetEditingContext refSetEditingContext;
	private Concept moduleConcept;
	private String nameSpace;
	private boolean uniquenessCheckEnabled = true;
	private Set<String> newComponentIds = Collections.synchronizedSet(Sets.<String>newHashSet());
	private SnomedDeletionPlan deletionPlan;

	/**
	 * Returns with a pair, identifying a preferred term associated with the specified SNOMED&nbsp;CT concept.
	 * <br>This method may return with {@code null}.
	 * <ul>
	 * 	<li>First value of the pair is the SNOMED CT description ID.</li>
	 * 	<li>Second value of the pair is a pair for identifying the language type reference set member:</li>
	 * 		<ul>
	 * 			<li>Language reference set member UUID.</li>
	 * 			<li>Language reference set member CDO ID.</li>
	 * 		</ul>
	 * </ul>
	 * <p>
	 * <b>NOTE:&nbsp;</b>This method should not not be called with dirty CDO view.
	 * @param concept SNOMED&nbsp;CT concept
	 * @param languageRefSetId 
	 * @return a pair identifying the preferred term member.
	 * @deprecated - revise API
	 */
	public static Pair<String, IdStorageKeyPair> getPreferredTermMemberFromIndex(final Concept concept, String languageRefSetId) {
		throw new UnsupportedOperationException("Unsupported API");
//		checkNotNull(concept, "SNOMED CT concept argument cannot be null.");
//		checkNotNull(concept.cdoView(), "Underlying CDO view for the SNOMED CT concept argument was null.");
//		Preconditions.checkArgument(!concept.cdoView().isClosed(), "Underlying CDO view for the SNOMED CT concept argument was closed.");
//
//		final Collection<SnomedRefSetMemberIndexEntry> preferredTermMembers = 
//				new SnomedRefSetMembershipLookupService().getPreferredTermMembers(concept, languageRefSetId);
//		final SnomedRefSetMemberIndexEntry preferredMember = Iterables.getFirst(preferredTermMembers, null);
//		
//		if (null == preferredMember) {
//			return null;
//		}
//
//		//throw new exception since it should not happen at all
//		if (preferredTermMembers.size() > 1) {
//			final String message = "Multiple preferred terms are associated with a SNOMED CT concept: " + concept.getId() + " " + 
//					concept.getFullySpecifiedName() + "\nMembers: " + Arrays.toString(preferredTermMembers.toArray());
//			LOGGER.error(message);
//			//XXX akitta: do not throw exception for now, as the application cannot be tested with the dataset with duplicate preferred term members
////			throw new IllegalArgumentException(message);
//		}
//		
//		return new Pair<String, IdStorageKeyPair>(preferredMember.getReferencedComponentId(), new IdStorageKeyPair(preferredMember.getId(), preferredMember.getStorageKey()));
	}

	public Concept buildDraftConceptFromNormalForm(final NormalFormWrapper normalForm) {
		return buildDraftConceptFromNormalForm(normalForm, null);
	}
	
	/**
	 * Build a new SNOMED&nbsp;CT concept based on the specified SCG normal form representation.
	 * @param editingContext the editing concept with an underlying audit CDO view for SNOMED&nbsp;CT concept creation. 
	 * @param normalForm the SCG normal for representation.
	 * @param conceptId the unique ID of the concept. Can be {@code null}. If {@code null}, then the ID will be generated via the specified editing context.
	 * @return the new concept.
	 */
	public Concept buildDraftConceptFromNormalForm(final NormalFormWrapper normalForm, @Nullable final String conceptId) {
		final Concept moduleConcept = getDefaultModuleConcept();
		final List<Concept> parentConcepts = getConcepts(normalForm.getParentConceptIds());
		final Concept[] additionalParentConcepts = CompareUtils.isEmpty(parentConcepts) ? new Concept[0] : Iterables.toArray(parentConcepts.subList(1, parentConcepts.size()), Concept.class);
		final Concept newConcept = buildDefaultConcept("", getNamespace(), moduleConcept, parentConcepts.get(0), additionalParentConcepts);
		if (null != conceptId) {
			newConcept.setId(conceptId);
		}
		
		for (final AttributeConceptGroupWrapper extractedGroup : normalForm.getAttributeConceptGroups()) {
			final Map<String, String> attributeConceptIdMap = extractedGroup.getAttributeConceptIds();
			final int groupId = extractedGroup.getGroup();
			for (final Entry<String, String> entry : attributeConceptIdMap.entrySet()) {
				final Concept type = lookup(entry.getKey(), Concept.class);
				final Concept destination = lookup(entry.getValue(), Concept.class);
				final Concept characteristicType = getQualifyingCharacteristicType(destination.getId());
				
				buildDefaultRelationship(newConcept, type, destination, characteristicType).setGroup(groupId);
			}
		}
		
		newConcept.eAdapters().add(new ConceptParentAdapter(Iterables.transform(parentConcepts, new Function<CDOObject, String>() {
			@Override public String apply(final CDOObject object) {
				return CDOUtils.getAttribute(object, SnomedPackage.eINSTANCE.getComponent_Id(), String.class);
			}
		})));
		
		return newConcept;
	}
	
	/**
	 * @param concreteDomainRefSetMembers
	 * @param targetSnomedComponent 
	 */
	private void copyInferredConcreteDomainMembers(Annotatable sourceSnomedComponent, Annotatable targetSnomedComponent) {
		
		EList<SnomedConcreteDataTypeRefSetMember> concreteDomainRefSetMembers = sourceSnomedComponent.getConcreteDomainRefSetMembers();
		for (SnomedConcreteDataTypeRefSetMember sourceConcreteDataType : concreteDomainRefSetMembers) {
			
			//skip inactive
			if (!sourceConcreteDataType.isActive()) {
				continue;
			}
			
			//skip non-inferred
			if (!sourceConcreteDataType.getCharacteristicTypeId().equals(SnomedConstants.Concepts.INFERRED_RELATIONSHIP)) {
				continue;
			}
				
			SnomedConcreteDataTypeRefSetMember newConcreteDatatype = SnomedRefSetFactory.eINSTANCE.createSnomedConcreteDataTypeRefSetMember();
			newConcreteDatatype.setUuid(UUID.randomUUID().toString());
			newConcreteDatatype.setActive(true);
			newConcreteDatatype.setCharacteristicTypeId(SnomedConstants.Concepts.STATED_RELATIONSHIP);
			newConcreteDatatype.setLabel(sourceConcreteDataType.getLabel());
			newConcreteDatatype.setModuleId(getDefaultModuleConcept().getId());
			newConcreteDatatype.setOperatorComponentId(sourceConcreteDataType.getOperatorComponentId());
			newConcreteDatatype.setReferencedComponentId(sourceConcreteDataType.getReferencedComponentId());
			newConcreteDatatype.setRefSet(sourceConcreteDataType.getRefSet());
			newConcreteDatatype.setReleased(false);
			newConcreteDatatype.setSerializedValue(sourceConcreteDataType.getSerializedValue());
			newConcreteDatatype.setUomComponentId(sourceConcreteDataType.getUomComponentId());
			
			targetSnomedComponent.getConcreteDomainRefSetMembers().add(newConcreteDatatype);
		}
		
	}

	/**
	 * @param concept passed in {@link Concept} instance.
	 * @return the <b>first</b> parent {@link Concept} of the passed in concept.
	 * @deprecated - unused, will be removed in 4.4
	 */
	public static Concept getFirstParentConcept(Concept concept) {
		Relationship firstOutgoingParentRelationship = getFirstOutgoingParentRelationship(concept);
		if (firstOutgoingParentRelationship != null)
			return firstOutgoingParentRelationship.getDestination();
		throw new IllegalArgumentException("Concept '" + concept.getId() + "' has no parent concept.");
	}

	/**
	 * This method returns all the direct parent (IS_A) relationships of the passed concept.
	 *	
	 * @return list of the parent concept, <b>empty</b> list otherwise
	 * @deprecated - unused, will be removed in 4.4
	 */
	public static Set<Concept> getDirectParentConcepts(Concept concept) {
		Set<Concept> parentConcepts = new HashSet<Concept>();
		
		EList<Relationship> outboundRelationships = concept.getOutboundRelationships();
		
		for (Relationship relationship : outboundRelationships) {
			if (IS_A.equals(relationship.getType().getId())) {
				parentConcepts.add(relationship.getDestination());
			}
		}
		
		return parentConcepts;
	}

	/**
	 * Sets the parent concept for the passed in {@link Concept} instance then returns with it.
	 * 
	 * @param concept the passed in 'child' concept. 
	 * @param parentConcept the parent concept to be set.
	 * @return the passed in 'child' concept with a new parent concept.
	 * @warning <br/><b>This method should be used only for </b>{@link Component}<b> instances generated by </b> {@link SnomedComponentBuilder}<b>. 
	 * In other cases invoking this method is discouraged.</b>
	 * @deprecated - unused, will be removed in 4.4
	 */
	public static Concept setParentConcept(Concept concept, Concept parentConcept) {
		Relationship firstOutgoingParentRelationship = getFirstOutgoingParentRelationship(concept);
		if (firstOutgoingParentRelationship != null) {
			firstOutgoingParentRelationship.setDestination(parentConcept);
		} else {
			throw new IllegalStateException("No existing parent relationship when trying to set new parent concept on concept '" + concept.getId() + "'.");
		}
		return concept;
	}

	/**
	 * @param concept the passed {@link Concept} instance.
	 * @return the first active fully specified name of the passed in concept as a {@link Description} instance. 
	 * <i>Fallback</i> if there is no active try to get the first inactive. If there no fully specified name at all 
	 * (which is invalid but can happen with inactive concepts) <b>throws</b> {@link IllegalArgumentException}.
	 * 
	 * @throws IllegalArgumentException if there is no fully specified name at all.
	 * @deprecated - unused, will be removed in 4.4
	 */
	public static Description getFirstFullySpecifedNameDescription(Concept concept) {
		for (Description desc : concept.getDescriptions()) {
			if (desc.getType() != null && desc.isActive() && desc.getType().getId().equals(FULLY_SPECIFIED_NAME))
				return desc;
		}
		
		//	fallback, try to get an inactive fullyspec, take the first
		for (Description desc : concept.getDescriptions()) {
			if (desc.getType() != null && desc.getType().getId().equals(FULLY_SPECIFIED_NAME))
				return desc;
		}
		
		throw new IllegalArgumentException("Concept '" + concept.getId() + "' doesn't have fully specified name.");
	}

	/*returns with a bunch of SNOMED CT concepts opened in the specified CDO view the given unique concept IDs*/
	private List<Concept> getConcepts(final Iterable<String> conceptIds) {
		return Lists.newArrayList(Iterables.transform(conceptIds, new Function<String, Concept>() {
			@Override public Concept apply(final String id) {
				return lookup(id, Concept.class);
			}
		}));
	}
	
	/**
	 * If the destination concept toplevel is 'Qualifying value', qualifying relationship should be used.
	 * Can return {@code null}.
	 * @param editingContext editing context for the concept creation. 
	 * @param destinationConceptid the unique ID of the destination concept.
	 * @return SnomedConstants.QUALIFYING_RELATIONSHIP if destination toplevel is 'Qualifying value', {@code null} otherwise.
	 */
	private Concept getQualifyingCharacteristicType(final String destinationConceptid) {
		final boolean isSubTypeOfQualifierValue = SnomedRequests.prepareSearchConcept()
				.setLimit(0)
				.filterByAncestor(QUALIFIER_VALUE_TOPLEVEL_CONCEPT)
				.filterById(destinationConceptid)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync().getTotal() > 0;
		if (isSubTypeOfQualifierValue) {
			return findConceptById(QUALIFYING_RELATIONSHIP);
		}
		return null;
	}
	
	/**
	 * @deprecated - unused (transitively), will be removed in 4.4
	 */
	private static Relationship getFirstOutgoingParentRelationship(Concept concept) {
		for (Relationship outgoingRelationship : concept.getOutboundRelationships()) {
			if (IS_A.equals(outgoingRelationship.getType().getId())) {
				return outgoingRelationship;
			}
		}
		return null;
	}

	/**
	 * Creates a new SNOMED CT core components editing context on the specified branch of the SNOMED CT repository.
	 * 
	 * @param branchPath the branch path to use
	 */
	public SnomedEditingContext(IBranchPath branchPath) {
		this(branchPath, getDefaultNamespace());
	}
	
	/**
	 * Creates a new SNOMED CT core components editing context on the specified
	 * branch of the SNOMED CT repository with the specified namespace to use
	 * when generating new componentIds.
	 * 
	 * @param branchPath
	 * @param nameSpace
	 */
	public SnomedEditingContext(IBranchPath branchPath, String nameSpace) {
		super(branchPath);
		init(nameSpace);
	}
	
	private void init(final String namespace) {
		this.refSetEditingContext = new SnomedRefSetEditingContext(this);
		setNamespace(namespace);
	}

	private void setNamespace(String nameSpace) {
		this.nameSpace = checkNotNull(nameSpace, "No namespace configured");
	}
	
	@Override
	public CDOCommitInfo commit(String commitMessage, IProgressMonitor monitor) throws SnowowlServiceException {
		try {
			return super.commit(commitMessage, monitor);
		} catch (Exception e) {
			releaseIds();
			throw e;
		}
	}
	
	@Override
	protected String getId(CDOObject component) {
		if (component instanceof Component) {
			return ((Component) component).getId();
		} else if (component instanceof SnomedRefSetMember) {
			return ((SnomedRefSetMember) component).getUuid();
		} else if (component instanceof SnomedRefSet) {
			return ((SnomedRefSet) component).getIdentifierId();
		}
		throw new UnsupportedOperationException("Cannot get ID for " + component);
	}
	
	@Override
	protected <T extends CDOObject> Iterable<? extends IComponent> fetchComponents(Collection<String> componentIds, Class<T> type) {
		if (type.isAssignableFrom(Concept.class)) {
			return SnomedRequests.prepareSearchConcept()
					.all()
					.filterByIds(componentIds)
					.setFields(SnomedDocument.Fields.ID, Revision.STORAGE_KEY)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.getSync();
		} else if (type.isAssignableFrom(Description.class)) {
			return SnomedRequests.prepareSearchDescription()
					.all()
					.filterByIds(componentIds)
					.setFields(SnomedDocument.Fields.ID, Revision.STORAGE_KEY)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.getSync();
		} else if (type.isAssignableFrom(Relationship.class)) {
			return SnomedRequests.prepareSearchRelationship()
					.all()
					.filterByIds(componentIds)
					.setFields(SnomedDocument.Fields.ID, Revision.STORAGE_KEY)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.getSync();
		} else if (type.isAssignableFrom(SnomedRefSetMember.class)) {
			return SnomedRequests.prepareSearchMember()
					.all()
					.filterByIds(componentIds)
					.setFields(SnomedDocument.Fields.ID, Revision.STORAGE_KEY)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.getSync();
		} else if (type.isAssignableFrom(SnomedRefSet.class)) {
			return SnomedRequests.prepareSearchRefSet()
					.all()
					.filterByIds(componentIds)
					.setFields(SnomedDocument.Fields.ID, SnomedConceptDocument.Fields.REFSET_STORAGEKEY)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.getSync();
		}
		throw new UnsupportedOperationException("Cannot get storage keys for " + type);
	}
	
	public void releaseIds() {
		if (!newComponentIds.isEmpty()) {
			final IEventBus bus = ApplicationContext.getInstance().getServiceChecked(IEventBus.class);
			SnomedRequests.identifiers().prepareRelease()
				.setComponentIds(newComponentIds)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(bus)
				.getSync();
			
			newComponentIds.clear();
		}
	}
	
	/**
	 * Unlike {@link CDOEditingContext#getContents()} this method returns with
	 * the {@link Concepts#getConcepts() concepts container} of the default
	 * {@link Concepts} instance stored in the root CDO resource.
	 * <br>May return with {@code null} if does not exist yet.
	 * <p>
	 * {@inheritDoc} 
	 * @return the root container of all concepts.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Concept> getContents() {
		final CDOID cdoid = SnomedConceptsContainerCdoIdSupplier.INSTANCE.getDefaultContainerCdoId();
		if (NULL.equals(cdoid)) {
			return emptyList();
		}
		return ((Concepts) getObjectIfExists(transaction, cdoid)).getConcepts(); 
	} 
	
	public SnomedRefSetEditingContext getRefSetEditingContext() {
		return refSetEditingContext;
	}
	
	/////////////////////////////////////////////////////////////////////////
	// Terminology component retrieval operations
	/////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a SNOMED CT Concept database object from the store by loading it, or from the transaction if previously added via {@link #add(EObject)}
	 * or {@link #addAll(Collection)}, otherwise if not found throws a {@link ComponentNotFoundException}.
	 * 
	 * @param conceptId
	 *            - the SNOMED CT identifier of the concept
	 * @return the concept, never <code>null</code>
	 * @throws ComponentNotFoundException
	 *             if the concept could not be retrieved
	 */
	public final Concept getConcept(String conceptId) {
		return lookup(conceptId, Concept.class);
	}
	
	/**
	 * @deprecated - use and see {@link SnomedEditingContext#getConcept(String)}, will be removed in 4.6
	 */
	public Concept findConceptById(final String conceptId) {
		return getConcept(conceptId);
	}

	/**
	 * Builds a new default SNOMED CT concept with a fully specified name description and ISA relationship to the specified parent concept.
	 * @param fullySpecifiedName
	 * @param parentConceptId
	 * @return
	 * @deprecated - will be removed in 4.4
	 */
	public Concept buildDefaultConcept(String conceptId, String fullySpecifiedName, String parentConceptId) {
		return buildDefaultConcept(conceptId, fullySpecifiedName, findConceptById(parentConceptId));
	}
	
	/**
	 * Builds a new default SNOMED CT concept with a fully specified name description and ISA relationship to the specified parent concept with the given identifier. 
	 * @param fullySpecifiedName
	 * @param parentConcept
	 * @return
	 * @deprecated - will be removed in 4.5
	 */
	public Concept buildDefaultConcept(String fullySpecifiedName, String parentConceptId) {
		return buildDefaultConcept(generateComponentId(ComponentCategory.CONCEPT, getNamespace()), fullySpecifiedName, findConceptById(parentConceptId));
	}
	
	/**
	 * @param fullySpecifiedName
	 * @param parentConcept can not be <tt>null</tt>.
	 * @return a valid concept populated with default values and the specified properties
	 * @deprecated - will be replaced with new component builder API in 4.4
	 */
	public Concept buildDefaultConcept(String conceptId, String fullySpecifiedName, Concept parentConcept) {
		
		checkNotNull(parentConcept, "parentConcept");
		
		Concept concept = SnomedFactory.eINSTANCE.createConcept();
		add(concept);
		
		// set concept properties
		concept.setId(conceptId);
		concept.setActive(true);
		concept.setDefinitionStatus(findConceptById(PRIMITIVE));
		concept.setModule(getDefaultModuleConcept());
		
		// add FSN
		Description description = buildDefaultDescription(fullySpecifiedName, FULLY_SPECIFIED_NAME);
		description.setConcept(concept);
		
		// add 'Is a' relationship to parent if specified
		buildDefaultIsARelationship(parentConcept, concept);
		
		return concept;
	}
	
	
	/**
	 * @param fullySpecifiedName the fully specified description's term.
	 * @param namespace the namespace for the new components.
	 * @param moduleConcept module concept
	 * @param parentConcept can not be {@code null}.
	 * @return a valid concept populated with default values and the specified properties
	 * @deprecated - will be replaced with new component builder API in 4.4
	 */
	private Concept buildDefaultConcept(final String fullySpecifiedName, final String namespace, final Concept moduleConcept, final Concept parentConcept, final Concept... parentConcepts) {
		
		checkNotNull(parentConcept, "parentConcept");
		
		Concept concept = SnomedFactory.eINSTANCE.createConcept();
		add(concept);
		
		// set concept properties
		concept.setId(generateComponentId(ComponentCategory.CONCEPT, namespace));
		concept.setActive(true);
		concept.setDefinitionStatus(findConceptById(PRIMITIVE));
		concept.setModule(moduleConcept);
		
		// add FSN
		Description fsn = buildDefaultDescription(fullySpecifiedName, namespace, findConceptById(FULLY_SPECIFIED_NAME), moduleConcept);
		fsn.setConcept(concept);
		
		//add PT
		Description pt = buildDefaultDescription(fullySpecifiedName, namespace, findConceptById(SYNONYM), moduleConcept);
		pt.setConcept(concept);

		//create language reference set members for the descriptions.
		final SnomedStructuralRefSet languageRefSet = getLanguageRefSet();
		for (final Description description : concept.getDescriptions()) {
			if (description.isActive()) { //this point all description should be active
				final String acceptabilityId = REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED;
				//create language reference set membership
				final String referencedComponentId = description.getId();
				final SnomedLanguageRefSetMember member = getRefSetEditingContext().createLanguageRefSetMember(referencedComponentId, acceptabilityId, moduleConcept.getId(), languageRefSet);
				description.getLanguageRefSetMembers().add(member);
			}
		}
		
		//add IS_A relationship to parent
		buildDefaultRelationship(concept, findConceptById(IS_A), parentConcept, 
				findConceptById(STATED_RELATIONSHIP), moduleConcept, namespace);
		
		//add IS_A relationships to optional parent concepts
		if (!CompareUtils.isEmpty(parentConcepts)) {
			for (final Concept parent : parentConcepts) {
				buildDefaultRelationship(concept, findConceptById(IS_A), parent, 
						findConceptById(STATED_RELATIONSHIP), moduleConcept, namespace);
			}
		}
		
		return concept;
	}
	
	/**Returns with the currently used language type reference set, falls back to an existing language if the configured identifier can not be resolved.*/
	public SnomedStructuralRefSet getLanguageRefSet() {
		final String languageRefSetId = getLanguageRefSetId();
		final SnomedRefSetLookupService snomedRefSetLookupService = new SnomedRefSetLookupService();
		return (SnomedStructuralRefSet) snomedRefSetLookupService.getComponent(languageRefSetId, transaction);
	}

	public String getLanguageRefSetId() {
		return ApplicationContext.getInstance().getServiceChecked(ILanguageConfigurationProvider.class).getLanguageConfiguration().getLanguageRefSetId(BranchPathUtils.createPath(transaction));
	}
	
	/**
	 * This method build a concept with Simple type refset concept id. This is used only by the importer to add
	 * the missing refset concept to the 0531 NEHTA releases
	 * 
	 * @param parentConcept
	 * @return
	 * @deprecated - unused, will be removed in 4.4
	 */
	public Concept buildSimpleTypeConcept(Concept parentConcept) {
		if (parentConcept == null) {
			throw new NullPointerException("Parent concept was null");
		}
		
		Concept concept = SnomedFactory.eINSTANCE.createConcept();
		add(concept);
		
		// set concept properties
		concept.setId(REFSET_SIMPLE_TYPE);
		concept.setActive(true);
		concept.setDefinitionStatus(new SnomedConceptLookupService().getComponent(PRIMITIVE, transaction));
		concept.setModule(getDefaultModuleConcept());
		
		// add FSN
		Description description = SnomedFactory.eINSTANCE.createDescription();
		description.setId("2879491016");
		description.setActive(true);
		description.setCaseSignificance(new SnomedConceptLookupService().getComponent(ENTIRE_TERM_CASE_SENSITIVE, transaction));
		description.setType(new SnomedConceptLookupService().getComponent(FULLY_SPECIFIED_NAME, transaction));
		description.setTerm("Simple type reference set");
		description.setLanguageCode("en");
		description.setModule(getDefaultModuleConcept());
		
		description.setConcept(concept);
		
		// add 'Is a' relationship to parent
		buildDefaultIsARelationship(parentConcept, concept);
		
		return concept;
	}
	
	/**
	 * This method build a concept with Complex map type refset concept id. This is used only by the importer to add
	 * the missing refset concept to the 0531 NEHTA releases
	 * 
	 * @param parentConcept
	 * @return
	 * @deprecated - unused, will be removed in 4.4
	 */
	public Concept buildComplexMapTypeConcept(Concept parentConcept) {
		Preconditions.checkNotNull(parentConcept, "Parent concept argument cannot be null.");
		Concept concept = SnomedFactory.eINSTANCE.createConcept();
		add(concept);
		
		// set concept properties
		concept.setId(REFSET_COMPLEX_MAP_TYPE);
		concept.setActive(true);
		concept.setDefinitionStatus(new SnomedConceptLookupService().getComponent(PRIMITIVE, transaction));
		concept.setModule(getDefaultModuleConcept());
		
		// add FSN
		Description fsn = SnomedFactory.eINSTANCE.createDescription();
		fsn.setId("2879500011");
		fsn.setActive(true);
		fsn.setCaseSignificance(new SnomedConceptLookupService().getComponent(ENTIRE_TERM_CASE_SENSITIVE, transaction));
		fsn.setType(new SnomedConceptLookupService().getComponent(FULLY_SPECIFIED_NAME, transaction));
		fsn.setTerm("Complex map type reference set");
		fsn.setLanguageCode("en");
		fsn.setModule(getDefaultModuleConcept());
		fsn.setConcept(concept);
		
		//add synonym
		Description synonym = SnomedFactory.eINSTANCE.createDescription();
		synonym.setId("2882969013");
		synonym.setActive(true);
		synonym.setCaseSignificance(new SnomedConceptLookupService().getComponent(ENTIRE_TERM_CASE_SENSITIVE, transaction));
		synonym.setType(new SnomedConceptLookupService().getComponent(SYNONYM, transaction));
		synonym.setTerm("Complex map type reference set");
		synonym.setLanguageCode("en");
		synonym.setModule(getDefaultModuleConcept());
		synonym.setConcept(concept);
		
		// add 'Is a' relationship to parent
		buildDefaultIsARelationship(parentConcept, concept);
		
		return concept;
	}
	
	
	/**
	 * @param source can be <tt>null</tt> then source will be an empty concept with empty <tt>String</tt> description.
	 * @param type can be <tt>null</tt> then type will be an empty concept with empty <tt>String</tt> description.
	 * @param destination can be <tt>null</tt> then destination will be an empty concept with empty <tt>String</tt> description.
	 * @param characteristicType can be <tt>null</tt> then characteristicType will be Defining concept.
	 * @param effectiveTime 
	 * @return a valid relationship populated with default values and the specified properties
	 * @deprecated - use {@link SnomedComponents#newRelationship()} instead
	 */
	public Relationship buildDefaultRelationship(Concept source, Concept type, Concept destination, Concept characteristicType) {
		return buildDefaultRelationship(source, type, destination, characteristicType, getDefaultModuleConcept(), getDefaultNamespace());
	}
	
	/**
	 * @deprecated - use {@link SnomedComponents#newRelationship()} instead
	 */
	public Relationship buildDefaultRelationship(final Concept source, final Concept type, final Concept destination, Concept characteristicType, final Concept module, final String namespace) {
		// default is stated
		if (characteristicType == null) {
			characteristicType = findConceptById(STATED_RELATIONSHIP);
		}
		
		final Relationship relationship = buildEmptyRelationship(namespace);
		relationship.setType(type);
		relationship.setActive(true);
		relationship.setCharacteristicType(characteristicType);
		relationship.setSource(source);
		relationship.setDestination(destination);
		relationship.setGroup(0);
		relationship.setModifier(findConceptById(EXISTENTIAL_RESTRICTION_MODIFIER));
		relationship.setModule(module);
		return relationship;
	}
	
	/**
	 * Creates a relationship and sets its Id attribute to a new identifier. It is the caller's responsibility to set all other
	 * attributes. 
	 * 
	 * @return a relationship instance that only has a generated component identifier
	 * @deprecated - unused, will be removed in 4.4
	 */
	public Relationship buildEmptyRelationship() {
		return buildEmptyRelationship();
	}
	
	/**
	 * Creates a relationship and sets its Id attribute to a new identifier. The relationship identifier will have the namespace
	 * specified in the arguments. It is the caller's responsibility to set all other attributes.
	 * 
	 * @param namespace the namespace of the relationship identifier
	 * @return a relationship instance that only has a generated component identifier
	 * @deprecated - will be replaced and removed in 4.4
	 */
	public Relationship buildEmptyRelationship(final String namespace) {
		final Relationship relationship = SnomedFactory.eINSTANCE.createRelationship();
		relationship.setId(generateComponentId(ComponentCategory.RELATIONSHIP, namespace));
		return relationship;
	}
	
	/**
	 * @param term
	 * @param type
	 * @param effectiveTime 
	 * @return a valid description populated with default values, the specified term and description type
	 * @deprecated - will be replaced and removed in 4.4
	 */
	public Description buildDefaultDescription(String term, String typeId) {
		return buildDefaultDescription(term, getDefaultNamespace(), findConceptById(typeId), getDefaultModuleConcept());
	}
	
	/**
	 * @param term
	 * @param type
	 * @param effectiveTime 
	 * @return a valid description populated with default values, the specified term and description type
	 * @deprecated - will be replaced and removed in 4.4
	 */
	public Description buildDefaultDescription(String term, final String namespace, final Concept type, final Concept moduleConcept) {
		return buildDefaultDescription(term, namespace, type, moduleConcept, getDefaultLanguageCode());
	}
	
	/*builds a description with the specified description type, module concept, language code and namespace.*/
	private Description buildDefaultDescription(String term, final String namespace, final Concept type, final Concept moduleConcept, final String languageCode) {
		Description description = SnomedFactory.eINSTANCE.createDescription();
		description.setId(generateComponentId(ComponentCategory.DESCRIPTION, namespace));
		description.setActive(true);
		description.setCaseSignificance(findConceptById(ENTIRE_TERM_CASE_INSENSITIVE));
		description.setType(type);
		description.setTerm(term);
		description.setLanguageCode(languageCode);
		description.setModule(moduleConcept);
		return description;
	}

	/**
	 * Inactivates the given concepts and all relevant SNOMED&nbsp;CT component references these concepts. Inactivation reference set memberships
	 * created when you perform the inactivation. This method return a plan to review the components which are going to be inactivated.
	 * 
	 * @param monitor
	 * @param conceptIds
	 * @return the inactivation plan what should be executed to perform the inactivation process.
	 */
	public SnomedInactivationPlan inactivateConcepts(@Nullable final IProgressMonitor monitor, final CDOID... storageKeys) {
		return inactivateConcepts(createDefaultPlan(), monitor, storageKeys);
	}
	
	/**
	 * Inactivates the given concepts and all relevant SNOMED&nbsp;CT component references these concepts. Inactivation reference set memberships
	 * created when you perform the inactivation. This method return the given plan to review the components which are going to be inactivated.
	 * 
	 * @param monitor
	 * @param conceptIds
	 * @return the inactivation plan what should be executed to perform the inactivation process.
	 */
	public SnomedInactivationPlan inactivateConcepts(SnomedInactivationPlan plan, @Nullable final IProgressMonitor monitor, final CDOID... storageKeys) {
		if (null != monitor) {
			monitor.beginTask("Creating inactivation plan...", storageKeys.length);
		}
		return internalInactivateConcept(plan, false, monitor, storageKeys);
	}
	
	/**
	 * Inactivates the specified SNOMED&nbsp;CT concept and all references. Also creates the required reference set memberships based on the inactivation reason.
	 * @param monitor progress monitor for the operation.
	 * @param conceptId the identifier of the SNOMED&nbsp;CT concept to inactivate.
	 * @return the inactivation plan what should be executed to perform the inactivation process.
	 */
	public SnomedInactivationPlan inactivateConcept(final IProgressMonitor monitor, final String... conceptId) {
		return internalInactivateConcept(createDefaultPlan(), true, monitor, findConceptById(conceptId[0]).cdoID());
	}
	
	private SnomedInactivationPlan createDefaultPlan() {
		return new SnomedInactivationPlan(this);
	}

	/*inactivates SNOMED CT concept identified by the specified CDO IDs.*/
	private SnomedInactivationPlan internalInactivateConcept(SnomedInactivationPlan plan, final boolean updateSubtypeRelationships, final IProgressMonitor monitor, final CDOID... conceptCdoIds) {
		
		for (final CDOID cdoId : conceptCdoIds) {
			
			if (monitor.isCanceled()) {
				return SnomedInactivationPlan.NULL_IMPL;
			}
			
			final Concept concept = getConceptChecked(cdoId);
			
//			if (updateSubtypeRelationships) {
//				updateChildren(concept, plan);
//			}
			
			if (monitor.isCanceled()) {
				return SnomedInactivationPlan.NULL_IMPL;
			}
			
			//concept
			plan.markForInactivation(concept);
			
			if (monitor.isCanceled()) {
				return SnomedInactivationPlan.NULL_IMPL;
			}
			
			//descriptions
			plan.markForInactivation(Iterables.toArray(concept.getDescriptions(), Description.class));
			
			if (monitor.isCanceled()) {
				return SnomedInactivationPlan.NULL_IMPL;
			}
			
			plan.markForInactivation(FluentIterable.from(getInboundRelationshipsFromIndex(concept.getId())).transform(new Function<SnomedRelationship, Long>() {
				@Override
				public Long apply(SnomedRelationship input) {
					return input.getStorageKey();
				}
			}).toSet());
			
			if (monitor.isCanceled()) {
				return SnomedInactivationPlan.NULL_IMPL;
			}
			
			//source descriptions
			plan.markForInactivation(Iterables.toArray(concept.getOutboundRelationships(), Relationship.class));
			
			if (monitor.isCanceled()) {
				return SnomedInactivationPlan.NULL_IMPL;
			}
			
			//reference set members
			final SnomedReferenceSetMembers members = SnomedRequests.prepareSearchMember()
				.all()
				.filterByActive(true)
				.filterByReferencedComponent(concept.getId())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
			
			for (final SnomedReferenceSetMember member : members) {
				if (monitor.isCanceled()) {
					return SnomedInactivationPlan.NULL_IMPL;
				}
				plan.markForInactivation(lookup(member.getId(), SnomedRefSetMember.class));
			}
			
			monitor.worked(1);
		}
		return plan;
	}
	
	private void updateChildren(Concept conceptToInactivate, SnomedInactivationPlan plan) {
		
		Iterable<Concept> parents = getStatedParents(conceptToInactivate);
		Iterable<Concept> children = getStatedChildren(conceptToInactivate);
		
		if (Iterables.isEmpty(parents) || Iterables.isEmpty(children)) {
			return;
		}
		
		final Concept isAConcept = findConceptById(IS_A);
		final Concept statedRelationshipTypeConcept = findConceptById(STATED_RELATIONSHIP);
		
		// connect all former children to the former parents by stated IS_As
		for (Concept parent : parents) {
			for (Concept child : children) {
				final String namespace = SnomedIdentifiers.create(child.getId()).getNamespace();
				buildDefaultRelationship(child, isAConcept, parent, statedRelationshipTypeConcept, child.getModule(), namespace);
			}
		}
		
		// inactivate any remaining inferred relationships of the children
		for (Concept child : children) {
			Iterable<Relationship> inferredRelationships = Iterables.filter(child.getOutboundRelationships(), new Predicate<Relationship>() {
				@Override public boolean apply(Relationship input) {
					return input.isActive() && INFERRED_RELATIONSHIP.equals(input.getCharacteristicType().getId());
				}
			});
			
			plan.markForInactivation(Iterables.toArray(inferredRelationships, Relationship.class));
		}
	}

	private Iterable<Concept> getStatedParents(Concept conceptToInactivate) {
		Iterable<Relationship> statedActiveOutboundIsaRelationships = filterActiveStatedIsaRelationships(conceptToInactivate.getOutboundRelationships());
		Iterable<Concept> parents = Iterables.transform(statedActiveOutboundIsaRelationships, new Function<Relationship, Concept>() {
			@Override public Concept apply(Relationship input) {
				return input.getDestination();
			}
		});
		return parents;
	}
	
	private Iterable<Concept> getStatedChildren(Concept conceptToInactivate) {
		Iterable<Relationship> statedActiveInboundIsaRelationships = filterActiveStatedIsaRelationships(getInboundRelationships(conceptToInactivate.getId()));
		Iterable<Concept> children = Iterables.transform(statedActiveInboundIsaRelationships, new Function<Relationship, Concept>() {
			@Override public Concept apply(Relationship input) {
				return input.getSource();
			}
		});
		return children;
	}

	private Iterable<Relationship> filterActiveStatedIsaRelationships(Collection<Relationship> relationships) {
		return Iterables.filter(relationships, new Predicate<Relationship>() {
			@Override public boolean apply(Relationship input) {
				return input.isActive() && IS_A.equals(input.getType().getId()) && STATED_RELATIONSHIP.equals(input.getCharacteristicType().getId());
			}
		});
	}

	private Concept getConceptChecked(final CDOID cdoId) {
		final CDOObject object = transaction.getObject(cdoId);
		Preconditions.checkState(object instanceof Concept, "CDO object must be a SNOMED CT concept with ID: " + cdoId);
		return (Concept) object; 
	}

	@Override
	public void delete(EObject object, boolean force) {
		if (object instanceof Concept) {
			delete((Concept) object, force);
		} else if (object instanceof Description) {
			delete((Description) object, force);
		} else if (object instanceof Relationship) {
			delete((Relationship) object, force);
		} else if (object instanceof SnomedRefSet) {
			delete((SnomedRefSet) object, force);
		} else if (object instanceof SnomedRefSetMember) {
			delete((SnomedRefSetMember) object, force);
		} else {
			super.delete(object, force);
		}
	}
	
	private void delete(Concept concept, boolean force) {
		deletionPlan = canDelete(concept, deletionPlan, force);
		if(deletionPlan.isRejected()) {
			throw new ConflictException(deletionPlan.getRejectionReasons().toString());
		}
	}

	private void delete(Description description, boolean force) {
		deletionPlan = canDelete(description, deletionPlan, force);
		if(deletionPlan.isRejected()) {
			throw new ConflictException(deletionPlan.getRejectionReasons().toString());
		}
	}

	private void delete(Relationship relationship, boolean force) {
		deletionPlan = canDelete(relationship, deletionPlan, force);
		if(deletionPlan.isRejected()) {
			throw new ConflictException(deletionPlan.getRejectionReasons().toString());
		}
	}

	private void delete(SnomedRefSet refSet, boolean force) {
		deletionPlan = canDelete(refSet, deletionPlan, force);
		if (deletionPlan.isRejected()) {
			throw new ConflictException(deletionPlan.getRejectionReasons().toString());
		}
	}
	
	private void delete(SnomedRefSetMember member, boolean force) {
		deletionPlan = canDelete(member, deletionPlan, force);
		if (deletionPlan.isRejected()) {
			throw new ConflictException(deletionPlan.getRejectionReasons().toString());
		}
	}
	
	public SnomedDeletionPlan canDelete(Concept concept, SnomedDeletionPlan deletionPlan, boolean force) {
		if (deletionPlan == null) {
			deletionPlan = new SnomedDeletionPlan();
		}
		
		// Check if concept is already released, and this is not a forced delete
		if (concept.isReleased() && !force) {
			deletionPlan.addRejectionReason(String.format(COMPONENT_IS_RELEASED_MESSAGE, "concept", toString(concept)));
			return deletionPlan;
		}
		
		// Also check inbound relationships, as these could have been released with different effective times
		for (Relationship relationship : getInboundRelationships(concept.getId())) {
			if (relationship != null) {
				deletionPlan = canDelete(relationship, deletionPlan, force);
				if (deletionPlan.getRejectionReasons().size() > 0) {
					deletionPlan.addRejectionReason(String.format(UNABLE_TO_DELETE_CONCEPT_MESSAGE, toString(concept)));
					return deletionPlan;
				}
			}
		}
		
		/*
		 * All other components below should become released when the concept is first released, which was handled 
		 * above, so don't exit early via a rejection check.
		 */
		
		for (Description description : concept.getDescriptions()) {
			deletionPlan = canDelete(description, deletionPlan, force);
		}
		
		for (Relationship outboundRelationship : concept.getOutboundRelationships()) {
			deletionPlan = canDelete(outboundRelationship, deletionPlan, force);
		}
		
		SnomedRefSet refSet = new SnomedRefSetLookupService().getComponent(concept.getId(), transaction);
		if (refSet != null) {
			deletionPlan = canDelete(refSet, deletionPlan, force);
		}
		
		List<SnomedRefSetMember> referringMembers = refSetEditingContext.getReferringMembers(concept);

		// If this concept is a member of the description format reference set, descriptions of this type have to be updated
		for (SnomedRefSetMember member : referringMembers) {
			if (REFSET_DESCRIPTION_TYPE.equals(member.getRefSetIdentifierId())) {
				for (SnomedDescriptionIndexEntry entry : getRelatedDescriptions(member.getReferencedComponentId())) {
					final Description description = lookup(entry.getId(), Description.class);
					if (null == description) {
						throw new SnowowlRuntimeException("Description does not exist in store with ID: " + entry.getId());
					} else {
						deletionPlan.addDirtyDescription(description);
					}
				}
			}
		}

		deletionPlan.markForDeletion(referringMembers);
		deletionPlan.markForDeletion(concept);
		return deletionPlan;
	}

	public SnomedDeletionPlan canDelete(Description description, SnomedDeletionPlan deletionPlan, boolean force) {
		// If the description is the target of the deletion, check validity
		if (deletionPlan == null) {
			deletionPlan = new SnomedDeletionPlan();
			
			// Check if description is already released, and this is not a forced delete
			if (description.isReleased() && !force) {
				deletionPlan.addRejectionReason(String.format(COMPONENT_IS_RELEASED_MESSAGE, "description", toString(description)));
				return deletionPlan;
			}
			
			// not the only fully specified name
			if (FULLY_SPECIFIED_NAME.equals(description.getType().getId())) {
				boolean hasOtherFullySpecifiedName = false;
				final List<Description> otherDescriptions = description.getConcept().getDescriptions();
				for (Description otherDescription: otherDescriptions) {
					// another fully specified name exists that is not this description
					if (FULLY_SPECIFIED_NAME.equals(otherDescription.getType().getId()) && description != otherDescription) {
						hasOtherFullySpecifiedName = true;
						break;
					}
				}
				
				if (!hasOtherFullySpecifiedName) {
					deletionPlan.addRejectionReason(UNABLE_TO_DELETE_ONLY_FSN_DESCRIPTION_MESSAGE);
					return deletionPlan;
				}
			}
		} 
		
		deletionPlan.markForDeletion(refSetEditingContext.getReferringMembers(description));
		deletionPlan.markForDeletion(description);
		return deletionPlan;
	}

	public SnomedDeletionPlan canDelete(Relationship relationship, SnomedDeletionPlan deletionPlan, boolean force) {
		// If the relationship is the target of the deletion, check validity
		if (deletionPlan == null) {
			deletionPlan = new SnomedDeletionPlan();
		
			// Check if description is already released, and this is not a forced delete
			if (relationship.isReleased() && !force) {
				deletionPlan.addRejectionReason(String.format(COMPONENT_IS_RELEASED_MESSAGE, "relationship", toString(relationship)));
				return deletionPlan;
			}
		}
		
		deletionPlan.markForDeletion(refSetEditingContext.getReferringMembers(relationship));		
		if (relationship.getSource() != null) {
			deletionPlan.markForDeletion(relationship);
		}
		return deletionPlan;
	}

	public SnomedDeletionPlan canDelete(SnomedRefSet refSet, SnomedDeletionPlan deletionPlan, boolean force) {
		// If the reference set is the target of the deletion, check validity for each member
		if (deletionPlan == null) {
			deletionPlan = new SnomedDeletionPlan();
			
			for (SnomedRefSetMember member : refSetEditingContext.getMembers(refSet)) {
				deletionPlan = canDelete(member, deletionPlan, force);
				if (deletionPlan.isRejected()) {
					deletionPlan.addRejectionReason(String.format(UNABLE_TO_DELETE_REFERENCE_SET_MESSAGE, refSet.getIdentifierId()));
					return deletionPlan;
				}
			}
		} else {
			// Otherwise members can be deleted without individually checking each one
			deletionPlan.markForDeletion(refSetEditingContext.getMembers(refSet));
		}
		
		deletionPlan.markForDeletion(refSet);
		return deletionPlan;
	}
	
	public SnomedDeletionPlan canDelete(SnomedRefSetMember member, SnomedDeletionPlan deletionPlan, boolean force) {
		if (deletionPlan == null) {
			deletionPlan = new SnomedDeletionPlan();
			
			if (member.isReleased() && !force) {
				deletionPlan.addRejectionReason(String.format(COMPONENT_IS_RELEASED_MESSAGE, "member", member.getUuid()));
				return deletionPlan;
			}
		}
		
		deletionPlan.markForDeletion(member);
		return deletionPlan;
	}

	/**
	 * This functions deletes the objects in the deletionplan from the database.
	 * For the sake of acceptable execution speed, the <code>remove(int index)</code> function is used
	 * instead of the <code>remove(object)</code>. This way, the CDO will not iterate through the large
	 * number of data in the resources. 
	 * @param deletionPlan the deletionplan containing all the objects to delete
	 */
	public void delete(SnomedDeletionPlan deletionPlan) {
		// organize elements regarding their index
		final Multimap<Integer, EObject> itemMap = ArrayListMultimap.create();
		
		for (CDOObject item : deletionPlan.getDeletedItems()) {
			
			// Set bogus value here instead of letting it pass when trying to use it (it would silently remove the first member of a list)
			int index = -1;
			
			if (item instanceof Concept) {
				index = getIndexFromDatabase(item, (Concepts) item.eContainer(), "SNOMED_CONCEPTS_CONCEPTS_LIST");
			} else if (item instanceof SnomedRefSet) {
				// from cdoRootResource
				index = getIndexFromDatabase(item, item.cdoResource(), "ERESOURCE_CDORESOURCE_CONTENTS_LIST");
			} else if (item instanceof SnomedRefSetMember) {
				// from the refset list
				final SnomedRefSetMember member = (SnomedRefSetMember) item;
				
				if (null == member.eContainer()) { //if the reference set member has been detached from its container.
					continue;
				}
				
				final SnomedRefSet refSet = member.getRefSet();
				
				if (!(refSet instanceof SnomedStructuralRefSet)) { // XXX: also includes the previous null check for refSet
					
					if (refSet instanceof SnomedMappingRefSet) {
						index = getIndexFromDatabase(item, ((SnomedRefSetMember) item).getRefSet(), "SNOMEDREFSET_SNOMEDMAPPINGREFSET_MEMBERS_LIST");
					} else if (refSet instanceof SnomedRegularRefSet) {
						index = getIndexFromDatabase(item, ((SnomedRefSetMember) item).getRefSet(), "SNOMEDREFSET_SNOMEDREGULARREFSET_MEMBERS_LIST");
					} else {
						throw new RuntimeException("Unknown reference set type");
					}
				}
			}
			
			itemMap.put(index, item);
		}
		// iterate through the elements in reverse order
		for(Entry<Integer, EObject> toDelete : Ordering.from(new Comparator<Entry<Integer, EObject>>() {
	
			@Override
			public int compare(Entry<Integer, EObject> o1, Entry<Integer, EObject> o2) {
				return o1.getKey() - o2.getKey();
			}
	
		}).reverse().sortedCopy(itemMap.entries())) {
			final EObject eObject = toDelete.getValue();
			final int index = toDelete.getKey();
			
			if (eObject instanceof Concept) {
				final Concepts concepts = (Concepts) eObject.eContainer();
				concepts.getConcepts().remove(index);
			} 
			else if (eObject instanceof SnomedRefSet){
				refSetEditingContext.getContents().remove(index);
			}
			else if (eObject instanceof SnomedRefSetMember) {
				// get the refset and remove the member from it's list
				SnomedRefSetMember member = (SnomedRefSetMember) eObject;	
				SnomedRefSet refSet = member.getRefSet();
	
				if (refSet != null) {
					
					if (refSet instanceof SnomedStructuralRefSet) {
						EcoreUtil.remove(member);
					} else if (refSet instanceof SnomedRegularRefSet) {
						((SnomedRegularRefSet) refSet).getMembers().remove(index);
					} else {
						throw new IllegalStateException("Don't know how to remove member from reference set class '" + refSet.eClass().getName() + "'.");
					}
				}
				
				//in case of relationship or description an index lookup is not necessary 
			} else if (eObject instanceof Relationship) {
				Relationship relationship = (Relationship) eObject;
				relationship.setSource(null);
				relationship.setDestination(null);
			} else if (eObject instanceof Description) {
				Description description = (Description) eObject;
				// maybe description was already removed before save, so the delete is reflected on the ui
				if (description.getConcept() != null) {
					description.setConcept(null);
				}
			}  else {
				throw new IllegalArgumentException("Don't know how to delete " + eObject.eClass());
			}
		}
	}

	public final List<Relationship> getInboundRelationships(String conceptId) {
		return FluentIterable.from(getInboundRelationshipsFromIndex(conceptId)).transform(new Function<SnomedRelationship, Relationship>() {
			@Override
			public Relationship apply(SnomedRelationship input) {
				return (Relationship) lookup(input.getStorageKey());
			}
		}).toList();
	}

	private Iterable<SnomedRelationship> getInboundRelationshipsFromIndex(String conceptId) {
		return SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByDestination(conceptId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
	}

	private Iterable<SnomedDescriptionIndexEntry> getRelatedDescriptions(String conceptId) {
		return SnomedRequests.prepareSearchDescription()
				.all()
				.filterByConcept(conceptId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedDescriptions, Iterable<SnomedDescriptionIndexEntry>>() {
					@Override
					public Iterable<SnomedDescriptionIndexEntry> apply(SnomedDescriptions input) {
						return SnomedDescriptionIndexEntry.fromDescriptions(input);
					}
				}).getSync();
	}

	private String toString(final Component component) {
		final String id = getAttribute(component, SnomedPackage.eINSTANCE.getComponent_Id(), String.class);
		if (component instanceof Concept) {
			return getServiceForClass(ISnomedConceptNameProvider.class).getComponentLabel(createPath(component), id);
		} else if (component instanceof Description) {
			return getAttribute(component, SnomedPackage.eINSTANCE.getDescription_Term(), String.class);
		} else if (component instanceof Relationship) {
			return getServiceForClass(ISnomedRelationshipNameProvider.class).getComponentLabel(createPath(component), id); 
		} else {
			return id;
		}
	}
	
	/**
	 * @return the module concept specified in the preferences, or falls back to the <em>SNOMED CT core module</em>
	 * concept if the specified concept is not found.
	 * @deprecated - use {@link ModulePreference} instead to get the module ID on client side
	 */
	public Concept getDefaultModuleConcept() {
		if (moduleConcept == null) {
			for (String modulePreference : ModulePreference.getModulePreference()) {
				if (moduleConcept != null) {
					break;
				}
				try {
					moduleConcept = getConcept(modulePreference);
				} catch (ComponentNotFoundException e) {
					// ignore and proceed to the next preference
				}
			}
			if (moduleConcept == null) {
				LOGGER.warn("Error while loading and caching SNOMED CT module concept.");
			}
		}
		return moduleConcept;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected <T> ILookupService<T, CDOView> getComponentLookupService(Class<T> type) {
		if (type == Concept.class) {
			return (ILookupService<T, CDOView>) new SnomedConceptLookupService();
		} else if (type == Description.class) {
			return (ILookupService<T, CDOView>) new SnomedDescriptionLookupService();
		} else if (type == Relationship.class) {
			return (ILookupService<T, CDOView>) new SnomedRelationshipLookupService();
		} else if (SnomedRefSet.class.isAssignableFrom(type)) {
			return (ILookupService<T, CDOView>) new SnomedRefSetLookupService();
		} else if (SnomedRefSetMember.class.isAssignableFrom(type)) {
			return (ILookupService<T, CDOView>) new SnomedRefSetMemberLookupService();
		} else {
			return super.getComponentLookupService(type);
		}
	}
	
	/**
	 * @return the default language code used for descriptions, calculated by
	 *         taking the language code of the currently used language reference
	 *         set, and removing any region-specific parts (everything after the
	 *         first dash character)
	 */
	public String getDefaultLanguageCode() {
		
		String languageRefSetCode = ApplicationContext.getInstance().getService(ILanguageConfigurationProvider.class).getLanguageConfiguration().getLanguageCode();
		
		if (languageRefSetCode == null) {
			throw new NullPointerException("No default language code configured");
		}
		
		int regionStart = languageRefSetCode.indexOf('-');
		
		if (regionStart != -1) {
			languageRefSetCode = languageRefSetCode.substring(0, regionStart);
		}
		
		return languageRefSetCode;
	}
	
	/**
	 * @return the currently configured namespace for use with component identifier generation
	 */
	public String getNamespace() {
		return nameSpace;
	}

	@Deprecated
	public static String getDefaultNamespace() {
		return getSnomedConfiguration().getNamespaces().getDefaultChildKey();
	}
	
	public static SnomedConfiguration getSnomedConfiguration() {
		return ApplicationContext.getInstance().getService(SnomedConfiguration.class);
	}
	
	private Relationship buildDefaultIsARelationship(Concept parentConcept, Concept concept) {
		
		Relationship relationship = buildDefaultRelationship(concept, findConceptById(IS_A), 
				parentConcept, findConceptById(STATED_RELATIONSHIP));
		
		relationship.setModule(concept.getModule());
		
		return relationship;
	}
	
	@Override
	public void preCommit() {
		if (deletionPlan != null) {
			delete(deletionPlan);
		}
		/* Ensure that all new components (concepts, descriptions and relationships) have unique 
		 * IDs both among themselves and the components already persisted in the database.
		 * Non-unique IDs will be overwritten with ones which are guaranteed to be unique 
		 * as of the time of this check. */
		if (isUniquenessCheckEnabled()) {
			List<CDOIDAndVersion> newObjects = transaction.getChangeSetData().getNewObjects();
			ComponentIdUniquenessValidator uniquenessEnforcer = new ComponentIdUniquenessValidator(this);
			for (CDOIDAndVersion newCdoIdAndVersion : newObjects) {
				CDOObject newObject = transaction.getObject(newCdoIdAndVersion.getID());
				if (newObject instanceof Component) {
					Component newComponent = (Component) newObject;
					uniquenessEnforcer.validateAndReplaceComponentId(newComponent);
				}
			}
		}
		
		/*
		 * Updates the module dependency refset members based on the changes. Source or target
		 * effective time is set to null if the changed component module id has dependency in
		 * the refset.
		 */
		SnomedModuleDependencyRefSetService dependencyRefSetService = new SnomedModuleDependencyRefSetService();
		dependencyRefSetService.updateModuleDependenciesDuringPreCommit(getTransaction());
	}
	
	public boolean isUniquenessCheckEnabled() {
		return uniquenessCheckEnabled;
	}
	
	public SnomedEditingContext setUniquenessCheckEnabled(boolean uniquenessCheckEnabled) {
		this.uniquenessCheckEnabled = uniquenessCheckEnabled;
		return this;
	}

	/**
	 * Concept deletion may affect (other) concept descriptions because it was member of description type reference set.
	 * Update the affected description types to synonym.
	 * 
	 */
	public void updateDescriptionTypes(Iterable<Description> dirtyDescriptions) {
		Concept synonym = findConceptById(SYNONYM);
		for (Description description : dirtyDescriptions) {
			description.setType(synonym);
		}
	}
	
	@Override
	protected String getRootResourceName() {
		return SnomedDatastoreActivator.ROOT_RESOURCE_NAME;
	}

	/**
	 * Returns all referring reference set members for the given {@link Concept}
	 * in attribute value, simple map and simple type reference sets.
	 * 
	 * @param concept
	 * @return
	 * @throws NullPointerException
	 *             - if the given concept is <code>null</code>
	 */
	public Collection<SnomedRefSetMember> getReferringMembers(Concept concept) {
		return getRefSetEditingContext().getReferringMembers(concept);
	}

	/**
	 * Generates a new SNOMED CT ID for the given component. The new ID will use
	 * the currently set nameSpace, see {@link #getNamespace()}.
	 * 
	 * @param component
	 *            - the component to generate ID for
	 * @return
	 * @see #getNamespace()
	 * @throws NullPointerException
	 *             - if the given component was <code>null</code>.
	 * @deprecated - use new {@link ISnomedIdentifierService}
	 */
	public String generateComponentId(Component component) {
		if (component instanceof Relationship) {
			return generateComponentId(ComponentCategory.RELATIONSHIP, getNamespace());
		} else if (component instanceof Concept) {
			return generateComponentId(ComponentCategory.CONCEPT, getNamespace());
		} else if (component instanceof Description) {
			return generateComponentId(ComponentCategory.DESCRIPTION, getNamespace());
		}
		throw new IllegalArgumentException(MessageFormat.format("Unexpected component class ''{0}''.", component));
	}
	
	public String generateComponentId(final ComponentCategory componentNature) {
		return generateComponentId(componentNature, getNamespace());
	}
	
	public String generateComponentId(final ComponentCategory componentNature, final String namespace) {
		final IEventBus bus = ApplicationContext.getInstance().getServiceChecked(IEventBus.class);
		final String generatedId = SnomedRequests.identifiers().prepareGenerate()
				.setCategory(componentNature)
				.setNamespace(namespace)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(bus)
				.getSync()
				.first()
				.get();
		newComponentIds.add(generatedId);
		return generatedId;
	}

}