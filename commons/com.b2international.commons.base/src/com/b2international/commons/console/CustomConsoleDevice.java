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
package com.b2international.commons.console;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;

/**
 *
 */
/*default*/public class CustomConsoleDevice implements IConsoleDevice {

  private final BufferedReader reader;
	private final PrintWriter writer;

	CustomConsoleDevice(final InputStream is, final OutputStream os) {
		this.reader = new BufferedReader(new InputStreamReader(is));
    this.writer = new PrintWriter(os, false);
  }
	
	/* (non-Javadoc)
	 * @see com.b2international.commons.console.IConsoleDevice#printf(java.lang.String, java.lang.Object[])
	 */
	@Override
	public IConsoleDevice printf(final String fmt, final Object... params) {
		writer.printf(fmt, params);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.b2international.commons.console.IConsoleDevice#readLine()
	 */
	@Override
	public String readLine() {
		try {
			
			return reader.readLine();
			
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.b2international.commons.console.IConsoleDevice#readPassword()
	 */
	@Override
	public char[] readPassword() {
		return readLine().toCharArray();
	}

	/* (non-Javadoc)
	 * @see com.b2international.commons.console.IConsoleDevice#reader()
	 */
	@Override
	public Reader reader() {
		return reader;
	}

	/* (non-Javadoc)
	 * @see com.b2international.commons.console.IConsoleDevice#writer()
	 */
	@Override
	public PrintWriter writer() {
		return writer;
	}
	
}