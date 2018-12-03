/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.lucene.uid.Versions;
import org.elasticsearch.common.xcontent.LoggingDeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;

public class RestHighLevelClientExt {
	
	private final RestHighLevelClient client;

	public RestHighLevelClientExt(RestHighLevelClient client) {
		this.client = client;
	}

	public ClearScrollResponse clearScroll(ClearScrollRequest clearScrollRequest, RequestOptions options) throws IOException {
		return client.performRequestAndParseEntity(
			clearScrollRequest, RequestConverters::clearScroll, options, ClearScrollResponse::fromXContent, Collections.singleton(404)
		);
	}

	public final BulkResponse bulk(BulkRequest bulkRequest, RequestOptions options) throws IOException {
		return client.performRequestAndParseEntity(bulkRequest, RestHighLevelClientExt::bulk, options, BulkResponse::fromXContent, Collections.emptySet());
	}

	public final void bulkAsync(BulkRequest bulkRequest, RequestOptions options, ActionListener<BulkResponse> listener) {
		client.performRequestAsyncAndParseEntity(bulkRequest, RestHighLevelClientExt::bulk, options, BulkResponse::fromXContent, listener, Collections.emptySet());
	}
	
	//
	// Methods and inner class extracted from org.elasticsearch.client.Request
	//
	
    static String endpoint(String index, String endpoint) {
        return new RequestConverters.EndpointBuilder().addPathPart(index).addPathPartAsIs(endpoint).build();
    }

    static Request bulk(BulkRequest bulkRequest) throws IOException {
        // Bulk API only supports newline delimited JSON or Smile. Before executing
        // the bulk, we need to check that all requests have the same content-type
        // and this content-type is supported by the Bulk API.
        XContentType bulkContentType = null;
        String index = null;
        for (int i = 0; i < bulkRequest.numberOfActions(); i++) {
            DocWriteRequest<?> writeRequest = bulkRequest.requests().get(i);
            index = enforceSameIndex(writeRequest.index(), index);
            
			// Remove index property, as it will be encoded in the request path
            DocWriteRequest.OpType opType = writeRequest.opType();
            
            switch (opType) {
            case INDEX: //$FALL-THROUGH$
			case CREATE:
				bulkContentType = enforceSameContentType((IndexRequest) writeRequest, bulkContentType);
				((IndexRequest) writeRequest).index(null);
				break;
			case DELETE:
				((DeleteRequest) writeRequest).index(null);
				break;
			case UPDATE:
                UpdateRequest updateRequest = (UpdateRequest) writeRequest;
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
        
        String endpoint = endpoint(index, "_bulk");
        Request request = new Request(HttpPost.METHOD_NAME, endpoint);
        
        if (bulkRequest.timeout() != null) {
        	request.addParameter("timeout", bulkRequest.timeout().getStringRep());
        }
        
        if (bulkRequest.getRefreshPolicy() != WriteRequest.RefreshPolicy.NONE) {
            request.addParameter("refresh", bulkRequest.getRefreshPolicy().getValue());
        }        

        final byte separator = bulkContentType.xContent().streamSeparator();
        final ContentType requestContentType = RequestConverters.createContentType(bulkContentType);

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        for (DocWriteRequest<?> writeRequest : bulkRequest.requests()) {
            DocWriteRequest.OpType opType = writeRequest.opType();

            try (XContentBuilder metadata = XContentBuilder.builder(bulkContentType.xContent())) {
                metadata.startObject();
                {
                    metadata.startObject(opType.getLowercase());
                    if (Strings.hasLength(writeRequest.index())) {
                        metadata.field("_index", writeRequest.index());
                    }
                    if (Strings.hasLength(writeRequest.type())) {
                        metadata.field("_type", writeRequest.type());
                    }
                    if (Strings.hasLength(writeRequest.id())) {
                        metadata.field("_id", writeRequest.id());
                    }
                    if (Strings.hasLength(writeRequest.routing())) {
                        metadata.field("routing", writeRequest.routing());
                    }
                    if (Strings.hasLength(writeRequest.parent())) {
                        metadata.field("parent", writeRequest.parent());
                    }
                    if (writeRequest.version() != Versions.MATCH_ANY) {
                        metadata.field("version", writeRequest.version());
                    }

                    VersionType versionType = writeRequest.versionType();
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
                        IndexRequest indexRequest = (IndexRequest) writeRequest;
                        if (Strings.hasLength(indexRequest.getPipeline())) {
                            metadata.field("pipeline", indexRequest.getPipeline());
                        }
                    } else if (opType == DocWriteRequest.OpType.UPDATE) {
                        UpdateRequest updateRequest = (UpdateRequest) writeRequest;
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
                IndexRequest indexRequest = (IndexRequest) writeRequest;
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
                source = XContentHelper.toXContent((UpdateRequest) writeRequest, bulkContentType, false).toBytesRef();
            }

            if (source != null) {
                content.write(source.bytes, source.offset, source.length);
                content.write(separator);
            }
        }

        HttpEntity entity = new ByteArrayEntity(content.toByteArray(), 0, content.size(), requestContentType);
		request.setEntity(entity);
		return request;
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
    
}
