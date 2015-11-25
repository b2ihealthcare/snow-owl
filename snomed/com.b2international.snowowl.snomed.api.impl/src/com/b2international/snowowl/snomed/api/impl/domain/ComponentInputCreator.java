package com.b2international.snowowl.snomed.api.impl.domain;

import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserComponentWithId;
import com.b2international.snowowl.snomed.datastore.server.request.BaseSnomedComponentUpdateRequest;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedComponentCreateRequest;

public interface ComponentInputCreator<I extends SnomedComponentCreateRequest, U extends BaseSnomedComponentUpdateRequest, T extends ISnomedBrowserComponentWithId> {

	I createInput(String branchPath, T component, InputFactory inputFactory);

	U createUpdate(T existingVersion, T newVersion);

	boolean canCreateInput(Class<? extends SnomedComponentCreateRequest> inputType);

	boolean canCreateUpdate(Class<? extends BaseSnomedComponentUpdateRequest> updateType);
}
