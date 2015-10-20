package com.b2international.snowowl.snomed.api.impl.domain;

import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserComponentWithId;
import com.b2international.snowowl.snomed.core.domain.SnomedComponentCreateAction;
import com.b2international.snowowl.snomed.core.domain.ISnomedComponentUpdate;

public interface ComponentInputCreator<I extends SnomedComponentCreateAction, U extends ISnomedComponentUpdate, T extends ISnomedBrowserComponentWithId> {
	I createInput(String branchPath, T component, InputFactory inputFactory);
	U createUpdate(T existingVersion, T newVersion);

	boolean canCreateInput(Class<? extends SnomedComponentCreateAction> inputType);
	boolean canCreateUpdate(Class<? extends ISnomedComponentUpdate> updateType);
}
