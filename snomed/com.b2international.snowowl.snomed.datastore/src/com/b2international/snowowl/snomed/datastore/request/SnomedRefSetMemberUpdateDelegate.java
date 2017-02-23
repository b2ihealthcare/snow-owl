/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 5.0
 */
abstract class SnomedRefSetMemberUpdateDelegate {

	private final SnomedRefSetMemberUpdateRequest request;

	protected SnomedRefSetMemberUpdateDelegate(SnomedRefSetMemberUpdateRequest request) {
		this.request = request;
	}

	String memberId() {
		return request.getMemberId();
	}

	boolean hasProperty(String key) {
		return request.hasProperty(key);
	}

	String getComponentId(String key) {
		return request.getComponentId(key);
	}

	String getProperty(String key) {
		return request.getProperty(key);
	}

	<T> T getProperty(String key, Class<T> valueType) {
		return request.getProperty(key, valueType);
	}

	abstract boolean execute(SnomedRefSetMember member, TransactionContext context);
}
