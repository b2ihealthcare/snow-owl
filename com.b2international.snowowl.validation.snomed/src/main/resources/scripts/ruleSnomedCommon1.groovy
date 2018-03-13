import com.b2international.index.aggregations.Aggregation
import com.b2international.index.aggregations.AggregationBuilder
import com.b2international.index.aggregations.Bucket
import com.b2international.index.query.Expression
import java.util.stream.Collectors
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.b2international.snowowl.core.domain.IComponent
import com.google.common.collect.Sets

Set<ComponentIdentifier> invalidIds = Sets.newHashSet()

SnomedConcepts inactiveConcepts = SnomedRequests.prepareSearchConcept()
	.all()
	.filterByActive(false)
	.build()
	.execute(ctx)	
	
def inactiveConceptIds = inactiveConcepts.stream().map({comp -> comp.getId()}).collect(Collectors.toSet());

SnomedRelationships invalidSourceRelationships = SnomedRequests.prepareSearchRelationship()
	.all()
	.filterBySource(inactiveConceptIds)
	.filterByActive(true)
	.build()
	.execute(ctx)
	
if (invalidSourceRelationships.getTotal() > 0) {
	def invalidRelationshipComponents = invalidSourceRelationships.stream().map({comp -> ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, comp.getId())}).collect(Collectors.toSet())
	invalidIds.addAll(invalidRelationshipComponents)
}

SnomedRelationships invalidDestionationRelationships = SnomedRequests.prepareSearchRelationship()
	.all()
	.filterByDestination(inactiveConceptIds)
	.filterByActive(true)
	.build()
	.execute(ctx)
	
if (invalidDestionationRelationships.getTotal() > 0) {
	def invalidRelationshipComponents = invalidDestionationRelationships.stream().map({comp -> ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, comp.getId())}).collect(Collectors.toSet())
	invalidIds.addAll(invalidRelationshipComponents)
}

SnomedRelationships invalidTypeRelationships = SnomedRequests.prepareSearchRelationship()
	.all()
	.filterByType(inactiveConceptIds)
	.filterByActive(true)
	.build()
	.execute(ctx)
	
if (invalidTypeRelationships.getTotal() > 0) {
	def invalidRelationshipComponents = invalidTypeRelationships.stream().map({comp -> ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, comp.getId())}).collect(Collectors.toSet())
	invalidIds.addAll(invalidRelationshipComponents)
}


return new ArrayList<>(invalidIds)


		
