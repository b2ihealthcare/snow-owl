package com.b2international.snowowl.snomed.core.rest.browser;

import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedConstants;

public abstract class AbstractInputCreator {

	protected String getModuleOrDefault(ISnomedBrowserComponent component) {
		final String moduleId = component.getModuleId();
		return moduleId != null ? moduleId : SnomedConstants.Concepts.MODULE_SCT_CORE;
	}
	
	protected String getDefaultNamespace() {
		return SnomedIdentifiers.INT_NAMESPACE;
	}
}
