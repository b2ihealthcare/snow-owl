/* The following example uses the disjunction operator (OR) to represent the 
 * disjunction of two attributes. This constraint is satisfied only by clinical
 * findings which have either an associated morphology of 'infarct' (or subtype)
 * or are due to a myocardial infarction (or subtype).
 */

<404684003|Clinical finding|:
	116676008|Associated morphology| = <<55641003|Infarct| OR
	42752001|Due to| = <<22298006|Myocardial infarction|