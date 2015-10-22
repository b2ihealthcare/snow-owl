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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.emf.cdo.common.branch.CDOBranch;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.core.exceptions.RequestTimeoutException;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.datastore.TerminologyRegistryService;
import com.b2international.snowowl.datastore.UserBranchPathMap;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.events.BranchReply;
import com.b2international.snowowl.datastore.events.ReadBranchEvent;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.inject.Provider;

/**
 * @since 4.5
 */
public final class RepositoryRequest<B> extends DelegatingRequest<ServiceProvider, RepositoryContext, B> {

	private static final IBranchPathMap MAIN_BRANCH_PATH_MAP = new UserBranchPathMap();
	private static final long DEFAULT_ASYNC_TIMEOUT_DELAY = 5000;
	
	private final String codeSystemShortName;
	private final String branchPath;

	// TODO replace short name with repositoryId or define which is the one true ID
	public RepositoryRequest(String codeSystemShortName, String branchPath, Request<RepositoryContext, B> original) {
		super(original);
		this.codeSystemShortName = codeSystemShortName;
		this.branchPath = branchPath;
	}
	
	@Override
	public B execute(final ServiceProvider context) {
		final Branch branch = ensureAvailability(context);
		// TODO replace execution with event bus dispatch??? or pass it onto a worker thread and do not execute on event thread
		// repositories could have fixed (but configurable) amount of worker thread
		return next(new RepositoryContext() {
			@Override
			public <T> T service(Class<T> type) {
				return context.service(type);
			}
			
			@Override
			public <T> Provider<T> provider(Class<T> type) {
				return context.provider(type);
			}
			
			@Override
			public Branch branch() {
				return branch;
			}
		});
	}
	
	private Branch ensureAvailability(ServiceProvider context) {
		// XXX: in case of a non-MAIN-registered code system, we would need a repository UUID to get the code system to get the repository UUID
		final String repositoryUuid = ensureCodesystemAvailability(context);
		return ensureBranchAvailability(context, repositoryUuid);
	}

	private String ensureCodesystemAvailability(ServiceProvider context) {
		final ICodeSystem codeSystem = context.service(TerminologyRegistryService.class).getCodeSystemByShortName(MAIN_BRANCH_PATH_MAP, codeSystemShortName);
		if (codeSystem == null) {
			throw new CodeSystemNotFoundException(codeSystemShortName);
		}
		return codeSystem.getRepositoryUuid();
	}

	private Branch ensureBranchAvailability(ServiceProvider context, final String repositoryUuid) {
		Branch branch = null;
		try {
			final ReadBranchEvent event = new ReadBranchEvent(repositoryUuid, branchPath);
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
		
		final ICDOConnection connection = context.service(ICDOConnectionManager.class).getByUuid(repositoryUuid);
		final CDOBranch cdoBranch = connection.getBranch(branch.branchPath());
		if (cdoBranch == null) {
			throw new NotFoundException("Branch", branchPath);
		}
		
		return branch;
	}
	
}
