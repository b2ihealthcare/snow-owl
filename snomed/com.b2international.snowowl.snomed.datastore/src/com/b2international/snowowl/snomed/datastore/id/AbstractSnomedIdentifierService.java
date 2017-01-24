/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Map;

import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration;
import com.b2international.snowowl.snomed.datastore.id.domain.SctId;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @since 4.5
 */
public abstract class AbstractSnomedIdentifierService implements ISnomedIdentifierService {

	public static final class SctIdStatusException extends BadRequestException {
		
		private final Map<String, Object> additionalInfo;

		public SctIdStatusException(String message, Map<String, SctId> problemSctIds) {
			super(message, problemSctIds.size());
			this.additionalInfo = ImmutableMap.copyOf(Maps.transformValues(problemSctIds, SctId::getStatus));
		}

		@Override
		protected Map<String, Object> getAdditionalInfo() {
			return additionalInfo;
		}
	}

	protected static void checkCategory(ComponentCategory category) {
		checkArgument(category == ComponentCategory.CONCEPT 
				|| category == ComponentCategory.DESCRIPTION
				|| category == ComponentCategory.RELATIONSHIP, "Cannot generate ID for component category %s.", category);
	}

	private final ISnomedIdentiferReservationService reservationService;
	private final SnomedIdentifierConfiguration config;

	protected AbstractSnomedIdentifierService(final ISnomedIdentiferReservationService reservationService, SnomedIdentifierConfiguration config) {
		this.reservationService = checkNotNull(reservationService);
		this.config = checkNotNull(config);
	}

	protected final ISnomedIdentiferReservationService getReservationService() {
		return reservationService;
	}
	
	protected final SnomedIdentifierConfiguration getConfig() {
		return config;
	}

	/**
	 * Checks if the server's configuration specifies a fixed namespace to use for all operations.
	 * <p>
	 * If such a setting is present, any attempts of generating component IDs for a different namespace will be
	 * changed to use the fixed namespace instead, otherwise the received namespace argument will pass through
	 * unmodified. 
	 */
	protected final String selectNamespace(final String namespace) {
		final String namespaceInConfig = getConfig().getEnforceNamespace();

		// Is a fixed namespace given in the configuration file?
		if (Strings.isNullOrEmpty(namespaceInConfig)) {
			return namespace;
		}
		
		if (SnomedIdentifiers.INT_NAMESPACE.equals(namespaceInConfig)) {
			return null;
		} else {
			return namespaceInConfig;
		}
	}

}
