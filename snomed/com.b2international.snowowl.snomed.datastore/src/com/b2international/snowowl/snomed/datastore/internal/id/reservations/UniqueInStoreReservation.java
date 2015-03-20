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

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifier;
import com.b2international.snowowl.snomed.datastore.id.reservations.Reservation;
import com.google.inject.Provider;

/**
 * Reserves all currently taken IDs by using the given {@link SnomedTerminologyBrowser#isUniqueId(com.b2international.snowowl.core.api.IBranchPath, String)}
 * 
 * @since 4.0
 */
public class UniqueInStoreReservation implements Reservation {

	private Provider<SnomedTerminologyBrowser> browser;

	public UniqueInStoreReservation(Provider<SnomedTerminologyBrowser> browser) {
		this.browser = checkNotNull(browser, "browser");
	}
	
	@Override
	public boolean includes(SnomedIdentifier identifier) {
		return !browser.get().isUniqueId(BranchPathUtils.createMainPath(), identifier.toString());
	}

}
