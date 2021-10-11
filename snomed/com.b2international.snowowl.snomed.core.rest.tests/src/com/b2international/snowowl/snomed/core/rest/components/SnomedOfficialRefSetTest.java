/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.components;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.test.commons.rest.BranchBase;

/**
 * @since 8.0
 */
@BranchBase(value = "MAIN")
@RunWith(Parameterized.class)
public class SnomedOfficialRefSetTest extends SnomedRefSetParameterizedTest {

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{ 	SnomedRefSetType.ASSOCIATION					}, 
			{ 	SnomedRefSetType.ATTRIBUTE_VALUE				}, 
			//  Concrete data type reference sets are tested separately 
			{ 	SnomedRefSetType.COMPLEX_MAP					},
			{ 	SnomedRefSetType.DESCRIPTION_TYPE				}, 
			{ 	SnomedRefSetType.EXTENDED_MAP					},
			{ 	SnomedRefSetType.LANGUAGE						},
			{ 	SnomedRefSetType.MODULE_DEPENDENCY				},
			//  Query type reference sets are tested separately 
			{ 	SnomedRefSetType.SIMPLE							}, 
			{ 	SnomedRefSetType.SIMPLE_MAP						}, 
			{ 	SnomedRefSetType.SIMPLE_MAP_WITH_DESCRIPTION	}, 
			{ 	SnomedRefSetType.OWL_AXIOM						},
			{ 	SnomedRefSetType.OWL_ONTOLOGY					},
			{ 	SnomedRefSetType.MRCM_DOMAIN					},
			{ 	SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN			},
			{ 	SnomedRefSetType.MRCM_ATTRIBUTE_RANGE			},
			{ 	SnomedRefSetType.MRCM_MODULE_SCOPE				},
		});
	}
	
	public SnomedOfficialRefSetTest(SnomedRefSetType refSetType) {
		super(refSetType);
	}

}
