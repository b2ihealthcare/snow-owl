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
package com.b2international.snowowl.snomed.importer.rf2.util

import com.b2international.snowowl.core.date.DateFormats
import com.b2international.snowowl.core.date.Dates
import com.b2international.snowowl.core.date.EffectiveTimes

/**
 * Collector for gathering effective time from RF2 files.
 */
class Rf2EffectiveTimeCollector {

	/**Returns with the maximum effective time from the given RF2 files.*/
	public Date collectMaximumEffectiveTime(final def rf2Files) {
		def maxEffectiveTime = new Date(Dates.MIN_DATE_LONG)
		rf2Files.each { file ->
			file.eachLine { line, number ->
				if (number > 1) {
					def currentEffectiveTime = EffectiveTimes.parse(line.split("\t")[1], DateFormats.SHORT)
					if (maxEffectiveTime.before(currentEffectiveTime)) {
						maxEffectiveTime = currentEffectiveTime
					}
				}
			}
		}
		return maxEffectiveTime
	}
	
}
