/* A maximum cardinality of '*' (or 'many') indicates that there is no constraint 
 * on the maximum number of attribute groups that may match the given attribute 
 * group criteria. For example, the following expression constraint is satisfied 
 * only by products that have at least one attribute group containing an active 
 * ingredient relationship (i.e. the minimum attribute group cardinality is '1' 
 * and the maximum attribute group cardinality is unconstrained).
 */
 
 <373873005|Pharmaceutical / biologic product|:
	[1..*] {127489000|Has active ingredient| = <105590001|Substance|}