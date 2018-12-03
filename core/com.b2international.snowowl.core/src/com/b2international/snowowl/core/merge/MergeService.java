/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.UUID;

import com.google.common.base.Predicate;

/**
 * @since 4.6
 */
public interface MergeService {

	Merge enqueue(String source, String target, String userId, String commitMessage, String reviewId, String parentLockContext);

	Merge getMerge(UUID id);
	
	MergeCollection search(Predicate<Merge> query);
	
	void deleteMerge(UUID id);
}
