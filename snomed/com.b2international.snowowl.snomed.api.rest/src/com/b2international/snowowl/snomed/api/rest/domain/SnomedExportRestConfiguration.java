/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.domain;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.AcceptHeader;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 1.0
 */
public class SnomedExportRestConfiguration {

	@NotNull(message = "RF2 release type was missing from the export configuration.")
	private Rf2ReleaseType type;
	
	@NotEmpty
	private String branchPath;
	
	@NotEmpty
	private String namespaceId = "INT";
	
	private Collection<String> moduleIds;
	private Collection<String> refsetIds;
	private Date startEffectiveTime;
	private Date endEffectiveTime;
	private String transientEffectiveTime;
	private boolean includeUnpublished;
	
	private String codeSystemShortName = SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME;
	private boolean extensionOnly = false;
	private String acceptLanguage = "en-US,en-GB";

	public String getAcceptLanguage() {
		return acceptLanguage;
	}
	
	public void setAcceptLanguage(String acceptLanguage) {
		this.acceptLanguage = acceptLanguage;
	}
	
	@JsonIgnore
	public List<ExtendedLocale> getLocales() {
		try {
			return AcceptHeader.parseExtendedLocales(new StringReader(acceptLanguage));
		} catch (IOException e) {
			throw new BadRequestException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
	}
	
	/**
	 * Returns with the RF2 release type of the current export configuration.
	 * @return the desired RF2 release type.
	 */
	public Rf2ReleaseType getType() {
		return type;
	}
	
	public void setType(Rf2ReleaseType type) {
		this.type = type;
	}
	
	/** 
	 * Returns the branch to run the export on.
	 * @return the branch to export
	 */
	public String getBranchPath() {
		return branchPath;
	}

	public void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}
	
	/**
	 * Returns with a restricting export start effective time. Can be {@code null}.
	 */
	@JsonFormat(shape=Shape.STRING, pattern="yyyyMMdd")
	public Date getStartEffectiveTime() {
		return startEffectiveTime;
	}
	
	public void setStartEffectiveTime(Date startEffectiveTime) {
		this.startEffectiveTime = startEffectiveTime;
	}

	/**
	 * Returns with a restricting export end effective time.May return with {@code null}.
	 */
	@JsonFormat(shape=Shape.STRING, pattern="yyyyMMdd")
	public Date getEndEffectiveTime() {
		return endEffectiveTime;
	}
	
	public void setEndEffectiveTime(Date endEffectiveTime) {
		this.endEffectiveTime = endEffectiveTime;
	}
	
	/**
	 * Returns with the namespace ID.
	 * <p>The namespace ID will be used when generating the folder structure 
	 * for the RF2 release format export.
	 * @return the namespace ID.
	 */
	public String getNamespaceId() {
		return namespaceId;
	}
	
	public void setNamespaceId(String namespaceId) {
		this.namespaceId = namespaceId;
	}
	
	/**
	 * Returns with a collection of SNOMED&nbsp;CT module concept IDs.
	 * <p>This collection of module IDs will define which components will be included in the export.
	 * Components having a module that is not included in the returning set will be excluded from 
	 * the export result.
	 * @return a collection of module dependency IDs.
	 */
	public Collection<String> getModuleIds() {
		return moduleIds;
	}
	
	public void setModuleIds(Collection<String> moduleIds) {
		this.moduleIds = moduleIds;
	}
	
	/**
	 * Returns with a collection of SNOMED&nbsp;CT refset concept IDs.
	 * <p>This collection of refset IDs will define which refsets and their members will be included in the export.
	 * Refsets that are not included in the returning set will be excluded from the export result.
	 * 
	 * @return a collection of refset IDs.
	 */
	public Collection<String> getRefsetIds() {
		return refsetIds;
	}
	
	public void setRefsetIds(Collection<String> refsets) {
		this.refsetIds = refsets;
	}
	
	/**
	 * Returns the transient effective time to use for unpublished components.
	 * 
	 * @return the transient effective time, or {@code null} if the default {@code UNPUBLISHED} value should be printed
	 * for unpublished components
	 */
	public String getTransientEffectiveTime() {
		return transientEffectiveTime;
	}

	public void setTransientEffectiveTime(String transientEffectiveTime) {
		this.transientEffectiveTime = transientEffectiveTime;
	}
	
	/**
	 * Sets whether unpublished components should be exported
	 * @param includeUnpublished
	 */
	public void setIncludeUnpublished(boolean includeUnpublished) {
		this.includeUnpublished = includeUnpublished;
	}
	
	/**
	 * Returns if unpublished components should be exported 
	 * @return
	 */
	public boolean isIncludeUnpublished() {
		return includeUnpublished;
	}
	
	/**
	 * Sets the short name of the code system that needs to be exported
	 * 
	 * @param codeSystemShortName the codeSystemShortName to set
	 */
	public void setCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
	}
	
	/**
	 * Returns the short name of the code system that needs to be exported
	 * 
	 * @return the codeSystemShortName
	 */
	public String getCodeSystemShortName() {
		return codeSystemShortName;
	}
	
	/**
	 * If set to true only the code system specified by it's short name will be exported. If set to false all versions from parent code systems
	 * will be collected and exported.
	 * 
	 * @param extensionOnly the extensionOnly to set
	 */
	public void setExtensionOnly(boolean extensionOnly) {
		this.extensionOnly = extensionOnly;
	}
	
	/**
	 * Returns true if only the code system specified by it's short name should be exported. If set to false all versions from parent code systems
	 * will be collected and exported.
	 * 
	 * @return the extensionOnly
	 */
	public boolean isExtensionOnly() {
		return extensionOnly;
	}
}
