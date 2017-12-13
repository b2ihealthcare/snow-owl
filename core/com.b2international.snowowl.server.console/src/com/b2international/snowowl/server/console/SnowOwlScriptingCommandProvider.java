/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.server.console;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Collectors;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.b2international.scripting.api.ScriptEngine;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.server.RepositoryClassLoaderProviderRegistry;

/**
 * OSGi command contribution with groovy script execution command(s).
 * Currently the following command(s) are supported:
 * <ul>
 * <li>script run [filename], where file is given with absolute path</li>
 * </ul>
 */
public class SnowOwlScriptingCommandProvider implements CommandProvider {

	@Override
	public String getHelp() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("---Snow Owl Scripting Commands---\n");
		buffer.append("\tscript run [filename] - execute a Groovy script file\n");
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
			final String script = Files.lines(Paths.get(groovyScriptFile)).collect(Collectors.joining(System.getProperty("line.separator")));
			final ClassLoader classLoader = ApplicationContext.getServiceForClass(RepositoryClassLoaderProviderRegistry.class).getClassLoader();
			ScriptEngine.run("groovy", classLoader, script, Collections.emptyMap());
		} catch (Exception e) {
			interpreter.printStackTrace(e);
		}
	}

	
}