/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index;

import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.snomed.SnomedReleaseType;

/**
 * @since 4.7
 */
public class SnomedReleaseEntry extends CodeSystemEntry {

	private static final long serialVersionUID = 1L;

	private final String baseCodeSystemOid;
	private final SnomedReleaseType snomedReleaseType;

	public SnomedReleaseEntry(final String oid, final String name, final String shortName, final String orgLink, final String language,
			final String citation, final String iconPath, final String snowOwlId, final String storageKey, final String repositoryUuid,
			final String branchPath, final String baseCodeSystemOid, final SnomedReleaseType snomedReleaseType) {
		super(oid, name, shortName, orgLink, language, citation, iconPath, snowOwlId, storageKey, repositoryUuid, branchPath);
		this.baseCodeSystemOid = baseCodeSystemOid;
		this.snomedReleaseType = snomedReleaseType;
	}

	public String getBaseCodeSystemOid() {
		return baseCodeSystemOid;
	}

	public SnomedReleaseType getSnomedReleaseType() {
		return snomedReleaseType;
	}

}
