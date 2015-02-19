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
package com.b2international.commons.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

public class IO {

	public static final Charset CHARSET = Charset.forName("UTF-8");
	
	public static void writeStrings(DataOutputStream os, String[] s) throws IOException {
		os.writeInt(s.length);
		for(String string: s) {
			writeString(os, string);
		}
	}

	public static void readStrings(DataInputStream is) throws IOException {
		
		int length = is.readInt();
		byte[] buf = new byte[128];
		String[] s = new String[length];

		for(int i = 0; i < length; i++) {
			s[i] = readString(is, buf);
		}
	}

	public static void writeArray(DataOutputStream os, int[] a) throws IOException {
		os.writeInt(a.length);
		for(int j = 0; j < a.length; j++) {
			os.writeInt(a[j]);
		}
	}
	
	public static int[] readArray(DataInputStream is) throws IOException {
		int length = is.readInt();
		int[] a = new int[length];
		for(int i = 0; i < length; i++) {
			a[i] = is.readInt();
		}
		return a;
	}
	
	public static void writeString(DataOutputStream os, String string) throws IOException {
		byte[] bytes = string.getBytes(CHARSET);
		os.writeInt(bytes.length);
		os.write(bytes);
	}

	public static String readString(DataInputStream is, byte[] buffer) throws IOException {
		int length = is.readInt();
		is.read(buffer, 0, length);
		return new String(buffer, 0, length, CHARSET);
	}	
	
	public static void writeMatrix(DataOutputStream os, int[][] matrix) throws IOException{
		
		os.writeInt(matrix.length);
		for(int i = 0; i < matrix.length; i++) {
			writeArray(os, matrix[i]);
		}
	}

	public static int[][] readMatrix(DataInputStream is) throws IOException{
		
		int n = is.readInt();
		int[][] matrix = new int[n][];
		for(int i = 0; i < n; i++) {
			matrix[i] = readArray(is);
		}
		
		return matrix;
	}
	
	public static long time() {
		return System.currentTimeMillis();
	}
	
	public static long time(long start, String message) {
		long time = System.currentTimeMillis();
		System.out.format("%s: %dms\n", message, time - start);
		return time;
	}

	public static void dumpRuntimeStats(long waifForMillis) {

		if(waifForMillis > 0) {
			try {
				Thread.sleep(waifForMillis);
			} catch (InterruptedException e) {
			}
		}
		
		int mb = 1024 * 1024;

		Runtime runtime = Runtime.getRuntime();

		System.out.println("##### Heap utilization statistics [MB] #####");
		System.out.println("Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / mb);
		System.out.println("Free Memory: " + runtime.freeMemory() / mb);
		System.out.println("Total Memory: " + runtime.totalMemory() / mb);
		System.out.println("Max Memory: " + runtime.maxMemory() / mb);

	}
	
	/**
	 * Returns the number of lines in a given file.
	 * @param filePath
	 * @return number of lines
	 * @throws Exception
	 */
	public static int getNumberOfLinesInFile(String filePath) throws Exception {
		LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filePath));
		lineNumberReader.skip(Long.MAX_VALUE);
		int lines = lineNumberReader.getLineNumber();
		lineNumberReader.close();
		lineNumberReader = null;
		return lines;
	}

	public static void dumpArrayToFile(String fileName, int[] array) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			Writer writer = new OutputStreamWriter(fos);
			for (int i : array) {
				writer.write(Integer.toString(i));
				writer.write("\n");
			}
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException("Error during array dump: ", e);
		}
	}	
}