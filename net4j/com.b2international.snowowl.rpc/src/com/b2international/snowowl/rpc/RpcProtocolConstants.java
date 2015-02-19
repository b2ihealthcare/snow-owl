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
 * Enumerates signal IDs for the Remote Procedure Call protocol. 
 *
 */
public interface RpcProtocolConstants {
	
	public static final String TYPE = "RpcProtocol";
	
	/*
	 * These are initiated by a client proxy, and not associated with a running method call.
	 */
	public static final short SIGNAL_RPC_PRIMARY_METHOD_CALL = 1;
	
	/* 
	 * These are initiated by the receiver, while processing a primary method call from a sender;
	 * as such, they refer to parameter indexes and also know about the currently running call's 
	 * correlation identifier.
	 */
	public static final short SIGNAL_RPC_SECONDARY_METHOD_CALL = 2;
}