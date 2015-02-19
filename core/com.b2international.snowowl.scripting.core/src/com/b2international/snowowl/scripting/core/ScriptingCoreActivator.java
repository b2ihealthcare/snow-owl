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
package com.b2international.snowowl.scripting.core;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Plugin;

public class ScriptingCoreActivator extends Plugin {

	private static ScriptingCoreActivator fgPlugin;
	private static final String RESOURCE_BUNDLE= "org.eclipse.jdt.internal.launching.macosx.MacOSXLauncherMessages";//$NON-NLS-1$
	private static ResourceBundle fgResourceBundle= ResourceBundle.getBundle(RESOURCE_BUNDLE);

	public ScriptingCoreActivator() {
		super();
		Assert.isTrue(fgPlugin == null);
		fgPlugin= this;
	}
	
	public static ScriptingCoreActivator getDefault() {
		return fgPlugin;
	}
	
	public static String getString(String key) {
		try {
			return fgResourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return "!" + key + "!";//$NON-NLS-2$ //$NON-NLS-1$
		}
	}

	/*
	 * Convenience method which returns the unique identifier of this plugin.
	 */
	public static String getUniqueIdentifier() {
		if (getDefault() == null) {
			// If the default instance is not yet initialized,
			// return a static identifier. This identifier must
			// match the plugin id defined in plugin.xml
			return "org.eclipse.jdt.launching.macosx"; //$NON-NLS-1$
		}
		return getDefault().getBundle().getSymbolicName();
	}

	public static String[] wrap(Class<?> clazz, String[] cmdLine) {
		
		for (int i= 0; i < cmdLine.length; i++) {
			// test whether we depend on SWT
			if (useSWT(cmdLine[i]))
				return createSWTlauncher(clazz, cmdLine, cmdLine[0]);
		}
		return cmdLine;
	}
	
	/*
	 * Returns path to executable.
	 */
	public static String[] createSWTlauncher(Class<?> clazz, String[] cmdLine, String vmVersion) {
		
		// the following property is defined if Eclipse is started via java_swt
		String java_swt= System.getProperty("org.eclipse.swtlauncher");	//$NON-NLS-1$
		
		if (java_swt == null) {	
			// not started via java_swt -> now we require that the VM supports the "-XstartOnFirstThread" option
			String[] newCmdLine= new String[cmdLine.length+1];
			int argCount= 0;
			newCmdLine[argCount++]= cmdLine[0];
			newCmdLine[argCount++]= "-XstartOnFirstThread"; //$NON-NLS-1$
			for (int i= 1; i < cmdLine.length; i++)
				newCmdLine[argCount++]= cmdLine[i];
			return newCmdLine;
		}
		
		try {
			// copy java_swt to /tmp in order to get the app name right
			Process process= Runtime.getRuntime().exec(new String[] { "/bin/cp", java_swt, "/tmp" }); //$NON-NLS-1$ //$NON-NLS-2$
			process.waitFor();
			java_swt= "/tmp/java_swt"; //$NON-NLS-1$
		} catch (IOException e) {
			// ignore and run java_swt in place
		} catch (InterruptedException e) {
			// ignore and run java_swt in place
		}
		
		String[] newCmdLine= new String[cmdLine.length+1];
		int argCount= 0;
		newCmdLine[argCount++]= java_swt;
		newCmdLine[argCount++]= "-XXvm=" + vmVersion; //$NON-NLS-1$
		for (int i= 1; i < cmdLine.length; i++)
			newCmdLine[argCount++]= cmdLine[i];
		
		return newCmdLine;
	}

	/*
	 * Heuristics: returns true if given argument refers to SWT. 
	 */
	private static boolean useSWT(String arg) {
		return arg.indexOf("swt.jar") >= 0 ||	//$NON-NLS-1$
			   arg.indexOf("org.eclipse.swt") >= 0 ||	//$NON-NLS-1$
			   "-ws".equals(arg);	//$NON-NLS-1$
	}

}