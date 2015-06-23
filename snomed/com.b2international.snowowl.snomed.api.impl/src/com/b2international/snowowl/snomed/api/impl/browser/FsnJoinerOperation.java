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
package com.b2international.snowowl.snomed.api.impl.browser;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.impl.domain.InternalComponentRef;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.api.ISnomedDescriptionService;
import com.b2international.snowowl.snomed.api.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.*;

/**
 * Given a set of {@link SnomedConceptIndexEntry concept index entries}, collects the corresponding "preferred" fully
 * specified names of each entry, and makes them available for converting to a response object.
 */
public abstract class FsnJoinerOperation<T> {

	private final IComponentRef conceptRef;
	private final List<Locale> locales;
	private final ISnomedDescriptionService descriptionService;

	protected IBranchPath branchPath;
	
	private Collection<SnomedConceptIndexEntry> conceptEntries;
	private Multimap<String, SnomedDescriptionIndexEntry> fsnsByConcept;
	private Table<String, String, Acceptability> descriptionAcceptability;
	private ImmutableBiMap<Locale, String> languageIdMap;

	protected static SnomedIndexService getIndexService() {
		return ApplicationContext.getServiceForClass(SnomedIndexService.class);
	}

	protected static SnomedTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getServiceForClass(SnomedTerminologyBrowser.class);
	}

	/**
	 * @param conceptRef
	 * @param locales
	 * @param descriptionService
	 */
	protected FsnJoinerOperation(final IComponentRef conceptRef, final List<Locale> locales, final ISnomedDescriptionService descriptionService) {

		this.conceptRef = conceptRef;
		this.locales = locales;
		this.descriptionService = descriptionService;
	}

	public final List<T> run() {
		initConceptEntries();
		initFsnDataStructures();
		return convertConceptEntries();
	}

	private void initConceptEntries() {

		final InternalComponentRef internalConceptRef = ClassUtils.checkAndCast(conceptRef, InternalComponentRef.class);
		internalConceptRef.checkStorageExists();
		branchPath = internalConceptRef.getBranch().branchPath();

		final String conceptId = conceptRef.getComponentId();

		if (!getTerminologyBrowser().exists(branchPath, conceptId)) {
			throw new ComponentNotFoundException(ComponentCategory.CONCEPT, conceptId);
		}

		conceptEntries = getConceptEntries(conceptId);
	}

	private void initFsnDataStructures() {

		final SnomedDescriptionIndexQueryAdapter fsnsQuery = SnomedDescriptionIndexQueryAdapter.createFindFsnByConceptIds(ComponentUtils.getIdSet(conceptEntries));
		final Collection<SnomedDescriptionIndexEntry> fsnsIndexEntries = getIndexService().searchUnsorted(branchPath, fsnsQuery);
		fsnsByConcept = Multimaps.index(fsnsIndexEntries, new Function<SnomedDescriptionIndexEntry, String>() {
			@Override public String apply(final SnomedDescriptionIndexEntry input) {
				return input.getConceptId();
			}
		});

		descriptionAcceptability = HashBasedTable.create();
		languageIdMap = descriptionService.getLanguageIdMap(locales, branchPath);

		for (final Locale locale : locales) {
			final String languageRefSetId = languageIdMap.get(locale);

			if (languageRefSetId != null) {
				final SnomedRefSetMembershipIndexQueryAdapter languageMembersQuery = SnomedRefSetMembershipIndexQueryAdapter.createFindAllLanguageMembersQuery(ComponentUtils.getIds(fsnsIndexEntries), languageRefSetId);
				final Collection<SnomedRefSetMemberIndexEntry> languageMemberEntries = getIndexService().searchUnsorted(branchPath, languageMembersQuery);

				for (final SnomedRefSetMemberIndexEntry languageMemberEntry : languageMemberEntries) {
					final Acceptability acceptability = Acceptability.getByConceptId(languageMemberEntry.getSpecialFieldId());
					descriptionAcceptability.put(languageMemberEntry.getReferencedComponentId(), languageMemberEntry.getRefSetIdentifierId(), acceptability);
				}
			}
		}
	}

	private List<T> convertConceptEntries() {
		final ImmutableList.Builder<T> resultBuilder = ImmutableList.builder();

		for (final SnomedConceptIndexEntry conceptEntry : conceptEntries) {
			resultBuilder.add(convertConceptEntry(conceptEntry, getFsn(conceptEntry.getId())));
		}

		return resultBuilder.build();
	}

	private Optional<String> getFsn(final String conceptId) {
		final Collection<SnomedDescriptionIndexEntry> fsnEntries = fsnsByConcept.get(conceptId);

		for (final Locale locale : locales) {
			final String languageRefSetId = languageIdMap.get(locale);
			for (final SnomedDescriptionIndexEntry indexEntry : fsnEntries) {
				if (Acceptability.PREFERRED.equals(descriptionAcceptability.get(indexEntry.getId(), languageRefSetId))) {
					return Optional.of(indexEntry.getLabel());
				}
			}
		}

		// FIXME: check language codes when it becomes available on the index entry
		return FluentIterable.from(fsnEntries).first().transform(ComponentUtils.getLabelFunction());
	}

	protected abstract Collection<SnomedConceptIndexEntry> getConceptEntries(String conceptId);

	protected abstract T convertConceptEntry(SnomedConceptIndexEntry conceptEntry, Optional<String> optionalFsn);
}
