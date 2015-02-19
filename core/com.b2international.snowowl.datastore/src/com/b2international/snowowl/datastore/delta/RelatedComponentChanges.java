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
package com.b2international.snowowl.datastore.delta;

import java.io.Serializable;
import java.util.Arrays;

import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.google.common.base.Objects;

/**
 * Class for encapsulating information about affected component changes 
 * for a component in the task context.  
 */
public class RelatedComponentChanges implements Serializable {

	private static final long serialVersionUID = -5814146619259812321L;
	
	private final LongSet ids;
	
	public RelatedComponentChanges(final long selfCdoId){

		ids = new LongOpenHashSet();
		ids.add(selfCdoId);
		
	}
	
	/**
	 * Returns with the CDO IDs of all related new, dirty and detached components. 
	 */
	public LongSet getIds() {
		return ids;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("IDs", Arrays.toString(ids.toArray())).toString();
	}

}