/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.b2international.snowowl.core.ServerInfo;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.datastore.request.RepositoryRequests;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @since 5.8
 */
@Api(value = "Server info", description="Info", tags = { "info" })
@Controller
@RequestMapping(value = "/info") 
public class ServerInfoRestService extends AbstractRestService {

	@ApiOperation(
		value="Retrieve server information",
		notes="Retrieves information about the running server, including version, available repositories, etc."
	)
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.HEAD }, produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<ServerInfo> info() {
		return RepositoryRequests.prepareGetServerInfo()
				.buildAsync()
				.execute(getBus());
	}
}
