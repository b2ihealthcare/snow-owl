/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
 * 
 * Includes portions of Elasticsearch high-level REST client classes, 
 * also licensed under the Apache 2.0 license:
 * 
 * - org.elasticsearch.client.RestHighLevelClient
 * - org.elasticsearch.client.Request
 */
package com.b2international.index.es;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.ClusterClient;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.CheckedFunction;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.SuppressForbidden;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.lucene.uid.Versions;
import org.elasticsearch.common.xcontent.LoggingDeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.rankeval.RankEvalRequest;
import org.elasticsearch.index.rankeval.RankEvalResponse;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.rest.action.search.RestSearchAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.ReflectionUtils;
import com.b2international.index.Activator;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * @since 6.6
 */
public final class EsClient {

	private static final Logger LOG = LoggerFactory.getLogger("elastic-snowowl");
	
	private static final LoadingCache<EsClientConfiguration, EsClient> CLIENTS_BY_HOST = CacheBuilder.newBuilder()
			.removalListener(EsClient::onRemove)
			.build(CacheLoader.from(EsClient::onAdd));
	
	private static EsClient onAdd(final EsClientConfiguration configuration) {
		return new EsClient(configuration);
	}
	
	private static void onRemove(final RemovalNotification<EsClientConfiguration, EsClient> notification) {
		Activator.withTccl(() -> {
			closeClient(notification.getKey(), notification.getValue().client);
		});
	}

	private static void closeClient(final EsClientConfiguration configuration, RestHighLevelClient client) {
		try {
			client.close();
			LOG.info("Closed ES REST client for '{}'", configuration.getHost().toURI());
		} catch (final IOException e) {
			LOG.error("Unable to close ES REST client", e);
		}
	}
	
	public static final EsClient create(final EsClientConfiguration configuration) {
		try {
			return CLIENTS_BY_HOST.getUnchecked(configuration);
		} catch (UncheckedExecutionException e) {
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			} else {
				throw new RuntimeException(e.getCause());
			}
		}
	}
	
	public static final void stop() {
		CLIENTS_BY_HOST.invalidateAll();
		CLIENTS_BY_HOST.cleanUp();
	}
	
	private final RestHighLevelClient client;
	private final NamedXContentRegistry registry;
	
	private EsClient(final EsClientConfiguration configuration) {
		// XXX: Adjust the thread context classloader while ES client is initializing 
		this.client = Activator.withTccl(() -> {
			
			final HttpHost host = configuration.getHost();

			final RequestConfigCallback requestConfigCallback = requestConfigBuilder -> requestConfigBuilder
					.setConnectTimeout(configuration.getConnectTimeout())
					.setSocketTimeout(configuration.getSocketTimeout());
			
			final RestClientBuilder restClientBuilder = RestClient.builder(host)
				.setRequestConfigCallback(requestConfigCallback)
				.setMaxRetryTimeoutMillis(configuration.getSocketTimeout()); // retry timeout should match socket timeout
			
			boolean useAuthentication = !Strings.isNullOrEmpty(configuration.getUserName()) && !Strings.isNullOrEmpty(configuration.getPassword());

			if (useAuthentication) {
				
				final HttpClientConfigCallback httpClientConfigCallback = httpClientConfigBuilder -> {
					final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
					credentialsProvider.setCredentials(AuthScope.ANY, 
							new UsernamePasswordCredentials(configuration.getUserName(), configuration.getPassword()));
					return httpClientConfigBuilder.setDefaultCredentialsProvider(credentialsProvider);
				};
				
				restClientBuilder.setHttpClientConfigCallback(httpClientConfigCallback);
				
			}
			
			LOG.info("ES REST client is connecting to '{}'{}, connect timeout: {} ms, socket timeout: {} ms.", 
					host.toURI(),
					useAuthentication ? " using basic authentication" : "",
					configuration.getConnectTimeout(),
					configuration.getSocketTimeout());
			
			final RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);
			
			try {
				checkState(client.ping(), "The cluster at '%s' is not available.", host.toURI());
			} catch (Exception e) {
				if (e instanceof ElasticsearchStatusException && ((ElasticsearchStatusException) e).status() == RestStatus.UNAUTHORIZED) {
					LOG.error("Unable to authenticate with remote cluster '{}' using the given credentials", host.toURI());
				}
				closeClient(configuration, client);
				throw e;
			}
			
			return client;
		}); 
		
		// XXX: Extract NamedXContentRegistry via reflection
		this.registry = ReflectionUtils.getField(RestHighLevelClient.class, client, "registry");
	}
	
	//
	// Methods extracted from org.elasticsearch.client.RestHighLevelClient
	//

	public final BulkResponse bulk(BulkRequest bulkRequest, Header... headers) throws IOException {
		return performRequestAndParseEntity(bulkRequest, EsClient::bulk, BulkResponse::fromXContent, headers);
	}

	public final void bulkAsync(BulkRequest bulkRequest, ActionListener<BulkResponse> listener, Header... headers) {
		performRequestAsyncAndParseEntity(bulkRequest, EsClient::bulk, BulkResponse::fromXContent, listener, headers);
	}
	
	public final MultiSearchResponse multiSearch(MultiSearchRequest multiSearchRequest, Header... headers)
			throws IOException {
		return performRequestAndParseEntity(multiSearchRequest, EsClient::multiSearch, MultiSearchResponse::fromXContext, headers);
	}

	public final void multiSearchAsync(MultiSearchRequest searchRequest, ActionListener<MultiSearchResponse> listener,
			Header... headers) {
		performRequestAsyncAndParseEntity(searchRequest, EsClient::multiSearch, MultiSearchResponse::fromXContext, listener, headers);
	}
	
	public final MultiGetResponse multiGet(MultiGetRequest multiGetRequest, Header... headers) throws IOException {
		return performRequestAndParseEntity(multiGetRequest, EsClient::multiGet, MultiGetResponse::fromXContent, headers);
	}

	public final void multiGetAsync(MultiGetRequest multiGetRequest, ActionListener<MultiGetResponse> listener,
			Header... headers) {
		performRequestAsyncAndParseEntity(multiGetRequest, EsClient::multiGet, MultiGetResponse::fromXContent, listener, headers);
	}
	
	protected final <Req extends ActionRequest, Resp> Resp performRequestAndParseEntity(Req request,
			CheckedFunction<Req, Request, IOException> requestConverter,
			CheckedFunction<XContentParser, Resp, IOException> entityParser,
			Header... headers) throws IOException {
		
		ActionRequestValidationException validationException = request.validate();
		if (validationException != null) {
			throw validationException;
		}
		Request req = requestConverter.apply(request);
		Response response;
		try {
			response = getLowLevelClient().performRequest(req.getMethod(), req.getEndpoint(), req.getParameters(), req.getEntity(), headers);
		} catch (ResponseException e) {
			throw parseResponseException(e);
		}

		try {
			return parseEntity(response.getEntity(), entityParser);
		} catch(Exception e) {
			throw new IOException("Unable to parse response body for " + response, e);
		}
	}
	
	protected final <Req extends ActionRequest, Resp> void performRequestAsyncAndParseEntity(Req request,
			CheckedFunction<Req, Request, IOException> requestConverter,
			CheckedFunction<XContentParser, Resp, IOException> entityParser,
			ActionListener<Resp> listener,
			Header... headers) {
		ActionRequestValidationException validationException = request.validate();
		if (validationException != null) {
			listener.onFailure(validationException);
			return;
		}
		Request req;
		try {
			req = requestConverter.apply(request);
		} catch (Exception e) {
			listener.onFailure(e);
			return;
		}

		ResponseListener responseListener = wrapResponseListener(entityParser, listener);
		getLowLevelClient().performRequestAsync(req.getMethod(), req.getEndpoint(), req.getParameters(), req.getEntity(), responseListener, headers);
	}
	
	final <Resp> ResponseListener wrapResponseListener(CheckedFunction<XContentParser, Resp, IOException> entityParser,
			ActionListener<Resp> listener) {
		return new ResponseListener() {
			@Override
			public void onSuccess(Response response) {
				try {
					listener.onResponse(parseEntity(response.getEntity(), entityParser));
				} catch(Exception e) {
					IOException ioe = new IOException("Unable to parse response body for " + response, e);
					onFailure(ioe);
				}
			}

			@Override
			public void onFailure(Exception exception) {
				if (exception instanceof ResponseException) {
					ResponseException responseException = (ResponseException) exception;
					listener.onFailure(parseResponseException(responseException));
				} else {
					listener.onFailure(exception);
				}
			}
		};
	}
	
	protected final <Resp> Resp parseEntity(final HttpEntity entity,
			final CheckedFunction<XContentParser, Resp, IOException> entityParser) throws IOException {
		if (entity == null) {
			throw new IllegalStateException("Response body expected but not returned");
		}
		if (entity.getContentType() == null) {
			throw new IllegalStateException("Elasticsearch didn't return the [Content-Type] header, unable to parse response body");
		}
		XContentType xContentType = XContentType.fromMediaTypeOrFormat(entity.getContentType().getValue());
		if (xContentType == null) {
			throw new IllegalStateException("Unsupported Content-Type: " + entity.getContentType().getValue());
		}
		try (XContentParser parser = xContentType.xContent().createParser(registry,
				LoggingDeprecationHandler.INSTANCE, entity.getContent())) {
			return entityParser.apply(parser);
		}
	}
	
    /**
     * Converts a {@link ResponseException} obtained from the low level REST client into an {@link ElasticsearchException}.
     * If a response body was returned, tries to parse it as an error returned from Elasticsearch.
     * If no response body was returned or anything goes wrong while parsing the error, returns a new {@link ElasticsearchStatusException}
     * that wraps the original {@link ResponseException}. The potential exception obtained while parsing is added to the returned
     * exception as a suppressed exception. This method is guaranteed to not throw any exception eventually thrown while parsing.
     */
    protected final ElasticsearchStatusException parseResponseException(ResponseException responseException) {
        Response response = responseException.getResponse();
        HttpEntity entity = response.getEntity();
        ElasticsearchStatusException elasticsearchException;
        if (entity == null) {
            elasticsearchException = new ElasticsearchStatusException(
                    responseException.getMessage(), RestStatus.fromCode(response.getStatusLine().getStatusCode()), responseException);
        } else {
            try {
                elasticsearchException = parseEntity(entity, BytesRestResponse::errorFromXContent);
                elasticsearchException.addSuppressed(responseException);
            } catch (Exception e) {
                RestStatus restStatus = RestStatus.fromCode(response.getStatusLine().getStatusCode());
                elasticsearchException = new ElasticsearchStatusException("Unable to parse response body", restStatus, responseException);
                elasticsearchException.addSuppressed(e);
            }
        }
        return elasticsearchException;
    }
	
	//
	// Methods and inner class extracted from org.elasticsearch.client.Request
	//
	
    /**
     * Utility class to build request's endpoint given its parts as strings
     */
    public static class EndpointBuilder {

        private final StringJoiner joiner = new StringJoiner("/", "/", "");

        public EndpointBuilder addPathPart(String... parts) {
            for (String part : parts) {
                if (Strings.hasLength(part)) {
                    joiner.add(encodePart(part));
                }
            }
            return this;
        }

        public EndpointBuilder addCommaSeparatedPathParts(String[] parts) {
            addPathPart(String.join(",", parts));
            return this;
        }

        public EndpointBuilder addPathPartAsIs(String part) {
            if (Strings.hasLength(part)) {
                joiner.add(part);
            }
            return this;
        }

        public String build() {
            return joiner.toString();
        }

        private static String encodePart(String pathPart) {
            try {
                //encode each part (e.g. index, type and id) separately before merging them into the path
                //we prepend "/" to the path part to make this pate absolute, otherwise there can be issues with
                //paths that start with `-` or contain `:`
                URI uri = new URI(null, null, null, -1, "/" + pathPart, null, null);
                //manually encode any slash that each part may contain
                return uri.getRawPath().substring(1).replaceAll("/", "%2F");
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Path part [" + pathPart + "] couldn't be encoded", e);
            }
        }
    }
    
    static String endpoint(String index, String endpoint) {
        return new EndpointBuilder().addPathPart(index).addPathPartAsIs(endpoint).build();
    }

    static Request bulk(BulkRequest bulkRequest) throws IOException {
        Map<String, String> parameters = newHashMap();
        
        if (bulkRequest.timeout() != null) {
        	parameters.put("timeout", bulkRequest.timeout().getStringRep());
        }
        
        if (bulkRequest.getRefreshPolicy() != WriteRequest.RefreshPolicy.NONE) {
            parameters.put("refresh", bulkRequest.getRefreshPolicy().getValue());
        }        

        // Bulk API only supports newline delimited JSON or Smile. Before executing
        // the bulk, we need to check that all requests have the same content-type
        // and this content-type is supported by the Bulk API.
        XContentType bulkContentType = null;
        String index = null;
        for (int i = 0; i < bulkRequest.numberOfActions(); i++) {
            DocWriteRequest<?> request = bulkRequest.requests().get(i);
            index = enforceSameIndex(request.index(), index);
            
			// Remove index property, as it will be encoded in the request path
            DocWriteRequest.OpType opType = request.opType();
            
            switch (opType) {
            case INDEX: //$FALL-THROUGH$
			case CREATE:
				bulkContentType = enforceSameContentType((IndexRequest) request, bulkContentType);
				((IndexRequest) request).index(null);
				break;
			case DELETE:
				((DeleteRequest) request).index(null);
				break;
			case UPDATE:
                UpdateRequest updateRequest = (UpdateRequest) request;
                if (updateRequest.doc() != null) {
                    bulkContentType = enforceSameContentType(updateRequest.doc(), bulkContentType);
                }
                if (updateRequest.upsertRequest() != null) {
                    bulkContentType = enforceSameContentType(updateRequest.upsertRequest(), bulkContentType);
                }
                updateRequest.index(null);
				break;
            }
        }

        if (bulkContentType == null) {
            bulkContentType = XContentType.JSON;
        }

        final byte separator = bulkContentType.xContent().streamSeparator();
        final ContentType requestContentType = createContentType(bulkContentType);

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        for (DocWriteRequest<?> request : bulkRequest.requests()) {
            DocWriteRequest.OpType opType = request.opType();

            try (XContentBuilder metadata = XContentBuilder.builder(bulkContentType.xContent())) {
                metadata.startObject();
                {
                    metadata.startObject(opType.getLowercase());
                    if (Strings.hasLength(request.index())) {
                        metadata.field("_index", request.index());
                    }
                    if (Strings.hasLength(request.type())) {
                        metadata.field("_type", request.type());
                    }
                    if (Strings.hasLength(request.id())) {
                        metadata.field("_id", request.id());
                    }
                    if (Strings.hasLength(request.routing())) {
                        metadata.field("routing", request.routing());
                    }
                    if (Strings.hasLength(request.parent())) {
                        metadata.field("parent", request.parent());
                    }
                    if (request.version() != Versions.MATCH_ANY) {
                        metadata.field("version", request.version());
                    }

                    VersionType versionType = request.versionType();
                    if (versionType != VersionType.INTERNAL) {
                        if (versionType == VersionType.EXTERNAL) {
                            metadata.field("version_type", "external");
                        } else if (versionType == VersionType.EXTERNAL_GTE) {
                            metadata.field("version_type", "external_gte");
                        } else if (versionType == VersionType.FORCE) {
                            metadata.field("version_type", "force");
                        }
                    }

                    if (opType == DocWriteRequest.OpType.INDEX || opType == DocWriteRequest.OpType.CREATE) {
                        IndexRequest indexRequest = (IndexRequest) request;
                        if (Strings.hasLength(indexRequest.getPipeline())) {
                            metadata.field("pipeline", indexRequest.getPipeline());
                        }
                    } else if (opType == DocWriteRequest.OpType.UPDATE) {
                        UpdateRequest updateRequest = (UpdateRequest) request;
                        if (updateRequest.retryOnConflict() > 0) {
                            metadata.field("retry_on_conflict", updateRequest.retryOnConflict());
                        }
                        if (updateRequest.fetchSource() != null) {
                            metadata.field("_source", updateRequest.fetchSource());
                        }
                    }
                    metadata.endObject();
                }
                metadata.endObject();

                BytesRef metadataSource = BytesReference.bytes(metadata).toBytesRef();
                content.write(metadataSource.bytes, metadataSource.offset, metadataSource.length);
                content.write(separator);
            }

            BytesRef source = null;
            if (opType == DocWriteRequest.OpType.INDEX || opType == DocWriteRequest.OpType.CREATE) {
                IndexRequest indexRequest = (IndexRequest) request;
                BytesReference indexSource = indexRequest.source();
                XContentType indexXContentType = indexRequest.getContentType();

                try (XContentParser parser = XContentHelper.createParser(NamedXContentRegistry.EMPTY,
                    LoggingDeprecationHandler.INSTANCE, indexSource, indexXContentType)) {
                    try (XContentBuilder builder = XContentBuilder.builder(bulkContentType.xContent())) {
                        builder.copyCurrentStructure(parser);
                        source = BytesReference.bytes(builder).toBytesRef();
                    }
                }
            } else if (opType == DocWriteRequest.OpType.UPDATE) {
                source = XContentHelper.toXContent((UpdateRequest) request, bulkContentType, false).toBytesRef();
            }

            if (source != null) {
                content.write(source.bytes, source.offset, source.length);
                content.write(separator);
            }
        }

        HttpEntity entity = new ByteArrayEntity(content.toByteArray(), 0, content.size(), requestContentType);
        String endpoint = endpoint(index, "_bulk");
		return new Request(HttpPost.METHOD_NAME, endpoint, parameters, entity);
    }
    
    static Request multiSearch(MultiSearchRequest multiSearchRequest) throws IOException {
    	Map<String, String> parameters = newHashMap();
        parameters.put(RestSearchAction.TYPED_KEYS_PARAM, "true");
        if (multiSearchRequest.maxConcurrentSearchRequests() != MultiSearchRequest.MAX_CONCURRENT_SEARCH_REQUESTS_DEFAULT) {
            parameters.put("max_concurrent_searches", Integer.toString(multiSearchRequest.maxConcurrentSearchRequests()));
        }
        
        String index = null;
        for (SearchRequest searchRequest : multiSearchRequest.requests()) {
			String[] indices = searchRequest.indices();
			checkArgument(indices.length < 2, "Multi-index requests in a multi search request is not allowed.");
			index = enforceSameIndex(indices[0], index);
			
			// Remove index property, as it will be encoded in the request path
			// XXX: Setting indices to null can only be done via reflection or wrapping
			ReflectionUtils.setField(searchRequest, "indices", null);
		}
        
        XContent xContent = XContentType.JSON.xContent();
        byte[] source = MultiSearchRequest.writeMultiLineFormat(multiSearchRequest, xContent);
        HttpEntity entity = new ByteArrayEntity(source, createContentType(xContent.type()));
        String endpoint = endpoint(index, "_msearch");
        return new Request(HttpPost.METHOD_NAME, endpoint, parameters, entity);
    }
    
    static Request multiGet(MultiGetRequest multiGetRequest) throws IOException {
    	Map<String, String> parameters = newHashMap();
        parameters.put("preference", multiGetRequest.preference());
        
        if (!multiGetRequest.realtime()) {
            parameters.put("realtime", Boolean.FALSE.toString());
        }
        
        if (multiGetRequest.refresh()) {
            parameters.put("refresh", WriteRequest.RefreshPolicy.IMMEDIATE.getValue());
        }

        String index = null;
        for (MultiGetRequest.Item item : multiGetRequest) {
			index = enforceSameIndex(item.index(), index);
			
			// Remove index property, as it will be encoded in the request path
			item.index(null);
		}
        
        HttpEntity entity = createEntity(multiGetRequest, XContentType.JSON);
        String endpoint = endpoint(index, "_mget");
        return new Request(HttpPost.METHOD_NAME, endpoint, parameters, entity);
    }
    
    private static HttpEntity createEntity(ToXContent toXContent, XContentType xContentType) throws IOException {
        BytesRef source = XContentHelper.toXContent(toXContent, xContentType, false).toBytesRef();
        return new ByteArrayEntity(source.bytes, source.offset, source.length, createContentType(xContentType));
    }
    
    private static String enforceSameIndex(String requestIndex, String index) {
    	if (index == null) {
    		return requestIndex;
    	}
    	if (!requestIndex.equals(index)) {
    		throw new IllegalArgumentException("Mismatching index found for request [" + requestIndex + "], previous requests "
    				+ "targeted index [" + index + "]");
    	}
		return index;
	}

	/**
     * Ensure that the {@link IndexRequest}'s content type is supported by the Bulk API and that it conforms
     * to the current {@link BulkRequest}'s content type (if it's known at the time of this method get called).
     *
     * @return the {@link IndexRequest}'s content type
     */
    static XContentType enforceSameContentType(IndexRequest indexRequest, @Nullable XContentType xContentType) {
        XContentType requestContentType = indexRequest.getContentType();
        if (requestContentType != XContentType.JSON && requestContentType != XContentType.SMILE) {
            throw new IllegalArgumentException("Unsupported content-type found for request with content-type [" + requestContentType
                    + "], only JSON and SMILE are supported");
        }
        if (xContentType == null) {
            return requestContentType;
        }
        if (requestContentType != xContentType) {
            throw new IllegalArgumentException("Mismatching content-type found for request with content-type [" + requestContentType
                    + "], previous requests have content-type [" + xContentType + "]");
        }
        return xContentType;
    }
    
    /**
     * Returns a {@link ContentType} from a given {@link XContentType}.
     *
     * @param xContentType the {@link XContentType}
     * @return the {@link ContentType}
     */
    @SuppressForbidden(reason = "Only allowed place to convert a XContentType to a ContentType")
    public static ContentType createContentType(final XContentType xContentType) {
        return ContentType.create(xContentType.mediaTypeWithoutParameters(), (Charset) null);
    }
    
    static Request clearScroll(ClearScrollRequest clearScrollRequest) throws IOException {
        HttpEntity entity = createEntity(clearScrollRequest, XContentType.JSON);
        return new Request(HttpDelete.METHOD_NAME, "/_search/scroll", ImmutableMap.of("ignore", "404"), entity);
    }
    
	//
	// Delegate methods are unmodified below
	//
	
	public final RestClient getLowLevelClient() {
		return client.getLowLevelClient();
	}

	public final void close() throws IOException {
		client.close();
	}

	public final IndicesClient indices() {
		return client.indices();
	}

	public final ClusterClient cluster() {
		return client.cluster();
	}

	public final boolean ping(Header... headers) throws IOException {
		return client.ping(headers);
	}

	public final MainResponse info(Header... headers) throws IOException {
		return client.info(headers);
	}

	public final GetResponse get(GetRequest getRequest, Header... headers) throws IOException {
		return client.get(getRequest, headers);
	}

	public final void getAsync(GetRequest getRequest, ActionListener<GetResponse> listener, Header... headers) {
		client.getAsync(getRequest, listener, headers);
	}

	public final boolean exists(GetRequest getRequest, Header... headers) throws IOException {
		return client.exists(getRequest, headers);
	}

	public final void existsAsync(GetRequest getRequest, ActionListener<Boolean> listener, Header... headers) {
		client.existsAsync(getRequest, listener, headers);
	}

	public final IndexResponse index(IndexRequest indexRequest, Header... headers) throws IOException {
		return client.index(indexRequest, headers);
	}

	public final void indexAsync(IndexRequest indexRequest, ActionListener<IndexResponse> listener, Header... headers) {
		client.indexAsync(indexRequest, listener, headers);
	}

	public final UpdateResponse update(UpdateRequest updateRequest, Header... headers) throws IOException {
		return client.update(updateRequest, headers);
	}

	public final void updateAsync(UpdateRequest updateRequest, ActionListener<UpdateResponse> listener,
			Header... headers) {
		client.updateAsync(updateRequest, listener, headers);
	}

	public final DeleteResponse delete(DeleteRequest deleteRequest, Header... headers) throws IOException {
		return client.delete(deleteRequest, headers);
	}

	public final void deleteAsync(DeleteRequest deleteRequest, ActionListener<DeleteResponse> listener,
			Header... headers) {
		client.deleteAsync(deleteRequest, listener, headers);
	}
	
	public final SearchResponse search(SearchRequest searchRequest, Header... headers) throws IOException {
		return client.search(searchRequest, headers);
	}

	public final void searchAsync(SearchRequest searchRequest, ActionListener<SearchResponse> listener,
			Header... headers) {
		client.searchAsync(searchRequest, listener, headers);
	}

	public final SearchResponse searchScroll(SearchScrollRequest searchScrollRequest, Header... headers)
			throws IOException {
		return client.searchScroll(searchScrollRequest, headers);
	}

	public final void searchScrollAsync(SearchScrollRequest searchScrollRequest,
			ActionListener<SearchResponse> listener, Header... headers) {
		client.searchScrollAsync(searchScrollRequest, listener, headers);
	}

	public final ClearScrollResponse clearScroll(ClearScrollRequest clearScrollRequest, Header... headers)
			throws IOException {
		return performRequestAndParseEntity(clearScrollRequest, EsClient::clearScroll, ClearScrollResponse::fromXContent, headers);
	}

	public final void clearScrollAsync(ClearScrollRequest clearScrollRequest,
			ActionListener<ClearScrollResponse> listener, Header... headers) {
		performRequestAsyncAndParseEntity(clearScrollRequest, EsClient::clearScroll, ClearScrollResponse::fromXContent, listener, headers);
	}

	public final RankEvalResponse rankEval(RankEvalRequest rankEvalRequest, Header... headers) throws IOException {
		return client.rankEval(rankEvalRequest, headers);
	}

	public final void rankEvalAsync(RankEvalRequest rankEvalRequest, ActionListener<RankEvalResponse> listener,
			Header... headers) {
		client.rankEvalAsync(rankEvalRequest, listener, headers);
	}
}
