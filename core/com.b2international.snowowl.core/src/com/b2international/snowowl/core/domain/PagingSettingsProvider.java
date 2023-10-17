/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.domain;

/**
 * @since 9.0
 */
public interface PagingSettingsProvider {

	/**
	 * @return the recommended page/batch size for streaming queries
	 */
	public int getPageSize();
	
	/**
	 * @return the recommended (maximum) number of terms that should appear in a single "terms" query
	 */
	public int getTermPartitionSize();

	/**
	 * @return the recommended number of changes in a single commit (soft limit)
	 */
	public int getCommitLimit();
}
