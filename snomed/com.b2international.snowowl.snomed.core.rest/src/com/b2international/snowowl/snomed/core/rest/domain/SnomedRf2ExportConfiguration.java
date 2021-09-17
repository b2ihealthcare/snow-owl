/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.domain;

import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @since 7.5
 */
public final class SnomedRf2ExportConfiguration {

	@Schema(description = "The RF2 type to use", allowableValues = "full,snapshot,delta", defaultValue = "snapshot")
	private String type = Rf2ReleaseType.SNAPSHOT.name();
	
	@Schema(description = "The namespaceId to use in the release archive name")
	private String namespaceId = "";
	
	@Schema(description = "Optional moduleIds to restrict the exported content")
	private Collection<String> moduleIds;
	
	@Schema(description = "Optional refSetIds to restrict the export content")
	private Collection<String> refSetIds;
	
	@Schema(description = "Delta export start effectiveTime. By default unbounded.")
	private String startEffectiveTime;
	
	@Schema(description = "Delta export end effectiveTime. By default unbounded.")
	private String endEffectiveTime;
	
	@Schema(description = "Transient effectiveTime to apply on unpublished content")
	private String transientEffectiveTime;
	
	@Schema(description = "To include unreleased changes in the export result")
	private boolean includeUnpublished = true;
	
	@Schema(description = "To export the content of the Extension only or all dependencies as well forming an Edition Release.")
	private boolean extensionOnly = false;
	
	@Schema(description = "The RF2 RefSet file layout to use. Defaults to server configuration key 'snomed.export.refSetLayout'.", allowableValues = "combined,individual")
	private String refSetLayout;
	
	@Schema(description = "The nrcCountryCode to use un the release archive name")
	private String nrcCountryCode = "";
	
	@Schema(description = "The maintainerType to use un the release archive name")
	private String maintainerType = "";
	
	@Schema(description = "The component types to export. By default it exports every core and refset component type.", allowableValues = {SnomedConcept.TYPE, SnomedDescription.TYPE, SnomedRelationship.TYPE, SnomedConcept.REFSET_TYPE})
	private List<String> componentTypes;
	
	/**
	 * Returns with the RF2 release type of the current export configuration.
	 * @return the desired RF2 release type.
	 */
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Returns with a restricting export start effective time. Can be {@code null}.
	 */
	public String getStartEffectiveTime() {
		return startEffectiveTime;
	}
	
	public void setStartEffectiveTime(String startEffectiveTime) {
		this.startEffectiveTime = startEffectiveTime;
	}

	/**
	 * Returns with a restricting export end effective time.May return with {@code null}.
	 */
	public String getEndEffectiveTime() {
		return endEffectiveTime;
	}
	
	public void setEndEffectiveTime(String endEffectiveTime) {
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
	public Collection<String> getRefSetIds() {
		return refSetIds;
	}
	
	public void setRefSetIds(Collection<String> refSetIds) {
		this.refSetIds = refSetIds;
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
	
	public String getRefSetLayout() {
		return refSetLayout;
	}
	
	public void setRefSetLayout(String refSetLayout) {
		this.refSetLayout = refSetLayout;
	}
	
	public String getMaintainerType() {
		return maintainerType;
	}
	
	public void setMaintainerType(String maintainerType) {
		this.maintainerType = maintainerType;
	}
	
	public String getNrcCountryCode() {
		return nrcCountryCode;
	}
	
	public void setNrcCountryCode(String nrcCountryCode) {
		this.nrcCountryCode = nrcCountryCode;
	}
	
	public List<String> getComponentTypes() {
		return componentTypes;
	}
	
	public void setComponentTypes(List<String> componentTypes) {
		this.componentTypes = componentTypes;
	}
}
