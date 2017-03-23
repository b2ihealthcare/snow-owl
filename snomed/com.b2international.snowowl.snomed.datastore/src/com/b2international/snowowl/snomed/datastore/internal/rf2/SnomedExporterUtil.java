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
package com.b2international.snowowl.snomed.datastore.internal.rf2;

import java.io.File;
import java.util.Set;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.MapSetType;
import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.google.common.collect.Sets;

public final class SnomedExporterUtil {

	public static final String REFERENCE_SET_RELATIVE_ROOT_DIR = "Reference Sets";
	public static final String STRUCTURAL_REFERENCE_SET_RELATIVE_DIR = REFERENCE_SET_RELATIVE_ROOT_DIR + File.separatorChar + "Structural";
	public static final String PRODUCTION_REFERENCE_SET_RELATIVE_DIR = REFERENCE_SET_RELATIVE_ROOT_DIR + File.separatorChar + "Clinical" + File.separatorChar + "Production";
	
	/**
	 * Creates a set of map set setting for the SNOMED&nbsp;CT publication process for the RF1 release format.
	 * <p><b>NOTE:&nbsp;</b>this method should not be called on the server side since it uses the Snor to retrieve information.<br>
	 * This method should be invoked when clients are sure the RF1 release format is also selected for the SNOMED&nbsp;CT publication process.
	 * @param refSets the reference set identifier concept ID.
	 * @return a collection of map set setting. Can be empty if no map set should be created while publishing SNOMED&nbsp;CT into RF1.
	 */
	public static final Set<SnomedMapSetSetting> createSettings(final Set<SnomedReferenceSet> refSets) {
		final Set<SnomedMapSetSetting> settings = Sets.newHashSet();
		for (final SnomedReferenceSet refSet : refSets) {
			if (shouldCreateMapSetSetting(refSet))
				settings.add(createSetting(refSet));
		}
		return settings;
	}

	/*returns true if a map set setting should be created for the RF1 publication process*/
	private static boolean shouldCreateMapSetSetting(final SnomedReferenceSet refSet) {
		return SnomedTerminologyComponentConstants.CONCEPT.equals(refSet.getReferencedComponentType()) && !(!SnomedRefSetUtil.isMapping(refSet.getType()) || isStructuralRefSet(refSet));
	}

	/*returns true if the passed in reference set identifier concept ID is either CTV3 simple map ID or SNOMED RT simple map reference set ID*/
	private static boolean isStructuralRefSet(final SnomedReferenceSet refSet) {
		return Concepts.CTV3_SIMPLE_MAP_TYPE_REFERENCE_SET_ID.equals(refSet.getId()) 
				|| Concepts.SNOMED_RT_SIMPLE_MAP_TYPE_REFERENCE_SET_ID.equals(refSet.getId());
	}
	
	/*creates and returns with a new map setting instance for the RF1 publication.*/
	private static SnomedMapSetSetting createSetting(final SnomedReferenceSet refSet) {
		if (Concepts.ICD_O_REFERENCE_SET_ID.equals(refSet.getId()))
			return SnomedMapSetSetting.ICD_O_SETTING;
		else if (Concepts.ICD_9_CM_REFERENCE_SET_ID.equals(refSet.getId()))
			return SnomedMapSetSetting.ICD_9_CM_SETTING;
		else if (Concepts.ICD_10_REFERENCE_SET_ID.equals(refSet.getId()))
			return SnomedMapSetSetting.ICD_10_SETTING;
		else 
			return new SnomedMapSetSetting(refSet.getId(), "", "", "", "", MapSetType.UNSPECIFIED, SnomedRefSetUtil.isComplexMapping(refSet.getType()));
	}

	private SnomedExporterUtil() { }
	
}