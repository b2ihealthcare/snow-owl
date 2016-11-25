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
package com.b2international.snowowl.snomed.importer.net4j;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;

/**
 * Representation of a validation defect about incomplete taxonomy. 
 *
 */
public class SnomedIncompleteTaxonomyValidationDefect extends SnomedValidationDefect {

	private static final long serialVersionUID = -4826632877877405283L;
	private final Collection<TaxonomyDefect> taxonomyDefects;
	
	public SnomedIncompleteTaxonomyValidationDefect(final String filePath, final Collection<TaxonomyDefect> taxonomyDefects) {
		super(filePath, DefectType.INCONSISTENT_TAXONOMY, Collections.singleton("fake"));
		this.taxonomyDefects = taxonomyDefects;
	}
	
	public Collection<TaxonomyDefect> getTaxonomyDefects() {
		return taxonomyDefects;
	}
	
	@Override
	public void writeTo(Writer writer) throws IOException {
		// writer header
		writer.write("defectType");
		writer.write(TAB);
		writer.write("id");
		writer.write(TAB);
		writer.write("effectiveTime");
		writer.write(TAB);
		writer.write("taxonomyDefectType");
		writer.write(TAB);
		writer.write("conceptId");
		writer.write(TAB);
		writer.write(LE);
		for (TaxonomyDefect defect : getTaxonomyDefects()) {
			// defectType
			writer.write(getDefectType().name());
			writer.write(TAB);
			// id
			writer.write(Long.toString(defect.getRelationshipId()));
			writer.write(TAB);
			// effectiveTime
			writer.write(defect.getEffectiveTime());
			writer.write(TAB);
			// tax.def.type
			writer.write(defect.getType().name());
			writer.write(TAB);
			// conceptId
			writer.write(Long.toString(defect.getMissingConceptId()));
			writer.write(TAB);
			writer.write(LE);
		}
	}
	
}