/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.ecl;

import static org.mockito.Mockito.when;

import java.util.UUID;

import org.eclipse.emf.common.util.WrappedException;
import org.mockito.Mockito;

import com.b2international.commons.ReflectionUtils;
import com.b2international.commons.exceptions.ApiException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.DelegatingContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.RepositoryContextProvider;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.BranchRequest;
import com.b2international.snowowl.datastore.request.IndexReadRequest;
import com.b2international.snowowl.datastore.request.RepositoryRequest;
import com.b2international.snowowl.eventbus.EventBusUtil;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;

/**
 * @since 5.4
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
	public RepositoryContext get(String repositoryId) {
		return this;
	}
	
	@Override
	public Branch branch() {
		return branch;
	}
	
	@Override
	public String branchPath() {
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
	
	public static TestBranchContext.Builder on(String branch) {
		return new TestBranchContext.Builder(branch);
	}
	
	public static class Builder {
		
		private TestBranchContext context;
		
		Builder(String branch) {
			final String repositoryId = UUID.randomUUID().toString();
			final Branch mockBranch = Mockito.mock(Branch.class);
			when(mockBranch.path()).thenReturn(branch);
			context = new TestBranchContext(repositoryId, mockBranch);
			final IEventBus bus = EventBusUtil.getWorkerBus(repositoryId, Runtime.getRuntime().availableProcessors());
			bus.registerHandler(Request.ADDRESS, new IHandler<IMessage>() {
				@Override
				public void handle(IMessage message) {
					try {
						final RepositoryRequest<?> repoReq = message.body(RepositoryRequest.class);
						final IndexReadRequest<?> indexReadReq = ReflectionUtils.getField(DelegatingRequest.class, repoReq, "next");
						final BranchRequest<?> branchReq = ReflectionUtils.getField(DelegatingRequest.class, indexReadReq, "next");
						final Request<BranchContext, ?> innerReq = ReflectionUtils.getField(DelegatingRequest.class, branchReq, "next");
						message.reply(innerReq.execute(context));
					} catch (WrappedException e) {
						message.fail(e.getCause());
					} catch (ApiException e) {
						message.fail(e);
					} catch (Throwable e) {
						message.fail(e);
					}
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
