#Add new database column
alter table snomed_codesystemversion ADD parentBranchPath varchar(2048)
update `snomed_codesystemversion` set parentBranchPath='MAIN'

alter table atc_codesystemversion ADD parentBranchPath varchar(2048)
update `atc_codesystemversion` set parentBranchPath='MAIN'

alter table loinc_codesystemversion ADD parentBranchPath varchar(2048)
update `loinc_codesystemversion` set parentBranchPath='MAIN'

alter table localterminology_codesystemversion ADD parentBranchPath varchar(2048)
update `localterminology_codesystemversion` set parentBranchPath='MAIN'

alter table valueset_codesystemversion ADD parentBranchPath varchar(2048)
update `valueset_codesystemversion` set parentBranchPath='MAIN'

alter table mappingset_codesystemversion ADD parentBranchPath varchar(2048)
update `mappingset_codesystemversion` set parentBranchPath='MAIN'