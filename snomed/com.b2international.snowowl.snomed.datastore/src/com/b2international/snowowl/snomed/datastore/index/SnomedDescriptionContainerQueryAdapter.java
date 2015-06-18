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
package com.b2international.snowowl.snomed.datastore.index;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.util.Collection;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;

public class SnomedDescriptionContainerQueryAdapter extends SnomedDescriptionIndexQueryAdapter implements Serializable {

    private static final long serialVersionUID = 8913979797366560442L;

    public static final int SEARCH_DESCRIPTION_CONTAINER_SYNYONYMS_ONLY = 1 << 0;
    public static final int SEARCH_DESCRIPTION_CONTAINER_FSN_ONLY = 1 << 1;

    private final Collection<String> conceptIds;

    public SnomedDescriptionContainerQueryAdapter(Collection<String> conceptIds, int searchFlags) {
        super(null, checkFlags(searchFlags, SEARCH_DESCRIPTION_CONTAINER_SYNYONYMS_ONLY, SEARCH_DESCRIPTION_CONTAINER_FSN_ONLY), null); // TODO: component id filtering
        checkArgument(!CompareUtils.isEmpty(conceptIds), "SNOMED CT concept identifiers cannot be empty.");
        this.conceptIds = conceptIds;
    }

    @Override
    protected IndexQueryBuilder createIndexQueryBuilder() {
        return super.createIndexQueryBuilder()
            .requireExactTerm(SnomedIndexBrowserConstants.COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1))
            .requireExactTermIf(anyFlagSet(SEARCH_DESCRIPTION_CONTAINER_SYNYONYMS_ONLY), SnomedIndexBrowserConstants.DESCRIPTION_TYPE_ID, IndexUtils.longToPrefixCoded(Concepts.SYNONYM))
            .requireExactTermIf(anyFlagSet(SEARCH_DESCRIPTION_CONTAINER_FSN_ONLY), SnomedIndexBrowserConstants.DESCRIPTION_TYPE_ID, IndexUtils.longToPrefixCoded(Concepts.FULLY_SPECIFIED_NAME))
            .requireAnyExactBytesRefTerms(SnomedIndexBrowserConstants.DESCRIPTION_CONCEPT_ID, IndexUtils.longToPrefixCoded(conceptIds));
    }
}
