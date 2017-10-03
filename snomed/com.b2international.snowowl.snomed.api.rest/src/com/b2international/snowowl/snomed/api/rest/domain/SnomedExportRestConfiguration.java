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
package com.b2international.snowowl.snomed.api.rest.domain;

import java.util.Collection;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

/**
 * @since 1.0
 */
public class SnomedExportRestConfiguration {

	@NotNull(message = "RF2 release type was missing from the export configuration.")
	private Rf2ReleaseType type;
	
	@NotEmpty
	private String branchPath;
	
	@NotEmpty
	private String countryAndNamespaceId = "INT";
	
	private Collection<String> moduleIds;
	private Date deltaStartEffectiveTime;
	private Date deltaEndEffectiveTime;
	private String transientEffectiveTime;

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
	 * Returns with the delta export start effective time.
	 * <br>Can be {@code null} even 
	 * if the {@link Rf2ReleaseType release type} is {@link Rf2ReleaseType#DELTA delta}.
	 */
	@JsonFormat(shape=Shape.STRING, pattern="yyyyMMdd")
	public Date getDeltaStartEffectiveTime() {
		return deltaStartEffectiveTime;
	}
	
	public void setDeltaStartEffectiveTime(Date deltaStartEffectiveTime) {
		this.deltaStartEffectiveTime = deltaStartEffectiveTime;
	}

	/**
	 * Returns with the delta export end effective time.
	 * <br>May return with {@code null} even 
	 * if the {@link Rf2ReleaseType release type} is {@link Rf2ReleaseType#DELTA delta}.
	 */
	@JsonFormat(shape=Shape.STRING, pattern="yyyyMMdd")
	public Date getDeltaEndEffectiveTime() {
		return deltaEndEffectiveTime;
	}
	
	public void setDeltaEndEffectiveTime(Date deltaEndEffectiveTime) {
		this.deltaEndEffectiveTime = deltaEndEffectiveTime;
	}
	
	/**
	 * Returns with the namespace ID.
	 * <p>The namespace ID will be used when generating the folder structure 
	 * for the RF2 release format export.
	 * @return the namespace ID.
	 */
	public String getCountryAndNamespaceId() {
		return countryAndNamespaceId;
	}
	
	public void setCountryAndNamespaceId(String countryAndNamespaceId) {
		this.countryAndNamespaceId = countryAndNamespaceId;
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
}
