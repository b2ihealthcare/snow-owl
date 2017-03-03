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
package com.b2international.snowowl.snomed.reasoner.classification;

import java.io.Serializable;
import java.util.UUID;

/**
 * The return type of {@link SnomedReasonerService#persistChanges(UUID, String)} requests.
 */
public class PersistChangesResponse extends AbstractResponse implements Serializable {

	private static final long serialVersionUID = -2422999450075484898L;

	private final String jobId;
	
	/**
	 * Creates a new response with the specified type.
	 * @param type the response type
	 */
	public PersistChangesResponse(final Type type) {
		this(type, null);
	}
	
	public PersistChangesResponse(final Type type, final String jobId) {
		super(type);
		this.jobId = jobId;
	}
	
	public String getJobId() {
		return jobId;
	}
}