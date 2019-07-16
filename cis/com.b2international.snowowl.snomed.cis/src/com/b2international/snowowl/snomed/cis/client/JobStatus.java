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
package com.b2international.snowowl.snomed.cis.client;

/**
 * @since 4.5
 */
public enum JobStatus {

	PENDING, RUNNING, FINISHED, ERROR;

	public static JobStatus get(int status) {
		switch (status) {
		case 0:
			return PENDING;
		case 1:
			return RUNNING;
		case 2:
			return FINISHED;
		case 3:
			return ERROR;
		default:
			throw new IllegalArgumentException(String.format("Couldn't transform number %d to enum.", status));
		}
	}
}
