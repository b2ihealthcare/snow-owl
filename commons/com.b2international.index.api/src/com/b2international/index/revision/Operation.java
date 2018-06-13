/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @since 7.0
 */
public enum Operation {
		
		ADD("+"),
	    REMOVE("-"),
	    CHANGE("~");
	
		private final static Map<String, Operation> OPS = initOps();

	    private static Map<String, Operation> initOps() {
	        Map<String, Operation> map = new HashMap<String, Operation>();
	        map.put(ADD.opType, ADD);
	        map.put(REMOVE.opType, REMOVE);
	        map.put(CHANGE.opType, CHANGE);
	        return Collections.unmodifiableMap(map);
	    }

	    private String opType;

	    Operation(String opType) {
	        this.opType = opType;
	    }

	    @JsonCreator
	    static Operation fromOpType(String opType) throws IllegalArgumentException {
	        if (opType == null) throw new IllegalArgumentException("opType cannot be null");
	        Operation op = OPS.get(opType.toLowerCase());
	        if (op == null) throw new IllegalArgumentException("unknown / unsupported operation " + opType);
	        return op;
	    }

	    @JsonValue
	    String opType() {
	        return this.opType;
	    }

		static Operation fromRfcName(String rfcName) {
			if (rfcName == null) throw new IllegalArgumentException("opType cannot be null");
			switch (rfcName) {
			case "add": return Operation.ADD;
			case "remove": return Operation.REMOVE;
			case "replace": return Operation.CHANGE;
			default: throw new IllegalArgumentException("unknown / unsupported operation " + rfcName);
			}
		}
		
	}