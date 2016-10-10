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
package com.b2international.snowowl.datastore.request;

import java.util.Collection;

import javax.validation.constraints.NotNull;

import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.5
 */
public abstract class RevisionSearchRequest<B> extends BaseSearchRequest<BranchContext, B> {
	
	@NotNull
	private Collection<String> componentIds;
	
	public void setComponentIds(Collection<String> componentIds) {
		this.componentIds = componentIds;
	}
	
	@JsonProperty
	protected final Collection<String> componentIds() {
		return componentIds;
	}
	
	protected Expression createComponentIdFilter() {
		return Expressions.matchAny(getIdField(), componentIds);
	}

	protected void addComponentIdFilter(ExpressionBuilder exp) {
		if (!componentIds().isEmpty()) {
			exp.must(RevisionDocument.Expressions.ids(componentIds()));
		}		
	}
	
	@JsonIgnore
	protected String getIdField() {
		return DocumentMapping._ID;
	}

}
