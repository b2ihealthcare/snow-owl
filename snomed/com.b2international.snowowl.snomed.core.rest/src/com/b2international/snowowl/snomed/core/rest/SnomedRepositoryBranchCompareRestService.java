/*
 * Copyright 2020 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.core.rest;

import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.core.rest.SnomedApiConfig;
import com.b2international.snowowl.core.rest.compare.RepositoryBranchCompareRestService;

/**
 * @since 7.3
 */
@RestController
public class SnomedRepositoryBranchCompareRestService extends RepositoryBranchCompareRestService {

	public SnomedRepositoryBranchCompareRestService() {
		super(SnomedApiConfig.REPOSITORY_ID);
	}

}
