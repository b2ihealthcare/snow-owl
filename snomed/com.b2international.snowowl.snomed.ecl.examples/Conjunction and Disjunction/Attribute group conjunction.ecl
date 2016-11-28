/* The following expression constraint is satisfied only by clinical findings which
 * have both a finding site of pulmonary valve structure (or subtype) and an
 * associated morphology of stenosis (or subtype), AND have a finding site of right
 * ventricular structure (or subtype) and an associated morphology of hypertrophy
 * (or subtype).
 */
 
<404684003|Clinical finding|:
	{363698007|Finding site| = <<39057004|Pulmonary valve structure|,
     116676008|Associated morphology| = <<415582006|Stenosis|} AND
    {363698007|Finding site| = <<53085002|Right ventricular structure|,
     116676008|Associated morphology| = <<56246009|Hypertrophy|}