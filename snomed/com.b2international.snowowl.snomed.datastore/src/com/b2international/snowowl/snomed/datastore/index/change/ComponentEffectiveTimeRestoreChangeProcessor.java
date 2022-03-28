/*
 * Copyright 2019-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.change;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.UnexpectedTypeException;

import org.slf4j.Logger;

import com.b2international.commons.ClassUtils;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.repository.EffectiveTimeChangeProcessorBase;
import com.b2international.snowowl.snomed.datastore.index.entry.*;
import com.google.common.collect.*;

/**
 * @since 7.1
 */
public final class ComponentEffectiveTimeRestoreChangeProcessor extends EffectiveTimeChangeProcessorBase {

	private final Logger log;

	protected ComponentEffectiveTimeRestoreChangeProcessor(Logger log) {
		super("effective time restore");
		this.log = log;
	}

	@Override
	public void process(StagingArea staging, RevisionSearcher searcher) throws IOException {
		final Multimap<Class<? extends SnomedDocument>, SnomedDocument> componentsByType = ArrayListMultimap.create();
		staging.getChangedObjects()
			.filter(SnomedDocument.class::isInstance)
			.map(SnomedDocument.class::cast)
			.filter(doc -> doc.isReleased() && EffectiveTimes.isUnset(doc.getEffectiveTime()))
			.forEach(doc -> componentsByType.put(doc.getClass(), doc));

		if (componentsByType.isEmpty())	{
			return;
		}
		
		final RepositoryContext context = ClassUtils.checkAndCast(staging.getContext(), RepositoryContext.class);
		final List<String> branchesForPreviousVersion = getAvailableVersionPaths(context, staging.getBranchPath());
		if (branchesForPreviousVersion.isEmpty()) {
			return;
		}
		
		final Multimap<Class<? extends SnomedDocument>, String> componentHadPreviousVersionOnAnyBranch = ArrayListMultimap.create();
		
		for (String branchToCheck : branchesForPreviousVersion) {
			for (Class<? extends SnomedDocument> componentType : ImmutableSet.copyOf(componentsByType.keySet())) {
				final Set<String> componentIds = componentsByType.get(componentType).stream().map(SnomedDocument::getId).collect(Collectors.toSet());
				final Map<String, ? extends SnomedDocument> previousVersions = Maps.uniqueIndex(fetchPreviousComponentRevisions(staging.getIndex(), branchToCheck, componentType, componentIds), SnomedDocument::getId);
				for (SnomedDocument changedRevision : ImmutableList.copyOf(componentsByType.get(componentType))) {
					final SnomedDocument previousVersion = previousVersions.get(changedRevision.getId());
					if (previousVersion != null) {
						if (canRestoreEffectiveTime(changedRevision, previousVersion)) {
							SnomedDocument restoredRevision = toBuilder(changedRevision).effectiveTime(previousVersion.getEffectiveTime()).build();
							stageChange(changedRevision, restoredRevision);
							// successfully restored, remove from remaining item list
							componentsByType.remove(componentType, changedRevision);
						} else {
							// register as a component that had an earlier version and can be ignored from the warning message beneath even if there were no prev versions to restore ET from
							componentHadPreviousVersionOnAnyBranch.put(componentType, changedRevision.getId());
						}
					}
				}
			}
		}
		
		// after checking all branches, clear everything that had at least one previous version, report anything that remains as released content without previous version
		componentHadPreviousVersionOnAnyBranch.forEach((componentType, changedRevisionId) -> {
			componentsByType.remove(componentType, changedRevisionId);
		});
		
		if (!componentsByType.isEmpty()) {
			log.warn("There were components which could not be restored, {}.", 
				componentsByType.values()
					.stream()
					.map(SnomedDocument::getId)
					.collect(Collectors.toSet())
			);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <B extends SnomedDocument.Builder<B, T>, T extends SnomedDocument> B toBuilder(T doc) {
		if (doc instanceof SnomedConceptDocument) {
			return (B) SnomedConceptDocument.builder((SnomedConceptDocument) doc);
		} else if (doc instanceof SnomedDescriptionIndexEntry) {
			return (B) SnomedDescriptionIndexEntry.builder((SnomedDescriptionIndexEntry) doc);
		} else if (doc instanceof SnomedRelationshipIndexEntry) {
			return (B) SnomedRelationshipIndexEntry.builder((SnomedRelationshipIndexEntry) doc);
		} else if (doc instanceof SnomedRefSetMemberIndexEntry) {
			return (B) SnomedRefSetMemberIndexEntry.builder((SnomedRefSetMemberIndexEntry) doc);
		} else {
			throw new UnsupportedOperationException("Not implemented for: " + doc);
		}
	}

	private boolean canRestoreEffectiveTime(SnomedDocument componentToRestore, SnomedDocument previousVersion) {
		if (componentToRestore.isActive() != previousVersion.isActive()) {
			return false;
		}
		
		if (!Objects.equals(componentToRestore.getModuleId(), previousVersion.getModuleId())) {
			return false;
		} 
		
		if (componentToRestore instanceof SnomedConceptDocument && previousVersion instanceof SnomedConceptDocument) {
			final SnomedConceptDocument conceptToRestore = (SnomedConceptDocument) componentToRestore;
			final SnomedConceptDocument previousConcept = (SnomedConceptDocument) previousVersion;
			
			return canRestoreEffectiveTime(conceptToRestore, previousConcept, 
					SnomedConceptDocument::isPrimitive);
		}
		
		if (componentToRestore instanceof SnomedDescriptionIndexEntry && previousVersion instanceof SnomedDescriptionIndexEntry) {
			final SnomedDescriptionIndexEntry descriptionToRestore = (SnomedDescriptionIndexEntry) componentToRestore;
			final SnomedDescriptionIndexEntry previousDescription = (SnomedDescriptionIndexEntry) previousVersion;
			
			return canRestoreEffectiveTime(descriptionToRestore, previousDescription, 
					SnomedDescriptionIndexEntry::getTerm,
					SnomedDescriptionIndexEntry::getCaseSignificanceId);
		}
		
		if (componentToRestore instanceof SnomedRelationshipIndexEntry && previousVersion instanceof SnomedRelationshipIndexEntry) {
			final SnomedRelationshipIndexEntry relationshipToRestore = (SnomedRelationshipIndexEntry) componentToRestore;
			final SnomedRelationshipIndexEntry previousRelationship = (SnomedRelationshipIndexEntry) previousVersion;
			
			return canRestoreEffectiveTime(relationshipToRestore, previousRelationship, 
					SnomedRelationshipIndexEntry::getGroup,
					SnomedRelationshipIndexEntry::getUnionGroup,
					SnomedRelationshipIndexEntry::getCharacteristicTypeId,
					SnomedRelationshipIndexEntry::getModifierId);
		}
		
		if (componentToRestore instanceof SnomedRefSetMemberIndexEntry && previousVersion instanceof SnomedRefSetMemberIndexEntry) {
			final SnomedRefSetMemberIndexEntry memberToRestore = (SnomedRefSetMemberIndexEntry) componentToRestore;
			final SnomedRefSetMemberIndexEntry previousMember = (SnomedRefSetMemberIndexEntry) previousVersion;
			
			final boolean additionalFieldsChanged = memberToRestore.getAdditionalFields()
					.entrySet()
					.stream()
					.anyMatch(entry -> !Objects.equals(
							entry.getValue(), 
							previousMember.getAdditionalFields().get(entry.getKey())));

			// Effective time can _not_ be restored if any of the additional fields mismatched
			return !additionalFieldsChanged;
		}
		
		throw new UnexpectedTypeException("Unexpected component type '" + componentToRestore.getClass() + "'.");
	}
	
	@SafeVarargs
	private <T extends SnomedDocument> boolean canRestoreEffectiveTime(T current, T previous, Function<T, Object>... accessors) {
		for (final Function<T, Object> accessor : accessors) {
			if (!Objects.equals(accessor.apply(current), accessor.apply(previous))) {
				return false;
			}
		}
		
		return true;
	}
	
	private Iterable<? extends SnomedDocument> fetchPreviousComponentRevisions(RevisionIndex index, String branch, Class<? extends SnomedDocument> componentType, Set<String> ids) {
		return index.read(branch, searcher -> searcher.get(componentType, ids));
	}

}
