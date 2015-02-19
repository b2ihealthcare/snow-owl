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
package com.b2international.snowowl.scripting.core.console;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.scripting.core.GroovyScriptHelper;
import com.b2international.snowowl.scripting.core.ScriptingCoreActivator;

/**
 * OSGi command contribution with groovy script execution command(s).
 * Currently the following command(s) are supported:
 * <ul>
 * <li>script run [filename], where file is given with absolute path</li>
 * </ul>
 * 
 *
 */
public class SnowOwlScriptingCommandProvider implements CommandProvider {

	@Override
	public String getHelp() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("---Snow Owl Scripting Commands---\n");
		buffer.append("\tscript run [filename] - execute Groovy script file\n");
		return buffer.toString();
	}
	
	public void _script(CommandInterpreter interpreter) {
		try {
			String cmd = interpreter.nextArgument();
			if ("run".equals(cmd)) {
				run(interpreter);
				return;
			}
			
			interpreter.println(getHelp());
		} catch (Exception ex) {
			interpreter.println(ex.getMessage());
		}
	}

	private void run(CommandInterpreter interpreter) {
		String groovyScriptFile = interpreter.nextArgument();
		
		if (groovyScriptFile == null || groovyScriptFile.length() == 0) {
			interpreter.println(getHelp());
			return;
		}
		
		try {
			GroovyScriptHelper.reflectiveGroovyScriptExecutor(groovyScriptFile);
		} catch (Exception e) {
			ApplicationContext.handleException(ScriptingCoreActivator.getDefault(), e, e.getMessage());
		}
	}

	
}