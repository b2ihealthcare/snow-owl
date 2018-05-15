/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.exceptions.BadRequestException;
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
import com.b2international.snowowl.snomed.datastore.id.domain.IdentifierStatus;
import com.b2international.snowowl.snomed.datastore.id.domain.SctId;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentifierReservationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * CIS (IHTSDO) based implementation of the identifier service.
 * 
 * @since 4.5
 */
public class CisSnomedIdentifierService extends AbstractSnomedIdentifierService implements IDisposableService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CisSnomedIdentifierService.class);
	
	private static final int BULK_LIMIT = 1000;

	private final long numberOfPollTries;
	private final long numberOfReauthTries;
	private final long timeBetweenPollTries;

	private final String clientKey;
	private final ObjectMapper mapper;

	private CisClient client;
	private boolean disposed;

	public CisSnomedIdentifierService(final SnomedIdentifierConfiguration conf, final ISnomedIdentifierReservationService reservationService, final ObjectMapper mapper) {
		super(reservationService, conf);
		
		this.clientKey = conf.getCisClientSoftwareKey();
		this.numberOfPollTries = conf.getCisNumberOfPollTries();
		this.timeBetweenPollTries = conf.getCisTimeBetweenPollTries();
		this.numberOfReauthTries = conf.getCisNumberOfReauthTries();
		this.mapper = mapper;
		this.client = new CisClient(conf, mapper);

		// Log in at startup, and keep the token as long as possible
		login();
	}

	@Override
	public Set<String> generate(final String namespace, final ComponentCategory category, final int quantity) {
		checkNotNull(category, "Component category must not be null.");
		checkArgument(quantity > 0, "Number of requested IDs should be non-negative.");
		checkCategory(category);

		LOGGER.debug("Generating {} component IDs for category {}.", quantity, category.getDisplayName());

		HttpPost generateRequest = null;
		HttpGet recordsRequest = null;
		try {

			if (quantity > 1) {
				LOGGER.debug(String.format("Sending %s ID bulk generation request.", category.getDisplayName()));
				
				generateRequest = httpPost(String.format("sct/bulk/generate?token=%s", getToken()), createBulkGenerationData(namespace, category, quantity));
				final String response = execute(generateRequest);
				final String jobId = mapper.readValue(response, JsonNode.class).get("id").asText();
				joinBulkJobPolling(jobId, quantity, getToken());
	
				recordsRequest = httpGet(String.format("bulk/jobs/%s/records?token=%s", jobId, getToken()));
				final String recordsResponse = execute(recordsRequest);
				final JsonNode[] records = mapper.readValue(recordsResponse, JsonNode[].class);
				return getComponentIds(records);
				
			} else {
				LOGGER.debug(String.format("Sending %s ID single generation request.", category.getDisplayName()));
				
				generateRequest = httpPost(String.format("sct/generate?token=%s", getToken()), createGenerationData(namespace, category));
				final String response = execute(generateRequest);
				final SctId sctid = mapper.readValue(response, SctId.class);
				
				return ImmutableSet.of(sctid.getSctid());
			}
			
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Caught exception while generating IDs.", e);
		} finally {
			release(generateRequest);
			release(recordsRequest);
		}
	}

	@Override
	public void register(final Set<String> componentIds) {
		LOGGER.debug(String.format("Registering {} component IDs.", componentIds.size()));

		final Map<String, SctId> sctIds = getSctIds(componentIds);
		final Map<String, SctId> problemSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.<SctId>not(Predicates.or(
				SctId::isAvailable, 
				SctId::isReserved, 
				SctId::isAssigned))));
		
		if (!problemSctIds.isEmpty()) {
			throw new SctIdStatusException("Cannot register %s component IDs because they are not available, reserved, or already assigned.", problemSctIds);
		}

		final Map<String, SctId> availableOrReservedSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.or(
				SctId::isAvailable, 
				SctId::isReserved)));
		
		if (availableOrReservedSctIds.isEmpty()) {
			return;
		}
		
		HttpPost registerRequest = null;
		String currentNamespace = null;
		
		try {
			
			if (availableOrReservedSctIds.size() > 1) {
				final Multimap<String, String> componentIdsByNamespace = toNamespaceMultimap(availableOrReservedSctIds.keySet());
				for (final Entry<String, Collection<String>> entry : componentIdsByNamespace.asMap().entrySet()) {
					currentNamespace = entry.getKey();
					
					for (final Collection<String> bulkIds : Iterables.partition(entry.getValue(), BULK_LIMIT)) {
						LOGGER.debug(String.format("Sending bulk registration request for namespace %s with size %d.", currentNamespace, bulkIds.size()));
						registerRequest = httpPost(String.format("sct/bulk/register?token=%s", getToken()), createBulkRegistrationData(bulkIds));
						execute(registerRequest);
					}
				}
				
			} else {
				
				final String componentId = Iterables.getOnlyElement(availableOrReservedSctIds.keySet());
				currentNamespace = SnomedIdentifiers.getNamespace(componentId);
				registerRequest = httpPost(String.format("sct/register?token=%s", getToken()), createRegistrationData(componentId));
				execute(registerRequest);
			}
		
		} catch (IOException e) {
			throw new SnowowlRuntimeException(String.format("Exception while reserving IDs for namespace %s.", currentNamespace), e);
		} finally {
			release(registerRequest);
		}
	}

	@Override
	public Set<String> reserve(String namespace, ComponentCategory category, int quantity) {
		checkNotNull(category, "Component category must not be null.");
		checkArgument(quantity > 0, "Number of requested IDs should be non-negative.");
		checkCategory(category);

		LOGGER.debug("Reserving {} component IDs for category {}.", quantity, category.getDisplayName());

		HttpPost reserveRequest = null;
		HttpGet recordsRequest = null;
		try {

			if (quantity > 1) {
				LOGGER.debug(String.format("Sending %s ID bulk reservation request.", category.getDisplayName()));
	
				reserveRequest = httpPost(String.format("sct/bulk/reserve?token=%s", getToken()), createBulkReservationData(namespace, category, quantity));
				final String bulkResponse = execute(reserveRequest);
				final String jobId = mapper.readValue(bulkResponse, JsonNode.class).get("id").asText();
				joinBulkJobPolling(jobId, quantity, getToken());
	
				recordsRequest = httpGet(String.format("bulk/jobs/%s/records?token=%s", jobId, getToken()));
				final String recordsResponse = execute(recordsRequest);
				final JsonNode[] records = mapper.readValue(recordsResponse, JsonNode[].class);
				return getComponentIds(records);
			
			} else {
				LOGGER.debug(String.format("Sending %s ID reservation request.", category.getDisplayName()));

				reserveRequest = httpPost(String.format("sct/reserve?token=%s", getToken()), createReservationData(namespace, category));
				final String response = execute(reserveRequest);
				final SctId sctid = mapper.readValue(response, SctId.class);
				
				return ImmutableSet.of(sctid.getSctid());
			}
			
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Exception while bulk reserving IDs.", e);
		} finally {
			release(reserveRequest);
			release(recordsRequest);
		}
	}

	@Override
	public void release(final Set<String> componentIds) {
		LOGGER.debug("Releasing {} component IDs.", componentIds.size());

		final Map<String, SctId> sctIds = getSctIds(componentIds);
		final Map<String, SctId> problemSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.<SctId>not(Predicates.or(
				SctId::isAssigned, 
				SctId::isReserved, 
				SctId::isAvailable))));

		if (!problemSctIds.isEmpty()) {
			throw new SctIdStatusException("Cannot release %s component IDs because they are not assigned, reserved, or already available.", problemSctIds);
		}

		final Map<String, SctId> assignedOrReservedSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.or(
				SctId::isAssigned, 
				SctId::isReserved)));
		
		if (assignedOrReservedSctIds.isEmpty()) {
			return;
		}

		HttpPut releaseRequest = null;
		String currentNamespace = null;
		
		try {
			
			if (assignedOrReservedSctIds.size() > 1) {
				final Multimap<String, String> componentIdsByNamespace = toNamespaceMultimap(assignedOrReservedSctIds.keySet());
				for (final Entry<String, Collection<String>> entry : componentIdsByNamespace.asMap().entrySet()) {
					currentNamespace = entry.getKey();
					
					for (final Collection<String> bulkIds : Iterables.partition(entry.getValue(), BULK_LIMIT)) {
						LOGGER.debug(String.format("Sending bulk release request for namespace %s with size %d.", currentNamespace, bulkIds.size()));
						releaseRequest = httpPut(String.format("sct/bulk/release?token=%s", getToken()), createBulkReleaseData(currentNamespace, bulkIds));
						execute(releaseRequest);
					}
				}
				
			} else {
				
				final String componentId = Iterables.getOnlyElement(assignedOrReservedSctIds.keySet());
				currentNamespace = SnomedIdentifiers.getNamespace(componentId);
				releaseRequest = httpPut(String.format("sct/release?token=%s", getToken()), createReleaseData(componentId));
				execute(releaseRequest);
			}
		
		} catch (IOException e) {
			throw new SnowowlRuntimeException(String.format("Exception while releasing IDs for namespace %s.", currentNamespace), e);
		} finally {
			release(releaseRequest);
		}		
	}

	@Override
	public void deprecate(final Set<String> componentIds) {
		LOGGER.debug("Deprecating {} component IDs.", componentIds.size());

		final Map<String, SctId> sctIds = getSctIds(componentIds);
		final Map<String, SctId> problemSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.<SctId>not(Predicates.or(
				SctId::isAssigned, 
				SctId::isPublished, 
				SctId::isDeprecated))));
		
		if (!problemSctIds.isEmpty()) {
			throw new SctIdStatusException("Cannot deprecate %s component IDs because they are not assigned, published, or already deprecated.", problemSctIds);
		}

		final Map<String, SctId> assignedOrPublishedSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.or(
				SctId::isAssigned, 
				SctId::isPublished)));
		
		if (assignedOrPublishedSctIds.isEmpty()) {
			return;
		}

		HttpPut deprecateRequest = null;
		String currentNamespace = null;
		
		try {
			
			if (assignedOrPublishedSctIds.size() > 1) {
				final Multimap<String, String> componentIdsByNamespace = toNamespaceMultimap(assignedOrPublishedSctIds.keySet());
				for (final Entry<String, Collection<String>> entry : componentIdsByNamespace.asMap().entrySet()) {
					currentNamespace = entry.getKey();
					
					for (final Collection<String> bulkIds : Iterables.partition(entry.getValue(), BULK_LIMIT)) {
						LOGGER.debug(String.format("Sending bulk deprecation request for namespace %s with size %d.", currentNamespace, bulkIds.size()));
						deprecateRequest = httpPut(String.format("sct/bulk/deprecate?token=%s", getToken()), createBulkDeprecationData(currentNamespace, bulkIds));
						execute(deprecateRequest);
					}
				}
				
			} else {
				
				final String componentId = Iterables.getOnlyElement(assignedOrPublishedSctIds.keySet());
				currentNamespace = SnomedIdentifiers.getNamespace(componentId);
				deprecateRequest = httpPut(String.format("sct/deprecate?token=%s", getToken()), createDeprecationData(componentId));
				execute(deprecateRequest);
			}
		
		} catch (IOException e) {
			throw new SnowowlRuntimeException(String.format("Exception while deprecating IDs for namespace %s.", currentNamespace), e);
		} finally {
			release(deprecateRequest);
		}	
	}

	@Override
	public void publish(final Set<String> componentIds) {
		LOGGER.debug("Publishing {} component IDs.", componentIds.size());
		
		final Map<String, SctId> sctIds = getSctIds(componentIds);
		final Map<String, SctId> problemSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.<SctId>not(Predicates.or(
				SctId::isAssigned, 
				SctId::isPublished))));
		
		HttpPut deprecateRequest = null;
		String currentNamespace = null;
		
		try {
			
			final Map<String, SctId> assignedSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, SctId::isAssigned));
			if (!assignedSctIds.isEmpty()) {
				if (assignedSctIds.size() > 1) {
					final Multimap<String, String> componentIdsByNamespace = toNamespaceMultimap(assignedSctIds.keySet());
					for (final Entry<String, Collection<String>> entry : componentIdsByNamespace.asMap().entrySet()) {
						currentNamespace = entry.getKey();
						
						for (final Collection<String> bulkIds : Iterables.partition(entry.getValue(), BULK_LIMIT)) {
							LOGGER.debug(String.format("Sending bulk publication request for namespace %s with size %d.", currentNamespace, bulkIds.size()));
							deprecateRequest = httpPut(String.format("sct/bulk/publish?token=%s", getToken()), createBulkPublishData(currentNamespace, bulkIds));
							execute(deprecateRequest);
						}
					}
					
				} else {
					
					final String componentId = Iterables.getOnlyElement(assignedSctIds.keySet());
					currentNamespace = SnomedIdentifiers.getNamespace(componentId);
					deprecateRequest = httpPut(String.format("sct/publish?token=%s", getToken()), createPublishData(componentId));
					execute(deprecateRequest);
				}
			}
			
			if (!problemSctIds.isEmpty()) {
				throw new SctIdStatusException("Cannot publish %s component IDs because they are not assigned or already published.", problemSctIds);
			}
			
		} catch (IOException e) {
			throw new SnowowlRuntimeException(String.format("Exception while publishing IDs for namespace %s.", currentNamespace), e);
		} finally {
			release(deprecateRequest);
		}	
	}

	@Override
	public Map<String, SctId> getSctIds(final Set<String> componentIds) {

		final Map<String, SctId> existingIdsMap = readSctIds(componentIds);
		
		if (existingIdsMap.size() == componentIds.size()) {
			return existingIdsMap;
		} else {
			final Set<String> knownComponentIds = existingIdsMap.keySet();
			final Set<String> difference = ImmutableSet.copyOf(Sets.difference(componentIds, knownComponentIds));
			
			final ImmutableMap.Builder<String, SctId> resultBuilder = ImmutableMap.builder();
			resultBuilder.putAll(existingIdsMap);
			
			for (final String componentId : difference) {
				resultBuilder.put(componentId, buildSctId(componentId, IdentifierStatus.AVAILABLE));
			}
			
			return resultBuilder.build();
		}
	}
	
	private SctId buildSctId(final String componentId, final IdentifierStatus status) {
		final SctId sctId = new SctId();
		
		sctId.setSctid(componentId);
		sctId.setStatus(status.getSerializedName());
		sctId.setSequence(SnomedIdentifiers.getItemId(componentId));
		sctId.setNamespace(SnomedIdentifiers.getNamespace(componentId));
		sctId.setPartitionId(SnomedIdentifiers.getPartitionId(componentId));
		sctId.setCheckDigit(SnomedIdentifiers.getCheckDigit(componentId));

		// TODO: Other attributes of SctId could also be set here
		return sctId;
	}

	private Map<String, SctId> readSctIds(final Set<String> componentIds) {
		HttpPost bulkRequest = null;
		HttpGet singleRequest = null;
		
		try {

			if (componentIds.size() > 1) {
				LOGGER.debug("Sending bulk component ID get request.");
				final ImmutableMap.Builder<String, SctId> resultBuilder = ImmutableMap.builder();
				
				for (final Collection<String> ids : Iterables.partition(componentIds, BULK_LIMIT)) {
					final String idsAsString = Joiner.on(',').join(ids);
					final ObjectNode idsAsJson = mapper.createObjectNode().put("sctids", idsAsString);
					bulkRequest = client.httpPost(String.format("sct/bulk/ids/?token=%s", getToken()), idsAsJson);
					final String response = execute(bulkRequest);
					
					final SctId[] sctIds = mapper.readValue(response, SctId[].class);
					final Map<String, SctId> sctIdMap = Maps.uniqueIndex(Arrays.asList(sctIds), SctId::getSctid); 
					resultBuilder.putAll(sctIdMap);
				}
				
				return resultBuilder.build();
				
			} else {
				
				final String componentId = Iterables.getOnlyElement(componentIds);
				LOGGER.debug(String.format("Sending component ID %s get request.", componentId));
				singleRequest = httpGet(String.format("sct/ids/%s?token=%s", componentId, getToken()));
				final String response = execute(singleRequest);

				final SctId sctId = mapper.readValue(response, SctId.class);
				return ImmutableMap.of(sctId.getSctid(), sctId);
			}
			
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Exception while getting IDs.", e);
		} finally {
			release(bulkRequest);
			release(singleRequest);
		}
	}
	
	@Override
	public boolean importSupported() {
		return false;
	}

	private void login() {
		client.login();
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

	private String execute(final HttpRequestBase request) throws IOException {
		CisClientException last = null;
		
		for (long attempt = 0; attempt < numberOfReauthTries; attempt++) {
			
			try {
				return client.execute(request);
			} catch (CisClientException e) {
				
				if (e.getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
					last = e;
					LOGGER.warn("Unauthorized response from CIS, retrying request ({} attempt(s) left).", numberOfReauthTries - attempt);
					login();
					
					// Update the corresponding query parameter in the request, then retry
					try {

						URI requestUri = request.getURI();
						URI updatedUri = new URIBuilder(requestUri)
								.setParameter("token", getToken())
								.build();
						
						request.setURI(updatedUri);
						request.reset();

					} catch (URISyntaxException se) {
						throw new IOException("Couldn't update authentication token.", se);
					}
					
				} else {
					throw new BadRequestException(e.getReasonPhrase());
				}
			}
		}
		
		// Re-throw the last captured exception otherwise
		throw new BadRequestException(last.getReasonPhrase());
	}

	private void release(final HttpRequestBase request) {
		if (null != request) {
			client.release(request);
		}
	}

	private void joinBulkJobPolling(final String jobId, final int quantity, final String token) {
		HttpGet request = null;
		JobStatus status = JobStatus.PENDING;

		try {
			LOGGER.debug(String.format("Polling job status with ID %s.", jobId));

			request = httpGet(String.format("bulk/jobs/%s?token=%s", jobId, token));

			for (long pollTry = numberOfPollTries; pollTry > 0; pollTry--) {
				final String response = execute(request);
				final JsonNode node = mapper.readValue(response, JsonNode.class);
				status = JobStatus.get(node.get("status").asInt());

				if (JobStatus.FINISHED == status) {
					break;
				} else if (JobStatus.ERROR == status) {
					throw new SnowowlRuntimeException("Bulk request has ended in error.");
				} else {
					Thread.sleep(timeBetweenPollTries);
				}
			}

		} catch (Exception e) {
			throw new SnowowlRuntimeException("Exception while polling job status.", e);
		} finally {
			release(request);
		}
		
		if (JobStatus.FINISHED != status) {
			throw new SnowowlRuntimeException("Job didn't finish with expected status."); 
		} 
	}

	private Set<String> getComponentIds(final JsonNode[] records) {
		return FluentIterable.from(Arrays.asList(records))
				.transform(jsonNode -> jsonNode.get("sctid").asText())
				.toSet();
	}

	private Multimap<String, String> toNamespaceMultimap(final Set<String> componentIds) {
		return FluentIterable.from(componentIds).index(componentId -> getNamespace(componentId));
	}
	
	private String getNamespace(final String componentId) {
		final String namespace = SnomedIdentifiers.getNamespace(componentId);
		
		if (Strings.isNullOrEmpty(namespace)) {
			return "0";
		} else {
			return namespace;
		}
	}

	private RequestData createGenerationData(final String namespace, final ComponentCategory category) throws IOException {
		return new GenerationData(namespace, clientKey, category);
	}

	private RequestData createBulkGenerationData(final String namespace, final ComponentCategory category, final int quantity) throws IOException {
		return new BulkGenerationData(namespace, clientKey, category, quantity);
	}
	
	private RequestData createRegistrationData(final String componentId) throws IOException {
		return new RegistrationData(SnomedIdentifiers.getNamespace(componentId), clientKey, componentId, "");
	}

	private RequestData createBulkRegistrationData(final Collection<String> componentIds) throws IOException {
		final Collection<Record> records = Lists.newArrayList();
		for (final String componentId : componentIds) {
			records.add(new Record(componentId));
		}

		return new BulkRegistrationData(SnomedIdentifiers.getNamespace(componentIds.iterator().next()), clientKey, records);
	}

	private RequestData createDeprecationData(final String componentId) throws IOException {
		return new DeprecationData(SnomedIdentifiers.getNamespace(componentId), clientKey, componentId);
	}

	private RequestData createBulkDeprecationData(final String namespace, final Collection<String> componentIds) throws IOException {
		return new BulkDeprecationData(namespace, clientKey, componentIds);
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
		return new ReleaseData(SnomedIdentifiers.getNamespace(componentId), clientKey, componentId);
	}

	private RequestData createBulkReleaseData(final String namespace, final Collection<String> componentIds) throws IOException {
		return new BulkReleaseData(namespace, clientKey, componentIds);
	}

	private RequestData createPublishData(final String componentId) throws IOException {
		return new PublicationData(SnomedIdentifiers.getNamespace(componentId), clientKey, componentId);
	}

	private RequestData createBulkPublishData(final String namespace, final Collection<String> componentIds) throws IOException {
		return new BulkPublicationData(namespace, clientKey, componentIds);
	}

	@Override
	public void dispose() {
		if (null != client) {
			client.logout();
			client.close();
			client = null;
		}

		disposed = true;
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	public String getToken() {
		return client.getToken();
	}
}
