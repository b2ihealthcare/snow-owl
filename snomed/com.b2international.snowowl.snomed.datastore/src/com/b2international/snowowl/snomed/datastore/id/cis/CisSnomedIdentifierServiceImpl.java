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
import java.util.Collection;
import java.util.Date;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration;
import com.b2international.snowowl.snomed.datastore.id.AbstractSnomedIdentifierServiceImpl;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.id.cis.request.BulkDeprecationData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.BulkGenerationData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.BulkPublicationData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.BulkRegistrationData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.BulkReleaseData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.BulkReservationData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.DeprecationData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.GenerationData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.PublicationData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.Record;
import com.b2international.snowowl.snomed.datastore.id.cis.request.RegistrationData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.ReleaseData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.ReservationData;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.inject.Provider;

/**
 * CIS (IHTSDO) based implementation of the identifier service.
 * 
 * @since 4.5
 */
public class CisSnomedIdentifierServiceImpl extends AbstractSnomedIdentifierServiceImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(CisSnomedIdentifierServiceImpl.class);
	private static final int BULK_LIMIT = 1000;

	private static final int MAX_NUMBER_OF_POLL_TRY = 5;

	private String clientKey;
	private final String username;
	private final String password;

	private final ComponentIdentifierServiceClient client;
	private final ComponentIdentifierServiceAuthenticator authenticator;

	private final ObjectMapper mapper = new ObjectMapper();

	public CisSnomedIdentifierServiceImpl(final SnomedIdentifierConfiguration conf, final Provider<SnomedTerminologyBrowser> provider,
			final ISnomedIdentiferReservationService reservationService) {
		super(provider, reservationService);
		this.clientKey = conf.getCisClientSoftwareKey();
		this.username = conf.getCisUserName();
		this.password = conf.getCisPassword();

		this.client = new ComponentIdentifierServiceClient(conf);
		this.authenticator = new ComponentIdentifierServiceAuthenticator(client);
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
			throw new SnowowlRuntimeException("Exception while generating ID.", e);
		} finally {
			release(request);
			logout(token);
		}
	}

	@Override
	public void register(final String componentId) {
		final SctId sctId = getSctId(componentId);
		if (!hasStatus(sctId, IdentifierStatus.AVAILABLE, IdentifierStatus.RESERVED)) {
			LOGGER.warn(String.format("Cannot register ID %s as it is already present with status %s.", componentId, sctId.getStatus()));
			return;
		}

		HttpPost request = null;
		final String token = login();

		try {
			LOGGER.info(String.format("Sending %s ID registration request.", componentId));

			request = httpPost(String.format("sct/register?token=%s", token), registrationData(componentId));
			execute(request);
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Exception while registering ID.", e);
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
			throw new SnowowlRuntimeException("Exception while reserving ID.", e);
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
			throw new SnowowlRuntimeException("Exception while deprecating ID.", e);
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
			final SctId sctId = getSctId(componentId);
			if (!hasStatus(sctId, IdentifierStatus.AVAILABLE)) {
				LOGGER.info(String.format("Sending component ID %s release request.", componentId));
				
				request = httpPut(String.format("sct/release?token=%s", token), releaseData(componentId));
				execute(request);
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Exception while releasing ID.", e);
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
			throw new SnowowlRuntimeException("Exception while publishing ID.", e);
		} finally {
			release(request);
			logout(token);
		}
	}

	@Override
	public SctId getSctId(final String componentId) {
		HttpGet request = null;
		final String token = login();

		try {
			LOGGER.info(String.format("Sending component ID %s get request.", componentId));

			request = httpGet(String.format("sct/ids/%s?token=%s", componentId, token));
			final String response = execute(request);

			return mapper.readValue(response, SctId.class);
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Exception while getting ID.", e);
		} finally {
			release(request);
			logout(token);
		}
	}

	@Override
	public boolean contains(final String componentId) {
		return !hasStatus(getSctId(componentId), IdentifierStatus.AVAILABLE);
	}

	@Override
	public Collection<String> bulkGenerate(final String namespace, final ComponentCategory category, final int quantity) {
		HttpPost bulkRequest = null;
		HttpGet recordsRequest = null;

		final String token = login();

		try {
			LOGGER.info(String.format("Sending %s ID bulk generation request.", category.getDisplayName()));

			bulkRequest = httpPost(String.format("sct/bulk/generate?token=%s", token), bulkGenerationData(namespace, category, quantity));
			final String bulkResponse = execute(bulkRequest);
			final String jobId = mapper.readValue(bulkResponse, JsonNode.class).get("id").asText();

			final int status = pollJob(jobId, token);

			if (0 == status) {
				throw new SnowowlRuntimeException("Couldn't get records from bulk request.");
			} else {
				recordsRequest = httpGet(String.format("bulk/jobs/%s/records?token=%s", jobId, token));
				final String recordsResponse = execute(recordsRequest);
				final JsonNode[] records = mapper.readValue(recordsResponse, JsonNode[].class);

				return getComponentIds(records);
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Exception while bulk generating IDs.", e);
		} finally {
			release(bulkRequest);
			release(recordsRequest);
			logout(token);
		}
	}

	@Override
	public void bulkRegister(final Collection<String> componentIds) {
		final Collection<String> componentIdsToRegister = Lists.newArrayList();
		final Collection<SctId> sctIds = getSctIds(componentIds);

		for (final SctId sctId : sctIds) {
			// we want to register only the available or reserved IDs
			if (hasStatus(sctId, IdentifierStatus.AVAILABLE, IdentifierStatus.RESERVED))
				componentIdsToRegister.add(sctId.getSctid());
		}

		if (componentIdsToRegister.isEmpty())
			return;

		HttpPost bulkRequest = null;
		HttpGet recordsRequest = null;

		final String token = login();

		try {
			for (final Collection<String> ids : Lists.partition(Lists.newArrayList(componentIds), BULK_LIMIT)) {
				LOGGER.info(String.format("Sending bulk registration request with size %d.", ids.size()));

				bulkRequest = httpPost(String.format("sct/bulk/register?token=%s", token), bulkRegistrationData(ids));
				execute(bulkRequest);
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Exception while bulk reserving IDs.", e);
		} finally {
			release(bulkRequest);
			release(recordsRequest);
			logout(token);
		}
	}

	@Override
	public Collection<String> bulkReserve(String namespace, ComponentCategory category, int quantity) {
		HttpPost bulkRequest = null;
		HttpGet recordsRequest = null;

		final String token = login();

		try {
			LOGGER.info(String.format("Sending %s ID bulk reservation request.", category.getDisplayName()));

			bulkRequest = httpPost(String.format("sct/bulk/reserve?token=%s", token), bulkReservationData(namespace, category, quantity));
			final String bulkResponse = execute(bulkRequest);
			final String jobId = mapper.readValue(bulkResponse, JsonNode.class).get("id").asText();

			final int status = pollJob(jobId, token);

			if (0 == status) {
				throw new SnowowlRuntimeException("Couldn't get records from bulk request.");
			} else {
				recordsRequest = httpGet(String.format("bulk/jobs/%s/records?token=%s", jobId, token));
				final String recordsResponse = execute(recordsRequest);
				final JsonNode[] records = mapper.readValue(recordsResponse, JsonNode[].class);

				return getComponentIds(records);
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Exception while bulk reserving IDs.", e);
		} finally {
			release(bulkRequest);
			release(recordsRequest);
			logout(token);
		}
	}

	@Override
	public void bulkDeprecate(final Collection<String> componentIds) {
		HttpPut request = null;
		final String token = login();

		try {
			for (final Collection<String> ids : Lists.partition(Lists.newArrayList(componentIds), BULK_LIMIT)) {
				LOGGER.info(String.format("Sending component ID bulk deprecation request with size %d.", ids.size()));

				request = httpPut(String.format("sct/bulk/deprecate?token=%s", token), bulkDeprecationData(ids));
				execute(request);
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Exception while bulk deprecating IDs.", e);
		} finally {
			release(request);
			logout(token);
		}
	}

	@Override
	public void bulkRelease(final Collection<String> componentIds) {
		final Collection<String> componentIdsToRelease = Lists.newArrayList();
		final Collection<SctId> sctIds = getSctIds(componentIds);

		for (final SctId sctId : sctIds) {
			if (!hasStatus(sctId, IdentifierStatus.AVAILABLE))
				componentIdsToRelease.add(sctId.getSctid());
		}

		if (componentIdsToRelease.isEmpty())
			return;
		
		HttpPut request = null;
		final String token = login();

		try {
			for (final Collection<String> ids : Lists.partition(Lists.newArrayList(componentIdsToRelease), BULK_LIMIT)) {
				LOGGER.info(String.format("Sending component ID bulk release request with size %d.", ids.size()));

				request = httpPut(String.format("sct/bulk/release?token=%s", token), bulkReleaseData(ids));
				execute(request);
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Exception while bulk releasing IDs.", e);
		} finally {
			release(request);
			logout(token);
		}
	}

	@Override
	public void bulkPublish(final Collection<String> componentIds) {
		HttpPut request = null;
		final String token = login();

		try {
			for (final Collection<String> ids : Lists.partition(Lists.newArrayList(componentIds), BULK_LIMIT)) {
				LOGGER.info(String.format("Sending component ID bulk publication request with size %d.", ids.size()));

				request = httpPut(String.format("sct/bulk/publish?token=%s", token), bulkPublishData(ids));
				execute(request);
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Exception while bulk publishing IDs.", e);
		} finally {
			release(request);
			logout(token);
		}
	}

	@Override
	public Collection<SctId> getSctIds(final Collection<String> componentIds) {
		HttpGet request = null;
		final String token = login();

		try {
			LOGGER.info("Sending bulk component ID get request.");

			final StringBuilder builder = new StringBuilder();
			for (final String componentId : componentIds) {
				if (0 != builder.length())
					builder.append(",");
				builder.append(componentId);
			}

			request = httpGet(String.format("sct/bulk/ids/?token=%s&sctids=%s", token, builder.toString()));
			final String response = execute(request);

			return Lists.newArrayList(mapper.readValue(response, SctId[].class));
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Exception while getting IDs.", e);
		} finally {
			release(request);
			logout(token);
		}
	}
	
	@Override
	public Collection<SctId> getSctIds() {
		HttpGet request = null;
		final String token = login();

		try {
			LOGGER.info("Sending component IDs get request.");

			request = httpGet(String.format("sct/ids/?token=%s", token));
			final String response = execute(request);

			return Lists.newArrayList(mapper.readValue(response, SctId[].class));
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Exception while getting IDs.", e);
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

	private int pollJob(final String jobId, final String token) {
		HttpGet request = null;

		try {
			LOGGER.info(String.format("Polling job status with ID %s.", jobId));

			request = httpGet(String.format("bulk/jobs/%s?token=%s", jobId, token));

			int status = 0;
			int pollTry = 0;

			while (pollTry < MAX_NUMBER_OF_POLL_TRY) {
				final String response = execute(request);
				final JsonNode node = mapper.readValue(response, JsonNode.class);
				status = node.get("status").asInt();

				if (0 != status) {
					break;
				} else {
					pollTry++;
					Thread.sleep(1000);
				}
			}

			return status;
		} catch (Exception e) {
			throw new SnowowlRuntimeException("Exception while polling job status.", e);
		} finally {
			release(request);
		}
	}

	private Collection<String> getComponentIds(final JsonNode[] records) {
		return Collections2.transform(Lists.newArrayList(records), new Function<JsonNode, String>() {
			@Override
			public String apply(JsonNode input) {
				return input.get("sctid").asText();
			}
		});
	}

	private String generationData(final String namespace, final ComponentCategory category) throws IOException {
		final GenerationData data = new GenerationData(convertNamesapce(namespace), clientKey, category);
		return mapper.writeValueAsString(data);
	}

	private String bulkGenerationData(final String namespace, final ComponentCategory category, final int quantity) throws IOException {
		final BulkGenerationData data = new BulkGenerationData(convertNamesapce(namespace), clientKey, category, quantity);
		return mapper.writeValueAsString(data);
	}

	private String registrationData(final String componentId) throws IOException {
		final RegistrationData data = new RegistrationData(getNamespace(componentId), clientKey, componentId, "");
		return mapper.writeValueAsString(data);
	}

	private String bulkRegistrationData(final Collection<String> componentIds) throws IOException {
		final Collection<Record> records = Lists.newArrayList();
		for (final String componentId : componentIds) {
			records.add(new Record(componentId));
		}
		final BulkRegistrationData data = new BulkRegistrationData(getNamespace(componentIds.iterator().next()), clientKey, records);
		return mapper.writeValueAsString(data);
	}

	private String deprecationData(final String componentId) throws IOException {
		final DeprecationData data = new DeprecationData(getNamespace(componentId), clientKey, componentId);
		return mapper.writeValueAsString(data);
	}

	private String bulkDeprecationData(final Collection<String> componentIds) throws IOException {
		final BulkDeprecationData data = new BulkDeprecationData(getNamespace(componentIds.iterator().next()), clientKey, componentIds);
		return mapper.writeValueAsString(data);
	}

	private String reservationData(final String namespace, final ComponentCategory category) throws IOException {
		final ReservationData data = new ReservationData(convertNamesapce(namespace), clientKey, getExpirationDate(), category);
		return mapper.writeValueAsString(data);
	}

	private String bulkReservationData(final String namespace, final ComponentCategory category, final int quantity) throws IOException {
		final BulkReservationData data = new BulkReservationData(convertNamesapce(namespace), clientKey, getExpirationDate(), category,
				quantity);
		return mapper.writeValueAsString(data);
	}

	private String getExpirationDate() {
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		final Date expirationDate = calendar.getTime();

		return new SimpleDateFormat("yyyy-MM-dd").format(expirationDate);
	}

	private String releaseData(final String componentId) throws IOException {
		final ReleaseData data = new ReleaseData(getNamespace(componentId), clientKey, componentId);
		return mapper.writeValueAsString(data);
	}

	private String bulkReleaseData(final Collection<String> componentIds) throws IOException {
		final BulkReleaseData data = new BulkReleaseData(getNamespace(componentIds.iterator().next()), clientKey, componentIds);
		return mapper.writeValueAsString(data);
	}

	private String publishData(final String componentId) throws IOException {
		final PublicationData data = new PublicationData(getNamespace(componentId), clientKey, componentId);
		return mapper.writeValueAsString(data);
	}

	private String bulkPublishData(final Collection<String> componentIds) throws IOException {
		final BulkPublicationData data = new BulkPublicationData(getNamespace(componentIds.iterator().next()), clientKey, componentIds);
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
