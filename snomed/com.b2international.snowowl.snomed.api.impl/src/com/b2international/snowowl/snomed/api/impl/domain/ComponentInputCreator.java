package com.b2international.snowowl.snomed.api.impl.domain;

import com.b2international.snowowl.snomed.api.domain.ISnomedComponentInput;
import com.b2international.snowowl.snomed.api.domain.ISnomedComponentUpdate;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserComponentWithId;

public interface ComponentInputCreator<I extends ISnomedComponentInput, U extends ISnomedComponentUpdate, T extends ISnomedBrowserComponentWithId> {
	I createInput(String branchPath, T component, InputFactory inputFactory);
	U createUpdate(T existingVersion, T newVersion);

	boolean canCreateInput(Class<? extends ISnomedComponentInput> inputType);
	boolean canCreateUpdate(Class<? extends ISnomedComponentUpdate> updateType);
}
