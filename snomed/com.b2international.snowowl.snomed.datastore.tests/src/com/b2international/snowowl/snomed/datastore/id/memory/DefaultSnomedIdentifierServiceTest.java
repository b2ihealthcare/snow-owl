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
package com.b2international.snowowl.snomed.datastore.id.memory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;

import org.junit.Test;

import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class DefaultSnomedIdentifierServiceTest {

	@Test
	public void issue_SO_1945() throws Exception {
		final Iterator<String> itemIds = Iterables.cycle("1000", "1001").iterator();
		final ItemIdGenerationStrategy itemIdGenerationStrategy = new ItemIdGenerationStrategy() {
			@Override
			public String generateItemId() {
				return itemIds.next();
			}
		};
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(itemIdGenerationStrategy);
		final String first = identifiers.generate("", ComponentCategory.CONCEPT);
		assertThat(first).contains("1000");
		final String second = identifiers.generate("", ComponentCategory.CONCEPT);
		assertThat(second).contains("1001");
		// third attempt should generate ID with itemId 1000 again, 
		// but that is already generated and no more itemIds available
		// therefore it will try to generate 1001, and that fails to, rinse and repeat until maxIdGenerationAttempts
		try {
			identifiers.generate("", ComponentCategory.CONCEPT);
		} catch (BadRequestException e) {
			assertThat(e.getMessage()).isEqualTo(String.format("Couldn't generate identifier in maximum (%s) number of attempts",
					SnomedIdentifierConfiguration.DEFAULT_ID_GENERATION_ATTEMPTS));
		}
	}
	
}
