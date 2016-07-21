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
package com.b2international.snowowl.snomed.refset.core.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentNameProvider;

/**
 * Component name provider implementation for SNOMED CT reference set members.
 */
public enum SnomedRefSetMemberNameProvider implements IComponentNameProvider {

	INSTANCE;

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedRefSetMemberNameProvider.class);
	
	@Override
	public String getComponentLabel(final IBranchPath branchPath, final String componentId) {
		LOGGER.warn("UNSUPPORTED getMemberLabel(): " + componentId);
		return componentId;
	}

}
