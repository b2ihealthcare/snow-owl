/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.cis;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.cis.reservations.ISnomedIdentifierReservationService;
import com.google.common.collect.Maps;

/**
 * @since 4.5
 */
public abstract class AbstractSnomedIdentifierService implements ISnomedIdentifierService {

	public static final class SctIdStatusException extends BadRequestException {
		
		private static final long serialVersionUID = 1L;

		public SctIdStatusException(String message, Map<String, SctId> problemSctIds) {
			super(message, problemSctIds.size());
			withAdditionalInfo(Map.copyOf(Maps.transformValues(problemSctIds, SctId::getStatus)));
		}

	}

	protected static void checkCategory(ComponentCategory category) {
		checkArgument(category == ComponentCategory.CONCEPT 
				|| category == ComponentCategory.DESCRIPTION
				|| category == ComponentCategory.RELATIONSHIP, "Cannot generate ID for component category %s.", category);
	}

	private final ISnomedIdentifierReservationService reservationService;
	private final SnomedIdentifierConfiguration config;

	protected AbstractSnomedIdentifierService(final ISnomedIdentifierReservationService reservationService, SnomedIdentifierConfiguration config) {
		this.reservationService = checkNotNull(reservationService);
		this.config = checkNotNull(config);
	}

	protected final ISnomedIdentifierReservationService getReservationService() {
		return reservationService;
	}
	
	protected final SnomedIdentifierConfiguration getConfig() {
		return config;
	}

}
