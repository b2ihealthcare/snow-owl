/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.cis.model;

import com.b2international.commons.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Superclass to hold common bean properties for Json requests sent to the CIS.
 * 
 * @since 4.5
 */
public class RequestData {

	private int namespace = 0;

	private String software = "Snow Owl";
	private String comment = "";

	public RequestData() {
	}

	public RequestData(final String namespace, final String software) {
		this(toNamespaceIntValue(namespace), software);
	}
	
	public RequestData(final int namespace, final String software) {
		this.namespace = namespace;
		this.software = software;
		this.comment = String.format("Requested by %s", software);
	}

	public int getNamespace() {
		return namespace;
	}
	
	@JsonIgnore
	public String getNamespaceAsString() {
		return fromNamespaceIntValue(namespace);
	}

	public void setNamespace(int namespace) {
		this.namespace = namespace;
	}

	public String getSoftware() {
		return software;
	}

	public void setSoftware(String software) {
		this.software = software;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	private static int toNamespaceIntValue(final String namespace) {
		return StringUtils.isEmpty(namespace) ? 0 : Integer.valueOf(namespace);
	}
	
	private static String fromNamespaceIntValue(final int namespace) {
		return namespace == 0 ? "" : Integer.toString(namespace);
	}
	
}
