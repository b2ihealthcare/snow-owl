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
package com.b2international.snowowl.snomed.datastore.internal.id;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.commons.VerhoeffCheck;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.google.common.base.Strings;

/**
 * SNOMED CT Identifiers v0.4:
 * <p />
 * <i>An item identifier can have a lowest permissible value of 100 (three digits) and a highest permissible value of 99999999 (8 digits) for short
 * format identifiers or 999999999999999 (15 digits) for long format identifiers. Leading zeros are not permitted in the item identifier.<//>
 * 
 * @since 4.0
 */
public class SnomedIdentifierServiceImpl implements ISnomedIdentifierService {

	private ItemIdGenerationStrategy itemIdGenerationStrategy;
	private ISnomedIdentiferReservationService reservationService;

	public SnomedIdentifierServiceImpl(final ISnomedIdentiferReservationService reservationService) {
		this(reservationService, ItemIdGenerationStrategy.RANDOM);
	}
	
	public SnomedIdentifierServiceImpl(final ISnomedIdentiferReservationService reservationService, ItemIdGenerationStrategy itemIdGenerationStrategy) {
		this.reservationService = checkNotNull(reservationService, "reservationService");
		this.itemIdGenerationStrategy = checkNotNull(itemIdGenerationStrategy, "itemIdGenerationStrategy");
	}
	
	@Override
	public String generateId(ComponentCategory component) {
		return generateId(component, null);
	}

	@Override
	public String generateId(ComponentCategory component, String namespace) {
		checkNotNull(component, "componentNature");
		checkCategory(component);
		String newId = generateComponentId(component, namespace);
		while (reservationService.isReserved(newId)) {
			newId = generateComponentId(component, namespace);
		}
		return newId;
	}

	private void checkCategory(ComponentCategory component) {
		checkArgument(component == ComponentCategory.CONCEPT || component == ComponentCategory.DESCRIPTION || component == ComponentCategory.RELATIONSHIP, "Cannot generate ID for componentCategory %s", component);
	}

	private String generateComponentId(ComponentCategory component, String namespace) {
		final StringBuilder buf = new StringBuilder();
		// generate the SCT Item ID
		buf.append(itemIdGenerationStrategy.generateItemId());
		// append namespace and the first part of the partition-identifier
		if (Strings.isNullOrEmpty(namespace)) {
			buf.append('0');
		} else {
			buf.append(namespace);
			buf.append('1');
		}
		// append the second part of the partition-identifier
		buf.append(component.ordinal());
		// calc check-digit
		buf.append(VerhoeffCheck.calculateChecksum(buf, false));
		return buf.toString();
	}
	
}
