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
package com.b2international.snowowl.snomed.api;

import java.util.List;

import com.b2international.snowowl.snomed.api.domain.classification.IClassificationRun;
import com.b2international.snowowl.snomed.api.domain.classification.IEquivalentConceptSet;
import com.b2international.snowowl.snomed.api.domain.classification.IRelationshipChangeList;

/**
 * Provides access to the classification functionality of the Snow Owl Server.
 *
 */
public interface ISnomedClassificationService {

	/**
	 * 
	 * @param version
	 * @param taskId
	 * @param userId
	 * @return
	 */
	List<IClassificationRun> getAllClassificationRuns(String version, String taskId, String userId);

	/**
	 * 
	 * @param version
	 * @param taskId
	 * @param userId
	 * @return
	 */
	IClassificationRun beginClassification(String version, String taskId, String reasonerId, String userId);

	/**
	 * 
	 * @param version
	 * @param taskId
	 * @param classificationId
	 * @param userId
	 * @return
	 */
	IClassificationRun getClassificationRun(String version, String taskId, String classificationId, String userId);

	/**
	 * 
	 * @param version
	 * @param taskId
	 * @param classificationId
	 * @param userId
	 * @return
	 */
	List<IEquivalentConceptSet> getEquivalentConceptSets(String version, String taskId, String classificationId, String userId);

	/**
	 * 
	 * @param version
	 * @param taskId
	 * @param classificationId
	 * @return
	 */
	IRelationshipChangeList getRelationshipChanges(String version, String taskId, String classificationId, String userId, int offset, int limit);

	/**
	 * 
	 * @param version
	 * @param taskId
	 * @param classificationId
	 * @param userId
	 */
	void persistChanges(String version, String taskId, String classificationId, String userId);

	/**
	 * 
	 * @param version
	 * @param taskId
	 * @param classificationId
	 * @param userId
	 */
	void removeClassificationRun(String version, String taskId, String classificationId, String userId);
}