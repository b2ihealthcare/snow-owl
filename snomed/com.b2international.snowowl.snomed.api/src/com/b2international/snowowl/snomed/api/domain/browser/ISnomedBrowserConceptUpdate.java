package com.b2international.snowowl.snomed.api.domain.browser;

import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;

public interface ISnomedBrowserConceptUpdate extends ISnomedBrowserConcept {

	InactivationIndicator getInactivationIndicator();
	
}
