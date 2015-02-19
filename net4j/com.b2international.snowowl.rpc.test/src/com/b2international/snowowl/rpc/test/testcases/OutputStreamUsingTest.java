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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

import com.b2international.snowowl.rpc.test.service.IOutputStreamUsingService;
import com.b2international.snowowl.rpc.test.service.impl.OutputStreamUsingService;

/**
 * Contains test cases for remote method calls that want to report their progress, indicated by an
 * {@link IProgressMonitor} parameter in the signature.
 * 
 */
public class OutputStreamUsingTest extends AbstractRpcTest<IOutputStreamUsingService, OutputStreamUsingService> {

	private static final String MESSAGE1 = "MESSAGE1";
	
	private static final String MESSAGE1_NL = MESSAGE1 + "\n";

	public OutputStreamUsingTest() {
		super(IOutputStreamUsingService.class);
	}

	@Test
	public void testOutputStream() throws IOException {
		
		final IOutputStreamUsingService serviceProxy = initializeService();
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		serviceProxy.writeToOutputStream(output, MESSAGE1);
		
		// TODO: change unsafe string conversion tests to use byte arrays
		assertEquals(MESSAGE1_NL, new String(output.toByteArray()));
	}

	@Override
	protected OutputStreamUsingService createServiceImplementation() {
		return new OutputStreamUsingService();
	}
}