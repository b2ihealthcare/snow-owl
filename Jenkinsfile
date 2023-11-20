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
	def revision
	def branch
	def mavenPhase = params.skipDeploy ? "verify" : "deploy"

	slack.notifyBuild()

	node('build') {

		stage('Checkout repository') {

			scmVars = checkout scm

			pom = readMavenPom file: 'pom.xml'
			currentVersion = pom.version
			revision = sh(returnStdout: true, script: "git rev-parse --short HEAD").trim()
			branch = scmVars.GIT_BRANCH.replaceAll("origin/", "")

		}

		stage('Build') {

			if (!custom_maven_settings.isEmpty()) {
				withMaven(jdk: 'OpenJDK_11', maven: 'Maven_3.8.4', mavenSettingsConfig: custom_maven_settings, options: [artifactsPublisher(disabled: true)],  publisherStrategy: 'EXPLICIT') {
					sh "mvn clean ${mavenPhase} -Dmaven.test.skip=${skipTests} -Dmaven.install.skip=true -Dtycho.localArtifacts=ignore"
				}
			} else {
				withMaven(jdk: 'OpenJDK_11', maven: 'Maven_3.8.4', options: [artifactsPublisher(disabled: true)], publisherStrategy: 'EXPLICIT') {
					sh "mvn clean ${mavenPhase} -Dmaven.test.skip=${skipTests} -Dmaven.install.skip=true -Dtycho.localArtifacts=ignore"
				}
			}

		}

	}

	if (currentBuild.resultIsBetterOrEqualTo('SUCCESS') && !params.skipDeploy) {

		build job: '/build/docker/snow-owl', parameters: [
			string(name: 'groupId', value: 'com.b2international.snowowl'),
			string(name: 'artifactId', value: 'com.b2international.snowowl.server.update'),
			string(name: 'artifactVersion', value: currentVersion),
			string(name: 'classifier', value: 'oss'),
			string(name: 'extension', value: 'tar.gz'),
			string(name: 'imageClassifier', value: 'oss'),
			string(name: 'gitRevision', value: revision),
			string(name: 'gitBranch', value: branch),
			string(name: 'baseImage', value: 'b2ihealthcare/ubuntu-jdk11:lts'),
			booleanParam(name: 'integrationTests', value: !params.skipTests)
		], quietPeriod: 1, wait: false

	}

	if (!params.skipDownstreamBuilds) {

		build job: downstreamBuild, parameters: [
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
