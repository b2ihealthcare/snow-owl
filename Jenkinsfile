@Library('jenkins-shared-library') _

/**
* Job Parameters:
*	skipTests - whether to skip unit tests during the build process or not
*	skipDeploy - whether to deploy build artifacts in case of successful maven build or not (should be false by default)
*	skipDownstreamBuilds - whether to skip execution of downstream builds
*	downstreamBuild - name of downstream build
**/
try {

	def currentVersion
	def majorVersion
	def revision
	def mavenPhase = params.skipDeploy ? "verify" : "deploy"

	slack.notifyBuild()

	node('build') {

		stage('Checkout repository') {

			checkout scm

			pom = readMavenPom file: 'pom.xml'
			currentVersion = pom.version
			majorVersion = currentVersion.take(1)
			revision = sh(returnStdout: true, script: "git rev-parse --short HEAD").trim()

		}

		stage('Build') {

			if (!custom_maven_settings.isEmpty()) {
				withMaven(jdk: 'OpenJDK_11', maven: 'Maven_3.6.3', mavenSettingsConfig: custom_maven_settings, options: [artifactsPublisher(disabled: true)],  publisherStrategy: 'EXPLICIT') {
					sh "mvn clean ${mavenPhase} -Dmaven.test.skip=${skipTests} -Dmaven.install.skip=true -Dtycho.localArtifacts=ignore"
				}
			} else {
				withMaven(jdk: 'OpenJDK_11', maven: 'Maven_3.6.3', options: [artifactsPublisher(disabled: true)], publisherStrategy: 'EXPLICIT') {
					sh "mvn clean ${mavenPhase} -Dmaven.test.skip=${skipTests} -Dmaven.install.skip=true -Dtycho.localArtifacts=ignore"
				}
			}

		}

	}

	if (currentBuild.resultIsBetterOrEqualTo('SUCCESS') && !params.skipDeploy) {

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

	if (!skipDownstreamBuilds) {

		build job: 'build-'+majorVersion+'.x/'+downstreamBuild+'', parameters: [
			booleanParam(name: 'skipTests', value: params.skipTests),
			booleanParam(name: 'skipDeploy', value: params.skipDeploy),
			booleanParam(name: 'skipDownstreamBuilds', value: params.skipDownstreamBuilds)
		], quietPeriod: 1, wait: false

	}

} catch (org.jenkinsci.plugins.workflow.steps.FlowInterruptedException e) {
	currentBuild.result = "ABORTED"
	throw e
} catch (e) {
	currentBuild.result = "FAILURE"
	throw e
} finally {
	slack.notifyBuild(currentBuild.result)
}
