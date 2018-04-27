/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2.importer;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * @since 6.0.0
 */
public final class Rf2Format {
	
	private static final List<Rf2ContentType<?>> CONTENT_TYPES = ImmutableList.<Rf2ContentType<?>>builder()
			// Terminology
			.add(new Rf2ConceptContentType())
			.add(new Rf2DescriptionContentType())
			.add(new Rf2RelationshipContentType())
			// Refset/Content
			.add(new Rf2AssociationRefSetContentType())
			.add(new Rf2AttributeValueRefSetContentType())
			.add(new Rf2SimpleRefSetContentType())
			.add(new Rf2OwlAxiomRefSetContentType())
			// Refset/Language
			.add(new Rf2LanguageRefSetContentType())
			// Refset/Map
			.add(new Rf2SimpleMapRefSetContentType())
			.add(new Rf2SimpleMapWithDescriptionContentType())
			.add(new Rf2ComplexMapRefSetContentType())
			.add(new Rf2ExtendedMapRefSetContentType())
			// Refset/Metadata
			.add(new Rf2ModuleDependencyRefSetContentType())
			.add(new Rf2DescriptionTypeRefSetContentType())
			.add(new Rf2MRCMDomainRefSetContentType())
			.add(new Rf2MRCMAttributeDomainRefSetContentType())
			.add(new Rf2MRCMAttributeRangeRefSetContentType())
			.add(new Rf2MRCMModuleScopeRefSetContentType())
			.build();
	
	private Rf2Format() {}

	public static List<Rf2ContentType<?>> getContentTypes() {
		return CONTENT_TYPES;
	}
}
