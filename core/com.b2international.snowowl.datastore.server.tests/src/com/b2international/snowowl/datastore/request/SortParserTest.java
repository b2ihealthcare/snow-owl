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
package com.b2international.snowowl.datastore.request;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.google.common.collect.ImmutableList;
import com.b2international.snowowl.core.request.SortParser;

public class SortParserTest {

	@Test
	public void parseRegularSortString() throws Exception {
		final List<Sort> expected = ImmutableList.of(
				SortField.ascending("field1"),
				SortField.ascending("field2"),
				SortField.descending("field3"));
		final List<Sort> actual = SortParser.parse("field1,field2:asc,field3:desc");
		assertEquals(expected, actual);
	}

	@Test
	public void parseEmptySeparator() throws Exception {
		final List<Sort> expected = ImmutableList.of(
				SortField.ascending("field1"),
				SortField.ascending("field2"),
				SortField.descending("field3"));
		final List<Sort> actual = SortParser.parse("field1,,,field2:asc,field3:desc");
		assertEquals(expected, actual);
	}

	@Test
	public void parseWhitespaceAroundSeparator() throws Exception {
		final List<Sort> expected = ImmutableList.of(
				SortField.ascending("field1"),
				SortField.ascending("field2"));
		final List<Sort> actual = SortParser.parse("   field1, field2");
		assertEquals(expected, actual);
	}

	@Test(expected=IllegalArgumentException.class)
	public void rejectIllegalCharacterInField() throws Exception {
		SortParser.parse("field!,field2");
	}

	@Test(expected=IllegalArgumentException.class)
	public void rejectIllegalSortOrder() throws Exception {
		SortParser.parse("field,field2:random");
	}
}
