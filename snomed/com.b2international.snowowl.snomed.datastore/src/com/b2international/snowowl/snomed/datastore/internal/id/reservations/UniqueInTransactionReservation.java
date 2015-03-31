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
package com.b2international.snowowl.snomed.datastore.internal.id.reservations;

import static com.google.common.base.Preconditions.checkArgument;

import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifier;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.b2international.snowowl.snomed.datastore.id.reservations.Reservation;

/**
 * Reserves all IDs taken by the new components in the given {@link SnomedEditingContext}. 
 * <p><i>NOTE: Its the transactions responsibility to
 * register/unregister a created instance via {@link ISnomedIdentiferReservationService#create(String, Reservation)} and
 * {@link ISnomedIdentiferReservationService#delete(String)}</i></p>
 * 
 * @since 4.0
 */
public class UniqueInTransactionReservation implements Reservation {

	private SnomedEditingContext context;

	public UniqueInTransactionReservation(SnomedEditingContext context) {
		checkArgument(context != null && !context.isClosed(), "Given context (%s) was null or its transaction was already closed", context);
		this.context = context;
	}

	@Override
	public boolean includes(SnomedIdentifier identifier) {
		return !context.isUniqueInTransaction(identifier);
	}
}
