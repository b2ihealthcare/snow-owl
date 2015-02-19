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
import java.util.Date;

import com.b2international.snowowl.importer.AbstractImportUnit;
import com.b2international.snowowl.importer.Importer;
import com.google.common.collect.Ordering;

/**
 * An {@link AbstractImportUnit} for SNOMED CT components. Carries the effective time,
 * the component type, the location of the release slice and the number of
 * records.
 * 
 */
public class ComponentImportUnit extends AbstractImportUnit {

	private static class UnitOrdering extends Ordering<AbstractImportUnit> {

		@Override
		public int compare(AbstractImportUnit left, AbstractImportUnit right) {

			final ComponentImportUnit castLeft = (ComponentImportUnit) left;
			final ComponentImportUnit castRight = (ComponentImportUnit) right;
			
			final int dateComparison = castLeft.getEffectiveTime().compareTo(castRight.getEffectiveTime());
			
			if (dateComparison != 0) {
				return dateComparison; 
			}
			
			return castLeft.getType().compareTo(castRight.getType());
		}
	}
	
	public static final Ordering<AbstractImportUnit> ORDERING = new UnitOrdering();
	
	private final Date effectiveTime;
	private final ComponentImportType type;
	private final File unitFile;
	private final int recordCount;
	
	public ComponentImportUnit(final Importer parent, final Date effectiveTime, final ComponentImportType type, final File unitFile, 
			final int recordCount) {
		
		super(parent);
		this.effectiveTime = checkNotNull(effectiveTime, "effectiveTime");
		this.type = checkNotNull(type, "type");
		this.unitFile = checkNotNull(unitFile, "unitFile");
		this.recordCount = recordCount;
	}
	
	public Date getEffectiveTime() {
		return effectiveTime;
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
		return String.format("ComponentImportUnit [importer=%s, effectiveTime=%s, type=%s, unitFile=%s, recordCount=%d]", 
				getImporter(), getEffectiveTime(), getType(), getUnitFile(), getRecordCount());
	}
}