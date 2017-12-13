/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.scripting.groovy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;

import org.junit.Test;

import com.b2international.scripting.api.ScriptEngine;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.1
 */
public class GroovyScriptEngineTest {

	@Test
	public void emptyScript() throws Exception {
		final String script = "";
		Object rv = ScriptEngine.run("groovy", getClass().getClassLoader(), script, Collections.emptyMap());
		assertNull(rv);
	}
	
	@Test
	public void addTwoNumbers() throws Exception {
		final String script = "a + b";
		int rv = ScriptEngine.run("groovy", getClass().getClassLoader(), script, ImmutableMap.of("a", 1, "b", 2));
		assertEquals(3, rv);
	}
	
}
