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
package com.b2international.snowowl.datastore.server.net4j;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.Net4jProtocolConstants;
import com.b2international.snowowl.core.users.IUserManager;
import com.b2international.snowowl.core.users.User;

public class FetchUsersIndication extends IndicationWithResponse {

	private IUserManager manager;

	public FetchUsersIndication(SignalProtocol<?> protocol) {
		super(protocol, Net4jProtocolConstants.FETCH_USERS_SIGNAL);
		manager = ApplicationContext.getInstance().getServiceChecked(IUserManager.class);
	}

	@Override
	protected void indicating(ExtendedDataInputStream in) throws Exception {
	}

	@Override
	protected void responding(ExtendedDataOutputStream out) throws Exception {
		final Set<User> users = manager.getUsers();
		out.writeObject(newHashSet(users));
	}

}