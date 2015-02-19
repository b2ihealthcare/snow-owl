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

import java.io.PrintStream;

/**
 * <p>Utility methods for measuring time.</p>
 * 
 *  <pre>Usage:
 *  long time = StopWatch.time();
 *  ...
 *  time = StopWatch.time("1 complete", time);
 *  ...
 *  StopWatch.time("2 complete", time);
 *  </pre>
 * 
 * 
 *
 */
public class StopWatch {

	public static long time() {
		return System.currentTimeMillis();
	}
	
	public static long time(String message, long startMillis) {
		long time = System.currentTimeMillis();
		System.out.format("%s: %dms\n", message, time - startMillis);
		return time; 
	}
	
	public static long timeNano(final String message, long startNano) {
		long time = System.nanoTime();
		System.out.format("%s: %dns\n", message, time - startNano);
		return time;
	}
	
	public static long timeErr(String message, long startMillis) {
		long time = System.currentTimeMillis();
		System.err.format("%s: %dms\n", message, time - startMillis);
		return time; 
	}
	
	public static long timeNanoErr(final String message, long startNano) {
		long time = System.nanoTime();
		System.err.format("%s: %dns\n", message, time - startNano);
		return time;
	}
	
	
	public static void runtimeStats(PrintStream out, long waifForMillis) {

		if(waifForMillis > 0) {
			try {
				Thread.sleep(waifForMillis);
			} catch (InterruptedException e) {
			}
		}
		
		int mb = 1024 * 1024;

		Runtime runtime = Runtime.getRuntime();

		out.println("##### Heap utilization statistics [MB] #####");
		out.println("Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / mb);
		out.println("Free Memory: " + runtime.freeMemory() / mb);
		out.println("Total Memory: " + runtime.totalMemory() / mb);
		out.println("Max Memory: " + runtime.maxMemory() / mb);
	}	
	
	public static String getRuntimeMemoryStatsAsString() {
		int mb = 1024*1024;

		Runtime runtime = Runtime.getRuntime();
		
		String stat = "##### Heap utilization statistics [MB] #####\n" + 
					  "Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / mb + "\n"+
					  "Free Memory: " + runtime.freeMemory() / mb + "\n" +
					  "Total Memory: " + runtime.totalMemory() / mb +"\n" +
					  "Max Memory: " + runtime.maxMemory() / mb + "\n";
		
		return stat;
	}
}