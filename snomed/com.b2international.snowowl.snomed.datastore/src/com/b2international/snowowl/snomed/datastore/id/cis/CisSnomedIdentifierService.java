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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration;
import com.b2international.snowowl.snomed.datastore.id.AbstractSnomedIdentifierService;
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
import com.b2international.snowowl.snomed.datastore.id.cis.request.RequestData;
import com.b2international.snowowl.snomed.datastore.id.cis.request.ReservationData;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * CIS (IHTSDO) based implementation of the identifier service.
 * 
 * @since 4.5
 */
public class CisSnomedIdentifierService extends AbstractSnomedIdentifierService implements IDisposableService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CisSnomedIdentifierService.class);
	private static final int BULK_LIMIT = 1000;
	private static final int BULK_GET_LIMIT = 3000;

	private final long numberOfPollTries;
	private final long timeBetweenPollTries;

	private final String clientKey;
	private final ObjectMapper mapper;
	private final CisClient client;

	private boolean disposed = false;

	public CisSnomedIdentifierService(final SnomedIdentifierConfiguration conf, final ISnomedIdentiferReservationService reservationService,
			final ObjectMapper mapper) {
		super(reservationService, conf);
		this.clientKey = conf.getCisClientSoftwareKey();
		this.numberOfPollTries = conf.getCisNumberOfPollTries();
		this.timeBetweenPollTries = conf.getCisTimeBetweenPollTries();
		this.mapper = mapper;
		this.client = new CisClient(conf, mapper);
	}

	@Override
	public String generate(String namespace, ComponentCategory category) {
		HttpPost request = null;
		final String token = login();

		try {
			LOGGER.debug(String.format("Sending %s ID generation request.", category.getDisplayName()));

			request = httpPost(String.format("sct/generate?token=%s", token), createGenerationData(namespace, category));
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
		if (!sctId.matches(IdentifierStatus.AVAILABLE, IdentifierStatus.RESERVED)) {
			LOGGER.warn(String.format("Cannot register ID %s as it is already present with status %s.", componentId, sctId.getStatus()));
			return;
		}

		HttpPost request = null;
		final String token = login();

		try {
			LOGGER.debug(String.format("Sending %s ID registration request.", componentId));

			request = httpPost(String.format("sct/register?token=%s", token), createRegistrationData(componentId));
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
			LOGGER.debug(String.format("Sending %s ID reservation request.", category.getDisplayName()));

			request = httpPost(String.format("sct/reserve?token=%s", token), createReservationData(namespace, category));
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
		final SctId sctId = getSctId(componentId);
		if (sctId.isDeprecated()) {
			return;
		}
		
		HttpPut request = null;
		final String token = login();

		try {
			LOGGER.debug(String.format("Sending component ID %s deprecation request.", componentId));

			request = httpPut(String.format("sct/deprecate?token=%s", token), createDeprecationData(componentId));
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
		final SctId sctId = getSctId(componentId);
		if (sctId.isAvailable()) {
			return;
		}

		HttpPut request = null;
		final String token = login();

		try {
			LOGGER.debug(String.format("Sending component ID %s release request.", componentId));

			request = httpPut(String.format("sct/release?token=%s", token), createReleaseData(componentId));
			execute(request);
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Exception while releasing ID.", e);
		} finally {
			release(request);
			logout(token);
		}
	}

	@Override
	public void publish(final String componentId) {
		final SctId sctId = getSctId(componentId);
		if (sctId.isPublished()) {
			return;
		}
		
		HttpPut request = null;
		final String token = login();

		try {
			LOGGER.debug(String.format("Sending component ID %s publication request.", componentId));

			request = httpPut(String.format("sct/publish?token=%s", token), createPublishData(componentId));
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
			LOGGER.debug(String.format("Sending component ID %s get request.", componentId));

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
	public Collection<String> generate(final String namespace, final ComponentCategory category, final int quantity) {
		HttpPost bulkRequest = null;
		HttpGet recordsRequest = null;

		final String token = login();

		try {
			LOGGER.debug(String.format("Sending %s ID bulk generation request.", category.getDisplayName()));

			bulkRequest = httpPost(String.format("sct/bulk/generate?token=%s", token),
					createBulkGenerationData(namespace, category, quantity));
			final String bulkResponse = execute(bulkRequest);
			final String jobId = mapper.readValue(bulkResponse, JsonNode.class).get("id").asText();

			final JobStatus status = pollJob(jobId, token);

			if (JobStatus.FINISHED != status) {
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
	public void register(final Collection<String> componentIds) {
		final Collection<String> componentIdsToRegister = Lists.newArrayList();
		final Collection<SctId> sctIds = getSctIds(componentIds);

		for (final SctId sctId : sctIds) {
			// we want to register only the available or reserved IDs
			if (sctId.matches(IdentifierStatus.AVAILABLE, IdentifierStatus.RESERVED))
				componentIdsToRegister.add(sctId.getSctid());
		}

		if (componentIdsToRegister.isEmpty())
			return;

		HttpPost bulkRequest = null;
		HttpGet recordsRequest = null;

		final String token = login();

		try {
			for (final Collection<String> ids : Lists.partition(Lists.newArrayList(componentIdsToRegister), BULK_LIMIT)) {
				LOGGER.debug(String.format("Sending bulk registration request with size %d.", ids.size()));

				bulkRequest = httpPost(String.format("sct/bulk/register?token=%s", token), createBulkRegistrationData(ids));
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
	public Collection<String> reserve(String namespace, ComponentCategory category, int quantity) {
		HttpPost bulkRequest = null;
		HttpGet recordsRequest = null;

		final String token = login();

		try {
			LOGGER.debug(String.format("Sending %s ID bulk reservation request.", category.getDisplayName()));

			bulkRequest = httpPost(String.format("sct/bulk/reserve?token=%s", token),
					createBulkReservationData(namespace, category, quantity));
			final String bulkResponse = execute(bulkRequest);
			final String jobId = mapper.readValue(bulkResponse, JsonNode.class).get("id").asText();

			final JobStatus status = pollJob(jobId, token);

			if (JobStatus.FINISHED != status) {
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
	public void deprecate(final Collection<String> componentIds) {
		final Collection<String> componentIdsToDeprecate = Lists.newArrayList();
		final Collection<SctId> sctIds = getSctIds(componentIds);

		for (final SctId sctId : sctIds) {
			if (!sctId.isDeprecated())
				componentIdsToDeprecate.add(sctId.getSctid());
		}

		if (componentIdsToDeprecate.isEmpty())
			return;
		
		HttpPut request = null;
		final String token = login();

		try {
			for (final Collection<String> ids : Lists.partition(Lists.newArrayList(componentIds), BULK_LIMIT)) {
				LOGGER.debug(String.format("Sending component ID bulk deprecation request with size %d.", ids.size()));

				request = httpPut(String.format("sct/bulk/deprecate?token=%s", token), createBulkDeprecationData(ids));
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
	public void release(final Collection<String> componentIds) {
		final Collection<String> componentIdsToRelease = Lists.newArrayList();
		final Collection<SctId> sctIds = getSctIds(componentIds);

		for (final SctId sctId : sctIds) {
			if (!sctId.isAvailable())
				componentIdsToRelease.add(sctId.getSctid());
		}

		if (componentIdsToRelease.isEmpty())
			return;

		HttpPut request = null;
		final String token = login();

		try {
			for (final Collection<String> ids : Lists.partition(Lists.newArrayList(componentIdsToRelease), BULK_LIMIT)) {
				LOGGER.debug(String.format("Sending component ID bulk release request with size %d.", ids.size()));

				request = httpPut(String.format("sct/bulk/release?token=%s", token), createBulkReleaseData(ids));
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
	public void publish(final Collection<String> componentIds) {
		final Collection<String> componentIdsToPublish = Lists.newArrayList();
		final Collection<SctId> sctIds = getSctIds(componentIds);

		for (final SctId sctId : sctIds) {
			if (!sctId.isPublished())
				componentIdsToPublish.add(sctId.getSctid());
		}

		if (componentIdsToPublish.isEmpty())
			return;
		
		HttpPut request = null;
		final String token = login();

		try {
			for (final Collection<String> ids : Lists.partition(Lists.newArrayList(componentIds), BULK_LIMIT)) {
				LOGGER.debug(String.format("Sending component ID bulk publication request with size %d.", ids.size()));

				request = httpPut(String.format("sct/bulk/publish?token=%s", token), createBulkPublishData(ids));
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
			LOGGER.debug("Sending bulk component ID get request.");
			final Collection<SctId> sctIds = Lists.newArrayList();

			for (final Collection<String> ids : Lists.partition(Lists.newArrayList(componentIds), BULK_GET_LIMIT)) {
				final StringBuilder builder = new StringBuilder();
				for (final String componentId : ids) {
					if (0 != builder.length())
						builder.append(",");
					builder.append(componentId);
				}

				request = httpGet(String.format("sct/bulk/ids/?token=%s&sctids=%s", token, builder.toString()));
				final String response = execute(request);
				sctIds.addAll(Lists.newArrayList(mapper.readValue(response, SctId[].class)));
			}

			return sctIds;
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
			LOGGER.debug("Sending component IDs get request.");

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
	
	@Override
	public boolean importSupported() {
		return false;
	}

	private String login() {
		return client.login();
	}

	private void logout(final String token) {
		if (null != token)
			client.logout(token);
	}

	private HttpGet httpGet(final String suffix) {
		return client.httpGet(suffix);
	}

	private HttpPost httpPost(final String suffix, final RequestData data) throws IOException {
		return client.httpPost(suffix, data);
	}

	private HttpPut httpPut(final String suffix, final RequestData data) throws IOException {
		return client.httpPut(suffix, data);
	}

	private String execute(final HttpRequestBase request) {
		return client.execute(request);
	}

	private void release(final HttpRequestBase request) {
		if (null != request)
			client.release(request);
	}

	private JobStatus pollJob(final String jobId, final String token) {
		HttpGet request = null;

		try {
			LOGGER.debug(String.format("Polling job status with ID %s.", jobId));

			request = httpGet(String.format("bulk/jobs/%s?token=%s", jobId, token));

			JobStatus status = JobStatus.PENDING;
			int pollTry = 0;

			while (pollTry < numberOfPollTries) {
				final String response = execute(request);
				final JsonNode node = mapper.readValue(response, JsonNode.class);
				status = JobStatus.get(node.get("status").asInt());

				if (JobStatus.FINISHED == status) {
					break;
				} else if (JobStatus.ERROR == status) {
					throw new SnowowlRuntimeException("Bulk request has ended in error.");
				} else {
					pollTry++;
					Thread.sleep(timeBetweenPollTries);
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

	private RequestData createGenerationData(final String namespace, final ComponentCategory category) throws IOException {
		return new GenerationData(selectNamespace(namespace), clientKey, category);
	}

	private RequestData createBulkGenerationData(final String namespace, final ComponentCategory category, final int quantity)
			throws IOException {
		return new BulkGenerationData(selectNamespace(namespace), clientKey, category, quantity);
	}
	
	private RequestData createRegistrationData(final String componentId) throws IOException {
		return new RegistrationData(getNamespace(componentId), clientKey, componentId, "");
	}

	private RequestData createBulkRegistrationData(final Collection<String> componentIds) throws IOException {
		final Collection<Record> records = Lists.newArrayList();
		for (final String componentId : componentIds) {
			records.add(new Record(componentId));
		}

		return new BulkRegistrationData(getNamespace(componentIds.iterator().next()), clientKey, records);
	}

	private RequestData createDeprecationData(final String componentId) throws IOException {
		return new DeprecationData(getNamespace(componentId), clientKey, componentId);
	}

	private RequestData createBulkDeprecationData(final Collection<String> componentIds) throws IOException {
		return new BulkDeprecationData(getNamespace(componentIds.iterator().next()), clientKey, componentIds);
	}

	private RequestData createReservationData(final String namespace, final ComponentCategory category) throws IOException {
		return new ReservationData(namespace, clientKey, getExpirationDate(), category);
	}

	private RequestData createBulkReservationData(final String namespace, final ComponentCategory category, final int quantity)
			throws IOException {
		return new BulkReservationData(namespace, clientKey, getExpirationDate(), category, quantity);
	}

	private String getExpirationDate() {
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		final Date expirationDate = calendar.getTime();

		return Dates.formatByGmt(expirationDate, DateFormats.DEFAULT);
	}

	private RequestData createReleaseData(final String componentId) throws IOException {
		return new ReleaseData(getNamespace(componentId), clientKey, componentId);
	}

	private RequestData createBulkReleaseData(final Collection<String> componentIds) throws IOException {
		return new BulkReleaseData(getNamespace(componentIds.iterator().next()), clientKey, componentIds);
	}

	private RequestData createPublishData(final String componentId) throws IOException {
		return new PublicationData(getNamespace(componentId), clientKey, componentId);
	}

	private RequestData createBulkPublishData(final Collection<String> componentIds) throws IOException {
		return new BulkPublicationData(getNamespace(componentIds.iterator().next()), clientKey, componentIds);
	}

	private String getNamespace(final String componentId) {
		return SnomedIdentifiers.create(componentId).getNamespace();
	}

	@Override
	public void dispose() {
		if (null != client) {
			client.close();
			disposed = true;
		}
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

}
