/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.options.Options;

/**
 * @since 8.11
 */
public class ExpandParserTest {

	@Test
	public void parseRegular() throws Exception {
		final Options actual = ExpandParser.parse("direct: true, limit: 100");
		assertEquals(true, actual.getBoolean("direct"));
		assertEquals(100, actual.get("limit"));
	}
	
	@Test
	public void parseNested() throws Exception {
		final Options actual = ExpandParser.parse("descendants(direct: true, limit: 100)");
		final Options actualDescendants = actual.getOptions("descendants");
		assertEquals(true, actualDescendants.getBoolean("direct"));
		assertEquals(100, actualDescendants.get("limit"));
	}
	
	@Test
	public void parseQuoted() throws Exception {
		final Options actual = ExpandParser.parse("refsets(limit: 100, ecl: \"(1234 OR 3425)\")");
		final Options actualRefsets = actual.getOptions("refsets");
		assertEquals(100, actualRefsets.get("limit"));
		assertEquals("(1234 OR 3425)", actualRefsets.getString("ecl"));
	}
	
	@Test(expected = BadRequestException.class)
	public void parseNonJson() throws Exception {
		ExpandParser.parse("refsets");
	}
}
