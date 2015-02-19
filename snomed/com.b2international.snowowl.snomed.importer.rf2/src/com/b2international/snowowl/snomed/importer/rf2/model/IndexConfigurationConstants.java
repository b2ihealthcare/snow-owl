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
package com.b2international.snowowl.snomed.importer.rf2.model;

import java.util.List;
import java.util.Map;

import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedAssociationRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedAttributeValueRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedComplexMapTypeRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedConcreteDataTypeRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedDescriptionTypeRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedLanguageRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedQueryRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedSimpleMapTypeRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedSimpleTypeRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.terminology.SnomedConceptImporter;
import com.b2international.snowowl.snomed.importer.rf2.terminology.SnomedDescriptionImporter;
import com.b2international.snowowl.snomed.importer.rf2.terminology.SnomedRelationshipImporter;
import com.google.common.collect.ImmutableMap;

/**
 * 
 */
public abstract class IndexConfigurationConstants {

	public static final Map<ComponentImportType, List<IndexConfiguration>> CONFIGURATIONS_BY_TYPE = ImmutableMap.<ComponentImportType, List<IndexConfiguration>>builder()
			.put(ComponentImportType.ASSOCIATION_TYPE_REFSET, SnomedAssociationRefSetImporter.INDEXES)
			.put(ComponentImportType.ATTRIBUTE_VALUE_REFSET, SnomedAttributeValueRefSetImporter.INDEXES)
			.put(ComponentImportType.COMPLEX_MAP_TYPE_REFSET, SnomedComplexMapTypeRefSetImporter.INDEXES)
			.put(ComponentImportType.CONCEPT, SnomedConceptImporter.INDEXES)
			.put(ComponentImportType.CONCRETE_DOMAIN_REFSET, SnomedConcreteDataTypeRefSetImporter.INDEXES)
			.put(ComponentImportType.DESCRIPTION, SnomedDescriptionImporter.INDEXES)
			.put(ComponentImportType.DESCRIPTION_TYPE_REFSET, SnomedDescriptionTypeRefSetImporter.INDEXES)
			.put(ComponentImportType.LANGUAGE_TYPE_REFSET, SnomedLanguageRefSetImporter.INDEXES)
			.put(ComponentImportType.QUERY_TYPE_REFSET, SnomedQueryRefSetImporter.INDEXES)
			.put(ComponentImportType.RELATIONSHIP, SnomedRelationshipImporter.INDEXES)
			.put(ComponentImportType.SIMPLE_MAP_TYPE_REFSET, SnomedSimpleMapTypeRefSetImporter.INDEXES)
			.put(ComponentImportType.SIMPLE_TYPE_REFSET, SnomedSimpleTypeRefSetImporter.INDEXES)
			.build();

	private IndexConfigurationConstants() {
		// Prevent instantiation
	}
}