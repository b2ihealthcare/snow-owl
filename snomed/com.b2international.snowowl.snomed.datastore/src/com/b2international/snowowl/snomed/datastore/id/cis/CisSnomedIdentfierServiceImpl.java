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
package com.b2international.snowowl.snomed.datastore.id.cis;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.id.AbstractSnomedIdentifierServiceImpl;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifier;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.id.cis.request.DeprecationData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.GenerationData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.PublicationData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.RegistrationData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.ReleaseData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.ReservationData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provider;

/**
 * CIS (IHTSDO) based implementation of the identifier service.
 * 
 * @since 4.5
 */
public class CisSnomedIdentfierServiceImpl extends AbstractSnomedIdentifierServiceImpl {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CisSnomedIdentfierServiceImpl.class);

	private String clientKey;
	private final String username;
	private final String password;

	private final ComponentIdentifierServiceClient client;
	private final ComponentIdentifierServiceAuthenticator authenticator;

	private final ObjectMapper mapper = new ObjectMapper();

	public CisSnomedIdentfierServiceImpl(final SnomedCoreConfiguration conf, final Provider<SnomedTerminologyBrowser> provider) {
		super(provider);
		this.clientKey = conf.getCisClientSoftwareKey();
		this.username = conf.getCisUserName();
		this.password = conf.getCisPassword();

		this.client = new ComponentIdentifierServiceClient(conf);
		this.authenticator = new ComponentIdentifierServiceAuthenticator(client);
	}

	@Override
	public boolean includes(final SnomedIdentifier identifier) {
		final SctId sctId = getSctId(identifier.toString());
		return super.includes(identifier) || sctId.getStatus().equals(IdentifierStatus.AVAILABLE.getSerializedName());
	}

	@Override
	public String generate(String namespace, ComponentCategory category) {
		HttpPost request = null;
		final String token = login();

		try {
			LOGGER.info(String.format("Sending %s ID generation request.", category.getDisplayName()));
			
			request = httpPost(String.format("sct/generate?token=%s", token), generationData(namespace, category));
			final String response = execute(request);
			final String sctid = mapper.readValue(response, SctId.class).getSctid();

			return sctid;
		} catch (IOException e) {
			// TODO change exception
			throw new RuntimeException("Exception while generating ID.", e);
		} finally {
			release(request);
			logout(token);
		}
	}

	@Override
	public void register(final String componentId) {
		HttpPost request = null;
		final String token = login();

		try {
			LOGGER.info(String.format("Sending %s ID registration request.", componentId));
			
			request = httpPost(String.format("sct/register?token=%s", token), registrationData(componentId));
			execute(request);
		} catch (IOException e) {
			// TODO change exception
			throw new RuntimeException("Exception while registering ID.", e);
		} finally {
			release(request);
			logout(token);
		}
	}

	@Override
	public String reserve(final String namespace, final ComponentCategory category) {
		HttpPost request = null;
		final String token = login();

		try {
			LOGGER.info(String.format("Sending %s ID reservation request.", category.getDisplayName()));
			
			request = httpPost(String.format("sct/reserve?token=%s", token), reservationData(namespace, category));
			final String response = execute(request);
			final String sctid = mapper.readValue(response, SctId.class).getSctid();

			return sctid;
		} catch (IOException e) {
			// TODO change exception
			throw new RuntimeException("Exception while reserving ID.", e);
		} finally {
			release(request);
			logout(token);
		}
	}

	@Override
	public void deprecate(final String componentId) {
		HttpPut request = null;
		final String token = login();

		try {
			LOGGER.info(String.format("Sending component ID %s deprecation request.", componentId));
			
			request = httpPut(String.format("sct/deprecate?token=%s", token), deprecationData(componentId));
			execute(request);
		} catch (IOException e) {
			// TODO change exception
			throw new RuntimeException("Exception while deprecating ID.", e);
		} finally {
			release(request);
			logout(token);
		}
	}

	@Override
	public void release(final String componentId) {
		HttpPut request = null;
		final String token = login();

		try {
			LOGGER.info(String.format("Sending component ID %s release request.", componentId));
			
			request = httpPut(String.format("sct/release?token=%s", token), releaseData(componentId));
			execute(request);
		} catch (IOException e) {
			// TODO change exception
			throw new RuntimeException("Exception while releasing ID.", e);
		} finally {
			release(request);
			logout(token);
		}
	}

	@Override
	public void publish(final String componentId) {
		HttpPut request = null;
		final String token = login();

		try {
			LOGGER.info(String.format("Sending component ID %s publication request.", componentId));
			
			request = httpPut(String.format("sct/publish?token=%s", token), publishData(componentId));
			execute(request);
		} catch (IOException e) {
			// TODO change exception
			throw new RuntimeException("Exception while publishing ID.", e);
		} finally {
			release(request);
			logout(token);
		}
	}

	public SctId getSctId(final String componentId) {
		HttpGet request = null;
		final String token = login();

		try {
			LOGGER.info(String.format("Sending component ID %s get request.", componentId));
			
			request = httpGet(String.format("sct/ids/%s?token=%s", componentId, token));
			final String response = execute(request);

			return mapper.readValue(response, SctId.class);
		} catch (IOException e) {
			// TODO change exception
			throw new RuntimeException("Exception while getting ID.", e);
		} finally {
			release(request);
			logout(token);
		}
	}

	private String login() {
		return authenticator.login(username, password);
	}

	private void logout(final String token) {
		if (null != token)
			authenticator.logout(token);
	}

	private HttpGet httpGet(final String suffix) {
		return client.httpGet(suffix);
	}

	private HttpPost httpPost(final String suffix, final String data) {
		return client.httpPost(suffix, data);
	}

	private HttpPut httpPut(final String suffix, final String data) {
		return client.httpPut(suffix, data);
	}

	private String execute(final HttpRequestBase request) {
		return client.execute(request);
	}

	private void release(final HttpRequestBase request) {
		if (null != request)
			client.release(request);
	}

	private String generationData(final String namespace, final ComponentCategory category) throws IOException {
		final GenerationData data = new GenerationData(convertNamesapce(namespace), clientKey, category);
		return mapper.writeValueAsString(data);
	}

	private String registrationData(final String componentId) throws IOException {
		final RegistrationData data = new RegistrationData(getNamespace(componentId), clientKey, componentId, "");
		return mapper.writeValueAsString(data);
	}

	private String deprecationData(final String componentId) throws IOException {
		final DeprecationData data = new DeprecationData(getNamespace(componentId), clientKey, componentId);
		return mapper.writeValueAsString(data);
	}

	private String reservationData(final String namespace, final ComponentCategory category) throws IOException {
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		final Date expirationDate = calendar.getTime();

		final ReservationData data = new ReservationData(convertNamesapce(namespace), clientKey,
				new SimpleDateFormat("yyyy-MM-dd").format(expirationDate), category);
		return mapper.writeValueAsString(data);
	}

	private String releaseData(final String componentId) throws IOException {
		final ReleaseData data = new ReleaseData(getNamespace(componentId), clientKey, componentId);
		return mapper.writeValueAsString(data);
	}

	private String publishData(final String componentId) throws IOException {
		final PublicationData data = new PublicationData(getNamespace(componentId), clientKey, componentId);
		return mapper.writeValueAsString(data);
	}
	
	private int convertNamesapce(final String namespace) {
		return StringUtils.isEmpty(namespace) ? 0 : Integer.valueOf(namespace);
	}

	private int getNamespace(final String componentId) {
		final String namespace = SnomedIdentifiers.of(componentId).getNamespace();
		return null == namespace ? 0 : Integer.valueOf(namespace);
	}

}
