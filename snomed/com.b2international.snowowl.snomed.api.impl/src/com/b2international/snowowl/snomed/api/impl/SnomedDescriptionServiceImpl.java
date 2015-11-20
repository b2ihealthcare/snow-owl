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
package com.b2international.snowowl.snomed.api.impl;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponentRef;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.server.domain.InternalComponentRef;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedConstants.LanguageCodeReferenceSetIdentifierMapping;
import com.b2international.snowowl.snomed.api.ISnomedDescriptionService;
import com.b2international.snowowl.snomed.api.exception.FullySpecifiedNameNotFoundException;
import com.b2international.snowowl.snomed.api.exception.PreferredTermNotFoundException;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Longs;

public class SnomedDescriptionServiceImpl 
	extends AbstractSnomedComponentServiceImpl<ISnomedDescription, Description> 
	implements ISnomedDescriptionService {

	private static ISnomedComponentService getSnomedComponentService() {
		return ApplicationContext.getServiceForClass(ISnomedComponentService.class);
	}

	private final SnomedDescriptionLookupService snomedDescriptionLookupService = new SnomedDescriptionLookupService();

	public SnomedDescriptionServiceImpl() {
		super(SnomedDatastoreActivator.REPOSITORY_UUID, ComponentCategory.DESCRIPTION);
	}

	@Override
	protected boolean componentExists(final IComponentRef ref) {
		final InternalComponentRef internalRef = ClassUtils.checkAndCast(ref, InternalComponentRef.class);
		final IBranchPath branch = internalRef.getBranch().branchPath();
		return snomedDescriptionLookupService.exists(branch, internalRef.getComponentId());
	}

	@Override
	public List<ISnomedDescription> readConceptDescriptions(final IComponentRef conceptRef) {
		throw new UnsupportedOperationException();
		// TODO: check that concept exists?

//		final InternalComponentRef internalConceptRef = ClassUtils.checkAndCast(conceptRef, InternalComponentRef.class);
//		final SnomedDescriptionIndexQueryAdapter queryAdapter = SnomedDescriptionIndexQueryAdapter.findByConceptId(internalConceptRef.getComponentId());
//		final IBranchPath branch = internalConceptRef.getBranch().branchPath();
//		
//		final Collection<SnomedDescriptionIndexEntry> descriptionIndexEntries = getIndexService().searchUnsorted(branch, queryAdapter);
//		final Collection<ISnomedDescription> transformedDescriptions = Collections2.transform(descriptionIndexEntries, getDescriptionConverter(branch));
//
//		return SnomedComponentOrdering.id().immutableSortedCopy(transformedDescriptions);
	}

	private Description getDescription(final String descriptionId, final SnomedEditingContext editingContext) {
		return snomedDescriptionLookupService.getComponent(descriptionId, editingContext.getTransaction());
	}

	@Override
	public ISnomedDescription getFullySpecifiedName(final IComponentRef conceptRef, final List<Locale> locales) {
		final List<ISnomedDescription> descriptions = readConceptDescriptions(conceptRef);
		return getFullySpecifiedName(descriptions, conceptRef, locales);
	}

	@Override
	public ISnomedDescription getFullySpecifiedName(final List<ISnomedDescription> descriptions, final IComponentRef conceptRef, final List<Locale> locales) {
		final InternalComponentRef internalRef = ClassUtils.checkAndCast(conceptRef, InternalComponentRef.class);
//		internalRef.checkStorageExists();

		final IBranchPath branch = internalRef.getBranch().branchPath();
		final ImmutableBiMap<Locale, String> languageIdMap = getLanguageIdMap(locales, branch);
		final Multimap<Locale, ISnomedDescription> descriptionsByLocale = HashMultimap.create();

		for (final ISnomedDescription description : descriptions) {
			if (!description.isActive()) {
				continue;
			}

			if (!Concepts.FULLY_SPECIFIED_NAME.equals(description.getTypeId())) {
				continue;
			}

			for (final Entry<String, Acceptability> acceptabilityEntry : description.getAcceptabilityMap().entrySet()) {
				final String languageRefSetId = acceptabilityEntry.getKey();
				if (Acceptability.PREFERRED.equals(acceptabilityEntry.getValue()) && languageIdMap.containsValue(languageRefSetId)) {
					descriptionsByLocale.put(languageIdMap.inverse().get(languageRefSetId), description);
				}
			}
		}

		for (final Locale locale : locales) {
			final Collection<ISnomedDescription> matchingDescriptions = descriptionsByLocale.get(locale);
			if (!matchingDescriptions.isEmpty()) {
				return matchingDescriptions.iterator().next();
			}
		}

		// If we got here, we can pick an FSN by language tag, in order of preference
		final Multimap<String, ISnomedDescription> descriptionsByLanguage = HashMultimap.create();
		for (ISnomedDescription description : descriptions) {
			if (!description.isActive()) {
				continue;
			}

			if (!Concepts.FULLY_SPECIFIED_NAME.equals(description.getTypeId())) {
				continue;
			}

			descriptionsByLanguage.put(description.getLanguageCode(), description);
		}

		for (final Locale locale : locales) {
			final Collection<ISnomedDescription> matchingDescriptions = descriptionsByLanguage.get(locale.getLanguage());
			if (!matchingDescriptions.isEmpty()) {
				return matchingDescriptions.iterator().next();
			}
		}

		// Last resort: pick an active FSN
		for (ISnomedDescription description : descriptions) {
			if (!description.isActive()) {
				continue;
			}

			if (!Concepts.FULLY_SPECIFIED_NAME.equals(description.getTypeId())) {
				continue;
			}

			return description;
		}
		
		throw new FullySpecifiedNameNotFoundException(conceptRef.getComponentId());
	}

	@Override
	public ISnomedDescription getPreferredTerm(final IComponentRef conceptRef, final List<Locale> locales) {
		final List<ISnomedDescription> descriptions = readConceptDescriptions(conceptRef);
		return getPreferredTerm(descriptions, conceptRef, locales);
	}

	@Override
	public ISnomedDescription getPreferredTerm(final List<ISnomedDescription> descriptions, final IComponentRef conceptRef, final List<Locale> locales) {
		final InternalComponentRef internalRef = ClassUtils.checkAndCast(conceptRef, InternalComponentRef.class);
		internalRef.checkStorageExists();

		final IBranchPath branch = internalRef.getBranch().branchPath();
		final ImmutableBiMap<Locale, String> languageIdMap = getLanguageIdMap(locales, branch);
		final Multimap<Locale, ISnomedDescription> descriptionsByLocale = HashMultimap.create();
		final Set<String> synonymAndDescendantIds = getSnomedComponentService().getSynonymAndDescendantIds(branch);

		for (final ISnomedDescription description : descriptions) {
			if (!description.isActive()) {
				continue;
			}
			
			if (!synonymAndDescendantIds.contains(description.getTypeId())) {
				continue;
			}

			for (final Entry<String, Acceptability> acceptabilityEntry : description.getAcceptabilityMap().entrySet()) {
				final String languageRefSetId = acceptabilityEntry.getKey();
				if (Acceptability.PREFERRED.equals(acceptabilityEntry.getValue()) && languageIdMap.containsValue(languageRefSetId)) {
					descriptionsByLocale.put(languageIdMap.inverse().get(languageRefSetId), description);
				}
			}
		}

		for (final Locale locale : locales) {
			final Collection<ISnomedDescription> matchingDescriptions = descriptionsByLocale.get(locale);
			if (!matchingDescriptions.isEmpty()) {
				return matchingDescriptions.iterator().next();
			}
		}

		throw new PreferredTermNotFoundException(conceptRef.getComponentId());
	}

	/*
	 * FIXME:
	 * - Add a warning if a locale could not be converted to a language reference set ID?
	 * - The user cannot refer to the same reference set ID via multiple locales (specifying en-GB and en-GB-x-900000000000508004 will
	 *   throw an exception)
	 * - Caching results
	 * - Different branch paths can have different available language refsets, looks like something which could be added to SnomedComponentService
	 * - Better fallback mechanism?
	 */
	@Override
	public ImmutableBiMap<Locale, String> getLanguageIdMap(final List<Locale> locales, final IBranchPath branchPath) {
		final ImmutableBiMap.Builder<Locale, String> resultBuilder = ImmutableBiMap.builder();

		for (final Locale locale : locales) {
			final String mappedRefSetId = LanguageCodeReferenceSetIdentifierMapping.getReferenceSetIdentifier(locale.toLanguageTag().toLowerCase());
			if (null != mappedRefSetId) {
				resultBuilder.put(locale, mappedRefSetId);
				continue;
			}

			final String extension = locale.getExtension('x');
			if (null != extension && null != Longs.tryParse(extension)) {
				final SnomedRefSetIndexEntry refSet = snomedRefSetLookupService.getComponent(branchPath, extension);
				if (SnomedRefSetType.LANGUAGE.equals(refSet.getType())) {
					resultBuilder.put(locale, extension);
				}
			}
		}

		final ImmutableBiMap<Locale, String> result = resultBuilder.build();
		return !result.isEmpty() ? result : ImmutableBiMap.of(Locale.forLanguageTag("en-US"), Concepts.REFSET_LANGUAGE_TYPE_US);
	}
}
