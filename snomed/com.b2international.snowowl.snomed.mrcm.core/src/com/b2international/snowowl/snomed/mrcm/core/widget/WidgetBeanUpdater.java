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
package com.b2international.snowowl.snomed.mrcm.core.widget;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EObject;

import com.b2international.commons.Pair;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.core.api.NullComponent;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.CaseSignificance;
import com.b2international.snowowl.snomed.datastore.IdStorageKeyPair;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedDeletionPlan;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipLookupService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.datastore.services.SnomedRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ContainerWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.DataTypeWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.DescriptionWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.MappingWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.RelationshipGroupWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.RelationshipWidgetBean;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class WidgetBeanUpdater implements IWidgetBeanUpdater {

	private static final String PREFERRED = REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED;
	private static final String ACCEPTABLE = REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE;
	
	private Map<String, IdStorageKeyPair> preferredTermMemberPair;
	private final AtomicBoolean hasPreferredTermChanges = new AtomicBoolean(false);
	private String languageRefSetId;
	
	@Override
	public void updateConcept(final SnomedEditingContext context, final ConceptWidgetBean widgetBean, final Concept concept, IProgressMonitor monitor) {
		if (monitor == null)
			monitor = new NullProgressMonitor();
		
		if (null == preferredTermMemberPair) {
			preferredTermMemberPair = Maps.newHashMap();
		}
		
		if (!context.isDirty()) {
			hasPreferredTermChanges.set(false);
		}
		
		languageRefSetId = widgetBean.getDescriptions().getLanguageRefSetId();
		
		//that means the preferred term member can be retrieved from the index.
		//this condition can be true when first modifying the concept or the concept has been saved
		if (!hasPreferredTermChanges.get()) {
			final Pair<String, IdStorageKeyPair> pair = SnomedEditingContext.getPreferredTermMemberFromIndex(concept, languageRefSetId);
			if (null != pair) { //can happen if the concept does not have preferred term and user set a description to preferred
				preferredTermMemberPair.put(pair.getA(), pair.getB());
			}
		}
		
		// FIXME: concept overview is currently directly bound to the concept on the UI.
		updateDescriptions(context, widgetBean, concept);
		updateRelationships(context, widgetBean, concept);
		updateDataTypes(context, widgetBean, concept, getDefaultModuleId(context));
		// FIXME: hack to allow concept editors to populate a fixed reference set with ATC mappings (ingredient reference set uses this currently)
		updateMappings(context, widgetBean, concept);
	}

	private void updateMappings(final SnomedEditingContext context, final ConceptWidgetBean widgetBean, final Concept concept) {
		if (widgetBean.getMappings() == null) {
			return;
		}
		final Collection<MappingWidgetBean> originalMappings = newArrayList(Iterables.filter(widgetBean.getMappings().getElements(), MappingWidgetBean.class));
		final Collection<MappingWidgetBean> unvisitedMappings = newArrayList(originalMappings);
		final SnomedRefSetMembershipLookupService lookupService = new SnomedRefSetMembershipLookupService();
		final Collection<SnomedRefSetMemberIndexEntry> unvisitedAtcMappings = newArrayList(lookupService.getAtcMappings(concept.getId()));
		for (final MappingWidgetBean mapping : originalMappings) {
			final String referenceSetMemberId = mapping.getUuid();
			final IComponent<String> selectedValue = mapping.getSelectedValue();
			// uninitialized mappings should be added if the value is set
			if (MappingWidgetBean.UNINITIALIZED.equals(referenceSetMemberId)) {
				if (!NullComponent.isNullComponent(selectedValue)) {
					addSimpleMapReferenceSetMember(context, mapping.getModel().getTargetReferenceSetId(), concept, mapping);
				}
				unvisitedMappings.remove(mapping);
			} else {
				// the model element is initialized, searching index first
				final SnomedRefSetMemberIndexEntry persistedEntry = Iterables.find(unvisitedAtcMappings, new Predicate<SnomedRefSetMemberIndexEntry>() {
					@Override
					public boolean apply(final SnomedRefSetMemberIndexEntry input) {
						return input.getId().equals(mapping.getUuid());
					}
				}, null);
				// actual mapping is represents a persisted one
				if (persistedEntry != null) {
					// if the persisted one is still on the UI, but the value is missing, then remove it
					final EObject member = context.getRefSetEditingContext().lookup(persistedEntry.getStorageKey());
					if (member instanceof SnomedSimpleMapRefSetMember) {
						final SnomedSimpleMapRefSetMember mapMember = (SnomedSimpleMapRefSetMember) member;
						if (NullComponent.isNullComponent(selectedValue)) {
							mapping.setUuid(MappingWidgetBean.UNINITIALIZED);
							SnomedModelExtensions.removeOrDeactivate(mapMember);
						} else {
							// check if the referenced component is changed, if remove the member and create a new one
							if (mapMember.getMapTargetComponentId() != null && !mapMember.getMapTargetComponentId().equals(selectedValue.getId())) {
								SnomedModelExtensions.removeOrDeactivate(mapMember);
								// change the target reference set id to the one within the persisted member
								addSimpleMapReferenceSetMember(context, mapMember.getRefSet().getIdentifierId(), concept, mapping);
							}
						}
						unvisitedMappings.remove(mapping);
						unvisitedAtcMappings.remove(persistedEntry);
					}
				} else {
					// the actual mapping is new one, so searching in the newObjects to find it
					final SnomedSimpleMapRefSetMember member = searchInNewObjects(context, mapping);
					if (member != null) {
						if (NullComponent.isNullComponent(selectedValue)) {
							mapping.setUuid(MappingWidgetBean.UNINITIALIZED);
							SnomedModelExtensions.removeOrDeactivate(member);
						} else if (!member.getMapTargetComponentId().equals(selectedValue.getId())) {
							SnomedModelExtensions.removeOrDeactivate(member);
							addSimpleMapReferenceSetMember(context, mapping.getModel().getTargetReferenceSetId(), concept, mapping);
						}
						unvisitedMappings.remove(mapping);
					} else {
						throw new IllegalStateException("Unhandled case the mapping not found in new objects: " + mapping.getSelectedValue().getLabel());
					}
				}
			}
		}
		// if the atcMappings is contains any existing member, then remove all of them
		if (!unvisitedMappings.isEmpty()) {
			throw new IllegalStateException("There is some unvisited mappings");
		}
		// remove all unvisited atc mappings, because they are removed from the beans
		for (final SnomedRefSetMemberIndexEntry entry : unvisitedAtcMappings) {
			final EObject member = context.getRefSetEditingContext().lookup(entry.getStorageKey());
			if (member instanceof SnomedRefSetMember) {
				SnomedModelExtensions.removeOrDeactivate((SnomedRefSetMember) member);
			}
		}
		// check new objects if some new object is there but there is no mapping for it
		final Iterable<SnomedSimpleMapRefSetMember> newMembers = ComponentUtils2.getNewObjects(context.getTransaction(), SnomedSimpleMapRefSetMember.class);
		for (final SnomedSimpleMapRefSetMember newMember : newArrayList(newMembers)) {
			final MappingWidgetBean bean = Iterables.find(originalMappings, new Predicate<MappingWidgetBean>() {
				@Override
				public boolean apply(final MappingWidgetBean input) {
					return newMember.getUuid().equals(input.getUuid());
				}
			}, null);
			// if no bean found then this newly created mapping is removed, so remove the member model as well
			if (bean == null) {
				SnomedModelExtensions.removeOrDeactivate(newMember);
			}
		}
	}

	private SnomedSimpleMapRefSetMember searchInNewObjects(final SnomedEditingContext context, final MappingWidgetBean mapping) {
		final Iterable<SnomedSimpleMapRefSetMember> newMembers = ComponentUtils2.getNewObjects(context.getTransaction(), SnomedSimpleMapRefSetMember.class);
		for (final SnomedSimpleMapRefSetMember newMember : newArrayList(newMembers)) {
			if (newMember.getUuid().equals(mapping.getUuid())) {
				return newMember;
			}
		}
		return null;
	}

	private void addSimpleMapReferenceSetMember(final SnomedEditingContext context, final String targetReferenceSet, final Concept concept, final MappingWidgetBean mapping) {
		checkNotNull(targetReferenceSet, "MappingWidgetBean should define the target reference set identifier");
		final SnomedRefSetEditingContext refSetEditingContext = context.getRefSetEditingContext();
		final ILookupService<String, Object, Object> refsetLookUpService = CoreTerminologyBroker.getInstance().getLookupService(SnomedTerminologyComponentConstants.REFSET);
		final long targetReferenceSetStorageKey = refsetLookUpService.getStorageKey(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE), targetReferenceSet);
		EObject component = refSetEditingContext.lookupIfExists(targetReferenceSetStorageKey);
		// the selected target reference set is deleted, or missing, try creating it a new one
		if (component == null) {
			component = refSetEditingContext.createSnomedSimpleMapRefSet(mapping.getModel().getTargetReferenceSetLabel(), SnomedTerminologyComponentConstants.CONCEPT);
		}
		if (component instanceof SnomedMappingRefSet) {
			final SnomedMappingRefSet mappingRefSet = (SnomedMappingRefSet) component;
			if (mappingRefSet.getMapTargetComponentType() <= 0) {
				mappingRefSet.setMapTargetComponentType(CoreTerminologyBroker.getInstance()
						.getTerminologyComponentIdAsShort(mapping.getModel().getAllowedTerminologyComponentId()));
			}
			final ComponentIdentifierPair<String> referencedComponentPair = SnomedRefSetEditingContext.createConceptTypePair(concept.getId());
			final ComponentIdentifierPair<String> mapTargetPair = ComponentIdentifierPair.<String>create(mapping.getModel().getAllowedTerminologyComponentId(), mapping.getSelectedValue().getId());
			final String moduleId = context.getDefaultModuleConcept().getId();
			final SnomedSimpleMapRefSetMember member = refSetEditingContext.createSimpleMapRefSetMember(referencedComponentPair, mapTargetPair, moduleId, mappingRefSet);
			// FIXME: This is only useful for "Simple map with map target description"-type reference sets, should be more general
			member.setMapTargetComponentDescription(mapping.getSelectedValue().getLabel());
			mappingRefSet.getMembers().add(member);
			mapping.setUuid(member.getUuid());
		}
	}

	/*retrieves the default module concept ID from the editing context for SNOMED CT concept.*/
	private String getDefaultModuleId(final SnomedEditingContext context) {
		return checkNotNull(context.getDefaultModuleConcept().getId(), "Default SNOMED CT module concept was not defined in the editing context");
	}

	private void updateDescriptions(final SnomedEditingContext context, final ConceptWidgetBean widgetBean, final Concept concept) {
		
		final Set<Description> unvisitedDescriptions = Sets.newHashSet(concept.getDescriptions());
		
		for (final DescriptionWidgetBean descriptionBean : Iterables.filter(widgetBean.getDescriptions().getElements(), DescriptionWidgetBean.class)) {
			
			final long descriptionId = descriptionBean.getSctId();
			final String term = descriptionBean.getTerm();
			final IComponent<String> selectedType = descriptionBean.getSelectedType();
			final boolean currentPreferred = descriptionBean.isPreferred();

			if (descriptionId == DescriptionWidgetBean.UNINITIALIZED) {
				
				// Create a new description if all fields are populated
				if (selectedType != NullComponent.<String>getNullImplementation() && !StringUtils.isEmpty(term)) {
					addDescription(context, concept, descriptionBean);
				}
			
			} else {
				
				final Description existingDescription = findDescription(context, concept, Long.toString(descriptionId));
				final String existingDescriptionTypeId = existingDescription.getType().getId();
				final boolean existingPreferred = null != preferredTermMemberPair.get(existingDescription.getId());

				//alter the case significance if it has been modified.
				if (!descriptionBean.getCaseSensitivity().equals(CaseSignificance.getForDescripition(existingDescription))) {
					existingDescription.setCaseSignificance(getCaseSignificanceConcept(descriptionBean, existingDescription));
				}
				
				if (StringUtils.isEmpty(term) 
						|| !existingDescription.getTerm().equals(term)
						|| !existingDescriptionTypeId.equals(selectedType.getId())) {
					
					removeOrDeactivate(context, existingDescription);
					descriptionBean.setSctId(DescriptionWidgetBean.UNINITIALIZED);
					
					// Consider creating a new description in case of a change in the description term
					if (selectedType != NullComponent.<String>getNullImplementation() && !StringUtils.isEmpty(term)) {
						addDescription(context, concept, descriptionBean);
					}
				
				} else if (existingPreferred != currentPreferred) {
				
					// Check for preferred status change, as this doesn't require removing and adding a description
					if (existingPreferred && !currentPreferred) {
						removePreferredState(context, descriptionBean);
					} else if (!existingPreferred && currentPreferred) { 
						//add member with preferred term acceptability
						addPreferredState(context, descriptionBean);
					}
				}
				
				// We have seen this description
				unvisitedDescriptions.remove(existingDescription);
			}
		}
		
		// Remove remaining active descriptions because they didn't show up in the widget bean tree
		for (final Description remainingDescription : unvisitedDescriptions) {
			if (remainingDescription.isActive()) {
				removeOrDeactivate(context, remainingDescription);
			}
		}
		
	}

	/*sets the acceptability state for the description to preferred. to achieve this all other associated language type reference set member
	 * should be either deleted or deactivated*/
	private void addPreferredState(final SnomedEditingContext context, final DescriptionWidgetBean descriptionBean) {
		final Description description = getDescription(context, descriptionBean);
		for (final SnomedLanguageRefSetMember acceptables : getLanguageMembersForDescription(context, description, ACCEPTABLE)) {
			SnomedModelExtensions.removeOrDeactivate(acceptables);
		}
		
		//have to remove all preferred terms as well. can happen if the preferred term member and the description is not initialized 
		//via addDescription(SnomedEditingContext, Concept, DescriptionWidgetBean) but via editing context.
		for (final SnomedLanguageRefSetMember acceptables : getLanguageMembersForDescription(context, description, PREFERRED)) {
			SnomedModelExtensions.removeOrDeactivate(acceptables);
		}
		
		//create language reference set member
		//no existing language reference set member update is required since this is a new description
		final SnomedRefSetEditingContext refSetEditingContext = context.getRefSetEditingContext();
		final SnomedStructuralRefSet languageRefSet = getLanguageRefSet(context);
		final ComponentIdentifierPair<String> acceptibilityPair = SnomedRefSetEditingContext.createConceptTypePair(PREFERRED);
		final ComponentIdentifierPair<String> referencedComponentPair = SnomedRefSetEditingContext.createDescriptionTypePair(description.getId());
		final String moduleId = getDefaultModuleId(context);
		final SnomedLanguageRefSetMember newMember = refSetEditingContext.createLanguageRefSetMember(referencedComponentPair, acceptibilityPair, moduleId, languageRefSet);
		description.getLanguageRefSetMembers().add(newMember);

		//CDO ID is not available for the language type reference set member yet.
		//we have to update the ID every time updateConcept is invoked and the context is dirty
		preferredTermMemberPair.put(description.getId(), new IdStorageKeyPair(newMember.getUuid(), -1L));
		hasPreferredTermChanges.set(true);
	}

	private void removePreferredState(final SnomedEditingContext context, final DescriptionWidgetBean descriptionBean) {
		final Description description = getDescription(context, descriptionBean);
		for (final SnomedLanguageRefSetMember acceptables : getLanguageMembersForDescription(context, description, PREFERRED)) {
			SnomedModelExtensions.removeOrDeactivate(acceptables);
		}
		
		//create language reference set member
		//no existing language reference set member update is required since this is a new description
		final SnomedRefSetEditingContext refSetEditingContext = context.getRefSetEditingContext();
		final SnomedStructuralRefSet languageRefSet = getLanguageRefSet(context);
		final ComponentIdentifierPair<String> acceptibilityPair = SnomedRefSetEditingContext.createConceptTypePair(ACCEPTABLE);
		final ComponentIdentifierPair<String> referencedComponentPair = SnomedRefSetEditingContext.createDescriptionTypePair(description.getId());
		final String moduleId = getDefaultModuleId(context);
		final SnomedLanguageRefSetMember newMember = refSetEditingContext.createLanguageRefSetMember(referencedComponentPair, acceptibilityPair, moduleId, languageRefSet);
		description.getLanguageRefSetMembers().add(newMember);

		//remove preferred member from cache
		//we have to update the ID every time updateConcept is invoked and the context is dirty
		preferredTermMemberPair.remove(description.getId());
		hasPreferredTermChanges.set(true);
	}
	
	/*returns with the SNOMED CT description associated with the description widget bean*/
	private Description getDescription(final SnomedEditingContext context, final DescriptionWidgetBean descriptionBean) {
		final Description description = new SnomedDescriptionLookupService().getComponent(String.valueOf(descriptionBean.getSctId()), context.getTransaction());
		if (null != description) {
			return description;
		}
		for (final Description newDescription : ComponentUtils2.getNewObjects(context.getTransaction(), Description.class)) {
			if (String.valueOf(descriptionBean.getSctId()).equals(newDescription.getId())) {
				return newDescription;
			}
		}
		throw new NullPointerException("Cannot find description with ID: " + descriptionBean.getSctId());
	}

	/*returns with the description case significance metadata concept*/
	private Concept getCaseSignificanceConcept(final DescriptionWidgetBean descriptionBean, final Description existingDescription) {
		return new SnomedConceptLookupService().getComponent(descriptionBean.getCaseSensitivity().getId(), existingDescription.cdoView());
	}
	
	/*adds a new description to the transaction. also creates the proper language type reference set member. if the 
	 * description in a FSN -> preferred, if the description is the preferred term -> preferred. Otherwise acceptable.*/
	private void addDescription(final SnomedEditingContext context, final Concept concept, final DescriptionWidgetBean bean) {

		//create description and add to concept 
		final String term = bean.getTerm();
		final IComponent<String> selectedType = bean.getSelectedType();
		final Description newDescription = context.buildDefaultDescription(term, selectedType.getId());
		newDescription.setCaseSignificance(new SnomedConceptLookupService().getComponent(bean.getCaseSensitivity().getId(), context.getTransaction()));
		concept.getDescriptions().add(newDescription);

		
		//specify language reference set member acceptability type
		final String acceptibilityId;
		if (bean.isPreferred()) {
			acceptibilityId = PREFERRED;
		} else {
			if (FULLY_SPECIFIED_NAME.equals(selectedType.getId())) {
				acceptibilityId = PREFERRED;
			} else {
				acceptibilityId = ACCEPTABLE;
			}
		}
		//at this point we have to remove all new language type reference set members from the transaction
		//can happen if we create either a new sibling or child and we create the proper reference set 
		//members with editing context and modify the description term
		for (final SnomedLanguageRefSetMember member : getLanguageMembersForDescription(context, newDescription, acceptibilityId)) {
			SnomedModelExtensions.removeOrDeactivate(member);
		}
		
		
		//create language reference set member
		//no existing language reference set member update is required since this is a new description
		final SnomedRefSetEditingContext refSetEditingContext = context.getRefSetEditingContext();
		final SnomedStructuralRefSet languageRefSet = getLanguageRefSet(context);
		final ComponentIdentifierPair<String> acceptibilityPair = SnomedRefSetEditingContext.createConceptTypePair(acceptibilityId);
		final ComponentIdentifierPair<String> referencedComponentPair = SnomedRefSetEditingContext.createDescriptionTypePair(newDescription.getId());
		final String moduleId = getDefaultModuleId(context);
		final SnomedLanguageRefSetMember newMember = refSetEditingContext.createLanguageRefSetMember(referencedComponentPair, acceptibilityPair, moduleId, languageRefSet);
		newDescription.getLanguageRefSetMembers().add(newMember);

		if (bean.isPreferred()) {
			//CDO ID is not available for the language type reference set member yet.
			//we have to update the ID every time updateConcept is invoked and the context is dirty
			preferredTermMemberPair.put(newDescription.getId(), new IdStorageKeyPair(newMember.getUuid(), -1L));
		}
		
		//update description ID on the bean.
		bean.setSctId(Long.valueOf(newDescription.getId()));
	}

	/*returns with the currently selected language type reference set*/
	private SnomedStructuralRefSet getLanguageRefSet(final SnomedEditingContext context) {
		return (SnomedStructuralRefSet) new SnomedRefSetLookupService().getComponent(getLanguageRefSetId(), context.getTransaction());
	}

	/*returns with the currently selected language type reference set identifier concept ID*/
	private String getLanguageRefSetId() {
		return languageRefSetId;
	}

	private Set<SnomedLanguageRefSetMember> getLanguageMembersForDescription(final SnomedEditingContext context, final Description description, final String acceptabilityId) {
		final Set<SnomedLanguageRefSetMember> members = Sets.newHashSet();
		final String languageRefSetId = getLanguageRefSetId();
		final Collection<SnomedRefSetMemberIndexEntry> languageMembers = new SnomedRefSetMembershipLookupService().getLanguageMembers(description.getConcept());
		for (final SnomedRefSetMemberIndexEntry languageMember : languageMembers) {
			if (!languageMember.isActive()) {
				continue;
			}
			if (!acceptabilityId.equals(languageMember.getAcceptabilityId())) {
				continue;
			}
			if (!languageRefSetId.equals(languageMember.getRefSetIdentifierId())) {
				continue;
			}
			if (!description.getId().equals(languageMember.getReferencedComponentId())) {
				continue;
			}
			
			final EObject object = context.lookupIfExists(languageMember.getStorageKey());
			if (object instanceof SnomedLanguageRefSetMember) {
				
				//has to double check CDO object since they might changed meanwhile
				final SnomedLanguageRefSetMember member = (SnomedLanguageRefSetMember) object;
				if (CDOState.TRANSIENT.equals((member).cdoState())) { //if it has been deleted
					continue;
				}
				
				if (!member.isActive()) {
					continue;
				}
				if (!acceptabilityId.equals(member.getAcceptabilityId())) {
					continue;
				}
				if (!languageRefSetId.equals(member.getRefSetIdentifierId())) {
					continue;
				}
				
				members.add(member);
			}
		}
		
		//then we have to add the new members from the transaction
		for (final SnomedLanguageRefSetMember member : description.getLanguageRefSetMembers()) {
			
			if (!CDOState.NEW.equals(member.cdoState())) {
				continue;
			}
			
			if (!member.isActive()) {
				continue;
			}
			if (!acceptabilityId.equals(member.getAcceptabilityId())) {
				continue;
			}
			if (!languageRefSetId.equals(member.getRefSetIdentifierId())) {
				continue;
			}
			if (!description.getId().equals(member.getReferencedComponentId())) {
				continue;
			}
			
			members.add(member);
		}
		
		
		return members;
	}
	
	private void removeOrDeactivate(final SnomedEditingContext context, final Description existingDescription) {
		
		// Remove or deactivate depending on the deletion plan 
		final SnomedDeletionPlan plan = context.canDelete(existingDescription, null);
		
		for (final SnomedLanguageRefSetMember member : Lists.newArrayList(existingDescription.getLanguageRefSetMembers())) {
			SnomedModelExtensions.removeOrDeactivate(member);
		}
		
		if (!plan.isRejected()) {
			context.delete(plan);
		} else {
			if (CDOState.NEW.equals(existingDescription.cdoState())) {
				// rejected because last FSN of the concept, although it can be removed since does not persisted yet. so we delete it
				SnomedModelExtensions.remove(existingDescription);
			} else {
				SnomedModelExtensions.deactivate(existingDescription);
			}
		}
	}
	
	private Description findDescription(final SnomedEditingContext context, final Concept concept, final String descriptionId) {
		final Description candidate = findDescriptionInTransaction(concept.getDescriptions(), descriptionId);
		return (candidate != null) ? candidate : new SnomedDescriptionLookupService().getComponent(descriptionId, context.getTransaction());
	}

	private Description findDescriptionInTransaction(final Collection<? extends CDOObject> transactionValues, final String descriptionId) {
		
		for (final CDOObject transactionObject : transactionValues) {
			if (transactionObject instanceof Description) {
				final Description description = (Description) transactionObject;
				if (description.getId().equals(descriptionId)) {
					return description;
				}
			}
		}
		
		return null;
	}
	
	private void updateRelationships(final SnomedEditingContext context, final ConceptWidgetBean widgetBean, final Concept concept) {

		final Set<Relationship> currentRelationships = Sets.newHashSet(concept.getOutboundRelationships());
		
		for (final RelationshipGroupWidgetBean groupBean : Iterables.filter(widgetBean.getProperties().getElements(), RelationshipGroupWidgetBean.class)) {
			
			for (final RelationshipWidgetBean relationshipBean : Iterables.filter(groupBean.getElements(), RelationshipWidgetBean.class)) {
				
				final long relationshipId = relationshipBean.getSctId();
				final IComponent<String> selectedCharacteristicType = relationshipBean.getSelectedCharacteristicType();
				final IComponent<String> selectedType = relationshipBean.getSelectedType();
				final IComponent<String> selectedValue = relationshipBean.getSelectedValue();
				
				if (relationshipId == RelationshipWidgetBean.UNINITIALIZED) {
					
					// Create a new relationship if all fields are populated
					if (selectedType != NullComponent.<String>getNullImplementation() && selectedValue != NullComponent.<String>getNullImplementation() && selectedCharacteristicType != NullComponent.<String>getNullImplementation()) {
						addRelationship(context, concept, relationshipBean);
					}
					
				} else {

					final Relationship existingRelationship = findRelationship(context, concept, Long.toString(relationshipId));

					if (selectedValue == null
							|| !existingRelationship.getType().getId().equals(selectedType.getId())
							|| !existingRelationship.getDestination().getId().equals(selectedValue.getId())
							|| !existingRelationship.getCharacteristicType().getId().equals(selectedCharacteristicType.getId())) {
						
						SnomedModelExtensions.removeOrDeactivate(existingRelationship);
						relationshipBean.setSctId(RelationshipWidgetBean.UNINITIALIZED);
						
						if (selectedValue != NullComponent.<String>getNullImplementation() && selectedType != NullComponent.<String>getNullImplementation() && selectedCharacteristicType != NullComponent.<String>getNullImplementation()) {
							addRelationship(context, concept, relationshipBean);
						}
					}
					
					currentRelationships.remove(existingRelationship);
				}
			}
		}
		
		// Remove remaining relationships because they were removed from the widget bean tree
		for (final Relationship remainingRelationship : currentRelationships) {
			SnomedModelExtensions.removeOrDeactivate(remainingRelationship);
		}
	}

	private void addRelationship(final SnomedEditingContext context, final Concept concept, final RelationshipWidgetBean relationshipBean) {
		
		final IComponent<String> selectedType = relationshipBean.getSelectedType();
		final IComponent<String> selectedValue = relationshipBean.getSelectedValue();
		final IComponent<String> selectedCharacteristicType = relationshipBean.getSelectedCharacteristicType();
		final int groupNumber = relationshipBean.getParent().getGroupNumber();
		
		final SnomedConceptLookupService lookupService = new SnomedConceptLookupService();
		final Concept convertedType = lookupService.getComponent(selectedType.getId(), context.getTransaction());
		final Concept convertedValue = lookupService.getComponent(selectedValue.getId(), context.getTransaction());
		final Concept convertedCharacteristicType = lookupService.getComponent(selectedCharacteristicType.getId(), context.getTransaction());
		
		final Relationship newRelationship = context.buildDefaultRelationship(concept, convertedType, convertedValue, 
				convertedCharacteristicType);
		
		newRelationship.setGroup(groupNumber);
		relationshipBean.setSctId(Long.valueOf(newRelationship.getId()));
		
		concept.getOutboundRelationships().add(newRelationship);
	}

	private Relationship findRelationship(final SnomedEditingContext context, final Concept concept, final String relationshipId) {
		final Relationship candidate = findRelationshipInTransaction(concept.getOutboundRelationships(), relationshipId);
		return (candidate != null) ? candidate : new SnomedRelationshipLookupService().getComponent(relationshipId, context.getTransaction());
	}

	private Relationship findRelationshipInTransaction(final Collection<? extends CDOObject> transactionValues, final String relationshipId) {
		
		for (final CDOObject transactionObject : transactionValues) {
			if (transactionObject instanceof Relationship) {
				final Relationship Relationship = (Relationship) transactionObject;
				if (Relationship.getId().equals(relationshipId)) {
					return Relationship;
				}
			}
		}
		
		return null;
	}

	private void updateDataTypes(final SnomedEditingContext context, final ConceptWidgetBean widgetBean, final Concept concept, final String moduleId) {
		
		final List<SnomedConcreteDataTypeRefSetMember> unvisitedDataTypes = Lists.newArrayList(concept.getConcreteDomainRefSetMembers());
		final ContainerWidgetBean ungroupedContainer = (ContainerWidgetBean) widgetBean.getProperties().getElements().get(0);
		
		final Iterable<DataTypeWidgetBean> beans = Iterables.concat(
				Iterables.filter(widgetBean.getDescriptions().getElements(), DataTypeWidgetBean.class),
				Iterables.filter(ungroupedContainer.getElements(), DataTypeWidgetBean.class)); 
				
		for (final DataTypeWidgetBean dataTypeBean : beans) {
			
			final String uuid = dataTypeBean.getUuid();
			final String selectedLabel = dataTypeBean.getSelectedLabel();
			final String selectedValue = dataTypeBean.getSelectedValue();
			
			if (DataTypeWidgetBean.UNINITIALIZED.equals(uuid)) {
				
				// Create a new data type entry if all fields are populated
				if (!StringUtils.isEmpty(selectedLabel) && !StringUtils.isEmpty(selectedValue)) {
					addDataType(context, concept, dataTypeBean, moduleId);
				}
			
			} else {
				
				final SnomedConcreteDataTypeRefSetMember existingMember = findDataType(context, uuid, unvisitedDataTypes);
				
				if (hasLabelChanges(selectedLabel, existingMember.getLabel()) || hasValueChanges(existingMember.getDataType(), selectedValue, existingMember.getSerializedValue())) {
					
					SnomedModelExtensions.removeOrDeactivate(existingMember);
					dataTypeBean.setUuid(DataTypeWidgetBean.UNINITIALIZED);
					
					// Consider creating a new data type member in case of a change in value
					if (!StringUtils.isEmpty(selectedLabel) && !StringUtils.isEmpty(selectedValue)) {
						addDataType(context, concept, dataTypeBean, moduleId);
					}
				}
				
				for (final Iterator<SnomedConcreteDataTypeRefSetMember> itr = unvisitedDataTypes.iterator(); itr.hasNext(); /* nothing */) {
					if (itr.next().getUuid().equals(uuid)) {
						itr.remove();
						break;
					}
				}
			}
		}
		
		// Remove remaining active data types because they didn't show up in the widget bean tree
		for (final SnomedConcreteDataTypeRefSetMember entry : Lists.newArrayList(unvisitedDataTypes)) {
			
			if (entry.isActive()) {
				final SnomedConcreteDataTypeRefSetMember existingMember = findDataType(context, entry.getUuid(), unvisitedDataTypes);
				SnomedModelExtensions.removeOrDeactivate(existingMember);
			}
		}
	}

	private boolean hasLabelChanges(String newLabel, String oldLabel) {
		return !Objects.equal(newLabel, oldLabel);
	}

	private boolean hasValueChanges(final DataType dataType, final String newValue, final String oldValue) {
		return StringUtils.isEmpty(newValue) || !SnomedRefSetUtil.deserializeValue(dataType, oldValue).equals(SnomedRefSetUtil.deserializeValue(dataType, newValue));
	}

	private SnomedConcreteDataTypeRefSetMember findDataType(final SnomedEditingContext context, final String uuid, 
			final List<SnomedConcreteDataTypeRefSetMember> dataTypeMemberIndexEntries) {
		
		final SnomedConcreteDataTypeRefSetMember candidateNewMember = findDataTypeInCollection(
				ComponentUtils2.getNewObjects(context.getTransaction(), SnomedConcreteDataTypeRefSetMember.class), uuid);
		
		if (candidateNewMember != null) {
			return candidateNewMember;
		}
		
		for (final SnomedConcreteDataTypeRefSetMember entry : dataTypeMemberIndexEntries) {
			if (entry.getUuid().equals(uuid)) {
				return (SnomedConcreteDataTypeRefSetMember) context.lookup(CDOIDUtil.getLong(entry.cdoID()));
			}
		}
		
		throw new IllegalStateException(MessageFormat.format("Could not find concrete domain reference set member with UUID {0}.", uuid));
	}
	
	private SnomedConcreteDataTypeRefSetMember findDataTypeInCollection(final Iterable<SnomedConcreteDataTypeRefSetMember> members, final String uuid) {
		for (final SnomedConcreteDataTypeRefSetMember member : members) {
			if (member.getUuid().equals(uuid)) {
				return member;
			}
		}
		
		return null;
	}

	private void addDataType(final SnomedEditingContext context, final Concept concept, final DataTypeWidgetBean dataTypeBean, final String moduleId) {
		
		final DataType ecoreDataType = dataTypeBean.getAllowedType();
		
		final Object serializedValue = SnomedRefSetUtil.deserializeValue(ecoreDataType, 
				dataTypeBean.getSelectedValue());
		
		final String characteristicType = dataTypeBean.getCharacteristicType();
		final ComponentIdentifierPair<String> identifierPair = ComponentIdentifierPair.create(
				SnomedTerminologyComponentConstants.CONCEPT_NUMBER, concept.getId());
		
		final ILookupService<String, SnomedRefSet, CDOView> lookupService = CoreTerminologyBroker.getInstance().getLookupService(SnomedTerminologyComponentConstants.REFSET);
		final String refSetId = SnomedRefSetUtil.DATATYPE_TO_REFSET_MAP.get(ecoreDataType);
		final SnomedRefSet refSet = lookupService.getComponent(refSetId, context.getTransaction());
		
		
		if (refSet instanceof SnomedConcreteDataTypeRefSet) {
			
			final SnomedConcreteDataTypeRefSet dataTypeRefSet = (SnomedConcreteDataTypeRefSet) refSet;
			final SnomedConcreteDataTypeRefSetMember newMember = context.getRefSetEditingContext().createConcreteDataTypeRefSetMember(
					identifierPair, 
					ecoreDataType, 
					serializedValue,
					characteristicType,
					dataTypeBean.getSelectedLabel(), moduleId,
					dataTypeRefSet);
			
			concept.getConcreteDomainRefSetMembers().add(newMember);
			dataTypeBean.setUuid(newMember.getUuid());
			
		} else {
			throw new IllegalArgumentException("Could not find concrete domain reference set for ID " + refSetId);
		}
	}
}
