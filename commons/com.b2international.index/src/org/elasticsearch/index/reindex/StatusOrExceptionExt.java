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
package org.elasticsearch.index.reindex;

import static org.elasticsearch.common.xcontent.XContentParserUtils.ensureExpectedToken;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.xcontent.XContentParseException;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentParser.Token;
import org.elasticsearch.index.reindex.BulkByScrollTask.Status;
import org.elasticsearch.index.reindex.BulkByScrollTask.StatusOrException;

public class StatusOrExceptionExt {

	public static Set<String> EXPECTED_EXCEPTION_FIELDS = new HashSet<>();
    static {
        EXPECTED_EXCEPTION_FIELDS.add("type");
        EXPECTED_EXCEPTION_FIELDS.add("reason");
        EXPECTED_EXCEPTION_FIELDS.add("caused_by");
        EXPECTED_EXCEPTION_FIELDS.add("suppressed");
        EXPECTED_EXCEPTION_FIELDS.add("stack_trace");
        EXPECTED_EXCEPTION_FIELDS.add("header");
        EXPECTED_EXCEPTION_FIELDS.add("error");
        EXPECTED_EXCEPTION_FIELDS.add("root_cause");
    }
	
	/**
     * Since {@link StatusOrException} can contain either an {@link Exception} or a {@link Status} we need to peek
     * at a field first before deciding what needs to be parsed since the same object could contains either.
     * The {@link #EXPECTED_EXCEPTION_FIELDS} contains the fields that are expected when the serialised object
     * was an instance of exception and the {@link Status#FIELDS_SET} is the set of fields expected when the
     * serialized object was an instance of Status.
     */
    public static StatusOrException fromXContent(XContentParser parser) throws IOException {
        XContentParser.Token token = parser.currentToken();
        if (token == null) {
            token = parser.nextToken();
        }
        if (token == Token.VALUE_NULL) {
            return null;
        } else {
            ensureExpectedToken(XContentParser.Token.START_OBJECT, token, parser::getTokenLocation);
            token = parser.nextToken();
            // This loop is present only to ignore unknown tokens. It breaks as soon as we find a field
            // that is allowed.
            while (token != Token.END_OBJECT) {
                ensureExpectedToken(Token.FIELD_NAME, token, parser::getTokenLocation);
                String fieldName = parser.currentName();
                // weird way to ignore unknown tokens
                if (StatusExt.FIELDS_SET.contains(fieldName)) {
                    return new StatusOrException(
                        StatusExt.innerFromXContent(parser)
                    );
                } else if (EXPECTED_EXCEPTION_FIELDS.contains(fieldName)){
                    return new StatusOrException(ElasticsearchException.innerFromXContent(parser, false));
                } else {
                    // Ignore unknown tokens
                    token = parser.nextToken();
                    if (token == Token.START_OBJECT || token == Token.START_ARRAY) {
                        parser.skipChildren();
                    }
                    token = parser.nextToken();
                }
            }
            throw new XContentParseException("Unable to parse StatusFromException. Expected fields not found.");
        }
    }
	
}
