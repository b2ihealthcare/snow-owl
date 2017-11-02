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
package com.b2international.snowowl.snomed.datastore.request.rf2;

import java.io.File;
import java.util.UUID;

import org.junit.Test;

/**
 * @since 6.0.0
 */
public class SnomedRf2ImportRequestTest {
	
	@Test
	public void mapDbImportRf2() throws Exception {
		new SnomedRf2ImportRequest(UUID.randomUUID()).doImport(new File("d:/SnomedCT_RF2Release_INT_20160731.zip"));
	}

}
