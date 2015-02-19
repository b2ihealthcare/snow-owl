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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

import com.b2international.snowowl.rpc.test.service.IInputStreamUsingService;
import com.b2international.snowowl.rpc.test.service.impl.InputStreamUsingService;

/**
 * Contains test cases for remote method calls that want to report their progress, indicated by an
 * {@link IProgressMonitor} parameter in the signature.
 * 
 */
public class InputStreamUsingTest extends AbstractRpcTest<IInputStreamUsingService, InputStreamUsingService> {

	private static final String MESSAGE1 = "MESSAGE1";
	
	private static final String MESSAGE2 = "MESSAGE2";
	
	private static final String MESSAGE1_NL = MESSAGE1 + "\n";
	
	private static final String MESSAGE2_NL = MESSAGE2 + "\n";

	public InputStreamUsingTest() {
		super(IInputStreamUsingService.class);
	}

	@Test
	public void testInputStream() throws IOException {
		
		final IInputStreamUsingService serviceProxy = initializeService();
		final InputStream input = new ByteArrayInputStream(MESSAGE1_NL.getBytes());
		
		assertEquals(MESSAGE1, serviceProxy.readInputStream(input));
	}
	
	@Test
	public void testTwoInputStreams() throws IOException {
		
		final IInputStreamUsingService serviceProxy = initializeService();
		final InputStream input1 = new ByteArrayInputStream(MESSAGE1_NL.getBytes());
		final InputStream input2 = new ByteArrayInputStream(MESSAGE2_NL.getBytes());
		
		assertEquals(MESSAGE2, serviceProxy.readSecondInputStream(input1, input2));
	}

	@Override
	protected InputStreamUsingService createServiceImplementation() {
		return new InputStreamUsingService();
	}
}