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
package com.b2international.snowowl.datastore.internal.branch;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.b2international.snowowl.core.branch.Branch;

/**
 * @since 4.1
 */
public class MockInternalCDOBranchManager implements InternalCDOBranchManager {

	private InternalCDOBranchManager delegate = mock(InternalCDOBranchManager.class);
	private CDOTimeProvider clock;
	
	public MockInternalCDOBranchManager(CDOTimeProvider clock) {
		this.clock = clock;
		when(delegate.getTimeProvider()).thenReturn(clock);
	}
	
	private AtomicInteger branchIds = new AtomicInteger(CDOBranch.MAIN_BRANCH_ID);

	public void activate() throws LifecycleException {
		delegate.activate();
	}

	public void addListener(IListener listener) {
		delegate.addListener(listener);
	}

	public Exception deactivate() {
		return delegate.deactivate();
	}

	public LifecycleState getLifecycleState() {
		return delegate.getLifecycleState();
	}

	public boolean isActive() {
		return delegate.isActive();
	}

	public BranchLoader getBranchLoader() {
		return delegate.getBranchLoader();
	}

	public void setBranchLoader(BranchLoader branchLoader) {
		delegate.setBranchLoader(branchLoader);
	}

	public CDOTimeProvider getTimeProvider() {
		return delegate.getTimeProvider();
	}

	public void removeListener(IListener listener) {
		delegate.removeListener(listener);
	}

	public void setTimeProvider(CDOTimeProvider timeProvider) {
		delegate.setTimeProvider(timeProvider);
	}

	public void initMainBranch(boolean local, long timestamp) {
		mockCDOBranch(null, CDOBranch.MAIN_BRANCH_NAME);
	}

	public boolean hasListeners() {
		return delegate.hasListeners();
	}

	public InternalCDOBranch getMainBranch() {
		return delegate.getMainBranch();
	}

	public InternalCDOBranch getBranch(int branchID) {
		return delegate.getBranch(branchID);
	}

	public InternalCDOBranch getBranch(int id, String name, InternalCDOBranch baseBranch, long baseTimeStamp) {
		return delegate.getBranch(id, name, baseBranch, baseTimeStamp);
	}

	public IListener[] getListeners() {
		return delegate.getListeners();
	}

	public InternalCDOBranch getBranch(int id, BranchInfo branchInfo) {
		return delegate.getBranch(id, branchInfo);
	}

	public InternalCDOBranch getBranch(String path) {
		return delegate.getBranch(path);
	}

	public InternalCDOBranch createBranch(int id, String name, InternalCDOBranch baseBranch, long baseTimeStamp) {
		return delegate.createBranch(id, name, baseBranch, baseTimeStamp);
	}

	@Deprecated
	public void handleBranchCreated(InternalCDOBranch branch) {
		delegate.handleBranchCreated(branch);
	}
	
	public void handleBranchChanged(InternalCDOBranch branch, ChangeKind changeKind) {
		delegate.handleBranchChanged(branch, changeKind);
	}
	
	public void renameBranch(CDOBranch branch, String newName) {
		delegate.renameBranch(branch, newName);
	}

	public int getBranches(int startID, int endID, CDOBranchHandler handler) {
		return delegate.getBranches(startID, endID, handler);
	}
	
	private CDOBranchPoint mockBase(InternalCDOBranch parent, long baseTimestamp) {
		final CDOBranchPoint base = mock(CDOBranchPoint.class);
		when(base.getBranch()).thenReturn(parent);
		when(base.getTimeStamp()).thenReturn(baseTimestamp);
		return base;
	}
	
	private void mockBranchID(InternalCDOBranch branch, int id) {
		when(branch.getID()).thenReturn(id);
		when(delegate.getBranch(id)).thenReturn(branch);
		if (id == 0) {
			when(delegate.getMainBranch()).thenReturn(branch);
		}
	}

	private void mockBranchPath(InternalCDOBranch branch, String parentPath, String name) {
		when(branch.getPathName()).thenReturn(parentPath.concat(name));
	}
	
	private void mockBranchCreation(InternalCDOBranch branch) {
		when(branch.createBranch(anyString())).thenAnswer(new Answer<InternalCDOBranch>() {
			@Override
			public InternalCDOBranch answer(InvocationOnMock invocation) throws Throwable {
				return mockCDOBranch((InternalCDOBranch)invocation.getMock(), (String) invocation.getArguments()[0]);
			}
		});		
	}

	private InternalCDOBranch mockCDOBranch(InternalCDOBranch parent, String name) {
		final InternalCDOBranch branch = mock(InternalCDOBranch.class);
		final CDOBranchPoint base = mockBase(parent, clock.getTimeStamp());
		mockBranchID(branch, branchIds.getAndIncrement());
		when(branch.getBase()).thenReturn(base);
		when(branch.isMainBranch()).thenReturn(CDOBranch.MAIN_BRANCH_NAME.equals(name));
		mockBasePath(parent, branch);
		if (parent == null) {
			mockBranchPath(branch, "", name);
		} else {
			mockBranchPath(branch, parent.getPathName().concat(Branch.SEPARATOR), name);
		}
		mockBranchCreation(branch);
		mockChildren(branch);
		return branch;
	}

	private void mockBasePath(InternalCDOBranch parent, InternalCDOBranch branch) {
		final CDOBranchPoint[] basePath;
		
		if (parent == null) {
			basePath = new CDOBranchPoint[] { mockBase(null, clock.getTimeStamp()) };
		} else {
			basePath = Arrays.copyOf(parent.getBasePath(), parent.getBasePath().length + 1);
			basePath[basePath.length - 1] = branch.getBase();
		}
		
		when(branch.getBasePath()).thenReturn(basePath);
	}

	private void mockChildren(InternalCDOBranch branch) {
		when(branch.getBranches()).thenReturn(new InternalCDOBranch[0]);
	}
}
