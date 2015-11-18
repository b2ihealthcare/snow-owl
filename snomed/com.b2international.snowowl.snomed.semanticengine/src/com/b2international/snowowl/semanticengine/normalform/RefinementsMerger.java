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
package com.b2international.snowowl.semanticengine.normalform;

import java.util.HashMap;
import java.util.Map;

import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.ScgFactory;
import com.b2international.snowowl.semanticengine.subsumption.SubsumptionTester;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;

/**
 * <b>5.3.5	Merge refinement</b><br/>
 * The normalized expression refinement from the "Normalize attribute values in refinement" process (5.3.2)
 * is merged with the combined definition from the "Merge definitions" process (5.3.4).<br/>
 * The rules for this process are the same as those for merging definitions (see 5.3.4) with the following additions.<br/><br/>
 * <b>5.3.5.1	Normalization of laterality</b><br/>
 * If an attribute representing a value for "laterality" (272741003) is present in the refinement and is applied to 
 * a focus concept that is not subsumed by "body structure" (123037004), the laterality attribute should be applied 
 * to any and every lateralizable "body structure" specified in the resulting refinement.<br/><br/>
 * <b>5.3.5.2	Normalization of non-context attributes applied in a context wrapper</b><br/>
 * If the focus concept is subsumed by "context-dependent categories" (364629017) and any attributes other than 
 * valid context attributes* are present in the refinement, these attributes are applied as additional refinement 
 * of the value of the "associated finding" (246090004) or "associated procedure" (363589002) attribute.<br/><br/>
 * <b>5.3.5.3	Completion of the definition merging</b><br/>
 * Once the refinement has been merged the resulting final refinement is passed to the "Create expression" process (5.3.6).<br/><br/>
 * 
 * <em>* The only valid context attributes are:<br/>
 * "associated finding" (246090004), "associated procedure" (363589002), "finding context" (2470590016), 
 * "procedure context" (2470591017), "temporal context" (2470592012) and "subject relationship context" (2470593019).</em>
 * 
 */
public class RefinementsMerger {
	
	private final IClientTerminologyBrowser<SnomedConceptIndexEntry, String> snor;

	public RefinementsMerger(IClientTerminologyBrowser<SnomedConceptIndexEntry, String> snor) {
		this.snor = snor;
	}

	/** 
	 * @param normalizedFocusConcepts
	 * @param normalizedExpressionRefinements
	 * @return the merged refinements
	 */
	public ConceptDefinition mergeRefinements(FocusConceptNormalizationResult normalizedFocusConcepts, ConceptDefinition normalizedExpressionRefinements) {
		ConceptDefinitionMerger conceptDefinitionMerger = new ConceptDefinitionMerger(new SubsumptionTester(snor));
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		// TODO: get rid of dummy concepts
		conceptDefinitionMap.put(ScgFactory.eINSTANCE.createConcept(), normalizedFocusConcepts.mergedConceptDefinition);
		conceptDefinitionMap.put(ScgFactory.eINSTANCE.createConcept(), normalizedExpressionRefinements);
		ConceptDefinition mergedRefinements = conceptDefinitionMerger.mergeDefinitions(conceptDefinitionMap);
		// TODO: handle special cases
		return mergedRefinements;
	}
}