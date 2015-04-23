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
package com.b2international.snowowl.datastore.internal.branch;

import java.util.concurrent.atomic.AtomicLong;

import com.b2international.snowowl.core.exceptions.NotImplementedException;
import com.b2international.snowowl.datastore.branch.Branch;
import com.b2international.snowowl.datastore.branch.BranchManager;
import com.b2international.snowowl.datastore.branch.TimestampProvider;
import com.b2international.snowowl.datastore.events.CreateBranchEvent;
import com.b2international.snowowl.datastore.events.BranchReply;
import com.b2international.snowowl.datastore.events.ReadBranchEvent;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;

/**
 * @since 4.1
 */
public class BranchEventHandler implements IHandler<IMessage> {

	private BranchManager branchManager = new BranchManagerImpl(0L, new TimestampProvider() {
		private AtomicLong clock = new AtomicLong(0L);
		@Override
		public long getTimestamp() {
			return clock.getAndIncrement();
		}
	});
	
	@Override
	public void handle(IMessage message) {
		// TODO how to handle multiple possible message bodies
		// TODO consider using eventbus APIs with method annotations, @Handler
		final Object event = message.body();
		if (event instanceof CreateBranchEvent) {
			message.reply(createBranch((CreateBranchEvent)event));
		} else if (event instanceof ReadBranchEvent) {
			message.reply(readBranch((ReadBranchEvent) event));
		} else {
			throw new NotImplementedException("Event handling not implemented: " + event);
		}
	}

	private BranchReply readBranch(ReadBranchEvent event) {
		return new BranchReply(branchManager.getBranch(event.getBranchPath()));
	}

	private BranchReply createBranch(CreateBranchEvent event) {
		final Branch child = branchManager.getBranch(event.getParent()).createChild(event.getName());
		return new BranchReply(child);
	}
	
}
