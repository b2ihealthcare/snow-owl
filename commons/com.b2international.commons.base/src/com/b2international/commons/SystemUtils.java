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
package com.b2international.commons;

import static com.google.common.base.Suppliers.memoize;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;

/**
 * Utility class for {@link System} class.
 */
public abstract class SystemUtils {

	public static final String OS_NAME = "os.name";
	
	private static final String LINE_SEPARATOR = "line.separator";
	private static final String MAC = "Mac";
	private static final String WINDOWS = "Windows";
	private static final long SLEEP_INTERVAL = 100;
	
	private static final Supplier<String> LINE_SEPARATOR_SUPPLIER = memoize(new Supplier<String>() {
		@Override
		public String get() {
			return getProperty(LINE_SEPARATOR);
		}
	});

	/**
	 * Gets the system property indicated by the specified key. Could be {@code null}.
	 * @param key the key of the property.
	 * @return the system property.
	 */
	public static String getProperty(final String key) {
		return System.getProperty(Preconditions.checkNotNull(key, "Property key argument cannot be null."));
	}
	
	/**Returns with the platform dependent line separator as a string.*/
	public static String getLineSeparator() {
		return LINE_SEPARATOR_SUPPLIER.get();
	}
	
	/**
	 * Requests a hard garbage collection from the virtual machine.
	 * <br>This method is identical as {@code #performHardGC(2)};
	 * @see #performHardGC(int)
	 */
	public static void performHardGC() {
		performHardGC(2);
	}

	/**
	 * Requests a hard GC from the VM.
	 * @param count the number of GC request.
	 */
	public static void performHardGC(int count) {
		
		for (int i = 0; i < count; i++) {
			
			try {
				
				System.gc();
				Thread.sleep(SLEEP_INTERVAL);
				
				System.runFinalization();
				Thread.sleep(SLEEP_INTERVAL);
				
			} catch (final InterruptedException e) {
				
				Thread.currentThread().interrupt();
				
			}
			
		}
		
	}
	
	/**
	 * Returns {@code true} if the underlying OS is Mac.
	 */
	public static boolean isMac() {
		return getOsSafe().indexOf(MAC) != -1;
	}

	/**
	 * Returns {@code true} if the underlying OS is Windows.
	 */
	public static boolean isWindows() {
		return getOsSafe().indexOf(WINDOWS) != -1;
	}

	private static String getOsSafe() {
		return Strings.nullToEmpty(getProperty(OS_NAME));
	}
	
	private SystemUtils() { /*suppress instantiation*/ }
	
}