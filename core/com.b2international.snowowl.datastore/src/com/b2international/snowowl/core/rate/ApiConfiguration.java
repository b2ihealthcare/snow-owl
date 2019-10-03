/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rate;

import javax.validation.constraints.Min;

/**
 * @since 7.2
 */
public class ApiConfiguration {

	@Min(0)
	private long overdraft = 0L;
	
	@Min(1)
	private long refillRate = 1L;

	public long getOverdraft() {
		return overdraft;
	}
	
	public void setOverdraft(long overdraft) {
		this.overdraft = overdraft;
	}
	
	public long getRefillRate() {
		return refillRate;
	}
	
	public void setRefillRate(long refillRate) {
		this.refillRate = refillRate;
	}
	
}
