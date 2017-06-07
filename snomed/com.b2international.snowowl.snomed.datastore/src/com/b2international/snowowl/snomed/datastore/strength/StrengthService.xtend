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
package com.b2international.snowowl.snomed.datastore.strength

import com.b2international.snowowl.snomed.SnomedConstants.Concepts
import com.google.common.collect.Multimaps
import com.google.common.collect.Range
import java.util.Collection
import org.slf4j.LoggerFactory
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember
import com.b2international.snowowl.snomed.common.SnomedRf2Headers
import java.math.BigDecimal
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil
import com.b2international.snowowl.snomed.snomedrefset.DataType

/**
 * Service implementation for extracting strength information from {@link SnomedReferenceSetMember}s.
 */
class StrengthService implements IStrengthService {
	
	val static LOG = LoggerFactory.getLogger(StrengthService)
	
	val CD_NUMERATOR_VALUE = "NumeratorValue";
	val CD_NUMERATOR_MIN_VALUE = "NumeratorMinValue";
	val CD_NUMERATOR_MAX_VALUE = "NumeratorMaxValue";
	val CD_DENOMINATOR_VALUE = "DenominatorValue";
	val CD_DELIMITER = "Delimiter";
	val CD_RANGE_NUMERATOR_MIN_VALUE = "RangeNumeratorMinValue";
	val CD_RANGE_NUMERATOR_MAX_VALUE = "RangeNumeratorMaxValue";
	
	val ALLOWED_STRENGTH_SUFFIXES = #[
		CD_RANGE_NUMERATOR_MIN_VALUE, CD_RANGE_NUMERATOR_MAX_VALUE, CD_NUMERATOR_VALUE, 
		CD_NUMERATOR_MIN_VALUE, CD_NUMERATOR_MAX_VALUE, CD_DENOMINATOR_VALUE, CD_DELIMITER
	]
	
	/**
	 * Returns a collection of {@link StrengthEntry} for the specified collection of {@link SnomedReferenceSetMember}s
	 * 
	 * @param entries
	 * @return
	 */
	override getStrengths(Collection<SnomedReferenceSetMember> entries) {
		if (!entries.empty) {
			val result = <StrengthEntry>newArrayList()
			val map = Multimaps.index(entries)[removeStrengthSuffixes(it.attributeLabel)]
			map.asMap.forEach[ name, cds |
				if (cds.simpleType) {
					result += name.createSimpleStrength(cds)
				} else if (cds.simpleRangeType) {
					result += name.createSimpleRangeStrength(cds)
				} else if (cds.ratioType) {
					result += name.createRatioStrength(cds)
				} else if (cds.ratioRangeType) {
					result += name.createRatioRangeStrength(cds)
				} else {
					throw new IllegalArgumentException("Unable to convert concrete domain to strength.")
				}
			]
			return result
		}
		return emptyList
	}
	
	def private removeStrengthSuffixes(String label) {
		ALLOWED_STRENGTH_SUFFIXES.fold(label, [c,n | c.replace(n, '')])
	}
	
	def private attributeLabel(SnomedReferenceSetMember it) {
		properties.get(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME) as String
	}
	
	def private uomComponentId(SnomedReferenceSetMember it) {
		properties.get(SnomedRf2Headers.FIELD_UNIT_ID) as String
	}
	
	def private BigDecimal floatValue(SnomedReferenceSetMember it) {
		SnomedRefSetUtil.deserializeValue(DataType.DECIMAL, properties.get(SnomedRf2Headers.FIELD_VALUE) as String)
	}
	
	def private String stringValue(SnomedReferenceSetMember it) {
		properties.get(SnomedRf2Headers.FIELD_VALUE) as String
	}
	
	def private isSimpleType(Collection<SnomedReferenceSetMember> entries) {
 		val result = entries.forall[it.attributeLabel.endsWith(CD_NUMERATOR_VALUE)]
 		
 		if (entries.size > 1 && result) {
 			entries.forEach[LOG.info("Found multiple simple type strength for {}: {}", it.referencedComponent.id, it)]
 		}
 		
 		return result;
 	}

	
	def private isSimpleRangeType(Collection<SnomedReferenceSetMember> entries) {
		Range.closed(1,2).contains(entries.size) && 
		entries.forall[attributeLabel.endsWithMinLabel || attributeLabel.endsWithMaxLabel]
	}
	
	def private isRatioType(Collection<SnomedReferenceSetMember> entries) {
		Range.closed(2,3).contains(entries.size) && 
		entries.exists[attributeLabel.endsWith(CD_NUMERATOR_VALUE)] && 
		entries.exists[attributeLabel.endsWith(CD_DENOMINATOR_VALUE)]
	}
	
	def private isRatioRangeType(Collection<SnomedReferenceSetMember> entries) {
		Range.closed(2,4).contains(entries.size) && 
		entries.exists[attributeLabel.endsWithMinLabel || attributeLabel.endsWithMaxLabel] && 
		entries.exists[attributeLabel.endsWith(CD_DENOMINATOR_VALUE)]
	}
	
	def private endsWithMinLabel(String it) {
		endsWith(CD_NUMERATOR_MIN_VALUE) || endsWith(CD_RANGE_NUMERATOR_MIN_VALUE)
	}
	
	def private endsWithMaxLabel(String it) {
		endsWith(CD_NUMERATOR_MAX_VALUE) || endsWith(CD_RANGE_NUMERATOR_MAX_VALUE)
	}
	
	def private StrengthEntry createSimpleStrength(String name, Collection<SnomedReferenceSetMember> entries) {
		val numeratorEntry = entries.head
		return new StrengthEntry(name, numeratorEntry.floatValue, numeratorEntry.uomComponentId.toLong)
	}
	
	def private StrengthEntry createSimpleRangeStrength(String name, Collection<SnomedReferenceSetMember> entries) {
		val numeratorMinEntry = entries.findFirst[attributeLabel.endsWithMinLabel]
		val numeratorMaxEntry = entries.findFirst[attributeLabel.endsWithMaxLabel]
		val numeratorUnit = if (numeratorMinEntry != null) numeratorMinEntry.uomComponentId.toLong else numeratorMaxEntry.uomComponentId.toLong 
		return new StrengthEntry(name, numeratorMinEntry?.floatValue, numeratorMaxEntry?.floatValue, numeratorUnit) 
	}
	
	def private StrengthEntry createRatioStrength(String name, Collection<SnomedReferenceSetMember> entries) {
		val numeratorEntry = entries.findFirst[attributeLabel.endsWith(CD_NUMERATOR_VALUE)]
		val denominatorEntry = entries.findFirst[attributeLabel.endsWith(CD_DENOMINATOR_VALUE)]
		val delimiterEntry = entries.findFirst[attributeLabel.endsWith(CD_DELIMITER)]
		
		val isDefaultUnit = isDefaultUnit(numeratorEntry.uomComponentId, denominatorEntry.uomComponentId)
		
		val numeratorUnit = if (isDefaultUnit) -1L else numeratorEntry.uomComponentId.toLong
		val denominatorUnit = if (isDefaultUnit) -1L else denominatorEntry.uomComponentId.toLong
		
		val delimiter = if (delimiterEntry != null) getDelimiterFor(delimiterEntry.stringValue) ?: StrengthEntryDelimiter.SLASH else StrengthEntryDelimiter.SLASH
		
		return new StrengthEntry(name, numeratorEntry.floatValue, numeratorUnit, delimiter, denominatorEntry.floatValue, denominatorUnit)
	}
	
	def private StrengthEntry createRatioRangeStrength(String name, Collection<SnomedReferenceSetMember> entries) {
		val numeratorMinEntry = entries.findFirst[attributeLabel.endsWithMinLabel]
		val numeratorMaxEntry = entries.findFirst[attributeLabel.endsWithMaxLabel]
		val denominatorEntry = entries.findFirst[attributeLabel.endsWith(CD_DENOMINATOR_VALUE)]
		val delimiterEntry = entries.findFirst[attributeLabel.endsWith(CD_DELIMITER)]
		
		val numeratorUnit = if (numeratorMinEntry != null) numeratorMinEntry.uomComponentId.toLong else numeratorMaxEntry.uomComponentId.toLong
		val denominatorUnit = denominatorEntry.uomComponentId.toLong
		
		val delimiter = if (delimiterEntry != null)  getDelimiterFor(delimiterEntry.stringValue) ?: StrengthEntryDelimiter.SLASH else StrengthEntryDelimiter.SLASH
		
		return new StrengthEntry(name, numeratorMinEntry?.floatValue, numeratorMaxEntry?.floatValue, numeratorUnit, delimiter, denominatorEntry.floatValue, denominatorUnit)
	}
	
	def private isDefaultUnit(String numeratorUnit, String denominatorUnit) {
		numeratorUnit == Concepts.DEFAULT_UNIT && denominatorUnit == Concepts.DEFAULT_UNIT
	}
	
	def private toLong(String element) {
		Long.valueOf(element)
	}
	
	def private StrengthEntryDelimiter getDelimiterFor(String value) {
		return StrengthEntryDelimiter.values.findFirst[value == it.literal]
	}
	
}