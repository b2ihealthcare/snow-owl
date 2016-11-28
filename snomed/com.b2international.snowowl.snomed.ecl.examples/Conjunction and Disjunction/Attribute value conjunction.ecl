/* Attribute values can also use conjunction. The following expression constraint 
 * is satisfied only by clinical findings with an associated morphology whose value
 * is both a subtype (or self) of ulcer and a subtype (or self) of hemorrhage.
 */
 
<404684003|Clinical finding|:
    116676008|Associated morphology| = (<<56208002|Ulcer| AND <<50960005|Hemorrhage|)