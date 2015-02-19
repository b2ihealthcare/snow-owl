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
package com.b2international.commons.arrays;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class ArrayIO {

	public static final Charset CHARSET = Charset.forName("UTF-8");
	
	private static void writeIntArray(DataOutputStream os, int[] a) throws IOException {
		os.writeInt(a.length);
		for(int j = 0; j < a.length; j++) {
			os.writeInt(a[j]);
		}
	}
	
	private static int[] readIntArray(DataInputStream is) throws IOException {
		int length = is.readInt();
		int[] a = new int[length];
		for(int i = 0; i < length; i++) {
			a[i] = is.readInt();
		}
		return a;
	}
	
	public static void writeIntMatrix(DataOutputStream os, int[][] matrix) throws IOException{
		
		os.writeInt(matrix.length);
		for(int i = 0; i < matrix.length; i++) {
			writeIntArray(os, matrix[i]);
		}
	}

	public static int[][] readIntMatrix(DataInputStream is) throws IOException{
		
		int n = is.readInt();
		int[][] matrix = new int[n][];
		for(int i = 0; i < n; i++) {
			matrix[i] = readIntArray(is);
		}
		
		return matrix;
	}
}