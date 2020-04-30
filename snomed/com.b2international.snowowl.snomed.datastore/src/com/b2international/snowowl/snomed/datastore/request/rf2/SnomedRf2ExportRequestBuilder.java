/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2;

import java.util.Collection;

import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.ResourceRequest;
import com.b2international.snowowl.core.request.ResourceRequestBuilder;
import com.b2international.snowowl.core.request.RevisionIndexRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.Rf2RefSetExportLayout;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;

/**
 * @since 5.7
 */
public final class SnomedRf2ExportRequestBuilder 
		extends ResourceRequestBuilder<SnomedRf2ExportRequestBuilder, BranchContext, Attachment> 
		implements RevisionIndexRequestBuilder<Attachment> {

	private Rf2ReleaseType releaseType;
	private Rf2RefSetExportLayout refSetExportLayout;
	private String countryNamespaceElement;
	private String namespaceFilter;
	private Long startEffectiveTime;
	private Long endEffectiveTime;
	private boolean includePreReleaseContent;
	private Collection<String> componentTypes = null;
	private Collection<String> modules = null;
	private Collection<String> refSets = null;
	private String transientEffectiveTime;
	private boolean extensionOnly;
	
	SnomedRf2ExportRequestBuilder() {}
	
	public SnomedRf2ExportRequestBuilder setReleaseType(final Rf2ReleaseType releaseType) {
		this.releaseType = releaseType;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setRefSetExportLayout(final Rf2RefSetExportLayout refSetExportLayout) {
		this.refSetExportLayout = refSetExportLayout;
		return getSelf();
	}

	public SnomedRf2ExportRequestBuilder setCountryNamespaceElement(final String countryNamespaceElement) {
		this.countryNamespaceElement = countryNamespaceElement;
		return getSelf();
	}

	public SnomedRf2ExportRequestBuilder setNamespaceFilter(final String namespaceFilter) {
		this.namespaceFilter = namespaceFilter;
		return getSelf();
	}

	public SnomedRf2ExportRequestBuilder setStartEffectiveTime(final String startEffectiveTime) {
		return setStartEffectiveTime(startEffectiveTime == null ? null : EffectiveTimes.getEffectiveTime(startEffectiveTime, DateFormats.SHORT));
	}
	
	public SnomedRf2ExportRequestBuilder setStartEffectiveTime(final Long startEffectiveTime) {
		this.startEffectiveTime = startEffectiveTime;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setEndEffectiveTime(final String endEffectiveTime) {
		return setEndEffectiveTime(endEffectiveTime == null ? null : EffectiveTimes.getEffectiveTime(endEffectiveTime, DateFormats.SHORT));
	}
	
	public SnomedRf2ExportRequestBuilder setEndEffectiveTime(final Long endEffectiveTime) {
		this.endEffectiveTime = endEffectiveTime;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setIncludePreReleaseContent(final boolean includePreReleaseContent) {
		this.includePreReleaseContent = includePreReleaseContent;
		return getSelf();
	}

	public SnomedRf2ExportRequestBuilder setComponentTypes(final Collection<String> componentTypes) {
		this.componentTypes = componentTypes;
		return getSelf();
	}

	public SnomedRf2ExportRequestBuilder setModules(final Collection<String> modules) {
		this.modules = modules;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setRefSets(final Collection<String> refSets) {
		this.refSets = refSets;
		return getSelf();
	}

	public SnomedRf2ExportRequestBuilder setTransientEffectiveTime(final String transientEffectiveTime) {
		this.transientEffectiveTime = transientEffectiveTime;
		return getSelf();
	}

	public SnomedRf2ExportRequestBuilder setExtensionOnly(final boolean extensionOnly) {
		this.extensionOnly = extensionOnly;
		return getSelf();
	}

	@Override
	protected ResourceRequest<BranchContext, Attachment> create() {
		final SnomedRf2ExportRequest req = new SnomedRf2ExportRequest();
		req.setReleaseType(releaseType);
		req.setRefSetExportLayout(refSetExportLayout);
		req.setCountryNamespaceElement(countryNamespaceElement);
		req.setNamespaceFilter(namespaceFilter);
		req.setStartEffectiveTime(startEffectiveTime);
		req.setEndEffectiveTime(endEffectiveTime);
		req.setIncludePreReleaseContent(includePreReleaseContent);
		req.setComponentTypes(componentTypes);
		req.setModules(modules);
		req.setRefSets(refSets);
		req.setTransientEffectiveTime(transientEffectiveTime);
		req.setExtensionOnly(extensionOnly);
		return req;
	}
	
}
