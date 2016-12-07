/* A maximum cardinality of '*' (or 'many') indicates that there is no constraint
 * on the maximum number of attributes that may match the given attribute criteria. 
 * For example, the following expression constraint is satisfied only by products 
 * that have at least one active ingredient (i.e. the minimum cardinality is '1' 
 * and the maximum cardinality is unconstrained).
 *
 * Note that the default cardinality of each attribute, where not explicitly stated,
 * is [1..*]. This means that the the constraint below is redundant and can also be
 * represented by omitting the cardinality constraint.
 */
 
 <373873005|Pharmaceutical / biologic product|:
	[1..*] 127489000|Has active ingredient| = <105590001|Substance|