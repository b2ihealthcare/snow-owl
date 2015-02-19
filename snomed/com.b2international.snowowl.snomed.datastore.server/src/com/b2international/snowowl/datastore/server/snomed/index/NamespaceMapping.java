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
package com.b2international.snowowl.datastore.server.snomed.index;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;

/**
 * Mapping between namespace IDs and the associated extension namespace concept IDs.
 * <p><b>NOTE:&nbsp;</b>I know this is a quite awful code. But any kind of contribution is much appreciated. 
 */
public abstract class NamespaceMapping {

	private static final Map<String, Long> NAME_SPANCE_MAPPING;
	
	static {

		NAME_SPANCE_MAPPING = Collections.unmodifiableMap(new HashMap<String, Long>()

		{

			private static final long serialVersionUID = -8988051040996630643L;

			{
				put("1000060", 427870009L);
				put("1000074", 427869008L);
				put("1000115", 440164009L);
				put("1000117", 440166006L);
				put("1000116", 440165005L);
				put("1000118", 440167002L);
				put("1000110", 440175008L);
				put("1000111", 440176009L);
				put("1000109", 440174007L);
				put("1000043", 425485007L);
				put("1000114", 440179002L);
				put("1000112", 440177000L);
				put("1000113", 440178005L);
				put("1000000", 370137002L);
				put("1000001", 370138007L);
				put("1000013", 419547002L);
				put("1000031", 420912005L);
				put("1000063", 428398003L);
				put("1000062", 428397008L);
				put("1000056", 428396004L);
				put("1000041", 423904001L);
				put("1000081", 440541002L);
				put("1000037", 424835001L);
				put("1000084", 440596008L);
				put("1000083", 440595007L);
				put("1000015", 420118008L);
				put("1000025", 418109006L);
				put("1000036", 424884009L);
				put("1000094", 440673002L);
				put("1000029", 421845003L);
				put("1000093", 440672007L);
				put("1000124", 442502009L);
				put("1000100", 440352009L);
				put("1000023", 418680007L);
				put("1000040", 423336000L);
				put("1000046", 426657009L);
				put("1000048", 425530005L);
				put("1000018", 420083008L);
				put("1000077", 439167007L);
				put("1000106", 439169005L);
				put("1000078", 439168002L);
				put("1000069", 427872001L);
				put("1000068", 427871008L);
				put("1000010", 417230006L);
				put("1000099", 440351002L);
				put("1000067", 429043007L);
				put("1000071", 429045000L);
				put("1000055", 429044001L);
				put("1000045", 426694000L);
				put("1000086", 440243003L);
				put("1000120", 442354003L);
				put("1000044", 425439009L);
				put("1000085", 440023001L);
				put("1000028", 422154004L);
				put("1000119", 442286005L);
				put("1000108", 440016002L);
				put("1000080", 440015003L);
				put("1000079", 440014004L);
				put("1000038", 423293007L);
				put("1000016", 420064001L);
				put("1000012", 418822000L);
				put("1000051", 427460004L);
				put("1000150", 445853007L);
				put("1000026", 420172001L);
				put("1000101", 440312007L);
				put("1000030", 422175007L);
				put("1000089", 440400009L);
				put("1000004", 413336004L);
				put("1000105", 440390003L);
				put("1000104", 440389007L);
				put("1000107", 439170006L);
				put("1000121", 442237008L);
				put("1000009", 416516009L);
				put("1000136", 445855000L);
				put("1000148", 445854001L);
				put("1000140", 445857008L);
				put("1000139", 445856004L);
				put("1000149", 445842001L);
				put("1000096", 439911000L);
				put("1000122", 442382005L);
				put("1000127", 442383000L);
				put("1000002", 384597007L);
				put("1000008", 414164006L);
				put("1000007", 414163000L);
				put("1000006", 414162005L);
				put("1000005", 414161003L);
				put("1000070", 429185009L);
				put("1000054", 429132002L);
				put("1000059", 429133007L);
				put("1000057", 429131009L);
				put("1000066", 429136004L);
				put("1000058", 429134001L);
				put("1000061", 429135000L);
				put("1000033", 423096008L);
				put("1000014", 419864006L);
				put("1000125", 442287001L);
				put("1000128", 442270003L);
				put("1000142", 445833008L);
				put("1000123", 442467004L);
				put("1000141", 445858003L);
				put("1000143", 445859006L);
				put("1000102", 439431007L);
				put("1000103", 439432000L);
				put("1000003", 413335000L);
				put("1000049", 427105003L);
				put("1000091", 440427008L);
				put("1000090", 440426004L);
				put("1000035", 422671004L);
				put("0", 373872000L); //Core Namespace (namespace concept)
				put("1000024", 418258001L);
				put("1000052", 429641008L);
				put("1000011", 417141002L);
				put("1000021", 419285003L);
				put("1000042", 424084008L);
				put("1000092", 439819004L);
				put("1000032", 424942002L);
				put("1000097", 440639007L);
				put("1000098", 440640009L);
				put("1000039", 422837005L);
				put("1000082", 440542009L);
				put("1000017", 419209006L);
				put("1000087", 439734003L);
				put("1000053", 428427000L);
				put("1000076", 428429002L);
				put("1000072", 428428005L);
				put("1000050", 428430007L);
				put("1000088", 439735002L);
				put("1000020", 419531004L);
				put("1000027", 419908007L);
				put("1000133", 443856002L);
				put("1000132", 443855003L);
				put("1000135", 443858001L);
				put("1000134", 443857006L);
				put("1000019", 419789008L);
				put("1000022", 418989005L);
				put("1000131", 443854004L);
				put("1000126", 442211005L);
				put("1000047", 426327008L);
				put("1000075", 429264004L);
				put("1000073", 429263005L);
				put("1000064", 429262000L);
				put("1000065", 429261007L);
				put("1000129", 442420003L);
				put("1000095", 439910004L);
				put("1000137", 445840009L);
				put("1000138", 445841008L);
				put("1000146", 445836000L);
				put("1000147", 445837009L);
				put("1000144", 445834002L);
				put("1000130", 442475005L);
				put("1000145", 445835001L);
				put("1000034", 423625004L);
				put("1000154", Long.parseLong(Concepts.B2I_NAMESPACE));

			}

		}

		);

	}
	
	
	/**
	 * Returns with the extension name space concept ID for a given namespace extracted from the specified SNOMED&nbsp;CT concept ID.
	 * @param conceptId the concept ID.
	 * @return the SNOMED&nbsp;CT concept ID of the extension namespace concept.
	 */
	public static long getExtensionNamespaceId(final long conceptId) {

		final String componentId = Long.toString(conceptId);
		
		if (SnomedTerminologyComponentConstants.CONCEPT_NUMBER != 
				SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(componentId)) {
			
			throw new IllegalArgumentException("Specified ID is not a valid SNOMED CT concept identifier. ID: " + componentId);
			
		}
		
		
		final char format = componentId.charAt(componentId.length() - 3);
		
		if ('0' == format) {
			
			return NAME_SPANCE_MAPPING.get("0");
			
		} else {
			
			final Long namespaceConceptId = NAME_SPANCE_MAPPING.get(componentId.substring(componentId.length() - 10, componentId.length() - 3));
			return null == namespaceConceptId ? -1L : namespaceConceptId.longValue();
			
		}
		
	}
	

}