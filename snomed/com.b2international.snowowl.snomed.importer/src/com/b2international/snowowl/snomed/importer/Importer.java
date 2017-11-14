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

import java.util.List;

import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;

/**
 * Collects operations for importing data into Snow Owl.
 * 
 */
public interface Importer {
	
	/**
	 * Performs any one-time initialization necessary for the import.
	 * 
	 * @param subMonitor
	 *            the {@link SubMonitor} instance to report progress on (may not
	 *            be {@code null})
	 */
	public void preImport(SubMonitor subMonitor);
	
	/**
	 * Collects all units of data available from the import source.
	 * 
	 * @param subMonitor
	 *            the {@link SubMonitor} instance to report progress on (may not
	 *            be {@code null})
	 * 
	 * @return a list of detected import units
	 */
	public List<? extends AbstractImportUnit> getImportUnits(SubMonitor subMonitor);
	
	/**
	 * Imports a single unit of data.
	 * <p>
	 * Intended to be called from {@link AbstractImportUnit#doImport(SubMonitor)} only,
	 * so the actual sub-type of ImportUnit can be known in advance.
	 * 
	 * @param subMonitor
	 *            the {@link SubMonitor} instance to report progress on (may not
	 *            be {@code null})
	 * 
	 * @param unit
	 *            the unit to import (may not be {@code null})
	 */
	public void doImport(SubMonitor subMonitor, AbstractImportUnit unit);
	
	/**
	 * Cleans up any temporary resources created during the process.  
	 * 
	 * @param subMonitor
	 *            the {@link SubMonitor} instance to report progress on (may not
	 *            be {@code null})
	 */
	public void postImport(SubMonitor subMonitor);
	
	/**
	 * @return the {@link Logger} instance to report import messages on
	 */
	public Logger getLogger();
}