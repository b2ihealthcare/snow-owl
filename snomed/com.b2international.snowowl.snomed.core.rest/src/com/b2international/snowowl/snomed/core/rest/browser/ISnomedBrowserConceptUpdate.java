package com.b2international.snowowl.snomed.core.rest.browser;

import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;

public interface ISnomedBrowserConceptUpdate extends ISnomedBrowserConcept {

	InactivationIndicator getInactivationIndicator();
	
}
