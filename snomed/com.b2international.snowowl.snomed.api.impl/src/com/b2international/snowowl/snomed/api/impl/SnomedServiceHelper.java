package com.b2international.snowowl.snomed.api.impl;

import com.b2international.snowowl.core.domain.IComponentRef;
import com.b2international.snowowl.datastore.server.domain.ComponentRef;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;

public class SnomedServiceHelper {

	protected static IComponentRef createComponentRef(final String branchPath, final String componentId) {
		final ComponentRef conceptRef = new ComponentRef(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath, componentId);
		conceptRef.checkStorageExists();
		return conceptRef;
	}

}
