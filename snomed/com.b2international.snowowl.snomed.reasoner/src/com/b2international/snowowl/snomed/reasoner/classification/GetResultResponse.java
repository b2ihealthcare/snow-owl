/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import javax.annotation.Nullable;

/**
 * The return type of {@link SnomedReasonerService#getResult(UUID)} requests.
 * 
 */
public class GetResultResponse extends AbstractResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private final GetResultResponseChanges changes;

	/**
	 * Creates a new response with the specified type and an empty reasoner change set.
	 * @param type the response type (preferably {@link Type#NOT_AVAILABLE} or {@link Type#STALE})
	 */
	public GetResultResponse() {
		this(null);
	}

	/**
	 * Creates a new response with the specified type and reasoner change set.
	 * @param type the response type
	 * @param changes the computed change set determined by the classification
	 */
	public GetResultResponse(final GetResultResponseChanges changes) {
		super(changes == null ? Type.NOT_AVAILABLE : Type.SUCCESS);
		this.changes = changes;
	}

	/**
	 * @return the computed change set determined by the classification (may be {@code null} if no changes are available for this response)
	 */
	public @Nullable GetResultResponseChanges getChanges() {
		return changes;
	}
}