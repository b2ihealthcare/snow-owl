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
package com.b2international.snowowl.snomed.api.domain.browser;

import java.util.Date;

import com.b2international.snowowl.core.date.DateFormats;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

/**
 * Holds common properties of components appearing in the IHTSDO SNOMED CT Browser.
 */
public interface ISnomedBrowserComponent extends IStatusWithModuleIdProvider {

	/** @return the component effective time (in {@code yyyyMMdd} format in JSON responses) */
	@JsonFormat(shape = Shape.STRING, pattern = DateFormats.SHORT, timezone = "UTC")
	Date getEffectiveTime();
}
