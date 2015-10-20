package com.b2international.snowowl.snomed.api.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.api.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.api.impl.FsnService;
import com.b2international.snowowl.snomed.api.rest.domain.ExpandableSnomedRelationship;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedConceptMini;

public class SnomedResourceExpander {

	@Autowired
	private FsnService fsnService;
	
	public List<ISnomedRelationship> expandRelationships(IComponentRef conceptRef, List<ISnomedRelationship> members, final List<Locale> locales, String[] expandArray) {
		if (expandArray.length == 0) {
			return members;			
		}
		final List<ISnomedRelationship> expandedMembers = new ArrayList<>();
		for (ISnomedRelationship relationship : members) {
			expandedMembers.add(new ExpandableSnomedRelationship(relationship));
		}
		for (String expand : expandArray) {
			if (expand.equals("source.fsn")) {
				Set<String> conceptIds = new HashSet<>();
				for (ISnomedRelationship relationship : expandedMembers) {
					conceptIds.add(relationship.getSourceId());
				}
				Map<String, String> conceptIdFsnMap = fsnService.getConceptIdFsnMap(conceptRef, conceptIds, locales);
				for (ISnomedRelationship relationship : expandedMembers) {
					ExpandableSnomedRelationship expandableSnomedRelationship = (ExpandableSnomedRelationship) relationship;
					String sourceId = relationship.getSourceId();
					SnomedConceptMini conceptMini = new SnomedConceptMini(sourceId);
					conceptMini.setFsn(conceptIdFsnMap.get(sourceId));
					expandableSnomedRelationship.setSource(conceptMini);
				}
			} else {
				throw new BadRequestException("Unrecognised expand parameter '%s'.", expand);
			}
		}
		return expandedMembers;
	}

}
