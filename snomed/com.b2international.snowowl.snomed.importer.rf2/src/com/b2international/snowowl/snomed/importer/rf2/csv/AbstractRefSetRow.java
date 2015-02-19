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
package com.b2international.snowowl.snomed.importer.rf2.csv;

import java.util.UUID;

/**
 * Abstract superclass for reference set release file row POJOs. Provides
 * storage for the following CSV fields:
 * <ul>
 * <li>{@code id}
 * <li>{@code refsetId}
 * </ul>
 * 
 */
public abstract class AbstractRefSetRow extends AbstractComponentRow {

	public static final String PROP_UUID = "uuid";
	public static final String PROP_REF_SET_ID = "refSetId";
	
	private UUID uuid;
	private String refSetId;

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(final UUID uuid) {
		this.uuid = uuid;
	}
	
	public String getRefSetId() {
		return refSetId;
	}
	
	public void setRefSetId(final String refSetId) {
		this.refSetId = refSetId;
	}
}