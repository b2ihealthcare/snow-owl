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
package com.b2international.snowowl.snomed.importer.rf2;

import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;

/**
 * @since 4.3
 */
public class Rf2RefSet extends FakeEObjectImpl implements SnomedRefSet {

	private SnomedRefSetType type;
	private short refComponentType;
	private String refSetId;

	public Rf2RefSet(long storageKey, final String refSetId, SnomedRefSetType type, short refComponentType) {
		super(storageKey);
		this.refSetId = refSetId;
		this.type = type;
		this.refComponentType = refComponentType;
	}

	@Override
	public SnomedRefSetType getType() {
		return type;
	}

	@Override
	public void setType(SnomedRefSetType value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public short getReferencedComponentType() {
		return refComponentType;
	}

	@Override
	public void setReferencedComponentType(short value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getIdentifierId() {
		return refSetId;
	}

	@Override
	public void setIdentifierId(String value) {
		throw new UnsupportedOperationException();
	}

}
