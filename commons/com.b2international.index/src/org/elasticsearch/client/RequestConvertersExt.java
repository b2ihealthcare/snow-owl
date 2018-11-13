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

import static org.elasticsearch.client.RequestConverters.REQUEST_BODY_CONTENT_TYPE;
import static org.elasticsearch.client.RequestConverters.createContentType;
import static org.elasticsearch.client.RequestConverters.endpoint;
import static org.elasticsearch.common.unit.TimeValue.timeValueMinutes;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.action.support.ActiveShardCount;
import org.elasticsearch.client.RequestConverters.Params;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequestExt;

public class RequestConvertersExt {
	
	static final int DEFAULT_SCROLL_SIZE = 1000;
	static final TimeValue DEFAULT_SCROLL_TIMEOUT = timeValueMinutes(5);

	private RequestConvertersExt() {}
	
    public static final Request updateByQuery(UpdateByQueryRequest updateByQueryRequest) throws IOException {
    	String endpoint =
                endpoint(updateByQueryRequest.indices(), getDocTypes(updateByQueryRequest), "_update_by_query");
        Request request = new Request(HttpPost.METHOD_NAME, endpoint);
        Params params = new Params(request)
//                .withRouting(updateByQueryRequest.getRouting())
            .withPipeline(updateByQueryRequest.getPipeline())
            .withRefresh(updateByQueryRequest.isRefresh())
            .withTimeout(updateByQueryRequest.getTimeout())
            .withWaitForActiveShards(updateByQueryRequest.getWaitForActiveShards(), ActiveShardCount.DEFAULT)
//                .withRequestsPerSecond(updateByQueryRequest.getRequestsPerSecond())
            .withIndicesOptions(updateByQueryRequest.indicesOptions());
        if (updateByQueryRequest.isAbortOnVersionConflict() == false) {
            params.putParam("conflicts", "proceed");
        }
        int batchSize = updateByQueryRequest.getSearchRequest().source().size();
		if (batchSize != DEFAULT_SCROLL_SIZE) {
            params.putParam("scroll_size", Integer.toString(batchSize));
        }
        if (updateByQueryRequest.getScrollTime() != DEFAULT_SCROLL_TIMEOUT) {
            params.putParam("scroll", updateByQueryRequest.getScrollTime());
        }
        if (updateByQueryRequest.getSize() > 0) {
            params.putParam("size", Integer.toString(updateByQueryRequest.getSize()));
        }
        request.setEntity(createEntity(new UpdateByQueryRequestExt(updateByQueryRequest), REQUEST_BODY_CONTENT_TYPE));
        return request;
    }

	private static String[] getDocTypes(UpdateByQueryRequest req) {
		if (req.getSearchRequest().types() != null) {
            return req.getSearchRequest().types();
        } else {
            return new String[0];
        }		
	}
	
	private static HttpEntity createEntity(ToXContent toXContent, XContentType xContentType) throws IOException {
        BytesRef source = XContentHelper.toXContent(toXContent, xContentType, false).toBytesRef();
        return new ByteArrayEntity(source.bytes, source.offset, source.length, createContentType(xContentType));
    }
	
}
