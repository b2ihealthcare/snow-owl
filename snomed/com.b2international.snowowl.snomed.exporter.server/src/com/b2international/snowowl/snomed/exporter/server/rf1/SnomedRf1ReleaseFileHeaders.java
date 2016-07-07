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
package com.b2international.snowowl.snomed.exporter.server.rf1;

/**
 * Constant collecting SNOMED&nbsp;CT RF1 headers for exporting.
 */
public final class SnomedRf1ReleaseFileHeaders {
	
	public static final String[] RF1_CONCEPT_HEADER = new String[] { "CONCEPTID", "CONCEPTSTATUS", "FULLYSPECIFIEDNAME", "CTV3ID", "SNOMEDID", "ISPRIMITIVE" };
	public static final String[] RF1_DESCRIPTION_HEADER = new String[] { "DESCRIPTIONID", "DESCRIPTIONSTATUS", "CONCEPTID", "TERM", "INITIALCAPITALSTATUS", "DESCRIPTIONTYPE", "LANGUAGECODE" };
	public static final String[] RF1_RELATIONSHIP_HEADER = new String[] { "RELATIONSHIPID", "CONCEPTID1", "RELATIONSHIPTYPE", "CONCEPTID2", "CHARACTERISTICTYPE", "REFINABILITY", "RELATIONSHIPGROUP" };
	public static final String[] RF1_CROSS_MAP_HEADER = new String[] { "MAPSETID", "MAPCONCEPTID", "MAPOPTION", "MAPPRIORITY", "MAPTARGETID", "MAPRULE", "MAPADVICE" };
	public static final String[] RF1_CROSS_MAP_SETS_HEADER = new String[] { "MAPSETID", "MAPSETNAME", "MAPSETTYPE", "MAPSETSCHEMEID", "MAPSETSCHEMENAME", "MAPSETSCHEMEVERSION", "MAPSETREALMID", "MAPSETSEPARATOR", "MAPSETRULETYPE" };
	public static final String[] RF1_CROSS_MAP_TARGETS_HEADER = new String[] { "TARGETID", "TARGETSCHEMEID", "TARGETCODES", "TARGETRULE", "TARGETADVICE" };
	public static final String[] RF1_SUBSETS_HEADER = new String[] { "SUBSETID", "SUBSETORIGINALID", "SUBSETVERSION", "SUBSETNAME", "SUBSETTYPE", "LANGUAGECODE", "REALMID", "CONTEXTID" };
	public static final String[] RF1_SUBSET_MEMBERS_HEADER = new String[] { "SUBSETID", "MEMBERID", "MEMBERSTATUS", "LINKEDID" };
	
	private SnomedRf1ReleaseFileHeaders() {
		//suppress instantiation
	}
}