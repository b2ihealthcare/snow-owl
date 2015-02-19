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
package com.b2international.snowowl.datastore.server.index;


/**
 * Represents an index post processor. Used to perform any arbitrary content modification.
 */
public interface IIndexPostProcessor {

	/**Performs the post processing on the given branch with the specified timestamp.*/
	void postProcess(final IIndexPostProcessingConfiguration configuration);

	/**Noop index post processor implementation. Does nothing.*/
	IIndexPostProcessor NOOP = new IIndexPostProcessor() {
		@Override public void postProcess(final IIndexPostProcessingConfiguration configuration) {
			//intentionally ignored.
		}
	};
	
}