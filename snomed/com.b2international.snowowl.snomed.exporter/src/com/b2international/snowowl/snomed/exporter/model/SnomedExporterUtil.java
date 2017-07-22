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
package com.b2international.snowowl.snomed.exporter.model;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.MapSetType;
import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.Sets;

public final class SnomedExporterUtil {

	public static final String REFERENCE_SET_RELATIVE_ROOT_DIR = "Reference Sets";
	public static final String STRUCTURAL_REFERENCE_SET_RELATIVE_DIR = REFERENCE_SET_RELATIVE_ROOT_DIR + File.separatorChar + "Structural";
	public static final String PRODUCTION_REFERENCE_SET_RELATIVE_DIR = REFERENCE_SET_RELATIVE_ROOT_DIR + File.separatorChar + "Clinical" + File.separatorChar + "Production";
	
	/**
	 * Creates a set of map set setting for the SNOMED&nbsp;CT publication process for the RF1 release format.
	 * <p><b>NOTE:&nbsp;</b>this method should not be called on the server side since it uses the Snor to retrieve information.<br>
	 * This method should be invoked when clients are sure the RF1 release format is also selected for the SNOMED&nbsp;CT publication process.
	 * @param refSetIds the reference set identifier concept ID.
	 * @return a collection of map set setting. Can be empty if no map set should be created while publishing SNOMED&nbsp;CT into RF1.
	 */
	public static final Set<SnomedMapSetSetting> createSettings(final Collection<SnomedRefSetIndexEntry> refSets) {
		final Set<SnomedMapSetSetting> settings = Sets.newHashSet();
		for (final SnomedRefSetIndexEntry refset : refSets) {
			if (shouldCreateMapSetSetting(refset))
				settings.add(createSetting(refset));
		}
		return settings;
	}

	/*returns true if a map set setting should be created for the RF1 publication process*/
	private static boolean shouldCreateMapSetSetting(final SnomedRefSetIndexEntry entry) {
		return isConceptType(entry) && isMapping(getType(entry)) && !isStructuralRF1RefSet(entry.getId());
	}

	/*returns true if the passed in reference set identifier concept ID is either CTV3 simple map ID or SNOMED RT simple map reference set ID*/
	private static boolean isStructuralRF1RefSet(final String refSetId) {
		return Concepts.CTV3_SIMPLE_MAP_TYPE_REFERENCE_SET_ID.equals(refSetId) 
				|| Concepts.SNOMED_RT_SIMPLE_MAP_TYPE_REFERENCE_SET_ID.equals(refSetId);
	}
	
	/*creates and returns with a new map setting instance for the RF1 publication.*/
	public static SnomedMapSetSetting createSetting(final SnomedRefSetIndexEntry entry) {
		if (Concepts.ICD_O_REFERENCE_SET_ID.equals(entry.getId()))
			return SnomedMapSetSetting.ICD_O_SETTING;
		else if (Concepts.ICD_9_CM_REFERENCE_SET_ID.equals(entry.getId()))
			return SnomedMapSetSetting.ICD_9_CM_SETTING;
		else if (Concepts.ICD_10_REFERENCE_SET_ID.equals(entry.getId()))
			return SnomedMapSetSetting.ICD_10_SETTING;
		else 
			return new SnomedMapSetSetting(entry.getId(), "", "", "", "", MapSetType.UNSPECIFIED, isComplex(getType(entry)));
	}

	/*returns true if the reference set type is either simple map or complex map type*/
	private static boolean isMapping(final SnomedRefSetType type) {
		return SnomedRefSetUtil.isMapping(type);
	}

	/*returns true if the reference set member is a complex type*/
	private static boolean isComplex(final SnomedRefSetType type) {
		return SnomedRefSetUtil.isComplexMapping(type);
	}
	
	/*returns with the type of the reference set identified by the reference set identifier concept ID*/
	private static SnomedRefSetType getType(final SnomedRefSetIndexEntry entry) {
		return entry.getType();
	}
	
	/*returns true if the referenced component is a SNOMED CT concept*/
	private static boolean isConceptType(final SnomedRefSetIndexEntry entry) {
		return SnomedTerminologyComponentConstants.CONCEPT_NUMBER == entry.getReferencedComponentType();
	}
	
	private SnomedExporterUtil() { }
	
}