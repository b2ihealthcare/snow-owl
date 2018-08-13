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

import java.util.Collection;

import com.b2international.commons.VerhoeffCheck;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.action.BulkDeprecateAction;
import com.b2international.snowowl.snomed.datastore.id.action.BulkGenerateAction;
import com.b2international.snowowl.snomed.datastore.id.action.BulkPublishAction;
import com.b2international.snowowl.snomed.datastore.id.action.BulkRegisterAction;
import com.b2international.snowowl.snomed.datastore.id.action.BulkReserveAction;
import com.b2international.snowowl.snomed.datastore.id.action.DeprecateAction;
import com.b2international.snowowl.snomed.datastore.id.action.GenerateAction;
import com.b2international.snowowl.snomed.datastore.id.action.IIdAction;
import com.b2international.snowowl.snomed.datastore.id.action.PublishAction;
import com.b2international.snowowl.snomed.datastore.id.action.RegisterAction;
import com.b2international.snowowl.snomed.datastore.id.action.ReserveAction;
import com.b2international.snowowl.snomed.datastore.internal.id.SnomedComponentIdentifierValidator;
import com.b2international.snowowl.snomed.datastore.internal.id.SnomedIdentifierImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * Utility methods to create SNOMED CT Identifiers and derive different parts from their string representations.
 * 
 * @since 4.0
 */
public class SnomedIdentifiers {
	
	public static final String INT_NAMESPACE = "INT";
	
	public static final long MIN_INT_ITEMID = 100L; // inclusive
	public static final long MIN_INT_METADATA_ITEMID = 9000_0000_0000_000L; // inclusive
	public static final long MAX_INT_ITEMID = MIN_INT_METADATA_ITEMID; // 15 digits for itemId, restricted to non-metadata IDs, exclusive
	
	public static final long MIN_NAMESPACE_ITEMID = 1L; // inclusive
	public static final long MAX_NAMESPACE_ITEMID = 9999_9999L + 1L; // 8 digits for itemId, exclusive

	private final ISnomedIdentifierService identifierService;
	
	private final Collection<IIdAction<?>> executedActions = Lists.newArrayList();

	public SnomedIdentifiers(final ISnomedIdentifierService identifierService) {
		this.identifierService = identifierService;
	}
	
	public void rollback() {
		for (final IIdAction<?> action : executedActions) {
			action.rollback();
		}
	}

	public void commit() {
		for (final IIdAction<?> action : executedActions) {
			action.commit();
		}
	}

	public String generate(final String namespace, final ComponentCategory category) {
		final GenerateAction action = new GenerateAction(namespace, category, identifierService);
		executeAction(action);

		return action.get();
	}

	public void register(final String componentId) {
		validate(componentId);
		
		final RegisterAction action = new RegisterAction(componentId, identifierService);
		executeAction(action);
	}

	public String reserve(final String namespace, final ComponentCategory category) {
		final ReserveAction action = new ReserveAction(namespace, category, identifierService);
		executeAction(action);

		return action.get();
	}

	public void deprecate(final String componentId) {
		validate(componentId);
		
		final DeprecateAction action = new DeprecateAction(componentId, identifierService);
		executeAction(action);
	}

	public void publish(final String componentId) {
		validate(componentId);
		
		final PublishAction action = new PublishAction(componentId, identifierService);
		executeAction(action);
	}

	public Collection<String> generate(final String namespace, final ComponentCategory category, final int quantity) {
		final BulkGenerateAction action = new BulkGenerateAction(namespace, category, quantity, identifierService);
		executeAction(action);

		return action.get();
	}

	public void register(final Collection<String> componentIds) {
		validate(componentIds);
		
		final BulkRegisterAction action = new BulkRegisterAction(componentIds, identifierService);
		executeAction(action);
	}

	public Collection<String> reserve(final String namespace, final ComponentCategory category, final int quantity) {
		final BulkReserveAction action = new BulkReserveAction(namespace, category, quantity, identifierService);
		executeAction(action);

		return action.get();
	}

	public void deprecate(final Collection<String> componentIds) {
		validate(componentIds);
		
		final BulkDeprecateAction action = new BulkDeprecateAction(componentIds, identifierService);
		executeAction(action);
	}

	public void publish(final Collection<String> componentIds) {
		validate(componentIds);
		
		final BulkPublishAction action = new BulkPublishAction(componentIds, identifierService);
		executeAction(action);
	}

	private void executeAction(final IIdAction<?> action) {
		try {
			executedActions.add(action);
			action.execute();
		} catch (Exception e) {
			action.setFailed(true);
			throw e;
		}
	}

	/**
	 * Creates a {@link SnomedIdentifierImpl} from the given {@link String} componentId.
	 * 
	 * @param componentId
	 * @return
	 */
	public static SnomedIdentifier create(String componentId) {
		validate(componentId);
		
		try {
			final int checkDigit = Character.getNumericValue(componentId.charAt(componentId.length() - 1));
			final int componentIdentifier = getComponentIdentifier(componentId);
			final int partitionIdentifier = Character.getNumericValue(componentId.charAt(componentId.length() - 3));
			final String namespace = partitionIdentifier == 0 ? null
					: componentId.substring(componentId.length() - 10, componentId.length() - 3);
			final long itemId = partitionIdentifier == 0 ? Long.parseLong(componentId.substring(0, componentId.length() - 3))
					: Long.parseLong(componentId.substring(0, componentId.length() - 10));
			return new SnomedIdentifierImpl(itemId, namespace, partitionIdentifier, componentIdentifier, checkDigit);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(String.format("Invalid SNOMED identifier: %s.", componentId));
		}
	}
	
	/**
	 * @param componentId - the ID to check
	 * @return <code>true</code> if the given componentId is a valid SNOMED CT core component identifier, <code>false</code> otherwise.
	 * @see #validate(String)
	 */
	public static boolean isValid(String componentId) {
		try {
			validate(componentId);
			return true;
		} catch (final IllegalArgumentException e) {
			return false;
		}
	}
	
	/**
	 * Validates the given collection of componentIds by using the rules defined
	 * in the latest SNOMED CT Identifier specification, which are the following
	 * constraints:
	 * <ul>
	 * <li>Can't start with leading zeros</li>
	 * <li>Lengths should be between 6 and 18 characters</li>
	 * <li>Should parse to a long value</li>
	 * <li>Should pass the Verhoeff check-digit test</li>
	 * </ul>
	 * 
	 * @param componentIds
	 * @see VerhoeffCheck
	 * @throws IllegalArgumentException
	 *             - if the given collection contains a component ID which is
	 *             invalid according to the SNOMED CT Identifier specification
	 */
	public static void validate(Collection<String> componentIds) {
		for (final String componentId : componentIds) {
			validate(componentId);
		}
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
			throw new IllegalArgumentException("ComponentId should be a number");
		}
		
		final CharSequence idHead = componentId.subSequence(0, componentId.length() - 1);
		final char originalChecksum = componentId.charAt(componentId.length() - 1);
		final char checksum = VerhoeffCheck.calculateChecksum(idHead, false);

		checkArgument(VerhoeffCheck.validateLastChecksumDigit(componentId), "%s has incorrect Verhoeff check-digit; expected %s, was %s", componentId, checksum, originalChecksum);
	}
	
	/**
	 * Extracts the component identifier from the given component ID.
	 * 
	 * @param componentId
	 * @return
	 */
	private static int getComponentIdentifier(String componentId) {
		final char ciChar = componentId.charAt(componentId.length() - 2);
		final int ci = Character.digit(ciChar, 10);
		if (ci == -1) {
			throw new IllegalArgumentException("Invalid component identifier " + ciChar);
		}
		return ci;
	}
	
	/**
	 * Returns the component category for a SNOMED CT identifier, or <code>null</code> in case of invalid 
	 * @param componentId
	 * @return
	 * @throws IllegalArgumentException - if the given component ID is not a valid SNOMED CT Component Identifier
	 */
	public static ComponentCategory getComponentCategory(String componentId) {
		validate(componentId);
		return ComponentCategory.getByOrdinal(getComponentIdentifier(componentId));
	}
	
	/**
	 * Constructs a {@link SnomedIdentifierValidator} to validate IDs of the given {@link ComponentCategory}.
	 * @param category
	 * @return
	 */
	public static SnomedIdentifierValidator getIdentifierValidator(ComponentCategory category) {
		switch (category) {
		case CONCEPT:
		case DESCRIPTION:
		case RELATIONSHIP:
			return new SnomedComponentIdentifierValidator(category);
		default: throw new UnsupportedOperationException("Can't create validator for category: " + category);
		}
	}
	
	public boolean importSupported() {
		return identifierService.importSupported();
	}
	
}
