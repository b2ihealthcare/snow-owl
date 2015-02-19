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
package com.b2international.snowowl.rpc;



/**
 * Represents the configuration settings of the RPC protocol.
 * 
 */
public class RpcConfiguration {

	private boolean compressed = false;
	private boolean logging = false;
	
	/**
	 * @return <code>true</code> if logging is enabled, <code>false</code> otherwise
	 */
	public boolean isLogging() {
		return logging;
	}
	
	/**
	 * @param logging
	 */
	public void setLogging(boolean logging) {
		this.logging = logging;
	}
	
	/**
	 * @return <code>true</code> if compression is enabled, <code>false</code> otherwise
	 */
	public boolean isCompressed() {
		return compressed;
	}
	
	/**
	 * @param compressed
	 */
	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}
	
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("logging");
		builder.append("=");
		builder.append(isLogging());
		builder.append(",");
		builder.append("compressed");
		builder.append("=");
		builder.append(isCompressed());
		builder.append("}");
		return builder.toString();
	}
	
}