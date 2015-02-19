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
package com.b2international.snowowl.snomed.importer.rf2.refset;

import java.io.InputStream;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.snomed.importer.rf2.csv.AssociatingRefSetRow;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;

public abstract class AbstractSnomedMapTypeRefSetImporter<T extends AssociatingRefSetRow> extends AbstractSnomedRefSetImporter<T, SnomedSimpleMapRefSetMember> {

	public AbstractSnomedMapTypeRefSetImporter(final SnomedImportConfiguration<T> importConfiguration, final SnomedImportContext importContext, 
			final InputStream releaseFileStream, final String releaseFileIdentifier) {
		
		super(importConfiguration, importContext, releaseFileStream, releaseFileIdentifier);
	}

	@Override
	protected SnomedRefSet createUninitializedRefSet(final String identifierConceptId) {
		return SnomedRefSetFactory.eINSTANCE.createSnomedMappingRefSet();
	}

	@Override
	protected void initRefSet(final SnomedRefSet refSet, final String referencedComponentId) {
		((SnomedMappingRefSet) refSet).setMapTargetComponentType(CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT);
	}
}