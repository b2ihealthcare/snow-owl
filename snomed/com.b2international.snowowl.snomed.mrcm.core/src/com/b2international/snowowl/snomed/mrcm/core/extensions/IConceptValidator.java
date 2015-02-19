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
package com.b2international.snowowl.snomed.mrcm.core.extensions;

import org.eclipse.core.runtime.IStatus;

import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ModeledWidgetBean;
import com.google.common.collect.Multimap;

/**
 * Represents a concept validator operating on the widget bean level.
 * 
 */
public interface IConceptValidator extends IConceptExtension {

	/**
	 * Validates the given {@link ConceptWidgetBean} and returns status information about validation errors.
	 * 
	 * @param conceptWidgetBean the widget bean to validate (may not be {@code null})
	 * @return a multimap of {@link IStatus} values representing validation results, keyed by the source
	 * {@link ModeledWidgetBean} instance
	 */
	public Multimap<ModeledWidgetBean, IStatus> validate(ConceptWidgetBean conceptWidgetBean);
}