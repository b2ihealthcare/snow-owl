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
 * @since 4.0
 */
public class DefaultSettings {

	private String setting1 = "setting1";
	private String setting2 = "setting2";
	
	public void setSetting1(String setting1) {
		this.setting1 = setting1;
	}
	
	public void setSetting2(String setting2) {
		this.setting2 = setting2;
	}
	
	public String getSetting1() {
		return setting1;
	}
	
	public String getSetting2() {
		return setting2;
	}
	
}
