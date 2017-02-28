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
package com.b2international.snowowl.core.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

/**
 * {@link CollectionResource} containing paging information like offset, limit and total.
 * 
 * @since 4.0
 */
//@ApiModel("Pageable Collection")
public class PageableCollectionResource<T> extends CollectionResource<T> {

//	@ApiModelProperty("Offset in the total collection")
	private int offset;
	
//	@ApiModelProperty("The number of requested maximum items")
	private int limit;
	
//	@ApiModelProperty("Total number of results available")
	private int total;

	protected PageableCollectionResource(List<T> items, int offset, int limit, int total) {
		super(items);
		this.offset = offset;
		this.limit = limit;
		this.total = total;
	}

	/**
	 * Returns the offset of this collection resource.
	 * 
	 * @return
	 */
	public final int getOffset() {
		return offset;
	}

	/**
	 * Returns the limit of this collection resource.
	 * 
	 * @return
	 */
	public final int getLimit() {
		return limit;
	}

	/**
	 * Returns the total number of results available.
	 * 
	 * @return
	 */
	public int getTotal() {
		return total;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(PageableCollectionResource.class).add("items", getItems()).add("offset", offset).add("limit", limit)
				.add("total", total).toString();
	}

	/**
	 * Creates a new {@link PageableCollectionResource} from the given items, offset, limit and total arguments.
	 * 
	 * @param items
	 * @param offset
	 * @param limit
	 * @param total
	 * @return
	 */
	@JsonCreator
	public static <T> PageableCollectionResource<T> of(@JsonProperty("items") List<T> items, 
			@JsonProperty("offset") int offset, 
			@JsonProperty("limit") int limit, 
			@JsonProperty("total") int total) {
		
		return new PageableCollectionResource<T>(items, offset, limit, total);
	}

}