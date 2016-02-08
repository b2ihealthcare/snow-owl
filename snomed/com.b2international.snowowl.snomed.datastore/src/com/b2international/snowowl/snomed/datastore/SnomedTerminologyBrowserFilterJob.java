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
package com.b2international.snowowl.snomed.datastore;

import com.b2international.snowowl.datastore.TerminologyBrowserFilterJob;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;

/**
 * Job for creating a SNOMED&nbsp;CT concept hierarchy browser based on a subset of concepts.
 * @see TerminologyBrowserFilterJob
 */
public class SnomedTerminologyBrowserFilterJob extends TerminologyBrowserFilterJob<String, SnomedConceptIndexEntry> {

	/**
	 * Initialize a new job instance.
	 * @param callbacks callbacks for the filter job.
	 */
	public SnomedTerminologyBrowserFilterJob(final IFilterJobCallback... callbacks) {
		super(SnomedTerminologyComponentConstants.CONCEPT, "SNOMED CT filtering...", callbacks);
	}

}