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
package org.easyb.junit;

public class RunProperties {

	private static Boolean isEclipse;
	private static Boolean isIDEA;

	public static boolean isIDEA() {
		if (isIDEA == null) {
			isIDEA = checkForIDE("com.intellij.rt.execution.junit");
		}
		return isIDEA;
	}

	public static boolean isEclipse() {
		if (isEclipse == null) {
			isEclipse = checkForIDE("org.eclipse.jdt.internal.junit.runner");
		}
		return isEclipse;
	}

	private static boolean checkForIDE(final String exceptionBegin) {
		boolean isWantedIDE = false;
		for (final StackTraceElement element : new Exception().getStackTrace()) {
			if (element.getClassName().startsWith(exceptionBegin))
				isWantedIDE = true;
		}

		return isWantedIDE;
	}

}