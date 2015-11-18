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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponentRef;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.server.domain.InternalComponentRef;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedConstants.LanguageCodeReferenceSetIdentifierMapping;
import com.b2international.snowowl.snomed.api.ISnomedDescriptionService;
import com.b2international.snowowl.snomed.api.exception.FullySpecifiedNameNotFoundException;
import com.b2international.snowowl.snomed.api.exception.PreferredTermNotFoundException;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescriptionUpdate;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedDescriptionConverter;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedDescriptionCreateRequest;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Longs;

public class SnomedDescriptionServiceImpl 
	extends AbstractSnomedComponentServiceImpl<ISnomedDescription, ISnomedDescriptionUpdate, Description> 
	implements ISnomedDescriptionService {

	private static SnomedIndexService getIndexService() {
		return ApplicationContext.getServiceForClass(SnomedIndexService.class);
	}

	private static ISnomedComponentService getSnomedComponentService() {
		return ApplicationContext.getServiceForClass(ISnomedComponentService.class);
	}

	private final SnomedDescriptionLookupService snomedDescriptionLookupService = new SnomedDescriptionLookupService();

	public SnomedDescriptionServiceImpl() {
		super(SnomedDatastoreActivator.REPOSITORY_UUID, ComponentCategory.DESCRIPTION);
	}

	private SnomedDescriptionConverter getDescriptionConverter(final IBranchPath branchPath) {
		return new SnomedDescriptionConverter(getMembershipLookupService(branchPath));
	}

	@Override
	protected boolean componentExists(final IComponentRef ref) {
		final InternalComponentRef internalRef = ClassUtils.checkAndCast(ref, InternalComponentRef.class);
		final IBranchPath branch = internalRef.getBranch().branchPath();
		return snomedDescriptionLookupService.exists(branch, internalRef.getComponentId());
	}

	@Override
	protected ISnomedDescription doRead(final IComponentRef ref) {
		final InternalComponentRef internalRef = ClassUtils.checkAndCast(ref, InternalComponentRef.class);
		final IBranchPath branch = internalRef.getBranch().branchPath();
		final SnomedDescriptionIndexEntry descriptionIndexEntry = snomedDescriptionLookupService.getComponent(branch, internalRef.getComponentId());
		return getDescriptionConverter(branch).apply(descriptionIndexEntry);
	}

	@Override
	public List<ISnomedDescription> readConceptDescriptions(final IComponentRef conceptRef) {
		// TODO: check that concept exists?

		final InternalComponentRef internalConceptRef = ClassUtils.checkAndCast(conceptRef, InternalComponentRef.class);
		final SnomedDescriptionIndexQueryAdapter queryAdapter = SnomedDescriptionIndexQueryAdapter.findByConceptId(internalConceptRef.getComponentId());
		final IBranchPath branch = internalConceptRef.getBranch().branchPath();
		
		final Collection<SnomedDescriptionIndexEntry> descriptionIndexEntries = getIndexService().searchUnsorted(branch, queryAdapter);
		final Collection<ISnomedDescription> transformedDescriptions = Collections2.transform(descriptionIndexEntries, getDescriptionConverter(branch));

		return SnomedComponentOrdering.id().immutableSortedCopy(transformedDescriptions);
	}

	private Description getDescription(final String descriptionId, final SnomedEditingContext editingContext) {
		return snomedDescriptionLookupService.getComponent(descriptionId, editingContext.getTransaction());
	}

	@Override
	protected void doUpdate(final IComponentRef ref, final ISnomedDescriptionUpdate update, final SnomedEditingContext editingContext) {
		final Description description = getDescription(ref.getComponentId(), editingContext);

		boolean changed = false;
		changed |= updateModule(update.getModuleId(), description, editingContext);
		changed |= updateStatus(update.isActive(), description, editingContext);
		changed |= updateCaseSignificance(update.getCaseSignificance(), description, editingContext);
		
		updateInactivationIndicator(update.isActive(), update.getInactivationIndicator(), description, editingContext);
		
		updateAssociationTargets(update.getAssociationTargets(), description, editingContext);

		// XXX: acceptability changes do not push the effective time forward on the description 
		updateAcceptabilityMap(update.getAcceptability(), description, editingContext);

		if (changed) {
			description.unsetEffectiveTime();
		}
	}

	private void updateInactivationIndicator(Boolean active, DescriptionInactivationIndicator inactivationIndicator, Description description, SnomedEditingContext context) {
		// the description should be inactive (indicated in the update) to be able to update the indicators
		if (Boolean.FALSE.equals(active) && inactivationIndicator != null) {
			boolean found = false;
			for (SnomedAttributeValueRefSetMember member : description.getInactivationIndicatorRefSetMembers()) {
				if (member.isActive()) {
					found = member.getValueId().equals(inactivationIndicator.getValueId());
				}
			}
			if (!found) {
				// inactivate or remove any active member(s) and add the new one
				for (SnomedAttributeValueRefSetMember member : newArrayList(description.getInactivationIndicatorRefSetMembers())) {
					SnomedModelExtensions.removeOrDeactivate(member);
				}
				final SnomedAttributeValueRefSetMember member = context.getRefSetEditingContext().createAttributeValueRefSetMember(
						SnomedRefSetEditingContext.createDescriptionTypePair(description.getId()),
						SnomedRefSetEditingContext.createConceptTypePair(inactivationIndicator.getValueId()),
						description.getModule().getId(),
						context.getRefSetEditingContext().findRefSetByIdentifierConceptId(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR));
				description.getInactivationIndicatorRefSetMembers().add(member);
			}
		}
	}

	private boolean updateCaseSignificance(final CaseSignificance newCaseSignificance, final Description description, final SnomedEditingContext editingContext) {
		if (null == newCaseSignificance) {
			return false;
		}

		final String existingCaseSignificanceId = description.getCaseSignificance().getId();
		final String newCaseSignificanceId = newCaseSignificance.getConceptId();
		if (!existingCaseSignificanceId.equals(newCaseSignificanceId)) {
			description.setCaseSignificance(editingContext.getConcept(newCaseSignificanceId));
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @deprecated - moved to {@link SnomedDescriptionCreateRequest}
	 */
	private void updateAcceptabilityMap(final Map<String, Acceptability> newAcceptabilityMap, final Description description, final SnomedEditingContext editingContext) {
		if (null == newAcceptabilityMap) {
			return;
		}

		final Map<String, Acceptability> languageMembersToCreate = newHashMap(newAcceptabilityMap);
		final List<SnomedLanguageRefSetMember> languageMembers = ImmutableList.copyOf(description.getLanguageRefSetMembers());
		for (final SnomedLanguageRefSetMember languageMember : languageMembers) {
			if (!languageMember.isActive()) {
				continue;
			}

			final String languageRefSetId = languageMember.getRefSetIdentifierId();
			final Acceptability currentAcceptability = Acceptability.getByConceptId(languageMember.getAcceptabilityId());
			final Acceptability newAcceptability = newAcceptabilityMap.get(languageRefSetId);

			if (!currentAcceptability.equals(newAcceptability)) {
				removeOrDeactivate(languageMember);
			} else {
				languageMembersToCreate.remove(languageRefSetId);
			}
		}

		for (final Entry<String, Acceptability> languageMemberEntry : languageMembersToCreate.entrySet()) {
			addLanguageMember(description, editingContext, languageMemberEntry.getKey(), languageMemberEntry.getValue());
		}

		final IBranchPath branchPath = BranchPathUtils.createPath(editingContext.getTransaction());

		for (final Entry<String, Acceptability> languageMemberEntry : languageMembersToCreate.entrySet()) {
			if (Acceptability.PREFERRED.equals(languageMemberEntry.getValue())) {
				final Set<String> synonymAndDescendantIds = getSnomedComponentService().getSynonymAndDescendantIds(branchPath);
				if (synonymAndDescendantIds.contains(description.getType().getId())) {
					updateOtherPreferredDescriptions(description.getConcept().getDescriptions(), description, languageMemberEntry.getKey(), 
							synonymAndDescendantIds, editingContext);
				}
			}
		}
	}

	/**
	 * @deprecated - moved to {@link SnomedDescriptionCreateRequest}
	 * Partially taken from WidgetBeanUpdater
	 */
	private void addLanguageMember(final Description description, final SnomedEditingContext editingContext, final String languageRefSetId, 
			final Acceptability acceptability) {

		final SnomedRefSetEditingContext refSetEditingContext = editingContext.getRefSetEditingContext();
		final SnomedStructuralRefSet languageRefSet = refSetEditingContext.lookup(languageRefSetId, SnomedStructuralRefSet.class);
		final ComponentIdentifierPair<String> acceptibilityPair = SnomedRefSetEditingContext.createConceptTypePair(acceptability.getConceptId());
		final ComponentIdentifierPair<String> referencedComponentPair = SnomedRefSetEditingContext.createDescriptionTypePair(description.getId());
		final SnomedLanguageRefSetMember newMember = refSetEditingContext.createLanguageRefSetMember(referencedComponentPair, acceptibilityPair, description.getModule().getId(), languageRefSet);

		description.getLanguageRefSetMembers().add(newMember);
	}

	/**
	 * @deprecated - moved to {@link SnomedDescriptionCreateRequest}
	 */
	private void updateOtherPreferredDescriptions(final List<Description> descriptions, final Description preferredDescription, final String languageRefSetId, 
			final Set<String> synonymAndDescendantIds, final SnomedEditingContext editingContext) {

		for (final Description description : descriptions) {
			if (!description.isActive() || description.equals(preferredDescription)) {
				continue;
			}

			if (!synonymAndDescendantIds.contains(description.getType().getId())) {
				continue;
			}

			for (final SnomedLanguageRefSetMember languageMember : description.getLanguageRefSetMembers()) {
				if (!languageMember.isActive()) {
					continue;
				}

				if (!languageMember.getRefSetIdentifierId().equals(languageRefSetId)) {
					continue;
				}

				if (languageMember.getAcceptabilityId().equals(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED)) {
					removeOrDeactivate(languageMember);
					addLanguageMember(description, editingContext, languageRefSetId, Acceptability.ACCEPTABLE);
					break;
				}
			}
		}
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
