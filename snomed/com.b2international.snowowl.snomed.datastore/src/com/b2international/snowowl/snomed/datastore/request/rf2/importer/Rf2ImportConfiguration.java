/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2.importer;

import java.io.File;

import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;

/**
 * @since 7.0 
 */
public final class Rf2ImportConfiguration {
	
	private String userId;
	private boolean createVersions;
	private File rf2Archive;
	private String codeSystemShortName;
	private Rf2ReleaseType releaseType;

	public Rf2ImportConfiguration(String userId, boolean createVersions, File rf2Archive, String codeSystemShortName, Rf2ReleaseType releaseType) {
		this.userId = userId;
		this.createVersions = createVersions;
		this.rf2Archive = rf2Archive;
		this.codeSystemShortName = codeSystemShortName;
		this.releaseType = releaseType;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isCreateVersions() {
		return createVersions;
	}

	public void setCreateVersions(boolean createVersions) {
		this.createVersions = createVersions;
	}

	public File getRf2Archive() {
		return rf2Archive;
	}

	public void setRf2Archive(File rf2Archive) {
		this.rf2Archive = rf2Archive;
	}

	public String getCodeSystemShortName() {
		return codeSystemShortName;
	}

	public void setCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
	}

	public Rf2ReleaseType getReleaseType() {
		return releaseType;
	}

	public void setReleaseType(Rf2ReleaseType releaseType) {
		this.releaseType = releaseType;
	}
	
}
