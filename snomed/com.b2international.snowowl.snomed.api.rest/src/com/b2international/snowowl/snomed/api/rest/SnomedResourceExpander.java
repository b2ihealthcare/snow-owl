package com.b2international.snowowl.snomed.api.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.IComponentRef;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.api.impl.DescriptionService;
import com.b2international.snowowl.snomed.api.rest.domain.ExpandableSnomedRelationship;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedConceptMini;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

public class SnomedResourceExpander {

	@Autowired
	private DescriptionService fsnService;
	
	public List<ISnomedRelationship> expandRelationships(IComponentRef conceptRef, List<ISnomedRelationship> members, final List<ExtendedLocale> extendedLocales, String[] expandArray) {
		if (expandArray.length == 0) {
			return members;			
		}
		
		final List<ExpandableSnomedRelationship> expandedMembers = new ArrayList<>();
		for (ISnomedRelationship relationship : members) {
			expandedMembers.add(new ExpandableSnomedRelationship(relationship));
		}
		
		for (String expand : expandArray) {
			if (expand.equals("source.fsn")) {
				
				Set<String> conceptIds = FluentIterable.from(expandedMembers)
						.transform(new Function<ExpandableSnomedRelationship, String>() {
							@Override public String apply(ExpandableSnomedRelationship input) {
								return input.getSourceId();
							}})
						.toSet();
				
				Map<String, ISnomedDescription> fsnMap = fsnService.getFullySpecifiedNames(conceptRef.getBranchPath(), conceptIds, extendedLocales);
				for (ExpandableSnomedRelationship relationship : expandedMembers) {
					String sourceId = relationship.getSourceId();
					relationship.setSource(new SnomedConceptMini(sourceId, getFsn(fsnMap, sourceId)));
				}
				
			} else if (expand.equals("type.fsn")) {
				
				Set<String> conceptIds = FluentIterable.from(expandedMembers)
						.transform(new Function<ExpandableSnomedRelationship, String>() {
							@Override public String apply(ExpandableSnomedRelationship input) {
								return input.getTypeId();
							}})
						.toSet();
				
				Map<String, ISnomedDescription> fsnMap = fsnService.getFullySpecifiedNames(conceptRef.getBranchPath(), conceptIds, extendedLocales);
				for (ExpandableSnomedRelationship relationship : expandedMembers) {
					String typeId = relationship.getTypeId();
					relationship.setType(new SnomedConceptMini(typeId, getFsn(fsnMap, typeId)));
				}
				
			} else {
				throw new BadRequestException("Unrecognised expand parameter '%s'.", expand);
			}
		}
		
		return new ArrayList<ISnomedRelationship>(expandedMembers);
	}

	private String getFsn(Map<String, ISnomedDescription> fsnMap, String conceptId) {
		if (fsnMap.containsKey(conceptId)) {
			return fsnMap.get(conceptId).getTerm();
		} else {
			return conceptId;
		}
	}
}
