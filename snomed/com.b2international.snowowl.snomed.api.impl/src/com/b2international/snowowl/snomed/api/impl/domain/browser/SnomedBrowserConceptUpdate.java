package com.b2international.snowowl.snomed.api.impl.domain.browser;

import com.b2international.snowowl.snomed.api.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConceptUpdate;

public class SnomedBrowserConceptUpdate extends SnomedBrowserConcept implements ISnomedBrowserConceptUpdate {

	private InactivationIndicator inactivationIndicator;
	
	@Override
	public InactivationIndicator getInactivationIndicator() {
		return inactivationIndicator;
	}
	
	public void setInactivationIndicator(InactivationIndicator inactivationIndicator) {
		this.inactivationIndicator = inactivationIndicator;
	}
}
