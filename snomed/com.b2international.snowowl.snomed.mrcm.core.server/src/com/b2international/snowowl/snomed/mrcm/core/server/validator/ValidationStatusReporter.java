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
package com.b2international.snowowl.snomed.mrcm.core.server.validator;

import org.eclipse.core.runtime.IStatus;

import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ModeledWidgetBean;
import com.google.common.collect.Multimap;

/**
 * @since 4.3
 */
public interface ValidationStatusReporter {

	void error(ModeledWidgetBean context, String message, Object...args);
	
	void warning(ModeledWidgetBean context, String message, Object...args);
	
	Multimap<ModeledWidgetBean, IStatus> getReport();
	
}
