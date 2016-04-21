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

import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.Collection;

import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.mrcm.core.configuration.SnomedSimpleTypeRefSetAttributeConfiguration;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;

/**
 * POJO containing all data required for opening an existing SNOMED CT concept
 * in an editor.
 * 
 */
public class SnomedConceptDetailsBean implements Serializable {

	private static final long serialVersionUID = -5813514548525821871L;

	private final ConceptWidgetBean conceptWidgetBean;
	private final long iconId;
	private final String label;
	private final Collection<PredicateIndexEntry> predicates;
	private final LongSet synonymAndDescendantIds;
	private final SnomedSimpleTypeRefSetAttributeConfiguration configuration;
	
	public SnomedConceptDetailsBean(String label, long iconId, ConceptWidgetBean conceptWidgetBean, 
			LongSet synonymAndDescendantIds, 
			SnomedSimpleTypeRefSetAttributeConfiguration configuration, 
			Collection<PredicateIndexEntry> predicates) {
		
		this.conceptWidgetBean = conceptWidgetBean;
		this.iconId = iconId;
		this.label = label;
		this.synonymAndDescendantIds = synonymAndDescendantIds;
		this.configuration = configuration;
		this.predicates = newArrayList(predicates);
	}

	public ConceptWidgetBean getConceptWidgetBean() {
		return conceptWidgetBean;
	}

	public long getIconId() {
		return iconId;
	}

	public String getLabel() {
		return label;
	}

	public Collection<PredicateIndexEntry> getPredicates() {
		return predicates;
	}
	
	public LongSet getSynonymAndDescendantIds() {
		return synonymAndDescendantIds;
	}
	
	public SnomedSimpleTypeRefSetAttributeConfiguration getConfiguration() {
		return configuration;
	}
}