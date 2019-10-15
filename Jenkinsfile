node('docker') {

	def serverArtifact
	def currentVersion
	def revision
	def tag
	
	def startDate = new Date()

	try {

		notifyBuild('STARTED', startDate)

		stage('Checkout repository') {

			checkout scm

			pom = readMavenPom file: 'pom.xml'
			currentVersion = pom.version

			if (currentVersion.contains("SNAPSHOT")) {
				tag = "7.x"
			} else {
				tag = "${pom.version}"
			}

			revision = sh(returnStdout: true, script: "git rev-parse --short HEAD").trim()
			
		}

		stage('Build') {
			
			if (!custom_maven_settings.isEmpty()) {
				withMaven(jdk: 'OpenJDK 11', maven: 'Maven 3.6.0', mavenSettingsConfig: custom_maven_settings, options: [artifactsPublisher()],  publisherStrategy: 'EXPLICIT') {
					sh "mvn clean verify -Dmaven.test.skip=${skipTests}"
				}
			} else {
				withMaven(jdk: 'OpenJDK 11', maven: 'Maven 3.6.0', options: [artifactsPublisher()], publisherStrategy: 'EXPLICIT') {
					sh "mvn clean verify -Dmaven.test.skip=${skipTests}"
				}
			}
			
		}

		if (currentBuild.resultIsBetterOrEqualTo('SUCCESS')) {

			stage('Deploy') {

				if (!custom_maven_settings.isEmpty()) {
					withMaven(jdk: 'OpenJDK 11', maven: 'Maven 3.6.0', mavenSettingsConfig: custom_maven_settings, publisherStrategy: 'EXPLICIT') {
						sh "mvn deploy -Dmaven.test.skip=true -Dmaven.install.skip=true"
					}
				} else {
					withMaven(jdk: 'OpenJDK 11', maven: 'Maven 3.6.0', publisherStrategy: 'EXPLICIT') {
						sh "mvn deploy -Dmaven.test.skip=true -Dmaven.install.skip=true"
					}
				}
				
			}

			stage('Find artifact') {
				
				def rpmArtifact = findFiles(glob: "releng/com.b2international.snowowl.server.update/target/snow-owl-oss*.rpm")[0]
				sh '\\cp -f -t ${WORKSPACE}/docker '+rpmArtifact.path+''
				serverArtifact = rpmArtifact.name
				
			}

			if (!custom_docker_registry.isEmpty()) {

				stage('Build docker image for local registry') {

					def buildArgs = "--build-arg SNOWOWL_RPM_PACKAGE=${serverArtifact}\
							--build-arg BUILD_TIMESTAMP=\"${BUILD_TIMESTAMP}\"\
							--build-arg VERSION=${tag}\
							--build-arg GIT_REVISION=${revision} ./docker"

					docker.withRegistry(custom_docker_registry, 'nexus_credentials') {

						def image = docker.build("snow-owl-oss:${tag}", "${buildArgs}")
						image.push()

						if (!currentVersion.contains("SNAPSHOT")) {
							image.push("latest")
						}
						
					}
					
				}
				
			}

			stage('Build docker image for docker-hub') {

				def buildArgs = "--build-arg SNOWOWL_RPM_PACKAGE=${serverArtifact}\
				  --build-arg BUILD_TIMESTAMP=\"${BUILD_TIMESTAMP}\"\
				  --build-arg VERSION=${tag}\
				  --build-arg GIT_REVISION=${revision} ./docker"

				docker.withRegistry('', 'docker-hub-credentials') {

					def image = docker.build("b2ihealthcare/snow-owl-oss:${tag}", "${buildArgs}")
					image.push()

					if (!currentVersion.contains("SNAPSHOT")) {
						image.push("latest")
					}
					
				}
				
			}

			stage('Clean up') {
				sh 'rm -fv docker/*.rpm'
				sh 'docker system prune -f'
			}
			
		}
		
	} catch (e) {
		currentBuild.result = "FAILURE"
		throw e
	} finally {
		notifyBuild(currentBuild.result, startDate)
	}
}

def notifyBuild(String buildStatus = 'STARTED', Date startDate) {

	// build status of null means successful
	buildStatus =  buildStatus ?: 'SUCCESS'

	// Default values
	def colorCode = '#A30200'
	def summary
	
	if (buildStatus == 'SUCCESS') {
		def duration = groovy.time.TimeCategory.minus(new Date(), startDate).toString()
		summary = "${env.JOB_NAME} - ${env.BUILD_DISPLAY_NAME} - ${buildStatus} after ${duration} (<${env.BUILD_URL}|Open>)"
	} else {
		summary = "${env.JOB_NAME} - ${env.BUILD_DISPLAY_NAME} - ${buildStatus} (<${env.BUILD_URL}|Open>)"
	}
	
	// Override default values based on build status
	if (buildStatus == 'STARTED' || buildStatus == 'SUCCESS') {
		colorCode = '#2EB886'
	} else if (buildStatus == 'UNSTABLE') {
		colorCode = '#DAA038'
	} else {
		colorCode = '#A30200'
	}

	// Send notifications
	slackSend (color: colorCode, message: summary)

}
