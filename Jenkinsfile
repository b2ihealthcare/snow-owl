
def startDate = new Date()

try {

	def currentVersion
	def revision
	
	notifyBuild('STARTED', startDate)

	node('build') {
		
		stage('Checkout repository') {
			
			checkout scm
			
			pom = readMavenPom file: 'pom.xml'
			currentVersion = pom.version
			revision = sh(returnStdout: true, script: "git rev-parse --short HEAD").trim()
				
		}
		
		stage('Build') {
			
			if (!custom_maven_settings.isEmpty()) {
				withMaven(jdk: 'OpenJDK 11', maven: 'Maven 3.6.0', mavenSettingsConfig: custom_maven_settings, options: [artifactsPublisher(disabled: true)],  publisherStrategy: 'EXPLICIT') {
					sh "mvn clean deploy -Dmaven.test.skip=${skipTests} -Dmaven.install.skip=true -Dtycho.localArtifacts=ignore --no-transfer-progress"
				}
			} else {
				withMaven(jdk: 'OpenJDK 11', maven: 'Maven 3.6.0', options: [artifactsPublisher(disabled: true)], publisherStrategy: 'EXPLICIT') {
					sh "mvn clean deploy -Dmaven.test.skip=${skipTests} -Dmaven.install.skip=true -Dtycho.localArtifacts=ignore --no-transfer-progress"
				}
			}
			
		}
		
	}

	if (currentBuild.resultIsBetterOrEqualTo('SUCCESS')) {
		
		build job: 'snow-owl-docker-build', parameters: [
			string(name: 'groupId', value: 'com.b2international.snowowl'),
			string(name: 'artifactId', value: 'com.b2international.snowowl.server.update'),
			string(name: 'artifactVersion', value: currentVersion),
			string(name: 'classifier', value: 'oss'),
			string(name: 'extension', value: 'tar.gz'),
			string(name: 'imageClassifier', value: 'oss'),
			string(name: 'gitRevision', value: revision),
		], quietPeriod: 1, wait: false

	}

} catch (e) {
	currentBuild.result = "FAILURE"
	throw e
} finally {
	notifyBuild(currentBuild.result, startDate)
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
