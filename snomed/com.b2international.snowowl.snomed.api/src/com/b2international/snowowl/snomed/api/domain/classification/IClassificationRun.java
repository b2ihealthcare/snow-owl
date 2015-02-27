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
package com.b2international.snowowl.snomed.api.domain.classification;

import java.util.Date;

/**
 * Reflects the current state of a classification run.
 */
public interface IClassificationRun extends IClassificationInput {

	/**
	 * Returns the unique identifier of this classification.
	 * 
	 * @return the classification identifier
	 */
	String getId();

	/**
	 * Returns the current status of this classification.
	 * 
	 * @return the classification's status
	 */
	ClassificationStatus getStatus();

	/**
	 * Returns the starting date of this classification, or {@code null} if the classification hasn't started yet.
	 * 
	 * @return the classification starting date
	 */
	Date getCreationDate();

	/**
	 * Returns the completion date of this classification, or {@code null} if the classification hasn't finished yet.
	 * 
	 * @return the classification completion date
	 */
	Date getCompletionDate();
}
