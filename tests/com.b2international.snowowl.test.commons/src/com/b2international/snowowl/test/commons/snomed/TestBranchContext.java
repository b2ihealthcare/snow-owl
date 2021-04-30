/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.test.commons.snomed;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.eclipse.emf.common.util.WrappedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.ReflectionUtils;
import com.b2international.commons.options.MetadataImpl;
import com.b2international.index.es.client.EsIndexStatus;
import com.b2international.index.revision.RevisionBranch.BranchState;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.DelegatingContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.RepositoryContextProvider;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.BranchRequest;
import com.b2international.snowowl.core.request.TerminologyResourceRequest;
import com.b2international.snowowl.eventbus.EventBusUtil;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * @since 6.4
 */
public final class TestBranchContext extends DelegatingContext implements BranchContext, RepositoryContextProvider {

	private final String repositoryId;
	private final Branch branch;

	private TestBranchContext(String repositoryId, Branch branch) {
		super(ServiceProvider.EMPTY);
		this.repositoryId = repositoryId;
		this.branch = branch;
	}
	
	@Override
	public RepositoryContext getContext(String repositoryId) {
		return this;
	}
	
	@Override
	public Logger log() {
		return LoggerFactory.getLogger(repositoryId);
	}
	
	@Override
	public Branch branch() {
		return branch;
	}
	
	@Override
	public String path() {
		return branch.path();
	}
	
	@Override
	public SnowOwlConfiguration config() {
		return service(SnowOwlConfiguration.class);
	}
	
	@Override
	public String id() {
		return repositoryId;
	}
	
	@Override
	public Health health() {
		return Health.GREEN;
	}
	
	@Override
	public List<EsIndexStatus> indices() {
		return Collections.emptyList();
	}
	
	public static TestBranchContext.Builder on(String branch) {
		return new TestBranchContext.Builder(branch);
	}
	
	public static class Builder {
		
		private TestBranchContext context;
		
		Builder(String branch) {
			final String repositoryId = UUID.randomUUID().toString();
			IBranchPath branchPath = BranchPathUtils.createPath(branch);
			final Branch mockBranch = new Branch(1L, branchPath.lastSegment(), branchPath.getParentPath(), 0L, 1L, false, new MetadataImpl(), BranchState.FORWARD, branchPath, Collections.emptyList());
			context = new TestBranchContext(repositoryId, mockBranch);
			final IEventBus bus = EventBusUtil.getDirectBus(repositoryId);
			bus.registerHandler(Request.ADDRESS, message -> {
				try {
					final Request<?, ?> req = message.body(Request.class);
					final BranchRequest<?> branchReq = Request.getNestedRequest(req, BranchRequest.class);
					final TerminologyResourceRequest<?> codeSystemResourceRequest = Request.getNestedRequest(req, TerminologyResourceRequest.class);
					final Request<BranchContext, ?> innerReq = branchReq != null ? ReflectionUtils.getField(DelegatingRequest.class, branchReq, "next") : ReflectionUtils.getField(DelegatingRequest.class, codeSystemResourceRequest, "next");
					message.reply(innerReq.execute(context));
				} catch (WrappedException e1) {
					message.fail(e1.getCause());
				} catch (Throwable e2) {
					message.fail(e2);
				}
			});
			with(IEventBus.class, bus);
			with(RepositoryContextProvider.class, context);
		}
		
		public <T> Builder with(Class<T> type, T object) {
			context.bind(type, object);
			return this;
		}
		
		public BranchContext build() {
			return context;
		}
		
	}
	
}