/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;
import java.util.UUID;

import org.eclipse.emf.common.util.WrappedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.ReflectionUtils;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.RepositoryInfo.Health;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.context.TerminologyResourceContentRequest;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.DelegatingContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.RepositoryContextProvider;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.BranchSnapshotContentRequest;
import com.b2international.snowowl.eventbus.EventBusUtil;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * @since 6.4
 */
public final class TestBranchContext extends DelegatingContext implements BranchContext, RepositoryContextProvider {

	private final String repositoryId;
	private final String branch;

	private TestBranchContext(String repositoryId, String branch) {
		super(ServiceProvider.EMPTY);
		this.repositoryId = repositoryId;
		this.branch = branch;
		bind(RepositoryInfo.class, RepositoryInfo.of(repositoryId, Health.GREEN, null, List.of()));
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
	public String path() {
		return branch;
	}
	
	@Override
	public SnowOwlConfiguration config() {
		return service(SnowOwlConfiguration.class);
	}
	
	public static TestBranchContext.Builder on(String branch) {
		return new TestBranchContext.Builder(branch);
	}
	
	public static class Builder {
		
		private TestBranchContext context;
		
		Builder(String branch) {
			final String repositoryId = UUID.randomUUID().toString();
			context = new TestBranchContext(repositoryId, branch);
			final IEventBus bus = EventBusUtil.getDirectBus(repositoryId);
			bus.registerHandler(Request.ADDRESS, message -> {
				try {
					final Request<?, ?> req = message.body(Request.class);
					final TerminologyResourceContentRequest<?> codeSystemResourceRequest = Request.getNestedRequest(req, TerminologyResourceContentRequest.class);
					final Request<RepositoryContext, ?> innerReq = req instanceof BranchSnapshotContentRequest<?> ? (Request<RepositoryContext, ?>) req : new BranchSnapshotContentRequest<>(branch, (Request<BranchContext, ?>) ReflectionUtils.getField(DelegatingRequest.class, codeSystemResourceRequest, "next"));
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