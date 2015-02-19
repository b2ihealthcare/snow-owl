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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.terminologyregistry.core.bean.CodeSystemShortNameProvider;
import com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryClientService;
import com.google.common.collect.Collections2;

/**
 * @since 3.1.0
 */
public class CodeSystemVersionCollector {

	private final TerminologyRegistryClientService registry;
	
	private ArrayList<String> versionList;
	
	public CodeSystemVersionCollector() {
		registry = ApplicationContext.getInstance().getService(TerminologyRegistryClientService.class);
	}

	public String[] getCodeSystemVersions(Object element) {
		return versionList.toArray(new String[versionList.size()]);
	}
	
	public List<String> getVersionList() {
		return versionList;
	}

	public void init(CodeSystemShortNameProvider member) {
		String shortName = member.getCodeSystemShortName();
		Collection<String> versions = Collections2.transform(registry.getCodeSystemVersions(shortName), new CodeSystemVersionToStringFunction());
		versionList = new ArrayList<>(versions);
		versionList.add(ICodeSystemVersion.UNVERSIONED);
	}

}