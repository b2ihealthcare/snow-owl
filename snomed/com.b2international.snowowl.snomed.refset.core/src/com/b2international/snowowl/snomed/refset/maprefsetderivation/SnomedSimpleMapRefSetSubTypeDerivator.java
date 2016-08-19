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
package com.b2international.snowowl.snomed.refset.maprefsetderivation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.SubMonitor;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.refset.derivation.AbstractSnomedRefSetDerivator;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

/**
 * This class is for Deriving SNOMED CT simple map reference set to simple type reference set.
 * This implementation takes all the <b>active</b> SNOMED CT referenced components or map targets,
 * calculates their subtypes (note: only active ones are collected) 
 * and use them as referenced component in the newly created simple type reference set.
 * 
 *
 */
public class SnomedSimpleMapRefSetSubTypeDerivator extends AbstractSnomedRefSetDerivator {
	
	public SnomedSimpleMapRefSetSubTypeDerivator(String refSetId, String newRefSetLabel, boolean mapTargetToReferencedComponent) throws SnowowlServiceException {
		super(refSetId, newRefSetLabel, mapTargetToReferencedComponent);
	}

	@Override
	protected int getTotalWork() {
		return 4;
	}

	@Override
	protected void deriveComponents(final SubMonitor monitor) throws SnowowlServiceException {
		final Set<String> collectedComponentIdSet = collectSubTypes();
		getConceptIds().addAll(collectedComponentIdSet);
		deriveConcepts(monitor);
	}
	
	private Set<String> collectSubTypes() {
		final Set<String> conceptIds = getConceptIds();
		final Set<String> collectedIdSet = new HashSet<String>();
		
		final SnomedClientTerminologyBrowser browser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);

		Map<String, SubTypeMarker> markedMap = Maps.newHashMap();
		for (String componentId : conceptIds) {
			markedMap.put(componentId, SubTypeMarker.UNTOUCHED);
		}
		
		for (String componentId : conceptIds) {
			if (markedMap.get(componentId).equals(SubTypeMarker.CALCULATED)) {
				continue;
			}
			
			collectedIdSet.add(componentId);
			markedMap.put(componentId, SubTypeMarker.CALCULATED);
			
			Collection<SnomedConceptIndexEntry> subTypes = browser.getAllSubTypes(browser.getConcept(componentId));
			
			//	filter out inactive subtypes, we don't care about them
			Collection<SnomedConceptIndexEntry> activeSubTypes = Collections2.filter(subTypes, new Predicate<SnomedConceptIndexEntry>() {
				@Override
				public boolean apply(SnomedConceptIndexEntry conceptMini) {
					return conceptMini.isActive();
				}
			});
			
			// collect the ids only
			Collection<String> collectedIds = Collections2.transform(activeSubTypes, new Function<SnomedConceptIndexEntry, String>() {

				@Override
				public String apply(SnomedConceptIndexEntry conceptMini) {
					return conceptMini.getId();
				}
			});
			
			for (String id : collectedIds) {
				markedMap.put(id, SubTypeMarker.CALCULATED);
			}
			
			collectedIdSet.addAll(collectedIds);
		}
		
		return collectedIdSet;
	}
	
	/**
	 * Dummy marker enum for administering components whose subtypes are already calculated.
	 * If we found a component marked with <code>CALCULATED</code> that means its subtypes 
	 * were calculated previously, we don't want to do that again
	 * 
	 *
	 */
	private enum SubTypeMarker {
		UNTOUCHED,
		CALCULATED;
	}

}