/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayListWithCapacity;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.eclipse.xtext.xbase.lib.Pair;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRF2Folder;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableList;

/**
 * Experimental RF2 RefSet Descriptor RefSet Exporter. Enable with sysprop: 
 * @since 6.12
 */
public final class Rf2RefSetDescriptorRefSetExporter extends Rf2Exporter<SnomedRefSetSearchRequestBuilder, SnomedReferenceSets, SnomedReferenceSet> {

	public Rf2RefSetDescriptorRefSetExporter(Rf2ReleaseType releaseType, 
			String countryNamespaceElement, 
			String namespaceFilter,
			String transientEffectiveTime, 
			String archiveEffectiveTime, 
			boolean includePreReleaseContent, 
			Collection<String> modules) {
		super(releaseType, countryNamespaceElement, namespaceFilter, transientEffectiveTime, archiveEffectiveTime, includePreReleaseContent, modules);
	}

	@Override
	protected final Path getRelativeDirectory() {
		return Paths.get(releaseType.toString(), SnomedRF2Folder.REFSET.getDisplayName(), SnomedRF2Folder.METADATA.getDisplayName());
	}

	@Override
	protected final Path getFileName() {
		return Paths.get(String.format("der2_cciRefset_RefsetDescriptor%s_%s_%s.txt",
				releaseType.toString(),
				countryNamespaceElement,
				archiveEffectiveTime));
	}

	@Override
	protected String[] getHeader() {
		return SnomedRf2Headers.REFSET_DESCRIPTOR_TYPE_HEADER;
	}

	@Override
	protected SnomedRefSetSearchRequestBuilder createSearchRequestBuilder() {
		return SnomedRequests
				.prepareSearchRefSet()
				.sortBy(SortField.ascending(SnomedConceptDocument.Fields.ID));
	}

	@Override
	protected Stream<List<String>> getMappedStream(SnomedReferenceSets results, RepositoryContext context, String branch) {
		return results.stream().flatMap(refSet -> toRefSetSpecificColumns(refSet).stream());
	}

	private List<List<String>> toRefSetSpecificColumns(SnomedReferenceSet refSet) {
		final String columnTypePrefix = Rf2RefSetExporter.getColumnTypePrefix(refSet.getType());
		final String[] header = Rf2RefSetExporter.getHeader(refSet.getType());
		final String[] additionalFields = Arrays.copyOfRange(header, header.length - columnTypePrefix.length() - 1, header.length);
		
		final List<List<String>> additionalFieldRows = newArrayListWithCapacity(additionalFields.length);
		
		for (int attributeIdx = 0; attributeIdx < additionalFields.length; attributeIdx++) {
			final Pair<String, String> attributeDescriptionAndType = getAttributeDescriptionAndType(refSet, additionalFields[attributeIdx]);
			additionalFieldRows.add(toRefSetSpecificColumn(refSet, attributeDescriptionAndType.getKey(), attributeDescriptionAndType.getValue(), attributeIdx));
		}
		
		return additionalFieldRows;
	}

	private List<String> toRefSetSpecificColumn(SnomedReferenceSet refSet, String attributeDescriptionId, String attributeTypeId, int order) {
		return ImmutableList.of(
			UUID.randomUUID().toString(),		// id
			getEffectiveTime(refSet),			// effectiveTime 
			getActive(refSet),					// active
			refSet.getModuleId(),				// moduleId
			Concepts.REFSET_DESCRIPTOR_REFSET,	// refSetId
			refSet.getId(),						// referencedComponentId,
			attributeDescriptionId,				// attributeDescription
			attributeTypeId,					// attributeType
			String.valueOf(order)				// attributeOrder
		);
	}
	
	private static Pair<String, String> getAttributeDescriptionAndType(SnomedReferenceSet refSet, String columnName) {
		switch (columnName) {
		// attribute
		case SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID: 
			return Pair.of(Concepts.ATTRIBUTE_DESCRIPTION_REFERENCED_COMPONENT, getReferencedComponentType(refSet.getReferencedComponentType()));
		case SnomedRf2Headers.FIELD_VALUE:
			if (Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR.equals(refSet.getId())) {
				return Pair.of(Concepts.DESCRIPTION_INACTIVATION_VALUE, Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT);
			} else if (Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR.equals(refSet.getId())) {
				return Pair.of(Concepts.CONCEPT_INACTIVATION_VALUE, Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT);
			} else {
				return Pair.of(Concepts.ATTRIBUTE_TYPE_ATTRIBUTE_VALUE, Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT);
			}
		// lang
		case SnomedRf2Headers.FIELD_ACCEPTABILITY_ID: 
			return Pair.of(Concepts.ATTRIBUTE_TYPE_ACCEPTABILITY, Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT);
		// association
		case SnomedRf2Headers.FIELD_TARGET_COMPONENT:
		case SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_ASSOCIATION_TARGET, Concepts.ATTRIBUTE_TYPE_COMPONENT_TYPE);
		// query
		case SnomedRf2Headers.FIELD_QUERY: 
			return Pair.of(Concepts.ATTRIBUTE_TYPE_QUERY, Concepts.ATTRIBUTE_TYPE_STRING_TYPE);
		// simple, complex, extended map
		case SnomedRf2Headers.FIELD_MAP_TARGET: 
			if (SnomedRefSetType.SIMPLE_MAP.equals(refSet.getType())) {
				return Pair.of(Concepts.ATTRIBUTE_TYPE_SCHEME_VALUE, Concepts.ATTRIBUTE_TYPE_STRING_TYPE);
			} else {
				return Pair.of(Concepts.ATTRIBUTE_TYPE_MAP_TARGET, Concepts.ATTRIBUTE_TYPE_STRING_TYPE);
			}
		// complex, extended map
		case SnomedRf2Headers.FIELD_MAP_GROUP: 
			return Pair.of(Concepts.ATTRIBUTE_TYPE_MAP_GROUP, Concepts.ATTRIBUTE_TYPE_UNSIGNED_INTEGER_TYPE);
		case SnomedRf2Headers.FIELD_MAP_PRIORITY: 
			return Pair.of(Concepts.ATTRIBUTE_TYPE_MAP_PRIORITY, Concepts.ATTRIBUTE_TYPE_UNSIGNED_INTEGER_TYPE);
		case SnomedRf2Headers.FIELD_MAP_RULE: 
			return Pair.of(Concepts.ATTRIBUTE_TYPE_MAP_RULE, Concepts.ATTRIBUTE_TYPE_STRING_TYPE);
		case SnomedRf2Headers.FIELD_MAP_ADVICE: 
			return Pair.of(Concepts.ATTRIBUTE_TYPE_MAP_ADVICE, Concepts.ATTRIBUTE_TYPE_STRING_TYPE);
		case SnomedRf2Headers.FIELD_CORRELATION_ID: 
			return Pair.of(Concepts.ATTRIBUTE_TYPE_CORRELATION_VALUE, Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT);
		// extended map
		case SnomedRf2Headers.FIELD_MAP_CATEGORY_ID: 
			return Pair.of(Concepts.ATTRIBUTE_TYPE_MAP_CATEGORY_VALUE, Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT);
		// module dependency
		case SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_SOURCE_EFFECTIVE_TIME, Concepts.ATTRIBUTE_TYPE_TIME);
		case SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_TARGET_EFFECTIVE_TIME, Concepts.ATTRIBUTE_TYPE_TIME);
		// desc type
		case SnomedRf2Headers.FIELD_DESCRIPTION_FORMAT: 
			return Pair.of(Concepts.DESCRIPTION_FORMAT_TYPE_ROOT_CONCEPT, Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT);
		case SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH: 
			return Pair.of(Concepts.ATTRIBUTE_TYPE_DESCRIPTION_FORMAT_LENGTH, Concepts.ATTRIBUTE_TYPE_UNSIGNED_INTEGER_TYPE);
		// mrcm domain
		case SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_DOMAIN_CONSTRAINT, Concepts.ATTRIBUTE_TYPE_SNOMEDCT_PARSABLE_STRING);
		case SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_PARENT_DOMAIN, Concepts.ATTRIBUTE_TYPE_SNOMEDCT_PARSABLE_STRING);
		case SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_PROXIMAL_PRIMITIVE_CONSTRAINT, Concepts.ATTRIBUTE_TYPE_SNOMEDCT_PARSABLE_STRING);
		case SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_PROXIMAL_PRIMITIVE_REFINEMENT, Concepts.ATTRIBUTE_TYPE_SNOMEDCT_PARSABLE_STRING);
		case SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, Concepts.ATTRIBUTE_TYPE_SNOMEDCT_PARSABLE_STRING);
		case SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, Concepts.ATTRIBUTE_TYPE_SNOMEDCT_PARSABLE_STRING);
		case SnomedRf2Headers.FIELD_MRCM_EDITORIAL_GUIDE_REFERENCE:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_GUIDE_URL, Concepts.ATTRIBUTE_TYPE_SNOMEDCT_PARSABLE_STRING);
		// mrcm attribute domain
		case SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_DOMAIN, Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT);
		case SnomedRf2Headers.FIELD_MRCM_GROUPED:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_GROUPED, Concepts.ATTRIBUTE_TYPE_UNSIGNED_INTEGER_TYPE);
		case SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_ATTRIBUTE_CARDINALITY, Concepts.ATTRIBUTE_TYPE_SNOMEDCT_PARSABLE_STRING);
		case SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_ATTRIBUTE_IN_GROUP_CARDINALITY, Concepts.ATTRIBUTE_TYPE_SNOMEDCT_PARSABLE_STRING);
		case SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_CONCEPT_MODEL_RULE_STRENGTH, Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT);
		case SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_CONTENT_TYPE, Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT);
		// mrcm attribute range
		case SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_RANGE_CONSTRAINT,  Concepts.ATTRIBUTE_TYPE_SNOMEDCT_PARSABLE_STRING);
		case SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_ATTRIBUTE_RULE, Concepts.ATTRIBUTE_TYPE_SNOMEDCT_PARSABLE_STRING);
		// mrcm module scope refset
		case SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID:
			return Pair.of(Concepts.ATTRIBUTE_TYPE_RULE_REFSET, Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT);
		// owl
		case SnomedRf2Headers.FIELD_OWL_EXPRESSION: 
			return Pair.of(Concepts.ATTRIBUTE_TYPE_OWL_EXPRESSION, Concepts.ATTRIBUTE_TYPE_OWL2_LANG_SYNTAX);
		// column headers that are currently not present in SNOMED CT will be mapped to the generic refset attribute concept ID  
		default: return Pair.of(Concepts.REFSET_ATTRIBUTE, Concepts.ATTRIBUTE_TYPE_STRING_TYPE);
		}
	}

	private static String getReferencedComponentType(String referencedComponentType) {
		switch (referencedComponentType) {
		case SnomedTerminologyComponentConstants.CONCEPT:
			return Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT;
		case SnomedTerminologyComponentConstants.DESCRIPTION:
			return Concepts.ATTRIBUTE_TYPE_DESCRIPTION_TYPE_COMPONENT;
		case SnomedTerminologyComponentConstants.RELATIONSHIP:
			return Concepts.ATTRIBUTE_TYPE_RELATIONSHIP_TYPE_COMPONENT;
		default:
			return Concepts.ATTRIBUTE_TYPE_COMPONENT_TYPE;
		}
	}
	
}
