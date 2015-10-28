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
package com.b2international.snowowl.datastore.request;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.emf.cdo.common.branch.CDOBranch;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.DefaultBranchContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.core.exceptions.RequestTimeoutException;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.events.BranchReply;
import com.b2international.snowowl.datastore.events.ReadBranchEvent;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * @since 4.5
 */
public final class BranchRequest<B> extends DelegatingRequest<RepositoryContext, BranchContext, B> {

	private static final long DEFAULT_ASYNC_TIMEOUT_DELAY = 5000; // TODO make this configurable
	
	private final String branchPath;
	
	public BranchRequest(String branchPath, Request<BranchContext, B> next) {
		super(next);
		this.branchPath = checkNotNull(branchPath, "branchPath");
	}
	
	@Override
	public B execute(RepositoryContext context) {
		final Branch branch = ensureAvailability(context);
		return next(new DefaultBranchContext(context, branch));
	}
	
	private Branch ensureAvailability(RepositoryContext context) {
		final String repositoryId = context.id();
		Branch branch = null;
		try {
			final ReadBranchEvent event = new ReadBranchEvent(repositoryId, branchPath);
			branch = event.send(context.service(IEventBus.class), BranchReply.class).get(DEFAULT_ASYNC_TIMEOUT_DELAY, TimeUnit.MILLISECONDS).getBranch();
		} catch (InterruptedException e) {
			throw new SnowowlRuntimeException(e);
		} catch (TimeoutException e) {
			throw new RequestTimeoutException(e);
		} catch (ExecutionException e) {
			final Throwable cause = e.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw new SnowowlRuntimeException(cause);
		}
		
		if (branch == null) {
			throw new NotFoundException("Branch", branchPath);
		}
		
		// TODO is it okay to test branch deleted here??? - what happens when migrating BranchEvents, probably we have to move this later to somewhere else
		if (branch.isDeleted()) {
			throw new BadRequestException("Branch '%s' has been deleted and cannot accept further modifications.", branchPath);
		}
		
		final ICDOConnection connection = context.service(ICDOConnectionManager.class).getByUuid(repositoryId);
		final CDOBranch cdoBranch = connection.getBranch(branch.branchPath());
		if (cdoBranch == null) {
			throw new NotFoundException("Branch", branchPath);
		}
		
		return branch;
	}

}
