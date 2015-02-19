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
package com.b2international.snowowl.rpc.test.service;

import java.util.Arrays;

public class PrimitiveArgs {

	private boolean b1;
	private byte b2;
	private byte[] ba;
	private int i;
	private long l;
	private float f;
	private double d;
	private String str;

	public PrimitiveArgs(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str) {
		this.b1 = b1;
		this.b2 = b2;
		this.ba = ba;
		this.i = i;
		this.l = l;
		this.f = f;
		this.d = d;
		this.str = str;
	}

	public byte[] getBa() {
		return ba;
	}

	public String getStr() {
		return str;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (b1 ? 1231 : 1237);
		result = prime * result + b2;
		result = prime * result + Arrays.hashCode(ba);
		long temp;
		temp = Double.doubleToLongBits(d);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Float.floatToIntBits(f);
		result = prime * result + i;
		result = prime * result + (int) (l ^ (l >>> 32));
		result = prime * result + ((str == null) ? 0 : str.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PrimitiveArgs)) {
			return false;
		}
		PrimitiveArgs other = (PrimitiveArgs) obj;
		if (b1 != other.b1) {
			return false;
		}
		if (b2 != other.b2) {
			return false;
		}
		if (!Arrays.equals(ba, other.ba)) {
			return false;
		}
		if (Double.doubleToLongBits(d) != Double.doubleToLongBits(other.d)) {
			return false;
		}
		if (Float.floatToIntBits(f) != Float.floatToIntBits(other.f)) {
			return false;
		}
		if (i != other.i) {
			return false;
		}
		if (l != other.l) {
			return false;
		}
		if (str == null) {
			if (other.str != null) {
				return false;
			}
		} else if (!str.equals(other.str)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PrimitiveArgs [b1=");
		builder.append(b1);
		builder.append(", b2=");
		builder.append(b2);
		builder.append(", ba=");
		builder.append(Arrays.toString(ba));
		builder.append(", i=");
		builder.append(i);
		builder.append(", l=");
		builder.append(l);
		builder.append(", f=");
		builder.append(f);
		builder.append(", d=");
		builder.append(d);
		builder.append(", str=");
		builder.append(str);
		builder.append("]");
		return builder.toString();
	}
}