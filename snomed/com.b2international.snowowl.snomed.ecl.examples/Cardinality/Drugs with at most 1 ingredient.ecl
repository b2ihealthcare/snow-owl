/* A minimum cardinality of '0' indicates that there is no constraint on the 
 * minimum number of attributes that may match the given attribute criteria. 
 * For example, the following expression constraint is satisfied only by products
 * with at most one active ingredient (i.e. the maximum cardinality is '1' and 
 * the minimum cardinality is unconstrained).
 */
 
 <373873005|Pharmaceutical / biologic product|:
	[0..1] 127489000|Has active ingredient| = <105590001|Substance|