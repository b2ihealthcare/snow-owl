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
package com.b2international.snowowl.snomed.exporter.server.rf1;

import static com.b2international.snowowl.snomed.exporter.server.rf1.SnomedRf1ReleaseFileHeaders.RF1_SUBSETS_HEADER;

import java.io.IOException;
import java.util.Date;
import java.util.function.Consumer;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;

/**
 * SNOMED&nbsp;CT subset exporter for language and simple type reference sets.
 * <p>
 * <b>RF1</b> CSV header:
 * <ul><li>SUBSETID&#9;SUBSETORIGINALID&#9;SUBSETVERSION&#9;SUBSETNAME&#9;SUBSETTYPE&#9;LANGUAGECODE&#9;REALMID&#9;CONTEXTID</li></ul>
 * </p>
 * @see SnomedRf1Exporter
 * @see AbstractSnomedSubsetExporter
 */
public class SnomedSubsetExporter extends AbstractSnomedSubsetExporter {

	private final int version;

	public SnomedSubsetExporter(
			final SnomedExportContext configuration, 
			final String refSetId, 
			final RevisionSearcher revisionSearcher,
			final int version) {
		super(configuration, refSetId, revisionSearcher);
		this.version = version;
	}
	
	@Override
	public String getFileName() {
		return new StringBuilder("der1_Subsets_")
		.append(isLanguageType(getRefSetId()) ? getLanguageCode(getRefSetId()) : getFolderName())
		.append("_INT_")
		.append(Dates.formatByHostTimeZone(new Date(), DateFormats.SHORT)).append(".txt").toString();
	}
	
	@Override
	public String[] getColumnHeaders() {
		return RF1_SUBSETS_HEADER;
	}

	@Override
	public ComponentExportType getType() {
		return ComponentExportType.REF_SET;
	}

	@Override
	public void writeLines(Consumer<String> lineProcessor) throws IOException {
		new StringBuilder()
		.append(getRefSetId())
		.append(HT)
		.append(getRefSetId())
		.append(HT)
		.append(version)
		.append(HT)
		.append(getLabel())
		.append(HT)
		.append(isLanguageType(getRefSetId()) ? "1" : getSubsetType())
		.append(HT)
		.append(isLanguageType(getRefSetId()) ? getLanguageCode(getRefSetId()) : "0")
		.append(HT)
		.append("0")
		.append(HT)
		.append("0").toString();
	}
	
}