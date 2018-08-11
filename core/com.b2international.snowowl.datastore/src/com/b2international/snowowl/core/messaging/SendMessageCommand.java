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

import org.eclipse.osgi.framework.console.CommandInterpreter;

import com.b2international.commons.extension.Component;
import com.b2international.snowowl.core.console.Command;
import com.google.common.base.Strings;

/**
 * @since 7.0
 */
@Component
public final class SendMessageCommand extends Command {

	@Override
	public void run(CommandInterpreter interpreter) {
		final String messageBody = interpreter.nextArgument();
		final StringBuilder sb = new StringBuilder(messageBody);
		sb.append(' ');
		while (true) {
			final String messageFragment = interpreter.nextArgument();
			if (Strings.isNullOrEmpty(messageFragment)) { 
				break;
			} else {
				sb.append(messageFragment);
				sb.append(' ');
			}
		}
		
		final String message = sb.toString();
		
		MessagingRequests.prepareSendMessage(message)
			.buildAsync()
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);
	}

	@Override
	public String getCommand() {
		return "message [message]";
	}

	@Override
	public String getDescription() {
		return "Send a message to all currently connected (via tcp client) users.";
	}

}
