/* Conjunction and disjunction may be used within refinements in a variety of ways.
 * The most common way of using these operators in a refinement is to define the 
 * conjunction or disjunction of individual attributes.
 *
 * For example, the expression constraint below, in which the comma between the two
 * attributes represents conjunction, is satisfied only by clinical findings which
 * have both a finding site of pulmonary valve structure (or subtype) and an 
 * associated morphology of stenosis (or subtype).
 * 
 *     <404684003|Clinical finding|:
 *         363698007|Finding site| = <<39057004|Pulmonary valve structure|,
 *         116676008|Associated morphology| = <<415582006|Stenosis|
 *
 * This expression constraint can equivalently be expressed as:
 */
 
<404684003|Clinical finding|:
	363698007|Finding site| = <<39057004|Pulmonary valve structure| AND
	116676008|Associated morphology| = <<415582006|Stenosis|