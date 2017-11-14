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
package com.b2international.snowowl.snomed.importer;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.runtime.SubMonitor;

/**
 * Represents an abstract data unit which can be imported in a single run.
 * 
 */
public abstract class AbstractImportUnit {

	private final Importer importer;

	public AbstractImportUnit(final Importer importer) {
		this.importer = checkNotNull(importer, "importer");
	}
	
	public Importer getImporter() {
		return importer;
	}
	
	public void doImport(final SubMonitor subMonitor) {
		importer.doImport(subMonitor, this);
	}
}