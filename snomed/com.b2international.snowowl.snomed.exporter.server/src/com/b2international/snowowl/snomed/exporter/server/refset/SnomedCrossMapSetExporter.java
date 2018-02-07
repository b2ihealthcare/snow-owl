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
package com.b2international.snowowl.snomed.exporter.server.refset;

import static com.b2international.snowowl.snomed.exporter.server.SnomedReleaseFileHeaders.RF1_CROSS_MAP_SETS_HEADER;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.Iterator;

import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedRf1Exporter;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExportConfiguration;

/**
 * SNOMED&nbsp;CT cross map set exporter for complex map and simple map type reference sets.
 * <p>
 * <b>RF1</b> CSV header:
 * <ul><li>MAPSETID&#9;MAPSETNAME&#9;MAPSETTYPE&#9;MAPSETSCHEMEID&#9;MAPSETSCHEMENAME&#9;MAPSETSCHEMEVERSION&#9;MAPSETREALMID&#9;MAPSETSEPARATOR&#9;MAPSETRULETYPE</li></ul>
 * </p>
 * @see SnomedRf1Exporter
 * @see AbstractSnomedCrossMapExporter
 */
public class SnomedCrossMapSetExporter extends AbstractSnomedCrossMapExporter implements SnomedRf1Exporter {

	private static final String FILE_NAME_PREFIX = "CrossMapSets";
	private Iterator<String> itr;

	public SnomedCrossMapSetExporter(final SnomedExportConfiguration configuration, final String refSetId, final SnomedMapSetSetting mapSetSetting) {
		super(configuration, refSetId, mapSetSetting);
		itr = createResultSet().iterator();
	}

	@Override
	protected String getFileNameprefix() {
		return FILE_NAME_PREFIX;
	}

	private Collection<String> createResultSet() {
		return newArrayList(
			"", 	//CDO ID
			"", 	//CDO created
			"", 	//CDO version
			"",		//Effective time
			// EXPORT VALUES
			getRefSetId(), //Reference set ID
			getMapSetSetting().getMapSetName(), // Map set name
			String.valueOf(getMapSetSetting().getMapSetType().getValue()), //Map set type
			getMapSetSetting().getMapSchemeId(), //Map scheme ID (OID)
			getMapSetSetting().getMapSchemeName(), //Map scheme name
			getMapSetSetting().getMapSchemeVersion(), //Map scheme version
			"", //Map set realm ID
			"|", //Map set separator
			"" //Map set rule type
		);
	}

	@Override
	public String[] getColumnHeaders() {
		return RF1_CROSS_MAP_SETS_HEADER;
	}

	@Override
	public ComponentExportType getType() {
		return ComponentExportType.REF_SET;
	}

	@Override
	public boolean hasNext() {
		return itr.hasNext();
	}

	@Override
	public String next() {
		return itr.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<String> iterator() {
		return itr;
	}

	@Override
	public void close() throws Exception {
		//nothing
	}
	
}