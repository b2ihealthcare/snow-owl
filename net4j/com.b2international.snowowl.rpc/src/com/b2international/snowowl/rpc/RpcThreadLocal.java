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
 * 
 */
public abstract class RpcThreadLocal {

	private static final ThreadLocal<RpcSession> SESSION = new ThreadLocal<RpcSession>();
	
	/**
	 * 
	 * @return
	 */
	public static RpcSession getSession() {
		return getSessionChecked();
	}
	
	/**
	 * 
	 * @return
	 */
	public static RpcSession getSessionUnchecked() {
		return SESSION.get();
	}

	/**
	 * 
	 * @param session
	 */
	public static void setSession(final RpcSession session) {
		checkNoSession();
		SESSION.set(session);
	}

	/**
	 * 
	 */
	public static void releaseSession() {
		getSessionChecked();
		SESSION.set(null);
	}
	
	private static RpcSession getSessionChecked() {
		
		final RpcSession result = getSessionUnchecked();
		if (null == result) {
			throw new IllegalStateException("No RPC session has been set for this thread.");
		}
		
		return result;
	}

	private static void checkNoSession() {
		
		final RpcSession result = getSessionUnchecked();
		if (null != result) {
			throw new IllegalStateException("An existing RPC session has already been set for this thread.");
		}
	}

	private RpcThreadLocal() {
		// Prevent instantiation
	}
}