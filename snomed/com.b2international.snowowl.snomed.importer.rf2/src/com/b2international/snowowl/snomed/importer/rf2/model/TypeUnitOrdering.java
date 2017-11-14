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

import com.b2international.snowowl.snomed.importer.AbstractImportUnit;
import com.google.common.collect.Ordering;

/**
 * Orders import unit by effective time first, then by component type. 
 */
public class TypeUnitOrdering extends Ordering<AbstractImportUnit> {

	public static final Ordering<AbstractImportUnit> INSTANCE = new TypeUnitOrdering();

	private TypeUnitOrdering() { }

	@Override
	public int compare(final AbstractImportUnit left, final AbstractImportUnit right) {

		final ComponentImportUnit castLeft = (ComponentImportUnit) left;
		final ComponentImportUnit castRight = (ComponentImportUnit) right;

		return castLeft.getType().compareTo(castRight.getType());
	}
}
