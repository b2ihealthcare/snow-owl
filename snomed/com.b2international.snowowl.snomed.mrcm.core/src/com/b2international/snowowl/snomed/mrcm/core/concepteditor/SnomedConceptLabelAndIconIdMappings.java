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
package com.b2international.snowowl.snomed.mrcm.core.concepteditor;

import java.io.Serializable;

import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.collections.longs.LongKeyMap;

public class SnomedConceptLabelAndIconIdMappings implements Serializable {
	
	private static final long serialVersionUID = -6075314958389383778L;

	private final LongKeyMap conceptIdToLabelMap;
	private final LongKeyLongMap conceptIdToIconIdMap;
	
	public SnomedConceptLabelAndIconIdMappings(LongKeyMap conceptIdToLabelMap, LongKeyLongMap conceptIdToIconIdMap) {
		this.conceptIdToLabelMap = conceptIdToLabelMap;
		this.conceptIdToIconIdMap = conceptIdToIconIdMap;
	}
	
	public long getIconId(long conceptId) {
		return conceptIdToIconIdMap.get(conceptId);
	}
	
	public String getLabel(long conceptId) {
		return (String) conceptIdToLabelMap.get(conceptId);
	}

	public String getIconId(String conceptId) {
		return Long.toString(getIconId(Long.parseLong(conceptId)));
	}
}