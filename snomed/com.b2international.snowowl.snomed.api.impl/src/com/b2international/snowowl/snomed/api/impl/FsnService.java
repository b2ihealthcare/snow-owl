package com.b2international.snowowl.snomed.api.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.impl.domain.InternalComponentRef;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.google.common.base.Optional;

public class FsnService {
	
	@Resource
	private SnomedDescriptionServiceImpl descriptionService;

	public Map<String, String> getConceptIdFsnMap(IComponentRef conceptRef, final Collection<String> conceptIds, final List<Locale> locales) {
		final Map<String, String> conceptIdFsnMap = new HashMap<>();
		if (conceptIds.isEmpty()) {
			return conceptIdFsnMap;
		}
		
		final SnomedTerminologyBrowser terminologyBrowser = ApplicationContext.getServiceForClass(SnomedTerminologyBrowser.class);
		final IBranchPath iBranchPath = ClassUtils.checkAndCast(conceptRef, InternalComponentRef.class).getBranch().branchPath();

		new FsnJoinerOperation<SnomedConceptIndexEntry>(conceptRef, locales, descriptionService) {
			@Override
			protected Collection<SnomedConceptIndexEntry> getConceptEntries(String id) {
				Set<SnomedConceptIndexEntry> indexEntries = new HashSet<>();
				for (String conceptId : conceptIds) {
					indexEntries.add(terminologyBrowser.getConcept(iBranchPath, conceptId));
				}
				return indexEntries;
			}

			@Override
			protected SnomedConceptIndexEntry convertConceptEntry(SnomedConceptIndexEntry snomedConceptIndexEntry, Optional<String> optional) {
				String conceptId = snomedConceptIndexEntry.getId();
				conceptIdFsnMap.put(conceptId, optional.or(conceptId));
				return snomedConceptIndexEntry;
			}
		}.run();

		return conceptIdFsnMap;
	}

}
