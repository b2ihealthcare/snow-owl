/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Collection;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * Defines support of SNOMED CT Reference Sets through the new Request based API.
 * 
 * @since 4.5
 */
public abstract class RefSetSupport {

	private RefSetSupport() {
	}

	private static final Multimap<SnomedRefSetType, String> SUPPORTED_REFERENCED_COMPONENTS = ImmutableMultimap.<SnomedRefSetType, String> builder()
			.put(SnomedRefSetType.ASSOCIATION, SnomedConcept.TYPE)
			.put(SnomedRefSetType.ASSOCIATION, SnomedDescription.TYPE)
			.put(SnomedRefSetType.ATTRIBUTE_VALUE, SnomedConcept.TYPE)
			.put(SnomedRefSetType.ATTRIBUTE_VALUE, SnomedDescription.TYPE)
			.put(SnomedRefSetType.ATTRIBUTE_VALUE, SnomedRelationship.TYPE)
			.put(SnomedRefSetType.CONCRETE_DATA_TYPE, TerminologyRegistry.UNSPECIFIED)
			.put(SnomedRefSetType.COMPLEX_MAP, SnomedConcept.TYPE)
			.put(SnomedRefSetType.COMPLEX_BLOCK_MAP, SnomedConcept.TYPE)
			.put(SnomedRefSetType.DESCRIPTION_TYPE, SnomedConcept.TYPE)
			.put(SnomedRefSetType.EXTENDED_MAP, SnomedConcept.TYPE)
			.put(SnomedRefSetType.LANGUAGE, SnomedDescription.TYPE)
			.put(SnomedRefSetType.MODULE_DEPENDENCY, SnomedConcept.TYPE)
			.put(SnomedRefSetType.QUERY, SnomedConcept.TYPE)
			.put(SnomedRefSetType.SIMPLE, SnomedConcept.TYPE)
			.put(SnomedRefSetType.SIMPLE, SnomedDescription.TYPE)
			.put(SnomedRefSetType.SIMPLE, SnomedRelationship.TYPE)
			.put(SnomedRefSetType.SIMPLE_MAP, SnomedConcept.TYPE)
			.put(SnomedRefSetType.OWL_AXIOM, SnomedConcept.TYPE)
			.put(SnomedRefSetType.OWL_ONTOLOGY, SnomedConcept.TYPE)
			.put(SnomedRefSetType.MRCM_DOMAIN, SnomedConcept.TYPE)
			.put(SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN, SnomedConcept.TYPE)
			.put(SnomedRefSetType.MRCM_ATTRIBUTE_RANGE, SnomedConcept.TYPE)
			.put(SnomedRefSetType.MRCM_MODULE_SCOPE, SnomedConcept.TYPE)
			.put(SnomedRefSetType.SIMPLE_MAP, SnomedDescription.TYPE)
			.put(SnomedRefSetType.SIMPLE_MAP_WITH_DESCRIPTION, SnomedConcept.TYPE)
			.put(SnomedRefSetType.SIMPLE_MAP_WITH_DESCRIPTION, SnomedDescription.TYPE)
			.build();

	public static boolean isSupported(SnomedRefSetType type) {
		return SUPPORTED_REFERENCED_COMPONENTS.containsKey(type);
	}

	public static boolean isReferencedComponentTypeSupported(SnomedRefSetType type, String referencedComponentType) {
		return getSupportedReferencedComponentTypes(type).contains(referencedComponentType);
	}

	public static Collection<String> getSupportedReferencedComponentTypes(SnomedRefSetType type) {
		return SUPPORTED_REFERENCED_COMPONENTS.get(type);
	}

	/**
	 * Checks whether the given {@link SnomedRefSetType} is supported via the {@link Request}-based API or not. Throws {@link NotImplementedException}
	 * if not.
	 * 
	 * @param type
	 * @throws NotImplementedException
	 */
	public static void check(SnomedRefSetType type) {
		if (!isSupported(type)) {
			throw new NotImplementedException("'%s' type reference sets are not supported", type);
		}
	}

	/**
	 * Checks whether the given {@link SnomedRefSetType} supports members of the given referenced component type. Throws {@link BadRequestException}
	 * if not.
	 * 
	 * @param type
	 * @param referencedComponentType
	 * @throws BadRequestException
	 */
	public static void checkType(SnomedRefSetType type, String referencedComponentType) {
		if (!isReferencedComponentTypeSupported(type, referencedComponentType)) {
			final String supportedReferencedComponentTypes = Joiner.on(",").join(getSupportedReferencedComponentTypes(type));
			throw new BadRequestException("'%s' type reference set does not support '%s' referenced component type. Only '%s' are supported.", type,
					referencedComponentType, supportedReferencedComponentTypes);
		}
	}
}
