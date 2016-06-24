/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.api.rest;

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_CITATION;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_ICON_PATH;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_LANGUAGE;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_LINK;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_NAME;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_OID;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.TERMINOLOGY_ID;
import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.REPOSITORY_UUID;

import org.junit.ClassRule;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.api.impl.codesystem.domain.CodeSystem;
import com.b2international.snowowl.snomed.api.rest.ext.SnomedExtensionDowngradeTest;
import com.b2international.snowowl.snomed.api.rest.ext.SnomedExtensionUpgradeTest;
import com.b2international.snowowl.snomed.api.rest.ext.SnomedExtensionVersioningTest;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.core.store.SnomedReleases;
import com.b2international.snowowl.test.commons.BundleStartRule;
import com.b2international.snowowl.test.commons.Resources;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.SnowOwlAppRule;

/**
 * @since 4.7
 */
@RunWith(Suite.class)
@SuiteClasses({ 
	SnomedExtensionUpgradeTest.class,
	SnomedExtensionDowngradeTest.class,
	SnomedExtensionVersioningTest.class
})
public class SnomedExtensionApiTests {

private static final String EXT_BRANCH_PATH = "MAIN/2016-01-31/SNOMEDCT-B2I";
	
	@ClassRule
	public static final RuleChain appRule = RuleChain
			.outerRule(SnowOwlAppRule.snowOwl().clearResources(true).config(PlatformUtil.toAbsolutePath(SnomedExtensionApiTests.class, "rest-configuration.yml")))
			.around(new BundleStartRule("com.b2international.snowowl.api.rest"))
			.around(new BundleStartRule("com.b2international.snowowl.snomed.api.rest"))
			.around(new SnomedContentRule(SnomedReleases.newSnomedInternationalRelease(), Resources.Snomed.MINI_RF2__INT_20160131, ContentSubType.FULL))
			.around(new SnomedContentRule(EXT_BRANCH_PATH, createExtCodeSystem(), Resources.Snomed.MINI_RF2_EXT, ContentSubType.DELTA));

	private static CodeSystem createExtCodeSystem() {
		return CodeSystem.builder()
				.name("Extension " + SNOMED_INT_NAME)
				.shortName("SNOMEDCT-B2I")
				.oid(SNOMED_INT_OID + ".B2I")
				.primaryLanguage(SNOMED_INT_LANGUAGE)
				.organizationLink(SNOMED_INT_LINK)
				.citation(SNOMED_INT_CITATION)
				.branchPath(EXT_BRANCH_PATH)
				.iconPath(SNOMED_INT_ICON_PATH)
				.repositoryUuid(REPOSITORY_UUID)
				.terminologyId(TERMINOLOGY_ID)
				.extensionOf("SNOMEDCT")
				.build();
	}
	
}
