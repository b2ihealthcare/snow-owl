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
package com.b2international.snowowl.snomed.datastore.id.store;

import com.b2international.snowowl.datastore.store.MemStore;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifier;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.id.cis.IdentifierStatus;
import com.b2international.snowowl.snomed.datastore.id.cis.SctId;

/**
 * @since 4.5
 */
public class SnomedIdentifierMemStore extends MemStore<SctId> {

	@Override
	public SctId get(final String componentId) {
		final SctId storedSctId = super.get(componentId);
		if (null != storedSctId) {
			return storedSctId;
		} else {
			final SnomedIdentifier identifier = SnomedIdentifiers.of(componentId);
			final SctId sctId = new SctId();
			sctId.setSctid(componentId);
			sctId.setStatus(IdentifierStatus.AVAILABLE.getSerializedName());
			sctId.setNamespace(Integer.valueOf(identifier.getNamespace()));
			sctId.setPartitionId(String.valueOf(identifier.getPartitionIdentifier()));
			sctId.setCheckDigit(identifier.getCheckDigit());

			// TODO set remaining attributes?

			return sctId;
		}
	}

}
