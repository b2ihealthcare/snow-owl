/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.api.browser.ITerminologyBrowser;
import com.b2international.snowowl.datastore.browser.ActiveBranchClientTerminologyBrowser;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;

/**
 * @since 4.6
 */
public abstract class BaseSnomedClientTerminologyBrowser extends ActiveBranchClientTerminologyBrowser<SnomedConceptIndexEntry, String> {

	private final IEventBus bus;

	protected BaseSnomedClientTerminologyBrowser(ITerminologyBrowser<SnomedConceptIndexEntry, String> wrappedBrowser, IEventBus bus) {
		super(wrappedBrowser);
		this.bus = bus;
	}
	
	protected final IEventBus getBus() {
		return bus;
	}
	
	@Override
	protected final EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
	
}
