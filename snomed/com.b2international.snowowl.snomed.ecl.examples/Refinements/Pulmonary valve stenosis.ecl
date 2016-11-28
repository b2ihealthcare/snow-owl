/* When more than one attribute is defined in an expression constraint, the 
 * attributes are normally separated by a comma. A comma between two attributes 
 * indicates a conjunction and implies that both attribute conditions must be 
 * true. For example, the expression constraint below is satisfied only by the 
 * set of clinical findings, which have both a finding site of 
 * |Pulmonary valve structure| (or a subtype of |Pulmonary valve structure|) and
 * an associated morphology of |Stenosis| (or a subtype of |stenosis|).
 */
 
<404684003|Clinical finding|:
	363698007|Finding site| = <<39057004|Pulmonary valve structure|, 
	116676008|Associated morphology| = <<415582006|Stenosis|