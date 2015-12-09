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
import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.google.common.base.Strings;

/**
 * @since 4.5
 */
public abstract class AbstractSnomedIdentifierService implements ISnomedIdentifierService {

	private final ISnomedIdentiferReservationService reservationService;
	private final SnomedIdentifierConfiguration config;

	protected AbstractSnomedIdentifierService(final ISnomedIdentiferReservationService reservationService, SnomedIdentifierConfiguration config) {
		this.reservationService = checkNotNull(reservationService);
		this.config = checkNotNull(config);
	}

	protected final void checkCategory(ComponentCategory category) {
		checkArgument(category == ComponentCategory.CONCEPT || category == ComponentCategory.DESCRIPTION
				|| category == ComponentCategory.RELATIONSHIP, "Cannot generate ID for component category %s.", category);
	}
	
	protected final ISnomedIdentiferReservationService getReservationService() {
		return reservationService;
	}
	
	protected final SnomedIdentifierConfiguration getConfig() {
		return config;
	}

	/**
	 * Method to enforce the namespace used for the new component ID if defined.
	 * @param namespace
	 * @return
	 */
	protected final String selectNamespace(final String namespace) {
		final String enforceNamespace = getConfig().getEnforceNamespace();
		if (!Strings.isNullOrEmpty(enforceNamespace)) {
			if (SnomedIdentifiers.INT_NAMESPACE.equals(enforceNamespace)) {
				return null;
			} else {
				return enforceNamespace;
			}
		}
		return namespace;
	}

}
