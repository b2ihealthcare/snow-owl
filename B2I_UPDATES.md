# Updating Process

# create a branch to work in:
git checkout -b myUpdateBranch
git push -u origin myUpdateBranch

# add b2i's remote repo with name "b2i" - you only need to do this once
git remote add b2i git@github.com:b2ihealthcare/snow-owl.git

# rebase our branch against it
git pull b2i 7.x

# resolve conflicts using the checklist https://github.com/babylonhealth/snow-owl/blob/7.x/B2iUpdates.md
git push 

# Merging changes from B2i's repository

There are numerous customisations applied at this point that need to be in place for the successful deployment and work of the 
server. Those can be grouped in the following categories to look out for when doing the update:

* Files added by us that should not be deleted or changed in the merge process
    * everything under the `.circleci` directory
    * everything under the `.mvn/wrapper` directory
    * the `mvnw` and `mvnw.cmd` files in the root directory 
    * the `Makefile`
* Files that make sense for B2i's setup but should be removed from our repo
    * the `travis.yml` file from the root directory
    * everything under the `.github` directory
    * the `Jenkinsfile` file from the root directory
* Files we changed that need to be updated carefully keeping our editions in place
    * all `snowowl.yml` files (lang codes, monitoring and ES configs)
    * the `settings.xml` file (adjustments required to push libs to Artifactory)
    * the main `pom.xml` file (distributionManagement adjustments)
    * the `Dockerfile`
    * the `core/com.b2international.snowowl.core.rest` (plugin replacement)