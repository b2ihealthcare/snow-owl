/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.Serializable;
import java.util.Collection;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.snomed.core.domain.ISnomedImportConfiguration.ImportStatus;
import com.google.common.collect.Lists;

/**
 * @since 7.0
 */
public final class Rf2ImportResponse implements Serializable {

	private ImportStatus status = ImportStatus.COMPLETED;
	
	private Collection<String> issues = Lists.newArrayList();

	public ImportStatus getStatus() {
		return status;
	}

	public void setStatus(ImportStatus status) {
		this.status = status;
	}

	public Collection<String> getIssues() {
		return issues;
	}
	
	public void setIssues(Collection<String> issues) {
		this.issues = Collections3.toImmutableList(issues);
	}
	
}
