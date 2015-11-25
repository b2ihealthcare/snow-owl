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
package com.b2international.snowowl.snomed.refset.core.automap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.NullComponent;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.SnomedDOIQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

/**
 * This class if for mapping members with SNOMED CT. The class takes the terms and uses {@link SnomedDescriptionIndexQueryAdapter} to try to 
 * get a SNOMED CT equivalent. 
 * 
 * For performance optimization the automapper uses a recursive algorithm to get back a corresponding and appropriate equivalent.
 * 
 */
public abstract class AbstractRefSetAutoMapper {
	
	private static final int SEARCHER_STEP_LIMIT = 243;

	private static final int SEARCHER_STEP_INCREMENT_MULTIPLIER = 3;

	protected final SnomedClientIndexService indexSearcher;
	protected String userId;

	protected IComponent<String> topLevelConcept;

	protected SnomedClientTerminologyBrowser terminologyBrowser;

	protected RefSetAutoMapperModel model;
	
	protected AbstractRefSetAutoMapper(IComponent<String> topLevelConcept, RefSetAutoMapperModel model) {
		this.model = model;
		this.topLevelConcept = topLevelConcept;
		this.indexSearcher = ApplicationContext.getInstance().getService(SnomedClientIndexService.class);
		userId = ApplicationContext.getInstance().getService(ICDOConnectionManager.class).getUserId();
		terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
	}
	
	/**
	 * This method traverses trough the passed map, takes the terms and tries to find an equivalent term in SNOMED CT.
	 * The resulting map key is a unique identifier (same as passed in the constructor) the value is the SNOMED CT equivalent <b>concept id</b>.
	 * If there is no equivalent concept for the given term <b>null</b> is put into the resulting map with its unique identifier.
	 * 
	 * @param model
	 * @param topLevelConcept the top-level SNOMED CT concept to restrict the search on
	 * @param monitor
	 * @return Map key is an <i>internal</i> unique identifier, value is the resolved <b>concept id</b>
	 */
	public Map<Integer, String> resolveValues(final IProgressMonitor monitor) {
		
		Map<Integer, String> values = getValuesFromColumn(model.getMappedSourceColumnIndex());
		
		final Map<Integer, String> resolvedValues = Maps.newHashMap();
		
		for (Entry<Integer, String> entry : values.entrySet()) {
			
			// TODO (apeteri): revive the fuzzy query adapter in some form later on 
			List<IIndexQueryAdapter<SnomedConceptIndexEntry>> adapters = ImmutableList.<IIndexQueryAdapter<SnomedConceptIndexEntry>>of(
					new SnomedDOIQueryAdapter(entry.getValue(), userId, (String[]) null));
			
			if (null == topLevelConcept) {
				topLevelConcept = NullComponent.<String>getNullImplementation();
			}
			
			for (int limit = 1; limit < SEARCHER_STEP_LIMIT; limit *= SEARCHER_STEP_INCREMENT_MULTIPLIER) {
				
				String validResultConceptId = getFirstValidResult(adapters, limit, entry.getKey());
				
				if (validResultConceptId != null && !resolvedValues.containsValue(validResultConceptId)) {
					resolvedValues.put(entry.getKey(), validResultConceptId);
					break;
				}
			}
			
			monitor.worked(1);
			
			if (monitor.isCanceled()) {
				return resolvedValues;
			}
		}
		
		return resolvedValues;
	}
	
	/**
	 * For performance optimization try to keep the number of the Lucene documents get back from the searcher as low as it is possibly,
	 * however we need to find the an active equivalent but the searcher may find a more relevant document with an inactive concept, so execute
	 * recursive search. Current limit is
	 * 
	 * @param adapters the search adapters to use in priority order
	 * @param limit how many document we want to get back from Lucene
	 * @param rowIndex 
	 * @return the corresponding concept id (always active) or <b>null</b> if the limit has reached and no <b>active</b> concept id was found
	 */
	protected abstract String getFirstValidResult(List<IIndexQueryAdapter<SnomedConceptIndexEntry>> adapters, int limit, Integer rowIndex);


	protected Map<Integer, String> getValuesFromColumn(int targetColumn) {
		Map<Integer, String> collectedValues = Maps.newHashMap();

		List<AutoMapEntry> content = model.getContent();
		for (int i = 0; i < content.size(); i++) {
			AutoMapEntry entry = content.get(i);
			String value = "";
			if (entry.getParsedValues().size() > targetColumn) {
				value = entry.getParsedValues().get(targetColumn);
			}

			collectedValues.put(i, value);
		}

		return collectedValues;
	}
	
}