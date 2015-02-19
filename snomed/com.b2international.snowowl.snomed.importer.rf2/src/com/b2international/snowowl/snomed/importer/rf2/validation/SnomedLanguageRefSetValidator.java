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
package com.b2international.snowowl.snomed.importer.rf2.validation;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import bak.pcj.set.LongSet;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.services.IClientSnomedComponentService;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.util.ValidationUtil;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;

/**
 * Represents a release file validator that validates the language type reference set.
 * 
 */
public class SnomedLanguageRefSetValidator extends SnomedRefSetValidator {
	
	private final Supplier<LongSet> descriptionIdSupplier = Suppliers.memoize(new Supplier<LongSet>() {
		@Override public LongSet get() {
			return ApplicationContext.getInstance().getService(IClientSnomedComponentService.class).getAllDescriptionIds();
		}
	});
	
	public SnomedLanguageRefSetValidator(final ImportConfiguration configuration, final URL url, final Set<SnomedValidationDefect> defects, final ValidationUtil validationUtil) throws IOException {
		super(configuration, url, ComponentImportType.LANGUAGE_TYPE_REFSET, defects, validationUtil, SnomedRf2Headers.LANGUAGE_TYPE_HEADER.length);
	}

	@Override
	protected void doValidate(final List<String> row, final int lineNumber) {
		super.doValidate(row, lineNumber);
		
		// TODO add more validation check
	}

	@Override
	protected String getName() {
		return "language type";
	}
	
	@Override
	protected String[] getExpectedHeader() {
		return SnomedRf2Headers.LANGUAGE_TYPE_HEADER;
	}
	
	@Override
	protected void validateReferencedComponent(final List<String> row, final int lineNumber) {
		
		final String descriptionId = row.get(5);
		
		//existing description
		if (descriptionIdSupplier.get().contains(Long.parseLong(descriptionId))) {
			return;
		}
		
		
		if (isComponentNotExist(descriptionId, ReleaseComponentType.DESCRIPTION)) {
			if (null == referencedComponentNotExist) {
				referencedComponentNotExist = Sets.newHashSet();
			}
			
			addDefectDescription(referencedComponentNotExist, lineNumber, row.get(5));
		}
	}

}