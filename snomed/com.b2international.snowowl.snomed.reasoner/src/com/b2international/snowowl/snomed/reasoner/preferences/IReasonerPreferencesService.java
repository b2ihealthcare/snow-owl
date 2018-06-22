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
package com.b2international.snowowl.snomed.reasoner.preferences;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.net4j.util.event.INotifier;
import org.protege.editor.owl.model.inference.ProtegeOWLReasonerInfo;

/**
 *
 */
public interface IReasonerPreferencesService extends INotifier {

	/**
	 * 
	 * @return
	 */
	public Map<String, ReasonerMetadata> getMetadata();

	/**
	 * 
	 * @return
	 */
	public ProtegeOWLReasonerInfo createReasonerInfo(String reasonerId);
	
	/**
	 * Checks whether all available reasoner can be instantiated via its factory or not.
	 * Returns with the outcome of the sanity check as a status.
	 * @return an iterable of status indicating the outcome of checking the availability of all reasoner. 
	 */
	public Iterable<IStatus> checkAllAvailableReasoners();
}