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
package com.b2international.snowowl.snomed.datastore.index;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.services.SnomedConceptNameProvider;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;

/**
 */
public class SnomedConceptModelMappingStrategy extends SnomedConceptIndexMappingStrategy {

	public static final float DEFAULT_DOI = 1.0f;

	public SnomedConceptModelMappingStrategy(final ISnomedTaxonomyBuilder taxonomyBuilder, final Concept concept, final String label, final Set<String> synonymAndDescendantIds, final float doi, final Collection<String> predicateKeys, final Collection<String> referringRefSetIds, final Collection<String> mappingRefSetIds, final boolean indexCompareKey) {
		super(taxonomyBuilder, checkNotNull(concept, "SNOMED CT concept argument cannot be null.").getId(), 
				CDOIDUtil.getLong(concept.cdoID()), 
				concept.isExhaustive(), 
				concept.isActive(), 
				concept.isPrimitive(), 
				concept.isReleased(), 
				concept.getModule().getId(), 
				null == label ? getLabel(concept) : label, 
				getActiveDescriptionInfos(concept, checkNotNull(synonymAndDescendantIds, "Synonym and descendant concept IDs argument cannot be null.")),
				doi,
				predicateKeys, 
				referringRefSetIds,
				mappingRefSetIds,
				concept.getEffectiveTime(),
				indexCompareKey);
	}
	
	/**
	 * Returns with the label for the SNOMED CT concept. First it tires to get PT from the underlying transaction, 
	 * if it fails, it falls back to fully specified name.
	 * 
	 * @param concept
	 * @return the label for the concept.
	 */
	private static String getLabel(final Concept concept) {
		
		
		//try to get the preferred term from the concept
		//if we found the first active proper language type reference set member we're fine
		final String refSetId = ApplicationContext.getInstance().getService(ILanguageConfigurationProvider.class).getLanguageConfiguration().getLanguageRefSetId();
		final Set<String> ids = ApplicationContext.getInstance().getService(ISnomedComponentService.class).getAvailablePreferredTermIds(BranchPathUtils.createPath(concept.cdoView()));
		
		for (final Description description : concept.getDescriptions()) {
			
			if (!description.isActive()) {
				continue; //do not care about inactive descriptions
			}
			
			if (!ids.contains(description.getType().getId())) {
				continue; //cannot be PT
			}

			for (final SnomedLanguageRefSetMember member : description.getLanguageRefSetMembers()) {

				if (!member.isActive()) {
					continue; //keep searching inactive reference set member
				}
				
				if (!Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(member.getAcceptabilityId())) {
					continue; //not a preferred member
				}
				
				if (!refSetId.equals(member.getRefSetIdentifierId())) {
					continue; //means different language
				}
				
				final String term = description.getTerm();
				if (!StringUtils.isEmpty(term)) {
					return term;
				}
				
			}
			
			
		}
		
		//try to retrieve the preferred term from the underlying CDO view
		final String label = SnomedConceptNameProvider.INSTANCE.getText(concept.getId(), concept.cdoView());
		//if the preferred term is still null, fallback to FSN
		return (null == label) ? concept.getFullySpecifiedName() : label;
	}
	
	private static Set<DescriptionInfo> getActiveDescriptionInfos(final Concept concept, final Set<String> synonymAndDescendantIds) {
		final Set<DescriptionInfo> results = newHashSet();
		
		for (final Description description : concept.getDescriptions()) {
			if (description.isActive()) {
				results.add(createDescriptionInfo(synonymAndDescendantIds, description));
			}
		}

		return results;
	}

	private static DescriptionInfo createDescriptionInfo(final Set<String> synonymAndDescendantIds, final Description description) {
		return new DescriptionInfo(getDescriptionType(synonymAndDescendantIds, description), description.getTerm());
	}

	private static DescriptionType getDescriptionType(final Set<String> synonymAndDescendantIds, final Description description) {
		final String typeId = description.getType().getId();
		
		if (Concepts.FULLY_SPECIFIED_NAME.equals(typeId)) {
			return DescriptionType.FULLY_SPECIFIED_NAME;
		} else if (synonymAndDescendantIds.contains(typeId)) {
			return DescriptionType.SYNONYM;
		} else {
			return DescriptionType.OTHER;
		}
	}
}