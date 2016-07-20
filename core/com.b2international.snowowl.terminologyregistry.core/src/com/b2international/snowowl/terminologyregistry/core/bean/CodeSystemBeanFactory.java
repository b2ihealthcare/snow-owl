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
package com.b2international.snowowl.terminologyregistry.core.bean;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.datastore.TerminologyRegistryService;
import com.b2international.snowowl.terminologyregistry.core.util.CodeSystemVersionToStringFunction;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @since 3.0.1
 */
public class CodeSystemBeanFactory {

	public static final String UNSPECIFIED_CODE_SYSTEM_VERSION = "<unspecified code system version>";
	public static final String UNSPECIFIED_SNOWOWL_TERMINOLOGY_COMPONENT_ID = CoreTerminologyBroker.UNSPECIFIED;
	public static final String UNSPECIFIED_CITATION = "<unspecified citation>";
	public static final String UNSPECIFIED_LANGUAGE = "<unspecified language>";
	public static final String UNSPECIFIED_MAINTAINING_ORGANIZATION_LINK = "<unspecified maintaining organization link>";
	public static final String UNSPECIFIED_CODE_SYSTEM_NAME = "<unspecified code system name>";
	public static final String UNSPECIFIED_CODE_SYSTEM_OID = "<unspecified code system OID>";
	
	private final IBranchPathMap branchPathMap;

	public CodeSystemBeanFactory(final IBranchPathMap branchPathMap) {
		this.branchPathMap = checkNotNull(branchPathMap, "branchPathMap");
	}

	public CodeSystemBean create(final String codeSystemShortName) {
		if (isSupported(codeSystemShortName)) {
			return createCodeSystem(codeSystemShortName);
		} else {
			return createUnspecifiedCodeSystem(codeSystemShortName);
		}
	}
	
	public CodeSystemBean createWithVersion(final String codeSystemShortName, final String codeSystemVersion) {
		CodeSystemBean codeSystem;
		if (isSupported(codeSystemShortName)) {
			codeSystem = createCodeSystem(codeSystemShortName);
		} else {
			codeSystem = createUnspecifiedCodeSystem(codeSystemShortName);
		}
		return new CodeSystemBean(
				codeSystem.getCodeSystemOid(), 
				codeSystem.getName(), 
				codeSystem.getCodeSystemShortName(),
				codeSystem.getMaintainingOrganizationLink(), 
				codeSystem.getLanguage(), 
				codeSystem.getCitation(), 
				codeSystem.getSnowOwlTerminologyComponentId(),
				codeSystemVersion, 
				codeSystem.getAvailableVersions());
	}

	protected boolean isSupported(final String codeSystemShortName) {
		final ICodeSystem codeSystem = findCodeSystemByShortName(codeSystemShortName);
		return codeSystem != null; 
	}

	private ICodeSystem findCodeSystemByShortName(final String codeSystemShortName) {
		return getTerminologyRegistryIndexService().getCodeSystemByShortName(branchPathMap, codeSystemShortName);
	}
	
	private TerminologyRegistryService getTerminologyRegistryIndexService() {
		return getServiceForClass(TerminologyRegistryService.class);
	}

	protected CodeSystemBean createCodeSystem(final String codeSystemShortName) {
		final ICodeSystem codeSystem = findCodeSystemByShortName(codeSystemShortName);
		return new CodeSystemBean(
				codeSystem.getOid(), 
				codeSystem.getName(), 
				codeSystemShortName, 
				codeSystem.getOrgLink(), 
				codeSystem.getLanguage(),
				codeSystem.getCitation(), 
				codeSystem.getTerminologyComponentId(),
				UNSPECIFIED_CODE_SYSTEM_VERSION,
				getVersions(codeSystemShortName));
	}

	private ArrayList<String> getVersions(final String codeSystemShortName) {
		return Lists.<String>newArrayList(
				Collections2.transform(
						getTerminologyRegistryIndexService().getCodeSystemVersions(branchPathMap, codeSystemShortName), 
						new CodeSystemVersionToStringFunction()));
	}

	protected CodeSystemBean createUnspecifiedCodeSystem(final String codeSystemShortName) {
		return new CodeSystemBean(
				UNSPECIFIED_CODE_SYSTEM_OID, 
				UNSPECIFIED_CODE_SYSTEM_NAME, 
				codeSystemShortName,
				UNSPECIFIED_MAINTAINING_ORGANIZATION_LINK, 
				UNSPECIFIED_LANGUAGE, 
				UNSPECIFIED_CITATION,
				UNSPECIFIED_SNOWOWL_TERMINOLOGY_COMPONENT_ID,
				UNSPECIFIED_CODE_SYSTEM_VERSION,
				ImmutableList.<String> of(UNSPECIFIED_CODE_SYSTEM_VERSION));
	}

}