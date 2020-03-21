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
package com.b2international.snowowl.core.branch.review;

/**
 * Enumerates possible states of a terminology review.
 *
 * @since 4.2
 */
public enum ReviewStatus {

	/** New, changed and detached concepts are still being collected. */
	PENDING,

	/** Changes are available, no commits have happened since the start of the review. */
	CURRENT,

	/** Computed differences are not up-to-date; a commit on either of the compared branches invalidated it. */
	STALE,
	
	/** Differences could not be computed for some reason. */
	FAILED
}
