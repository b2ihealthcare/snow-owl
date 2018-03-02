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
package com.b2international.snowowl.snomed.datastore.config;

import com.b2international.snowowl.snomed.core.domain.Rf2MaintainerType;
import com.b2international.snowowl.snomed.core.domain.Rf2RefSetExportLayout;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holds SNOMED CT RF2 export-related configuration values.
 * 
 * @since 6.3
 */
public class SnomedExportDefaultConfiguration {

	@JsonProperty(value = "maintainer", required = false)
	private Rf2MaintainerType maintainerType = Rf2MaintainerType.SNOMED_INTERNATIONAL;

	@JsonProperty(value = "nrcCountryCode", required = false)
	private String nrcCountryCode = "";

	@JsonProperty(value = "extensionNamespaceId", required = false)
	private String extensionNamespaceId = "";

	@JsonProperty(value = "refSetLayout", required = false)
	private Rf2RefSetExportLayout refSetExportLayout = Rf2RefSetExportLayout.COMBINED;

	public Rf2MaintainerType getMaintainerType() {
		return maintainerType;
	}

	public void setMaintainerType(final Rf2MaintainerType maintainerType) {
		this.maintainerType = maintainerType;
	}

	public String getNrcCountryCode() {
		return nrcCountryCode;
	}

	public void setNrcCountryCode(final String nrcCountryCode) {
		this.nrcCountryCode = nrcCountryCode;
	}

	public String getExtensionNamespaceId() {
		return extensionNamespaceId;
	}

	public void setExtensionNamespaceId(final String extensionNamespaceId) {
		this.extensionNamespaceId = extensionNamespaceId;
	}

	public Rf2RefSetExportLayout getRefSetExportLayout() {
		return refSetExportLayout;
	}

	public void setRefSetExportType(final Rf2RefSetExportLayout refSetExportLayout) {
		this.refSetExportLayout = refSetExportLayout;
	}
}
