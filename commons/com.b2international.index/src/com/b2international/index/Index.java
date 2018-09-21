/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index;

import com.b2international.index.admin.Administrable;
import com.b2international.index.admin.IndexAdmin;

/**
 * @since 4.7
 */
public interface Index extends Administrable<IndexAdmin> {

	/**
	 * Returns the name of the index.
	 * 
	 * @return
	 */
	String name();

	/**
	 * Reads from the index via a {@link IndexRead read transaction}.
	 * 
	 * @param read
	 * @return
	 */
	<T> T read(IndexRead<T> read);

	/**
	 * Writes to this index via an {@link IndexWrite write transaction}.
	 * 
	 * @param write
	 * @return
	 */
	<T> T write(IndexWrite<T> write);

}