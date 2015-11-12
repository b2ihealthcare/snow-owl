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
		final SctId sctId = getSctId(identifier);
		return super.includes(identifier) || sctId.getStatus().equals(IdentifierStatus.AVAILABLE.getSerializedName());
	}

	@Override
	public SnomedIdentifier generate(String namespace, ComponentCategory category) {
		HttpPost request = null;
		final String token = login();

		try {
			request = httpPost(String.format("sct/generate?token=%s", token), generationData(namespace, category));
			final String response = execute(request);
			final String sctid = mapper.readValue(response, SctId.class).getSctid();

			return SnomedIdentifiers.of(sctid);
		} catch (IOException e) {
			// TODO change exception
			throw new RuntimeException("Exception while generating ID.", e);
		} finally {
			release(request);
			logout(token);
		}
	}

	@Override
	public void register(SnomedIdentifier identifier) {
		HttpPost request = null;
		final String token = login();

		try {
			request = httpPost(String.format("sct/register?token=%s", token),
					registrationData(identifier.toString(), identifier.getNamespace()));
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
	public SnomedIdentifier reserve(String namespace, ComponentCategory category) {
		HttpPost request = null;
		final String token = login();

		try {
			request = httpPost(String.format("sct/reserve?token=%s", token), reservationData(namespace, category));
			final String response = execute(request);
			final String sctid = mapper.readValue(response, SctId.class).getSctid();

			return SnomedIdentifiers.of(sctid);
		} catch (IOException e) {
			// TODO change exception
			throw new RuntimeException("Exception while reserving ID.", e);
		} finally {
			release(request);
			logout(token);
		}
	}

	@Override
	public void deprecate(SnomedIdentifier identifier) {
		HttpPut request = null;
		final String token = login();

		try {
			request = httpPut(String.format("sct/deprecate?token=%s", token),
					deprecationData(identifier.toString(), identifier.getNamespace()));
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
	public void release(SnomedIdentifier identifier) {
		HttpPut request = null;
		final String token = login();

		try {
			request = httpPut(String.format("sct/release?token=%s", token), releaseData(identifier.toString(), identifier.getNamespace()));
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
	public void publish(SnomedIdentifier identifier) {
		HttpPut request = null;
		final String token = login();

		try {
			request = httpPut(String.format("sct/publish?token=%s", token), publishData(identifier.toString(), identifier.getNamespace()));
			execute(request);
		} catch (IOException e) {
			// TODO change exception
			throw new RuntimeException("Exception while publishing ID.", e);
		} finally {
			release(request);
			logout(token);
		}
	}
	
	public SctId getSctId(final SnomedIdentifier identifier) {
		HttpGet request = null;
		final String token = login();

		try {
			request = httpGet(String.format("sct/ids/%s?token=%s", identifier.toString(), token));
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
		final GenerationData data = new GenerationData(Integer.valueOf(namespace), clientKey, category);
		return mapper.writeValueAsString(data);
	}

	private String registrationData(final String id, final String namespace) throws IOException {
		final RegistrationData data = new RegistrationData(Integer.valueOf(namespace), clientKey, id, "");
		return mapper.writeValueAsString(data);
	}

	private String deprecationData(final String id, final String namespace) throws IOException {
		final DeprecationData data = new DeprecationData(Integer.parseInt(namespace), clientKey, id);
		return mapper.writeValueAsString(data);
	}

	private String reservationData(final String namespace, final ComponentCategory category) throws IOException {
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		final Date expirationDate = calendar.getTime();

		final ReservationData data = new ReservationData(Integer.valueOf(namespace), clientKey,
				new SimpleDateFormat("yyyy-MM-dd").format(expirationDate), category);
		return mapper.writeValueAsString(data);
	}

	private String releaseData(final String id, final String namespace) throws IOException {
		final ReleaseData data = new ReleaseData(Integer.parseInt(namespace), clientKey, id);
		return mapper.writeValueAsString(data);
	}

	private String publishData(final String id, final String namespace) throws IOException {
		final PublicationData data = new PublicationData(Integer.parseInt(namespace), clientKey, id);
		return mapper.writeValueAsString(data);
	}

}
