/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2;

import com.b2international.snowowl.core.jobs.RemoteJobEntry;

/**
 * @since 5.7
 */
public final class SnomedRf2Requests {

	public static final String SNOMED_IMPORT_PREFIX = "snomed-import-"; 
	
	public SnomedRf2ExportRequestBuilder prepareExport() {
		return new SnomedRf2ExportRequestBuilder();
	}
	
	public SnomedRf2ImportRequestBuilder prepareImport() {
		return new SnomedRf2ImportRequestBuilder();
	}

	public static String importJobKey(String branchPath) {
		return SNOMED_IMPORT_PREFIX.concat(branchPath);
	}
	
	public static boolean isSnomedImportJob(RemoteJobEntry job) {
		return job != null && job.getKey().startsWith(SNOMED_IMPORT_PREFIX);
	}
	
}
