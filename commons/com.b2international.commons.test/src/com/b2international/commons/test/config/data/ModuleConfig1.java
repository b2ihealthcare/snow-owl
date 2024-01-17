/*
 * Copyright 2011-2016 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.commons.test.config.data;

/**
 * @since 3.4
 */
public class ModuleConfig1 {

	private boolean attribute1_1;
	private String attribute1_2;

	public String getAttribute1_2() {
		return attribute1_2;
	}
	
	public boolean isAttribute1_1() {
		return attribute1_1;
	}
	
	public void setAttribute1_2(String attribute1_2) {
		this.attribute1_2 = attribute1_2;
	}
	
	public void setAttribute1_1(boolean attribute1_1) {
		this.attribute1_1 = attribute1_1;
	}
	
}