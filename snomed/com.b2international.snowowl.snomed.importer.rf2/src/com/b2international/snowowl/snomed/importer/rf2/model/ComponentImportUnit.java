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
package com.b2international.snowowl.snomed.importer.rf2.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import com.b2international.snowowl.snomed.importer.AbstractImportUnit;
import com.b2international.snowowl.snomed.importer.Importer;
import com.google.common.collect.Ordering;

/**
 * An {@link AbstractImportUnit} for SNOMED CT components. Carries the effective time,
 * the component type, the location of the release slice and the number of
 * records.
 */
public class ComponentImportUnit extends AbstractImportUnit {

	public static final Ordering<AbstractImportUnit> ORDERING = EffectiveTimeUnitOrdering.INSTANCE.compound(TypeUnitOrdering.INSTANCE);
	
	private String effectiveTimeKey;

	private final ComponentImportType type;
	private final File unitFile;
	private final int recordCount;
	
	public ComponentImportUnit(final Importer parent, final String effectiveTimeKey, final ComponentImportType type, final File unitFile, 
			final int recordCount) {
		
		super(parent);
		this.effectiveTimeKey = checkNotNull(effectiveTimeKey, "effectiveTimeKey");
		this.type = checkNotNull(type, "type");
		this.unitFile = checkNotNull(unitFile, "unitFile");
		this.recordCount = recordCount;
	}
	
	public String getEffectiveTimeKey() {
		return effectiveTimeKey;
	}
	
	public void setEffectiveTimeKey(String effectiveTimeKey) {
		this.effectiveTimeKey = effectiveTimeKey;
	}
	
	public ComponentImportType getType() {
		return type;
	}
	
	public File getUnitFile() {
		return unitFile;
	}
	
	public int getRecordCount() {
		return recordCount;
	}

	@Override
	public String toString() {
		return String.format("ComponentImportUnit [importer=%s, effectiveTimeKey=%s, type=%s, unitFile=%s, recordCount=%d]", 
				getImporter(), getEffectiveTimeKey(), getType(), getUnitFile(), getRecordCount());
	}
}
