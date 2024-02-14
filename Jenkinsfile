@Library('jenkins-shared-library') _

properties(
	[
		disableConcurrentBuilds(),
		parameters(
			[
				booleanParam(
					name: 'skipTests',
					description: 'Skip running the entire test suite'
				),
				booleanParam(
					name: 'skipDeploy',
					description: 'Skip deploying artifacts at the end'
				),
				booleanParam(
					name: 'skipDownstreamBuilds',
					description: 'Skip execution of downstream builds'
				),
				hidden(
					name: 'downstreamBuild',
					defaultValue: 'snow-owl-ext'
				),
				hidden(
					name: 'custom_maven_global_settings',
					defaultValue: '895dc1f0-42a1-4b7d-8b6c-20f93e45e9b8'
				)
			]
		),
		buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '3'))
	]
)

try {

	def currentVersion
	def revision
	def branch
	def mavenPhase = params.skipDeploy ? "verify" : "deploy"

	slack.notifyBuild()

	node('build-jdk17') {

		stage('Checkout repository') {

			scmVars = checkout scm

			pom = readMavenPom file: 'pom.xml'
			currentVersion = pom.version

			revision = sh(returnStdout: true, script: "git rev-parse --short HEAD").trim()
			println("revision:" + revision)
			branch = scmVars.GIT_BRANCH.replaceAll("origin/", "")
			println("branch: " + branch)

		}

		stage('Build') {

			withMaven(globalMavenSettingsConfig: custom_maven_global_settings, publisherStrategy: 'EXPLICIT', traceability: true) {
				sh "./mvnw clean ${mavenPhase} -Dmaven.test.skip=${skipTests} -Dmaven.install.skip=true"
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
			string(name: 'baseImage', value: 'b2ihealthcare/ubuntu:lts'),
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
