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

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.exceptions.ApiException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.core.exceptions.NotImplementedException;
import com.b2international.snowowl.datastore.branch.Branch;
import com.b2international.snowowl.datastore.branch.BranchManager;
import com.b2international.snowowl.datastore.events.BranchEvent;
import com.b2international.snowowl.datastore.events.BranchReply;
import com.b2international.snowowl.datastore.events.BranchesReply;
import com.b2international.snowowl.datastore.events.CreateBranchEvent;
import com.b2international.snowowl.datastore.events.DeleteBranchEvent;
import com.b2international.snowowl.datastore.events.MergeEvent;
import com.b2international.snowowl.datastore.events.ReadAllBranchEvent;
import com.b2international.snowowl.datastore.events.ReadBranchEvent;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;

/**
 * @since 4.1
 */
public class BranchEventHandler implements IHandler<IMessage> {

	private BranchManager branchManager;
	
	public BranchEventHandler(BranchManager branchManager) {
		this.branchManager = checkNotNull(branchManager, "manager");
	}
	
	@Override
	public void handle(IMessage message) {
		try {
			// TODO how to handle multiple possible message bodies
			// TODO consider using eventbus APIs with method annotations, @Handler
			final Object event = message.body();
			if (event instanceof CreateBranchEvent) {
				message.reply(createBranch((CreateBranchEvent)event));
			} else if (event instanceof ReadBranchEvent) {
				message.reply(readBranch((ReadBranchEvent) event));
			} else if (event instanceof DeleteBranchEvent) {
				message.reply(deleteBranch((DeleteBranchEvent) event));
			} else if (event instanceof ReadAllBranchEvent) {
				message.reply(readAllBranch());
			} else if (event instanceof MergeEvent) {
				message.reply(merge((MergeEvent)event));
			} else {
				throw new NotImplementedException("Event handling not implemented: " + event);
			}
		} catch (ApiException e) {
			message.fail(e);
		}
	}

	private BranchesReply readAllBranch() {
		return new BranchesReply(branchManager.getBranches());
	}

	private BranchReply deleteBranch(DeleteBranchEvent event) {
		return new BranchReply(getBranch(event).delete());
	}

	public BranchReply readBranch(ReadBranchEvent event) {
		return new BranchReply(getBranch(event));
	}

	private BranchReply createBranch(CreateBranchEvent event) {
		try {
			final Branch parent = branchManager.getBranch(event.getParent());
			final Branch child = parent.createChild(event.getName());
			return new BranchReply(child);
		} catch (NotFoundException e) {
			// if parent not found, convert it to BadRequestException
			throw e.toBadRequestException();
		}
	}
	
	private BranchReply merge(MergeEvent event) {
		try {
			final Branch source = branchManager.getBranch(event.getSource());
			final Branch target = branchManager.getBranch(event.getTarget());
			if (source.parent().equals(target)) {
				// merge into target
				final Branch merged = target.merge(source);
				return new BranchReply(merged);
			} else if (target.parent().equals(source)) {
				// rebase into target
				final Branch rebased = target.rebase(source);
				return new BranchReply(rebased);
			}
			throw new BadRequestException("Cannot merge source '%s' into target '%s', because there is no relation between them.", source.path(), target.path());
		} catch (NotFoundException e) {
			throw e.toBadRequestException();
		}
	}
	
	private Branch getBranch(BranchEvent event) {
		return branchManager.getBranch(event.getBranchPath());
	}
	
}
