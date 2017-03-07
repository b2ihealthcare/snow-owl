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
package com.b2international.snowowl.datastore.config;

/**
 * @since 5.7
 */
public abstract class ConnectionPoolConfiguration {

	private int readerPoolCapacity = 7;
	private int writerPoolCapacity = 3;
	
	public void setReaderPoolCapacity(int readerPoolCapacity) {
		this.readerPoolCapacity = readerPoolCapacity;
	}
	
	public void setWriterPoolCapacity(int writerPoolCapacity) {
		this.writerPoolCapacity = writerPoolCapacity;
	}
	
	public int getReaderPoolCapacity() {
		return readerPoolCapacity;
	}
	
	public int getWriterPoolCapacity() {
		return writerPoolCapacity;
	}
	
}
