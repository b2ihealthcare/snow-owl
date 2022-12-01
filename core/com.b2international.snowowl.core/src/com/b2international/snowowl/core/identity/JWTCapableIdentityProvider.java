/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.identity;

import java.util.Objects;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.setup.Environment;
import com.google.common.base.Preconditions;

/**
 * @since 8.8.0
 */
public abstract class JWTCapableIdentityProvider<T extends JWTCapableIdentityProviderConfig> implements IdentityProvider {

	private final T configuration;
	private JWTSupport jwt;
	
	public JWTCapableIdentityProvider(T configuration) {
		this.configuration = Preconditions.checkNotNull(configuration);
	}
	
	protected final T getConfiguration() {
		return configuration;
	}
	
	@Override
	public final JWTSupport jwt(String issuer) {
		if (!Objects.equals(issuer, jwt.config().getIssuer())) {
			throw new BadRequestException("");
		}
		return jwt;
	}
	
	@Override
	@OverridingMethodsMustInvokeSuper
	public void init(Environment env) throws Exception {
		final JWTConfiguration conf = getConfiguration().getJWTConfiguration();
		this.jwt = createJWTSupport(conf);
	}

	protected JWTSupport createJWTSupport(final JWTConfiguration conf) throws Exception {
		return new DefaultJWTSupport(getType(), conf);
	}
	
	protected abstract String getType();

}
