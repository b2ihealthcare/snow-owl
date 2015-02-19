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
package com.b2international.snowowl.rpc.test.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.net4j.util.io.IOUtil;

import com.b2international.snowowl.rpc.test.service.IInputStreamUsingService;

public class InputStreamUsingService implements IInputStreamUsingService {

	@Override
	public String readInputStream(InputStream input) throws IOException {

		BufferedReader reader = null; 
		
		try {
			reader = new BufferedReader(new InputStreamReader(input));
			return reader.readLine();
		} finally {
			IOUtil.closeSilent(reader);
		}
	}
	
	@Override
	public String readSecondInputStream(InputStream input1, InputStream input2) throws IOException {
		return readInputStream(input2);
	}
}
