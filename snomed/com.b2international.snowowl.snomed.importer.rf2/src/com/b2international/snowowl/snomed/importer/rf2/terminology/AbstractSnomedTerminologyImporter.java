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
package com.b2international.snowowl.snomed.importer.rf2.terminology;

import java.io.InputStream;
import java.util.Date;

import org.eclipse.core.runtime.SubMonitor;

import com.b2international.snowowl.importer.ImportAction;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.importer.rf2.csv.AbstractTerminologyComponentRow;
import com.b2international.snowowl.snomed.importer.rf2.model.AbstractSnomedImporter;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;

public abstract class AbstractSnomedTerminologyImporter<T extends AbstractTerminologyComponentRow, C extends Component> extends AbstractSnomedImporter<T, C> {

	protected AbstractSnomedTerminologyImporter(final SnomedImportConfiguration<T> importConfiguration, 
			final SnomedImportContext importContext, 
			final InputStream releaseFileStream, 
			final String releaseFileIdentifier) {
		
		super(importConfiguration, importContext, releaseFileStream, releaseFileIdentifier);
	}

	@Override
	protected ImportAction commit(final SubMonitor subMonitor, final String formattedEffectiveTime) {
		final ImportAction action = super.commit(subMonitor, formattedEffectiveTime);
		getComponentLookup().registerNewComponents();
		return action;
	}
	
	protected ComponentLookup<Component> getComponentLookup() {
		return getImportContext().getComponentLookup();
	}
	
	@Override
	protected Date getComponentEffectiveTime(C editedComponent) {
		return editedComponent.getEffectiveTime();
	}
}