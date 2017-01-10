/* As with attribute cardinalities, a minimum cardinality of '0' indicates that
 * there is no constraint on the minimum number of attribute groups that may match
 * the given attribute group criteria. For example, the following expression 
 * constraint is satisfied only by products with at most one attribute group 
 * containing an active ingredient relationship (i.e. the maximum attribute group 
 * cardinality is '1' and the minimum attribute group cardinality is unconstrained).
 */ 
 
 <373873005|Pharmaceutical / biologic product|:
	[0..1] {127489000|Has active ingredient| = <105590001|Substance|}