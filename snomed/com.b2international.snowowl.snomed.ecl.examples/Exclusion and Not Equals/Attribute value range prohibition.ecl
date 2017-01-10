/* To prohibit an attribute from having a value in a particular range, a
 * cardinality of [0..0] must be used. For example, the following expression
 * constraint represents the set of clinical findings which have exactly zero
 * (i.e. they do not have any) associated morphologies that are a descendant
 * or self of obstruction.
 */
 
<404684003|Clinical finding|:
	[0..0] 116676008|Associated morphology| = <<26036001|Obstruction|