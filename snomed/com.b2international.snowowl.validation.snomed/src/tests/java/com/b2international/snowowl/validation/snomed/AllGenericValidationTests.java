/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
 * @since 6.4
 */
@RunWith(Suite.class)
@SuiteClasses({ GenericValidationRuleTest.class })
public class AllGenericValidationTests {

	@BeforeClass
	public static void init() {
		ValidationRuleEvaluator.Registry.register(new SnomedQueryValidationRuleEvaluator());
		String resourcesDir = PlatformUtil.toAbsoluteBundlePath(BaseGenericValidationRuleTest.class.getClassLoader().getResource("src/main/resources"));
		ValidationRuleEvaluator.Registry.register(new GroovyScriptValidationRuleEvaluator(Paths.get(resourcesDir)));
	}

}
