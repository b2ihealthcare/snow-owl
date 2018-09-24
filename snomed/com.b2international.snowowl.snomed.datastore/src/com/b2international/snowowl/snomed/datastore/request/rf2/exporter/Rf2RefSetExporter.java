/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2.exporter;

import static com.google.common.collect.Lists.newArrayList;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.b2international.commons.BooleanUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.snomed.common.SnomedRF2Folder;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Rf2RefSetExportLayout;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 6.3
 */
public class Rf2RefSetExporter extends Rf2Exporter<SnomedRefSetMemberSearchRequestBuilder, SnomedReferenceSetMembers, SnomedReferenceSetMember> {

	private static final char[] INVALID_RESOURCE_CHARACTERS = { '\\', '/', ':', '*', '?', '"', '<', '>', '|', '\0' };
	
	private static final CharMatcher INVALID_RESOURCE_MATCHER = CharMatcher.WHITESPACE
			.or(CharMatcher.anyOf(String.valueOf(INVALID_RESOURCE_CHARACTERS)))
			.or(CharMatcher.JAVA_ISO_CONTROL)
			.precomputed();
	
	protected final Rf2RefSetExportLayout refSetExportLayout;
	protected final SnomedRefSetType refSetType;
	protected final Collection<SnomedConcept> referenceSets;

	public Rf2RefSetExporter(final Rf2ReleaseType releaseType, 
			final String countryNamespaceElement,
			final String namespaceFilter, 
			final String transientEffectiveTime, 
			final String archiveEffectiveTime, 
			final boolean includePreReleaseContent, 
			final Collection<String> modules, 
			final Rf2RefSetExportLayout refSetExportLayout, 
			final SnomedRefSetType refSetType, 
			final Collection<SnomedConcept> referenceSets) {

		super(releaseType, 
				countryNamespaceElement, 
				namespaceFilter, 
				transientEffectiveTime, 
				archiveEffectiveTime, 
				includePreReleaseContent, 
				modules);

		this.refSetExportLayout = refSetExportLayout;
		this.refSetType = refSetType;
		this.referenceSets = referenceSets;
	}

	@Override
	protected final Path getRelativeDirectory() {
		SnomedRF2Folder folder = SnomedRefSetUtil.REFSET_TYPE_TO_FOLDER_MAP.get(refSetType);
		if (folder != null) {
			
			if (SnomedRF2Folder.TERMINOLOGY == folder) {
				return Paths.get(releaseType.toString(), SnomedRF2Folder.TERMINOLOGY.getDisplayName());
			} else {
				return Paths.get(releaseType.toString(), SnomedRF2Folder.REFSET.getDisplayName(), folder.getDisplayName());
			}
			
		}
		throw new IllegalArgumentException("Unknown SNOMED CT reference set type: " + refSetType);
	}

	@Override
	protected final Path getFileName() {
		final String prefix;
		if (refSetType == SnomedRefSetType.OWL_AXIOM || refSetType == SnomedRefSetType.OWL_ONTOLOGY) {
			prefix = "sct2";
		} else {
			prefix = "der2";
		}
		return Paths.get(String.format("%s_%sRefset_%s%s%s_%s_%s.txt",
				prefix,
				getColumnTypePrefix(),
				getRefSetName(),
				releaseType.toString(),
				getLanguageElement(),
				countryNamespaceElement,
				archiveEffectiveTime));
	}

	private String getColumnTypePrefix() {
		switch (refSetType) {
			case SIMPLE: 
				return "";
			case ATTRIBUTE_VALUE:
				return "c";
			case LANGUAGE:
				return "c";
			case ASSOCIATION:
				return "c";
			case CONCRETE_DATA_TYPE: 
				return "ccss";
			case QUERY: 
				return "s";
			case DESCRIPTION_TYPE: 
				return "ci";
			case SIMPLE_MAP: 
				return "s";
			case SIMPLE_MAP_WITH_DESCRIPTION: 
				return "ss";
			case COMPLEX_MAP: 
				return "iisssc";
			case EXTENDED_MAP: 
				return "iissscc";
			case MODULE_DEPENDENCY: 
				return "ss";
			case OWL_AXIOM: //$FALL-THROUGH$
			case OWL_ONTOLOGY:
				return "s";
			case MRCM_DOMAIN:
				return "sssssss";
			case MRCM_ATTRIBUTE_DOMAIN:
				return "cisscc";
			case MRCM_ATTRIBUTE_RANGE:
				return "sscc";
			case MRCM_MODULE_SCOPE:
				return "c";
			default: 
				throw new IllegalArgumentException("Unknown SNOMED CT reference set type: " + refSetType);
		}
	}

	private String getRefSetName() {
		if (Rf2RefSetExportLayout.COMBINED.equals(refSetExportLayout)) {
			return getCombinedRefSetName();
		} else {
			return getIndividualRefSetName();
		}
	}

	private String getCombinedRefSetName() {
		switch (refSetType) {
			case CONCRETE_DATA_TYPE:
				return "ConcreteDomainReferenceSet";
			case OWL_AXIOM:
				return "OWLAxiom";
			case OWL_ONTOLOGY:
				return "OWLOntology";
			case MRCM_DOMAIN:
				return "MRCMDomain";
			case MRCM_ATTRIBUTE_DOMAIN:
				return "MRCMAttributeDomain";
			case MRCM_ATTRIBUTE_RANGE:
				return "MRCMAttributeRange";
			case MRCM_MODULE_SCOPE:
				return "MRCMModuleScope";
			case ASSOCIATION: //$FALL-THROUGH$
			case SIMPLE: //$FALL-THROUGH$
			case QUERY: //$FALL-THROUGH$
			case ATTRIBUTE_VALUE: //$FALL-THROUGH$
			case EXTENDED_MAP: //$FALL-THROUGH$
			case SIMPLE_MAP: //$FALL-THROUGH$
			case SIMPLE_MAP_WITH_DESCRIPTION: //$FALL-THROUGH$
			case COMPLEX_MAP: //$FALL-THROUGH$
			case DESCRIPTION_TYPE: //$FALL-THROUGH$
			case MODULE_DEPENDENCY: //$FALL-THROUGH$ 
			case LANGUAGE: 
				return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, refSetType.name());
			default: 
				throw new IllegalArgumentException("Unknown SNOMED CT reference set type: " + refSetType);
		}
	}

	private String getIndividualRefSetName() {
		final SnomedConcept singleReferenceSet = Iterables.getOnlyElement(referenceSets);
		final SnomedDescription referenceSetPt = singleReferenceSet.getPt();
		final String refSetName = (referenceSetPt != null)
				? toCamelCase(referenceSetPt.getTerm())
				: singleReferenceSet.getId();
				
		// Replace dangerous characters with an underscore
		return INVALID_RESOURCE_MATCHER.replaceFrom(refSetName, '_');
	}

	private String toCamelCase(final String term) {
		if (StringUtils.isEmpty(term)) {
			return term;
		}

		boolean lastCharacterSpace = true;
		int writeIdx = 0;
		final StringBuilder result = new StringBuilder(term);

		for (int readIdx = 0; readIdx < result.length(); readIdx++) {
			final char readCharacter = result.charAt(readIdx);
			final boolean currentCharacterSpace = Character.isWhitespace(readCharacter);

			if (!currentCharacterSpace) {
				final char writtenCharacter = lastCharacterSpace ? Character.toUpperCase(readCharacter) : readCharacter;
				result.setCharAt(writeIdx++, writtenCharacter);
			}

			lastCharacterSpace = currentCharacterSpace;
		}

		return result.substring(0, writeIdx);
	}

	protected String getLanguageElement() {
		return "";
	}

	@Override
	protected String[] getHeader() {
		switch (refSetType) {
			case SIMPLE: 
				return SnomedRf2Headers.SIMPLE_TYPE_HEADER;
			case ATTRIBUTE_VALUE:
				return SnomedRf2Headers.ATTRIBUTE_VALUE_TYPE_HEADER;
			case LANGUAGE:
				return SnomedRf2Headers.LANGUAGE_TYPE_HEADER;
			case ASSOCIATION:
				return SnomedRf2Headers.ASSOCIATION_TYPE_HEADER;
			case CONCRETE_DATA_TYPE:
				// XXX: Choosing the non-AU format here, which includes the attribute name
				return SnomedRf2Headers.CONCRETE_DATA_TYPE_HEADER_WITH_LABEL;
			case QUERY:
				return SnomedRf2Headers.QUERY_TYPE_HEADER;
			case DESCRIPTION_TYPE: 
				return SnomedRf2Headers.DESCRIPTION_TYPE_HEADER;
			case SIMPLE_MAP: 
				return SnomedRf2Headers.SIMPLE_MAP_TYPE_HEADER;
			case SIMPLE_MAP_WITH_DESCRIPTION: 
				return SnomedRf2Headers.SIMPLE_MAP_TYPE_HEADER_WITH_DESCRIPTION;
			case COMPLEX_MAP: 
				return SnomedRf2Headers.COMPLEX_MAP_TYPE_HEADER;
			case EXTENDED_MAP: 
				return SnomedRf2Headers.EXTENDED_MAP_TYPE_HEADER;
			case MODULE_DEPENDENCY: 
				return SnomedRf2Headers.MODULE_DEPENDENCY_HEADER;
			case OWL_AXIOM: //$FALL-THROUGH$
			case OWL_ONTOLOGY:
				return SnomedRf2Headers.OWL_EXPRESSION_HEADER;
			case MRCM_DOMAIN:
				return SnomedRf2Headers.MRCM_DOMAIN_HEADER;
			case MRCM_ATTRIBUTE_DOMAIN:
				return SnomedRf2Headers.MRCM_ATTRIBUTE_DOMAIN_HEADER;
			case MRCM_ATTRIBUTE_RANGE:
				return SnomedRf2Headers.MRCM_ATTRIBUTE_RANGE_HEADER;
			case MRCM_MODULE_SCOPE:
				return SnomedRf2Headers.MRCM_MODULE_SCOPE_HEADER;
			default: 
				throw new IllegalArgumentException("Unknown SNOMED CT reference set type: " + refSetType);
		}
	}

	@Override
	protected SnomedRefSetMemberSearchRequestBuilder createSearchRequestBuilder() {
		final Set<String> referenceSetIds = referenceSets.stream()
				.map(c -> c.getId())
				.collect(Collectors.toSet());
		
		return SnomedRequests.prepareSearchMember()
				.filterByRefSet(referenceSetIds)
				.sortBy(
						SortField.ascending(SnomedRefSetMemberIndexEntry.Fields.REFERENCE_SET_ID), 
						SortField.ascending(SnomedRefSetMemberIndexEntry.Fields.ID));
	}

	@Override
	@SuppressWarnings("deprecation")
	protected Stream<List<String>> getMappedStream(final SnomedReferenceSetMembers results, 
			final RepositoryContext context, 
			final String branch) {
		
		final List<String> extraColumns = newArrayList(getHeader());
		
		// Remove the first 6 columns, which are the same for each reference set type
		for (int i = 0; i < 6; i++) {
			extraColumns.remove(0);
		}
		
		// XXX: Some members use a different property name; translate them at this point
		for (int j = 0; j < extraColumns.size(); j++) {
			switch (extraColumns.get(j)) {
				case SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID:
					extraColumns.set(j, SnomedRf2Headers.FIELD_TARGET_COMPONENT);
					break;
				case SnomedRf2Headers.FIELD_UOM_ID:
					extraColumns.set(j, SnomedRf2Headers.FIELD_UNIT_ID);
					break;
				case SnomedRf2Headers.FIELD_DATA_VALUE:
					extraColumns.set(j, SnomedRf2Headers.FIELD_VALUE);
					break;
				default:
					// Use RF2 column name for the property name
					break;
			}
		}
		
		return results.stream()
				.map(member -> {
					final ImmutableList.Builder<String> builder = ImmutableList.<String>builder()
							.add(member.getId())								// uuid
							.add(getEffectiveTime(member))						// effectiveTime 
							.add(getActive(member))								// active
							.add(member.getModuleId())							// moduleId
							.add(member.getReferenceSetId())					// refSetId
							.add(member.getReferencedComponent().getId());		// referencedComponentId

					// Append extra columns using the properties map
					final Map<String, Object> properties = member.getProperties();
					for (final String extraColumn : extraColumns) {
						builder.add(toColumn(properties.get(extraColumn)));
					}

					return builder.build();
				});
	}

	private String toColumn(final Object object) {
		if (object == null) {
			return "";
		} else if (object instanceof SnomedCoreComponent) {
			return ((SnomedCoreComponent) object).getId();
		} else if (object instanceof Boolean) {
			return BooleanUtils.toString((Boolean) object);
		} else if (object instanceof Date) {
			return getEffectiveTime((Date) object);
		} else {
			return String.valueOf(object);
		}
	}
}
