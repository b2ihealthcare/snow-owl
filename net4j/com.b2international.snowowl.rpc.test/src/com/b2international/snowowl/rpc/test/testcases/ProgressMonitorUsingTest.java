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
package com.b2international.snowowl.rpc.test.testcases;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import com.b2international.snowowl.rpc.test.service.IProgressMonitorUsingService;
import com.b2international.snowowl.rpc.test.service.impl.ProgressMonitorUsingService;

/**
 * Contains test cases for remote method calls that want to report their progress, indicated by an
 * {@link IProgressMonitor} parameter in the signature.
 * 
 */
public class ProgressMonitorUsingTest extends AbstractRpcTest<IProgressMonitorUsingService, ProgressMonitorUsingService> {

	public ProgressMonitorUsingTest() {
		super(IProgressMonitorUsingService.class);
	}

	@Test
	public void testIProgressMonitor() {
		
		final IProgressMonitorUsingService serviceProxy = initializeService();
		final IProgressMonitor mockMonitor = mock(IProgressMonitor.class);
		serviceProxy.reportWithIProgressMonitor(mockMonitor);
		
		verify(mockMonitor).beginTask(IProgressMonitorUsingService.TASK_NAME, IProgressMonitorUsingService.TOTAL_WORK);
		verify(mockMonitor, times(IProgressMonitorUsingService.TOTAL_WORK)).worked(1);
		verify(mockMonitor, never()).internalWorked(anyDouble());
		verify(mockMonitor).done();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testNullProgressMonitor() {
		
		final IProgressMonitorUsingService serviceProxy = initializeService();
		final NullProgressMonitor nullMonitor = new NullProgressMonitor();
		serviceProxy.reportWithNullProgressMonitor(nullMonitor);
	}

	@Override
	protected ProgressMonitorUsingService createServiceImplementation() {
		return new ProgressMonitorUsingService();
	}
}