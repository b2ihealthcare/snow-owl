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

/**
 * {@link IConsoleDevice Console device} representation as a workaround 
 * for {@code null} {@link System#console()} while running application in
 * IDE environment. 
 * <br>Methods are copied from {@link Console} class.
 * <p>For more details reference 
 * <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=122429">System.console() (Java 6.0)
 *  returns null when running inside Eclipse</a> bug.
 */
public interface IConsoleDevice {

  IConsoleDevice printf(final String fmt, final Object... params);

  String readLine();

  char[] readPassword();

  Reader reader();

  PrintWriter writer();
	
}