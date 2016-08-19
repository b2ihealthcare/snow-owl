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
package com.b2international.snowowl.datastore.server.snomed.escg;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import bak.pcj.LongIterator;
import bak.pcj.set.LongSet;

import com.b2international.snowowl.snomed.dsl.query.RValue;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.escg.IQueryEvaluator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class QueryEvaluator implements Serializable, IQueryEvaluator<Collection<SnomedConceptIndexEntry>, RValue> {
	
	private static final long serialVersionUID = -8592143402592449211L;
	private final IBranchPath branchPath;
	private final ConceptIdQueryEvaluator2 delegate;

	public QueryEvaluator() {
		this(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE));
	}

	/**
	 * @param branchPath the branch path.
	 */
	public QueryEvaluator(final IBranchPath branchPath) {
		this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		delegate = new ConceptIdQueryEvaluator2(this.branchPath);
	}

	@Override
	public Collection<SnomedConceptIndexEntry> evaluate(final RValue expression) {
		
		final LongSet conceptIds = delegate.evaluate(expression);
		
		if (LongSets.isEmpty(conceptIds)) {
			return Lists.newArrayList();
		}
		
		//XXX akitta: this is a real performance killer
		//instead of this create a collector that performs a concept instance initialization
		//concept ID should be extracted from docvalues.
		final SnomedTerminologyBrowser browser = ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
		final SnomedConceptIndexEntry[] concepts = new SnomedConceptIndexEntry[conceptIds.size()];
		
		int i = 0;
		for (final LongIterator itr = conceptIds.iterator(); itr.hasNext(); /*nothing*/) {
			concepts[i++] = browser.getConcept(branchPath, Long.toString(itr.next()));
		}
		return Arrays.asList(concepts);
		
	}
}