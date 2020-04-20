# Merging changes from B2i's repository

There are numerous customisations applied at this point that need to be in place for the successful deployment and work of the 
server. Those can be grouped in the following categories to look out for when doing the update:

* Files added by us that should not be deleted or changed in the merge process
    * everything under the `.circleci` directory
    * everything under the `.mvn/wrapper` directory
    * the `mvnw` and `mvnw.cmd` files in the root directory 
    * the `babylonhealth` module/directory in the root one
    * the `Makefile`
* Files that make sense for B2i's setup but should be removed from our repo
    * the `travis.yml` file from the root directory
    * everything under the `.github/workflows` directory
* Files we changed that need to be updated carefully keeping our editions in place
    * all `snowowl.yml` files (lang codes, monitoring and ES configs)
    * the `snow-owl-oss.product` (the babylonhealth feature addition)
    * the `settings.xml` file (adjustments required to push libs to Artifactory)
    * the `dist/pom.xml` file (added dependency to the babaylonhealth plugin)
    * the main `pom.xml` file (added babylonhealth module and distributionManagement adjustments)
    * the `Dockerfile`
    * the `BaseApiConfig.java` (edited paths filter)
* Files that might need recreation 
    * the `package.json` - we are on a later version of some of the dependencies - keep that, if others are updated recreate the `package-lock.json` file
    * the `snow-owl-oss.launch` - might need to accept all new changes and in Eclipse add the the babylon plugin the launch config to avoid messing it up by manually changing it
    
#### Important note:     
Update the babylonhealth project version if required, otherwise the build fails
