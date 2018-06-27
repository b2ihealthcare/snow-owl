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
package com.b2international.snowowl.snomed.reasoner.domain;

/**
 * Enumerates possible states of a single classification run.
 * 
 * @since
 */
public enum ClassificationStatus {

	/**
	 * The classification was requested to run, but has no free reasoner instance to
	 * use.
	 */
	SCHEDULED,

	/**
	 * The classification is running.
	 */
	RUNNING,

	/**
	 * The computational stages of the classification were completed; equivalent
	 * concepts and suggested changes can be retrieved for review.
	 */
	COMPLETED,

	/**
	 * The computation failed for this run.
	 */
	FAILED,

	/**
	 * The computation was canceled by the user.
	 */
	CANCELED,

	/**
	 * The computation completed, but an incoming commit changed the repository's
	 * state, so changes can no longer be saved. It is still possible to review
	 * suggestions, though.
	 */
	STALE,

	/**
	 * The suggested changes are being saved.
	 */
	SAVING_IN_PROGRESS,

	/**
	 * Changes were successfully committed to the terminology store.
	 */
	SAVED,

	/**
	 * Changes could not be saved successfully.
	 */
	SAVE_FAILED;
}
