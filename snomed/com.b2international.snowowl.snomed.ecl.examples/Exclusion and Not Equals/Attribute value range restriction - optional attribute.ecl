/* To prohibit an attribute from having a value outside a particular range, a
 * cardinality of [0..0] is used in conjunction with the 'not equal to'
 * comparison operator. For example, the following expression constraint
 * represents the set of clinical findings which have exactly zero associated
 * morphologies that are not a descendant or self of obstruction. In other words,
 * clinical findings for which all associated morphologies (if any exist) are
 * descendants (or self) of obstruction.
 */

<404684003|Clinical finding|:
	[0..0] 116676008|Associated morphology| != <<26036001|Obstruction|
