/*******************************************************************************
 * Copyright (c) 2018 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.validation.snomed;

import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.core.validation.eval.GroovyScriptValidationRuleEvaluator;
import com.b2international.snowowl.core.validation.eval.ValidationRuleEvaluator;
import com.b2international.snowowl.snomed.validation.SnomedQueryValidationRuleEvaluator;

/**
 * 
 * @since 6.4
 */
@RunWith(Suite.class)
@SuiteClasses({ GenericValidationRuleTest.class })
public class AllGenericValidationTests {

	@BeforeClass
	public static void init() {
		ValidationRuleEvaluator.Registry.register(new SnomedQueryValidationRuleEvaluator());
		String resourcesDir = PlatformUtil
				.toAbsoluteBundlePath(BaseGenericValidationRuleTest.class.getClassLoader().getResource("src/main/resources"));
		ValidationRuleEvaluator.Registry.register(new GroovyScriptValidationRuleEvaluator(Paths.get(resourcesDir)));
	}

}
