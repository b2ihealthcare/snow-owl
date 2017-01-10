/* This constraint returns findings that have at least one associated morphology
 * which is a descendant or self of obstruction and that do NOT have any values for
 * associated morphology other than obstruction (or descendants).
 */
  
<404684003|Clinical finding|:
	[0..0] 116676008|Associated morphology| != <<26036001 |Obstruction| AND
	[1..*] 116676008|Associated morphology| = <<26036001 |Obstruction|
	
 
/* This differs from
 *
 * <404684003|Clinical finding|:
 *     [0..0] 116676008|Associated morphology| != <<26036001|Obstruction|
 *
 * which would also include findings that do not have an associated morphology attribute.
 */
	