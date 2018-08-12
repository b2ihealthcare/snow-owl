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
package com.b2international.snowowl.core.messaging;

import java.util.concurrent.TimeUnit;

import com.b2international.snowowl.core.console.Command;
import com.b2international.snowowl.core.console.CommandLineStream;

import picocli.CommandLine;
import picocli.CommandLine.Parameters;

/**
 * @since 7.0
 */
@CommandLine.Command(
	name = "message",
	header = "Send a message to the end users",
	description = "Sends a message to all currently connected (via the TCP client) end users"
)
public final class SendMessageCommand extends Command {

	@Parameters(arity = "1..*", paramLabel = "MESSAGE", description = { "The message to send to the end users" })
	String[] messages;
	
	@Override
	public void run(CommandLineStream out) {
		final String message = String.join(" ", messages);
		MessagingRequests.prepareSendMessage(message)
			.buildAsync()
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);
		out.println("Message has been successfully sent:");
		out.println(message);
	}

}
