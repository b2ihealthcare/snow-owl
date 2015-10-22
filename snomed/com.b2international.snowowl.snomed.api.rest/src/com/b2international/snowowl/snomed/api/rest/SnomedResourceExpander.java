package com.b2international.snowowl.snomed.api.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.api.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.api.impl.FsnService;
import com.b2international.snowowl.snomed.api.rest.domain.ExpandableSnomedRelationship;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedConceptMini;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class SnomedResourceExpander {

	@Autowired
	private FsnService fsnService;
	
	public List<ISnomedRelationship> expandRelationships(IComponentRef conceptRef, List<ISnomedRelationship> members, final List<Locale> locales, String[] expandArray) {
		if (expandArray.length == 0) {
			return members;			
		}
		final List<ExpandableSnomedRelationship> expandedMembers = new ArrayList<>();
		for (ISnomedRelationship relationship : members) {
			expandedMembers.add(new ExpandableSnomedRelationship(relationship));
		}
		for (String expand : expandArray) {
			if (expand.equals("source.fsn")) {
				List<String> conceptIds = Lists.transform(expandedMembers, new Function<ExpandableSnomedRelationship, String>() {
					@Override
					public String apply(ExpandableSnomedRelationship input) {
						return input.getSourceId();
					}
				});
				Map<String, String> conceptIdFsnMap = fsnService.getConceptIdFsnMap(conceptRef, conceptIds, locales);
				for (ExpandableSnomedRelationship relationship : expandedMembers) {
					String sourceId = relationship.getSourceId();
					relationship.setSource(new SnomedConceptMini(sourceId, conceptIdFsnMap.get(sourceId)));
				}
			} else if (expand.equals("type.fsn")) {
				List<String> conceptIds = Lists.transform(expandedMembers, new Function<ExpandableSnomedRelationship, String>() {
					@Override
					public String apply(ExpandableSnomedRelationship input) {
						return input.getTypeId();
					}
				});
				Map<String, String> conceptIdFsnMap = fsnService.getConceptIdFsnMap(conceptRef, conceptIds, locales);
				for (ExpandableSnomedRelationship relationship : expandedMembers) {
					String typeId = relationship.getTypeId();
					relationship.setType(new SnomedConceptMini(typeId, conceptIdFsnMap.get(typeId)));
				}
			} else {
				throw new BadRequestException("Unrecognised expand parameter '%s'.", expand);
			}
		}
		return new ArrayList<ISnomedRelationship>(expandedMembers);
	}

}
