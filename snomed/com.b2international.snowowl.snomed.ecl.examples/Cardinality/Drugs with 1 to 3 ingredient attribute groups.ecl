/* Minimum and maximum cardinalities may also be applied to attribute groups. A 
 * minimum attribute group cardinality of X constrains the valid clinical meanings
 * to those which have at least (i.e. >=) X non-redundant attribute groups that 
 * match the given attribute group criteria. A maximum cardinality of Y constrains
 * the valid clinical meanings to those which have at most (i.e. <=) Y non-redundant
 * attribute groups that match the given attribute group criteria. For example, a 
 * cardinality of "[1..2]" indicates that all clinical meanings that satisfy the 
 * given expression constraint must have at least one and at most two attribute 
 * groups that match the given attribute group criteria.
 *
 * The expression constraint below is satisfied only by products with one, two, or
 * three attribute groups, which each contain at least one active ingredient relationship.
 */
 
<373873005|Pharmaceutical / biologic product|:
	[1..3] {127489000|Has active ingredient| = <105590001|Substance|}