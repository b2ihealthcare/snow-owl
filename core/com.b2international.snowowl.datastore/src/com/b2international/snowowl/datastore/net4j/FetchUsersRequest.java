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
package com.b2international.snowowl.datastore.net4j;

import java.util.Set;

import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import com.b2international.snowowl.core.api.Net4jProtocolConstants;
import com.b2international.snowowl.core.users.User;

public class FetchUsersRequest extends RequestWithConfirmation<Set<User>> {

	public FetchUsersRequest(SignalProtocol<Object> protocol) {
		super(protocol, Net4jProtocolConstants.FETCH_USERS_SIGNAL);
	}

	@Override
	protected void requesting(ExtendedDataOutputStream out) throws Exception {
	}

	@Override
	protected Set<User> confirming(ExtendedDataInputStream in) throws Exception {
		Object result = in.readObject(User.class.getClassLoader());
		return (Set<User>) result;
	}

}