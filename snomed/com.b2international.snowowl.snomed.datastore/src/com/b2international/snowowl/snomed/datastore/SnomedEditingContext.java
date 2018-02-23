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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.getAttribute;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.getObjectIfExists;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ENTIRE_TERM_CASE_INSENSITIVE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.EXISTENTIAL_RESTRICTION_MODIFIER;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.IS_A;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.PRIMITIVE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_DESCRIPTION_TYPE;
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
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

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
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Concepts;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.preference.ModulePreference;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.b2international.snowowl.snomed.datastore.services.ISnomedRelationshipNameProvider;
import com.b2international.snowowl.snomed.datastore.services.SnomedModuleDependencyRefSetService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
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
	
	
	/**Returns with the currently used language type reference set, falls back to an existing language if the configured identifier can not be resolved.*/
	public SnomedStructuralRefSet getLanguageRefSet() {
		return lookup(getLanguageRefSetId(), SnomedStructuralRefSet.class);
	}

	public String getLanguageRefSetId() {
		return ApplicationContext.getInstance().getServiceChecked(ILanguageConfigurationProvider.class).getLanguageConfiguration().getLanguageRefSetId(BranchPathUtils.createPath(transaction));
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
			
			// inactivate refset members if the inactivated concept is a reference set
			final SnomedReferenceSetMembers membersOfIdentifierConcept = SnomedRequests.prepareSearchMember()
					.all()
					.filterByActive(true)
					.filterByRefSet(concept.getId())
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.getSync();
			
			for (final SnomedReferenceSetMember member : membersOfIdentifierConcept) {
				if (monitor.isCanceled()) {
					return SnomedInactivationPlan.NULL_IMPL;
				}
				plan.markForInactivation(lookup(member.getId(), SnomedRefSetMember.class));
			}
			
			monitor.worked(1);
		}
		return plan;
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
		
		SnomedRefSet refSet = lookupIfExists(concept.getId(), SnomedRefSet.class);
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