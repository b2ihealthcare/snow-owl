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

import java.util.List;

import com.b2international.commons.beans.BeanPropertyChangeSupporter;

/**
 * @since 2.8
 */
public class CodeSystemBean extends BeanPropertyChangeSupporter implements CodeSystemShortNameProvider, CodeSystemVersionProvider {

	private static final long serialVersionUID = -7666462487729940162L;
	
	private final String codeSystemOid;
	private final String name;
	private final String shortName;
	private final String maintainingOrganizationLink;
	private final String language;
	private final String citation;
	private final String snowOwlTerminologyComponentId;
	private final String codeSystemVersion;
	
	private List<String> availableCodeSystemVersions;

	
	public CodeSystemBean(String codeSystemOid, String name, String shortName, String maintainingOrganizationLink, String language, String citation,
			String snowOwlTerminologyComponentId, String codeSystemVersion, List<String> availableCodeSystemVersions) {
		this.codeSystemOid = codeSystemOid;
		this.name = name;
		this.shortName = shortName;
		this.maintainingOrganizationLink = maintainingOrganizationLink;
		this.language = language;
		this.citation = citation;
		this.snowOwlTerminologyComponentId = snowOwlTerminologyComponentId;
		this.codeSystemVersion = codeSystemVersion;
		this.availableCodeSystemVersions = availableCodeSystemVersions;
	}

	public CodeSystemBean(CodeSystemBean codeSystemBean) {
		this(
				codeSystemBean.getCodeSystemOid(),
				codeSystemBean.getName(),
				codeSystemBean.getCodeSystemShortName(),
				codeSystemBean.getMaintainingOrganizationLink(),
				codeSystemBean.getLanguage(),
				codeSystemBean.getCitation(),
				codeSystemBean.getSnowOwlTerminologyComponentId(),
				codeSystemBean.getCodeSystemVersion(),
				codeSystemBean.getAvailableVersions());
	}
	
	/**
	 * Resolves the specified code system version from the available versions.
	 * Note: only used for Value Sets.
	 * 
	 * @return the one available version which is matching or null if the
	 *         version could not be resolved.
	 * @throws RuntimeException if multiple versions would match.
	 */
	public String resolveCodeSytemVersion() {
		String resolved = null;
		for (String availableVersion : availableCodeSystemVersions) {
			if (availableVersion.equals(codeSystemVersion)) {
				if (resolved == null) {
					resolved = availableVersion;
				} else {
					throw new RuntimeException("Multiple versions are present with '" + codeSystemVersion + "' id.");
				}
			} 
		}
		return resolved;
	}

	public String getCodeSystemOid() {
		return codeSystemOid;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getCodeSystemShortName() {
		return shortName;
	}

	public String getMaintainingOrganizationLink() {
		return maintainingOrganizationLink;
	}

	public String getLanguage() {
		return language;
	}

	public String getCitation() {
		return citation;
	}

	public String getSnowOwlTerminologyComponentId() {
		return snowOwlTerminologyComponentId;
	}
	
	@Override
	public String getCodeSystemVersion() {
		return codeSystemVersion;
	}
	
	public List<String> getAvailableVersions() {
		return availableCodeSystemVersions;
	}

	public void setCodeSystemVersions(List<String> codeSystemVersions) {
		this.availableCodeSystemVersions = codeSystemVersions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((citation == null) ? 0 : citation.hashCode());
		result = prime * result + ((codeSystemOid == null) ? 0 : codeSystemOid.hashCode());
		result = prime * result + ((codeSystemVersion == null) ? 0 : codeSystemVersion.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((maintainingOrganizationLink == null) ? 0 : maintainingOrganizationLink.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
		result = prime * result + ((snowOwlTerminologyComponentId == null) ? 0 : snowOwlTerminologyComponentId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CodeSystemBean other = (CodeSystemBean) obj;
		if (citation == null) {
			if (other.citation != null)
				return false;
		} else if (!citation.equals(other.citation))
			return false;
		if (codeSystemOid == null) {
			if (other.codeSystemOid != null)
				return false;
		} else if (!codeSystemOid.equals(other.codeSystemOid))
			return false;
		if (codeSystemVersion == null) {
			if (other.codeSystemVersion != null)
				return false;
		} else if (!codeSystemVersion.equals(other.codeSystemVersion))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (maintainingOrganizationLink == null) {
			if (other.maintainingOrganizationLink != null)
				return false;
		} else if (!maintainingOrganizationLink.equals(other.maintainingOrganizationLink))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		if (snowOwlTerminologyComponentId == null) {
			if (other.snowOwlTerminologyComponentId != null)
				return false;
		} else if (!snowOwlTerminologyComponentId.equals(other.snowOwlTerminologyComponentId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CodeSystemBean [codeSystemOid=");
		builder.append(codeSystemOid);
		builder.append(", name=");
		builder.append(name);
		builder.append(", shortName=");
		builder.append(shortName);
		builder.append(", maintainingOrganizationLink=");
		builder.append(maintainingOrganizationLink);
		builder.append(", language=");
		builder.append(language);
		builder.append(", citation=");
		builder.append(citation);
		builder.append(", snowOwlTerminologyComponentId=");
		builder.append(snowOwlTerminologyComponentId);
		builder.append(", codeSystemVersions=");
		builder.append(availableCodeSystemVersions);
		builder.append("]");
		return builder.toString();
	}
	
}