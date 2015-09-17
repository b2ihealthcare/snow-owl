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
package com.b2international.snowowl.datastore.server.snomed.index.change;

import static java.lang.Long.parseLong;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyMapIterator;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.datastore.index.ComponentCompareFieldsUpdater;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedComponentLabelCollector;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedIndexServerService;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetMemberLookupService;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.update.ComponentLabelProvider;
import com.b2international.snowowl.snomed.datastore.index.update.ComponentLabelUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.ConceptLabelUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.RefSetMemberLabelUpdater;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * Map for storing concept IDs and associated new labels. Basically this map will contain PTs as values. We will track only new language reference set
 * member with preferred acceptability.
 * 
 * @since 4.3
 */
public class ComponentLabelChangeProcessor extends ChangeSetProcessorBase<SnomedDocumentBuilder> implements ComponentLabelProvider {

	/**
	 * Threshold which modifies the current {@link ConceptLabelProvider concept label provider} behavior.
	 * <br>Threshold: {@value}. 
	 */
	private static final int THRESHOLD = 1000;
	private LoadingCache<String, String> labelCache;
	private SnomedIndexServerService index;
	private IBranchPath branchPath;
	/**
	 * Predicate for returning {@code true} only and if only the processed SNOMED&nbsp;CT reference set
	 * member is *NOT* concrete data type and the referenced component is a SNOMED&nbsp;CT concept.
	 */
	private final Predicate<SnomedRefSetMember> PREDICATE = new Predicate<SnomedRefSetMember>() {
		@Override public boolean apply(final SnomedRefSetMember member) {
			Preconditions.checkNotNull(member, "SNOMED CT reference set member argument cannot be null.");
			if (member instanceof SnomedConcreteDataTypeRefSetMember) {
				return false;
			}
			return SnomedTerminologyComponentConstants.CONCEPT_NUMBER == member.getReferencedComponentType()
					|| SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER == member.getReferencedComponentType();
		}
	};

	public ComponentLabelChangeProcessor(IBranchPath branchPath, SnomedIndexServerService index) {
		super("label changes");
		this.index = index;
		this.branchPath = branchPath;
		final ISnomedComponentService componentService = ApplicationContext.getInstance().getService(ISnomedComponentService.class);
		this.labelCache = CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {
			@Override
			public String load(String key) throws Exception {
				return componentService.getLabels(getBranchPath(), key)[0];
			}
		});
	}
	
	private IBranchPath getBranchPath() {
		return branchPath;
	}
	
	@Override
	public void process(ICDOCommitChangeSet commitChangeSet) {
		this.populateExistingLabels(commitChangeSet);
		this.populateNewLabels(commitChangeSet);
		
		final SnomedRefSetMemberLookupService refSetMembershipLookupService = new SnomedRefSetMemberLookupService();
		
		// update label on concept and referring reference set members
		for (SnomedLanguageRefSetMember member : getNewComponents(commitChangeSet, SnomedLanguageRefSetMember.class)) {
			if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(member.getAcceptabilityId()) && member.eContainer() instanceof Description) {
				final Description description = (Description) member.eContainer();
				if (!Concepts.FULLY_SPECIFIED_NAME.equals(description.getType().getId())) {
					final Concept relatedConcept = description.getConcept();
					final String conceptId = relatedConcept.getId();
					
					// register label update for concept and their reference set membes
					final String newLabel = getComponentLabel(conceptId);
					registerUpdate(conceptId, new ConceptLabelUpdater(conceptId, newLabel));
					registerUpdate(conceptId, new ComponentCompareFieldsUpdater<SnomedDocumentBuilder>(conceptId, CDOIDUtil.getLong(relatedConcept.cdoID())));
					
					final Collection<SnomedRefSetMemberIndexEntry> referringMembers = index.search(branchPath, SnomedRefSetMembershipIndexQueryAdapter.createFindReferencingMembers(conceptId));
					for (SnomedRefSetMemberIndexEntry entry : referringMembers) {
						final SnomedRefSetMember entryMember = refSetMembershipLookupService.getComponent(entry.getId(), relatedConcept.cdoView());
						if (entryMember != null) {
							registerUpdate(entryMember.getUuid(), new RefSetMemberLabelUpdater(entryMember, newLabel, this));
						}
					}
				}
			}
		}
		
		for (SnomedRefSetMember member : getNewComponents(commitChangeSet, SnomedRefSetMember.class)) {
			if (member instanceof SnomedConcreteDataTypeRefSetMember) {
				registerUpdate(member.getUuid(), new RefSetMemberLabelUpdater(member, ((SnomedConcreteDataTypeRefSetMember)member).getLabel(), this));
			} else {
				final String label = getComponentLabel(member.getReferencedComponentId());
				registerUpdate(member.getUuid(), new RefSetMemberLabelUpdater(member, label, this));
			}
		}
		
		// update label on reference set members referring descriptions
		for (Description description : getNewComponents(commitChangeSet, Description.class)) {
			registerUpdate(description.getId(), new ComponentLabelUpdater<SnomedDocumentBuilder>(description.getId(), Strings.nullToEmpty(description.getTerm())));
		}
		
	}

	private void populateExistingLabels(ICDOCommitChangeSet commitChangeSet) {
		final Iterable<SnomedRefSetMember> members = FluentIterable.from(Iterables.concat(commitChangeSet.getNewComponents(), commitChangeSet.getDirtyComponents())).filter(SnomedRefSetMember.class).filter(PREDICATE);
		final Iterable<Concept> concepts = FluentIterable.from(commitChangeSet.getDirtyComponents()).filter(Concept.class);
		
		// init concept and member label changes
		final int labelChangeSize = Iterables.size(concepts) + Iterables.size(members);
		if (labelChangeSize > THRESHOLD) {
			final LongSet conceptIds = new LongOpenHashSet(labelChangeSize);
			
			for (final SnomedRefSetMember member : members) {
				conceptIds.add(parseLong(member.getReferencedComponentId()));
			}
			
			for (final Concept concept : concepts) {
				conceptIds.add(parseLong(concept.getId()));
			}
			
			final SnomedComponentLabelCollector collector = new SnomedComponentLabelCollector(conceptIds);
			
			//get labels
			index.search(branchPath, SnomedMappings.newQuery().concept().matchAll(), collector);
			final LongKeyMap idLabels = collector.getIdLabelMapping();
			for (final LongKeyMapIterator itr = idLabels.entries(); itr.hasNext(); /**/) {
				itr.next();
				labelCache.put(
						Long.toString(itr.getKey()), //ID
						String.valueOf(itr.getValue())); //label
			}
		}
		getComponentLabel(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE);
		getComponentLabel(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED);
	}

	@Override
	public String getComponentLabel(String componentId) {
		return labelCache.getUnchecked(componentId);
	}

	/* calculates the new label for concepts and descriptions, if any */
	// we are ignoring detached language members
	// first: according to SNOMED CT TIG we have to create a new member either the previous one is retired/detached
	// second: makes no sense to remove the PT from the concept
	private void populateNewLabels(ICDOCommitChangeSet commitChangeSet) {

		final Iterable<SnomedLanguageRefSetMember> languageMembers = FluentIterable.from(commitChangeSet.getNewComponents()).filter(
				SnomedLanguageRefSetMember.class);
		
		final Iterable<Description> newAndDirtyDescriptions = FluentIterable.from(
				Iterables.concat(commitChangeSet.getNewComponents(), commitChangeSet.getDirtyComponents())).filter(Description.class);

		// initialize lazily
		final Map<String, Description> descriptionsById = Maps.uniqueIndex(newAndDirtyDescriptions, new Function<Description, String>() {
			@Override
			public String apply(final Description description) {
				return Preconditions.checkNotNull(description, "Description argument cannot be null").getId();
			}
		});

		for (final SnomedLanguageRefSetMember member : languageMembers) {
			final String descriptionId = member.getReferencedComponentId();
			Description description = descriptionsById.get(descriptionId);
			// could happen that description has not changed in transaction
			if (null == description) {
				description = new SnomedDescriptionLookupService().getComponent(descriptionId, member.cdoView());
			}

			Preconditions.checkNotNull(description, "Cannot find description. ID: " + descriptionId);

			labelCache.put(descriptionId, description.getTerm());

			if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(member.getAcceptabilityId())) {
				// we are ignoring FSNs, as they could replace real PT descriptions for concepts.
				if (Concepts.FULLY_SPECIFIED_NAME.equals(description.getType().getId())) {
					continue;
				}

				final String conceptId = description.getConcept().getId();
				labelCache.put(conceptId, description.getTerm());
			}
		}
	}
	
}
