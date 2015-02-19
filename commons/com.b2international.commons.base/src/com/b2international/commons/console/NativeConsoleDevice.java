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

import java.io.Console;
import java.io.PrintWriter;
import java.io.Reader;

import com.google.common.base.Preconditions;

/**
 *
 */
/*default*/ class NativeConsoleDevice implements IConsoleDevice {

	private final Console console;

	NativeConsoleDevice() {
		this.console = Preconditions.checkNotNull(System.console());
	}

	/* (non-Javadoc)
	 * @see com.b2international.commons.console.IConsoleDevice#printf(java.lang.String, java.lang.Object[])
	 */
	@Override
	public IConsoleDevice printf(final String fmt, final Object... params) {
		console.format(fmt, params);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.b2international.commons.console.IConsoleDevice#readLine()
	 */
	@Override
	public String readLine() {
		return console.readLine();
	}

	/* (non-Javadoc)
	 * @see com.b2international.commons.console.IConsoleDevice#readPassword()
	 */
	@Override
	public char[] readPassword() {
		return console.readPassword();
	}

	/* (non-Javadoc)
	 * @see com.b2international.commons.console.IConsoleDevice#reader()
	 */
	@Override
	public Reader reader() {
		return console.reader();
	}

	/* (non-Javadoc)
	 * @see com.b2international.commons.console.IConsoleDevice#writer()
	 */
	@Override
	public PrintWriter writer() {
		return console.writer();
	}
	
}