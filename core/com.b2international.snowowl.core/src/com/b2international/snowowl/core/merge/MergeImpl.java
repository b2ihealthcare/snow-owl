/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.merge;

import java.util.Collection;

import com.b2international.commons.exceptions.ApiError;

/**
 * @since 4.6
 */
public class MergeImpl implements Merge {

	private final String source;
	private final String target;
	private final ApiError apiError;
	private final Collection<MergeConflict> conflicts;
	
	public static Builder builder(String source, String target) {
		return new Builder(source, target);
	}

	MergeImpl(String source, String target, ApiError apiError, Collection<MergeConflict> conflicts) {
		this.source = source;
		this.target = target;
		this.apiError = apiError;
		this.conflicts = conflicts;
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public String getTarget() {
		return target;
	}

	@Override
	public ApiError getApiError() {
		return apiError;
	}

	@Override
	public Collection<MergeConflict> getConflicts() {
		return conflicts;
	}

	@Override
	public Merge start() {
		return new MergeImpl(source, target, apiError, conflicts);
	}

	@Override
	public Merge completed() {
		return new MergeImpl(source, target, apiError, conflicts);
	}

	@Override
	public Merge failed(ApiError newApiError) {
		return new MergeImpl(source, target, newApiError, conflicts);
	}

	@Override
	public Merge failedWithConflicts(Collection<MergeConflict> newConflicts) {
		return new MergeImpl(source, target, apiError, newConflicts);
	}

	@Override
	public Merge cancelRequested() {
		return new MergeImpl(source, target, apiError, conflicts);
	}

}
