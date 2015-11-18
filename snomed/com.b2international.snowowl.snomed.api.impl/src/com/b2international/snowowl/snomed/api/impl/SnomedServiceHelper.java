package com.b2international.snowowl.snomed.api.impl;

import com.b2international.snowowl.core.domain.IComponentRef;
import com.b2international.snowowl.datastore.server.domain.ComponentRef;

public class SnomedServiceHelper {

	public static final String SNOMEDCT = "SNOMEDCT";

	protected static IComponentRef createComponentRef(final String branchPath, final String componentId) {
		final ComponentRef conceptRef = new ComponentRef(SNOMEDCT, branchPath, componentId);
		conceptRef.checkStorageExists();
		return conceptRef;
	}

}
