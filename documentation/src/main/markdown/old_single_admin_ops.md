# List of single administrator operations

| Name | Console comman | Snow Owl UI | Admin console |
| ------- | :-- | :--: | :--: |
| ATC Import ClaML||✔||
|ICD-10 import CLaML||✔||
|ICD-10 AM import from .zip archive||✔||
|Local Code System import from Excel spreadsheet|`localcodesystem importXL`|✔|✔|
|LOINC import from .zip archive|`loinc import`|✔|✔|
|Mapping set import from Excel spreadsheet|`mappingset import`|✔|✔|
|SNOMED CT release import from RF2 files|`sctimport rf2_release`|✔(zip only)|✔|
|SNOMED CT reference set import from RF2 file|`sctimport rf2_refset`|✔|✔|
|SNOMED CT reference set import from delimiter-separated file (includes RF1 subset files)|`sctimport dsv_refset`|✔|✔|
|Value set import from Excel spreadsheet||✔||
|Value set import from UMLS SVS XML file|`valueset import`|✔|✔|
|Import MRCM rules from XMI file|`mrcm import`||✔|
|Export MRCM rules to XMI file|`mrcm export`||✔|

### List of operations that can be executed by regular users

* ATC export to ClaML
* Local Code System export to Excel spreadsheet
* Mapping set export to Excel spreadsheet
* SNOMED CT core components export to OWL 2
* SNOMED CT reference set export to RF1 and RF2
* SNOMED CT reference set export to Delimiter-Separated Values text file
* Value domain export to Excel spreadsheet
* Value domain export to UMLS SVS XML file