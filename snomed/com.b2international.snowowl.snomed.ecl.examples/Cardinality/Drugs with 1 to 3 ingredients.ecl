/* To support use cases such as the SNOMED CT concept model and terminology binding,
 * expression constraints may constrain the number of times an attribute can be 
 * included in an expression or concept definition represented in the SNOMED CT 
 * distribution view. This is done using a cardinality constraint, which consists
 * of a minimum cardinality and a maximum cardinality (written "[X..Y]"). A minimum
 * cardinality of X constrains the valid clinical meanings to those which have at 
 * least (i.e. >=) X non-redundant attributes that match the given attribute criteria.
 * A maximum cardinality of Y constrains the valid clinical meanings to those which
 * have at most (i.e. <=) Y non-redundant attributes that match the given attribute
 * criteria. For example, a cardinality of "[1..5]" indicates that all clinical
 * meanings that satisfy the given expression constraint must have at least one and 
 * at most five attributes that match the given attribute criteria.
 *
 * The expression constraint below is satisfied only by products with one, two, or
 * three active ingredients.
 */
 
<373873005|Pharmaceutical / biologic product|:
	[1..3] 127489000|Has active ingredient| = <105590001|Substance|