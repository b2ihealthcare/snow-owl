/* Attribute values, represented by compound expression constraints, may also
 * contain exclusions. When this occurs, the expression constraint is satisfied
 * by any concept or expression which has at least one attribute (of the given
 * type) whose value is satisfied by the compound constraint defined in the
 * attribute value. For example, the expression constraint below represents the
 * set of clinical findings, which have an associated morphology that is a
 * descendant or self of ulcer and a descendant or self of hemorrhage, but not
 * a descendant or self of obstruction.
 */
 
<404684003|Clinical finding|: 
    116676008|Associated morphology| =
        ((<<56208002|Ulcer| AND <<50960005|Hemorrhage|) MINUS <<26036001|Obstruction|)