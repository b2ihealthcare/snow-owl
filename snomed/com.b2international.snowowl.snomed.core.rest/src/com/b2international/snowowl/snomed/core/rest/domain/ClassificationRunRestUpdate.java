/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.snomed.reasoner.domain.ClassificationStatus;

/**
 */
public class ClassificationRunRestUpdate {

	@NotNull
	private ClassificationStatus status;

	@NotEmpty
	private String module;

	@NotNull
	private String namespace;

	private String assigner;

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public ClassificationStatus getStatus() {
		return status;
	}

	public void setStatus(final ClassificationStatus status) {
		this.status = status;
	}

	public String getAssigner() {
		return assigner;
	}

	public void setAssigner(String assigner) {
		this.assigner = assigner;
	}

	@Override
	public String toString() {
		return "ClassificationRunRestUpdate [status=" + status + ", module=" + module + ", namespace=" + namespace + ", assigner=" + assigner + "]";
	}
}
