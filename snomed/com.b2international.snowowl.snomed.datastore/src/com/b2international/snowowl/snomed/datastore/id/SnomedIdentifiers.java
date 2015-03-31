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
package com.b2international.snowowl.snomed.datastore.id;

import static com.google.common.base.Preconditions.checkArgument;

import com.b2international.commons.VerhoeffCheck;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.gen.SingleItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.internal.id.SnomedIdentifierImpl;
import com.b2international.snowowl.snomed.datastore.internal.id.SnomedIdentifierServiceImpl;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.SnomedIdentifierReservationServiceImpl;
import com.google.common.base.Strings;

/**
 * Shortcut methods to create SNOMED CT Identifiers.
 * <p><i>TODO: add support to track/take into account global reservations, currently it uses internal ID and Reservation services</i></p>
 * <p>Mostly used from test cases</p> 
 * 
 * @since 4.0
 */
public class SnomedIdentifiers {

	private SnomedIdentifiers() {
	}

	public static String generateConceptId() {
		return generateConceptId(null);
	}

	public static String generateConceptId(String namespace) {
		return generateComponentId(ComponentCategory.CONCEPT, namespace);
	}

	public static String generateRelationshipId() {
		return generateRelationshipId(null);
	}

	public static String generateRelationshipId(String namespace) {
		return generateComponentId(ComponentCategory.RELATIONSHIP, namespace);
	}

	public static String generateDescriptionId() {
		return generateRelationshipId(null);
	}

	public static String generateDescriptionId(String namespace) {
		return generateComponentId(ComponentCategory.DESCRIPTION, namespace);
	}

	private static String generateComponentId(ComponentCategory component, String namespace) {
		return getSnomedIdentifierService().generateId(component, namespace);
	}

	private static ISnomedIdentifierService getSnomedIdentifierService() {
		return new SnomedIdentifierServiceImpl(new SnomedIdentifierReservationServiceImpl());
	}

	/**
	 * Creates a {@link SnomedIdentifierImpl} from the given {@link String} componentId.
	 * 
	 * @param componentId
	 * @return
	 */
	public static SnomedIdentifier of(String componentId) {
		validate(componentId);
		final int checkDigit = Character.getNumericValue(componentId.charAt(componentId.length() - 1));
		final int componentIdentifier = Character.getNumericValue(componentId.charAt(componentId.length() - 2));
		final int partitionIdentifier = Character.getNumericValue(componentId.charAt(componentId.length() - 3));
		final String namespace = partitionIdentifier == 0 ? null : componentId.substring(componentId.length() - 10, componentId.length() - 3);
		final long itemId = partitionIdentifier == 0 ? Long.parseLong(componentId.substring(0, componentId.length() - 3)) : Long
				.parseLong(componentId.substring(0, componentId.length() - 10));
		return new SnomedIdentifierImpl(itemId, namespace, partitionIdentifier, componentIdentifier, checkDigit);
	}

	/**
	 * Validates the given componentId by using the rules defined in the latest SNOMED CT Identifier specification, which are the following constraints:
	 * <ul>
	 * 	<li>Can't start with leading zeros</li>
	 * 	<li>Lengths should be between 6 and 18 characters</li>
	 * 	<li>Should parse to a long value</li>
	 * 	<li>Should pass the Verhoeff check-digit test</li>
	 * </ul>
	 * 
	 * @param componentId
	 * @see VerhoeffCheck
	 * @throws IllegalArgumentException - if the given componentId is invalid according to the SNOMED CT Identifier specification
	 */
	public static void validate(String componentId) throws IllegalArgumentException {
		checkArgument(!Strings.isNullOrEmpty(componentId), "ComponentId must be defined");
		checkArgument(!componentId.startsWith("0"), "ComponentId can't start with leading zeros");
		checkArgument(componentId.length() >= 6 && componentId.length() <= 18, "ComponentId's length should be between 6-18 character length");
		try {
			Long.parseLong(componentId);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("ComponentId should parse to a Long");
		}
		checkArgument(VerhoeffCheck.validateLastChecksumDigit(componentId), "ComponentId should pass Verhoeff check-digit test");
	}

	/**
	 * Generates a valid SNOMED CT Identifier from the given spec, which should be sufficient for a SNOMED CT Identifier.
	 * 
	 * @param itemId
	 *            - the itemId to use for the newly created SNOMED CT Identifier
	 * @param component
	 *            - the component type to use
	 * @return
	 */
	public static SnomedIdentifier generateFrom(int itemId, ComponentCategory component) {
		return generateFrom(itemId, null, component);
	}

	/**
	 * Generates a valid SNOMED CT Identifier from the given spec, which should be sufficient for a SNOMED CT Identifier.
	 * 
	 * @param itemId
	 *            - the itemId to use for the newly created SNOMED CT Identifier
	 * @param namespace
	 *            - the namespace to use
	 * @param component
	 *            - the component type to use
	 * @return
	 */
	public static SnomedIdentifier generateFrom(int itemId, String namespace, ComponentCategory component) {
		return of(new SnomedIdentifierServiceImpl(new SnomedIdentifierReservationServiceImpl(), new SingleItemIdGenerationStrategy(String.valueOf(itemId))).generateId(component, namespace));
	}

}
