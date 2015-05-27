/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.history.domain.IHistoryInfo;
import com.b2international.snowowl.snomed.api.ISnomedReferenceSetHistoryService;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedReferenceSetHistory;
import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @since 1.0
 */
@ApiIgnore
@Api("History")
@RestController
@RequestMapping(
		produces={ AbstractRestService.SO_MEDIA_TYPE })
public class SnomedReferenceSetHistoryRestService extends AbstractSnomedRestService {

	@Autowired
	protected ISnomedReferenceSetHistoryService delegate;

	@RequestMapping(value="/{path:**}/reference-sets/{refSetId}/history", method=RequestMethod.GET)
	@ApiOperation(
			value="Get history for a reference set", 
			notes="Retrieves history for the specified SNOMED CT reference set.")
	public SnomedReferenceSetHistory getHistory(
			@PathVariable(value="path") final String branchPath,
			@PathVariable(value="refSetId") final String refSetId) {

		final IComponentRef refSetRef = createComponentRef(branchPath, refSetId);
		final List<IHistoryInfo> referenceSetHistory = delegate.getHistory(refSetRef);

		final SnomedReferenceSetHistory result = new SnomedReferenceSetHistory();
		result.setReferenceSetHistory(referenceSetHistory);
		return result;
	}

}