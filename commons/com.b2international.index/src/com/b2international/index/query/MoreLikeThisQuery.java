/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.query;

import org.elasticsearch.index.query.MoreLikeThisQueryBuilder.Item;

import com.b2international.commons.CompareUtils;
import com.google.common.base.Joiner;

/**
 * @since 8.4.0
 */
public final class MoreLikeThisQuery implements Expression {
	
	private String[] fields;
	private String[] likeTexts;
	private Item[] likeItems;
	private int maxQueryTerms;
	
	MoreLikeThisQuery(Item[] likeItems) {
		this.likeItems = likeItems;
	}
	
	MoreLikeThisQuery(String[] likeTexts) {
		this.likeTexts = likeTexts;
	}
	
	MoreLikeThisQuery(String[] likeTexts, Item[] likeItems) {
		this.likeTexts = likeTexts;
		this.likeItems = likeItems;
	}
	
	MoreLikeThisQuery(String[] fields, String[] likeTexts, Item[] likeItems) {
		this.fields = fields;
		this.likeTexts = likeTexts;
		this.likeItems = likeItems;
	}
	
	public String[] getFields() {
		return fields;
	}
	
	public String[] getLikeTexts() {
		return likeTexts;
	}
	
	public Item[] getLikeItems() {
		return likeItems;
	}
	
	
	@Override
	public String toString() {
		final String fieldParamters = CompareUtils.isEmpty(fields) ? "" 
				: String.format(" FIELDS( %s )", Joiner.on(", ").join(fields));
		final String textParameters = CompareUtils.isEmpty(likeTexts) ? "" 
				: String.format(" TEXTS( %s )", Joiner.on(", ").join(likeTexts));
		final String itemParameters = CompareUtils.isEmpty(likeItems) ? "" 
				: String.format(" ITEMS( %s )" ,Joiner.on(", ").join(likeItems));
		return String.format("MORE LIKE THIS(%s, %s, %s)", fieldParamters, textParameters, itemParameters);
	}

	public int getMaxQueryTerms() {
		return maxQueryTerms;
	}

	public void setMaxQueryTerms(int maxQueryTerms) {
		this.maxQueryTerms = maxQueryTerms;
	}
	
}
