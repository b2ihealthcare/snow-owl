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
package com.b2international.snowowl.terminologyregistry.core.util;

import org.eclipse.core.runtime.IConfigurationElement;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;

/**
 * @since 3.1.0
 */
public class CodeSystemVersionProviderUtil {
	
	public static final String COMPONENT_VERSION_PROVIDER_EXTENSION_POINT_ID = "com.b2international.snowowl.terminologyregistry.core.codeSystemVersionProvider";

	public static ICodeSystemVersionProvider getComponentVersionProvider(String terminologyComponentId) {
		CoreTerminologyBroker broker = CoreTerminologyBroker.getInstance();
		IConfigurationElement configurationElement = broker.getTerminologyComponentLevelConfigurationElement(terminologyComponentId, COMPONENT_VERSION_PROVIDER_EXTENSION_POINT_ID);
		return (ICodeSystemVersionProvider) broker.createExecutableExtension(configurationElement);
	}
	
	public static String getVersion(final String terminologyComponentId, final String componentId, final IBranchPath branchPath) {
		ICodeSystemVersionProvider versionProvider = getComponentVersionProvider(terminologyComponentId);
		return versionProvider.getVersion(terminologyComponentId, componentId, branchPath);
	}
}
