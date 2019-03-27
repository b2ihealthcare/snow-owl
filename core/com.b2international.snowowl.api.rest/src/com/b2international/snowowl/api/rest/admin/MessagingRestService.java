/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.rest.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.api.admin.IMessagingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Spring controller for exposing {@link IMessagingService} functionality.
 * 
 */
@RestController
@RequestMapping(value={"/messages"}, consumes={ MediaType.TEXT_PLAIN_VALUE }, produces={ MediaType.TEXT_PLAIN_VALUE })
@Api("Administration")
@ApiIgnore
public class MessagingRestService extends AbstractAdminRestService {

	@Autowired
	protected IMessagingService delegate;

	@RequestMapping(value="send", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(
			value="Send message to connected users",
			notes="Sends an informational message to all connected users; the message is displayed "
					+ "in the desktop application immediately.")
	@ApiResponses({
		@ApiResponse(code=204, message="Message sent")
	})
	public void sendMessage(
			@RequestBody
			@ApiParam(value="the message to send")
			final String message) {

		delegate.sendMessage(message);
	}
}