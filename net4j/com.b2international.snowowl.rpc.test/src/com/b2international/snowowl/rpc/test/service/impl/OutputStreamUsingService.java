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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.eclipse.net4j.util.io.IOUtil;

import com.b2international.snowowl.rpc.test.service.IOutputStreamUsingService;

public class OutputStreamUsingService implements IOutputStreamUsingService {

	@Override
	public void writeToOutputStream(OutputStream output, String message) throws IOException {

		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(output);
			writer.print(message);
			writer.print("\n");
		} finally {
			IOUtil.close(writer);
		}
	}
}
