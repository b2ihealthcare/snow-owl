package com.b2international.snowowl.snomed.api.impl.domain;

import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserComponent;
import com.b2international.snowowl.snomed.common.SnomedConstants;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;

public abstract class AbstractInputCreator {

	protected String getModuleOrDefault(ISnomedBrowserComponent component) {
		final String moduleId = component.getModuleId();
		return moduleId != null ? moduleId : SnomedConstants.Concepts.MODULE_SCT_CORE;
	}
	
	protected String getDefaultNamespace() {
		return SnomedIdentifiers.INT_NAMESPACE;
	}
}
