/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Date;
import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.RepositoryRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.Rf2ExportResult;
import com.b2international.snowowl.snomed.core.domain.Rf2RefSetExportLayout;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;

/**
 * @since 5.7
 */
public final class SnomedRf2ExportRequestBuilder 
		extends BaseRequestBuilder<SnomedRf2ExportRequestBuilder, RepositoryContext, Rf2ExportResult> 
		implements RepositoryRequestBuilder<Rf2ExportResult> {

	private String userId;
	private String codeSystem;
	private String referenceBranch;
	private Rf2ReleaseType releaseType;
	private Rf2RefSetExportLayout refSetExportLayout;
	private String countryNamespaceElement;
	private String namespaceFilter;
	private Date startEffectiveTime;
	private Date endEffectiveTime;
	private boolean includePreReleaseContent;
	private Collection<String> componentTypes = null;
	private Collection<String> modules = null;
	private Collection<String> refSets = null;
	private String transientEffectiveTime;
	private boolean extensionOnly;
	private List<ExtendedLocale> locales;
	
	SnomedRf2ExportRequestBuilder() {}
	
	public SnomedRf2ExportRequestBuilder setUserId(final String userId) {
		this.userId = userId;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setCodeSystem(final String codeSystem) {
		this.codeSystem = codeSystem;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setReferenceBranch(final String referenceBranch) {
		this.referenceBranch = referenceBranch;
		return getSelf();
	}
	
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

	public SnomedRf2ExportRequestBuilder setStartEffectiveTime(final Date startEffectiveTime) {
		this.startEffectiveTime = startEffectiveTime;
		return getSelf();
	}
	
	public SnomedRf2ExportRequestBuilder setEndEffectiveTime(final Date endEffectiveTime) {
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
	
	public SnomedRf2ExportRequestBuilder setLocales(List<ExtendedLocale> locales) {
		this.locales = locales;
		return getSelf();
	}
	
	@Override
	protected Request<RepositoryContext, Rf2ExportResult> doBuild() {
		final SnomedRf2ExportRequest req = new SnomedRf2ExportRequest();
		req.setUserId(userId);
		req.setCodeSystem(codeSystem);
		req.setReferenceBranch(referenceBranch);
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
		req.setLocales(locales);
		return req;
	}
}
