import hudson.tasks.test.AbstractTestResultAction

pipeline {
  agent any

  tools {
    jdk 'OpenJDK8'
  }

  environment {
    ARTIFACTORY_USER = credentials('artifactory-user')
    ARTIFACTORY_PASSWORD = credentials('artifactory-password')
    JAVA_HOME = "/usr/local/java/jdk1.8/"
    BUILD_VERSION = VersionNumber(versionNumberString: '${BUILD_DATE_FORMATTED, "yyyy.MM.dd"}-${BUILDS_THIS_WEEK,XX}')
    String branchOrTag = getBranchOrTag(tagName, "${GIT_BRANCH}")
  }

  stages {
    stage('Build') {
      steps {
        sh './gradlew --refresh-dependencies -PversionQualifier= clean build -x test --parallel'
        stash includes: "java-sdk/build/libs/java-sdk*.jar", name: "target-java-sdk.jar"
      }
    }
    stage('Test') {
      steps {
        sh './gradlew cleanTest test jacocoTestReport jacocoTestCoverageVerification --parallel'
      }
      post {
        always {
          junit '**/build/test-results/test/TEST-*.xml'
        }
        success {
          jacoco(execPattern: 'build/jacoco/*.exec')
        }
      }
    }
    stage('Code Analysis') {
      when {
        expression {
          return !params.SKIP_ANALYSIS
        }
      }
      failFast true
      parallel {
        stage('SonarQube') {
          when {
            branch 'master'
          }
          steps {
            script {
              scannerHome = tool 'SonarScanner'
            }
            withSonarQubeEnv('TnTSonarQubeEnvironment') {
              sh './gradlew sonarqube'
            }
            script { currentBuild.result = 'SUCCESS' }
            step([$class: 'MasterCoverageAction', scmVars: [GIT_URL: env.GIT_URL]])
          }
        }
        stage('PR Coverage') {
          when {
            expression { return isPullRequest(env) }
          }
          steps {
            script { currentBuild.result = 'SUCCESS' }
            step([$class: 'CompareCoverageAction', publishResultAs: 'comment', scmVars: [GIT_URL: env.GIT_URL]])
          }
        }
      }
    }
  }
  post {
    success {
      unstash "target-java-sdk.jar"
      archiveArtifacts artifacts: 'java-sdk/build/libs/**/*.jar', fingerprint: true
      script {
        slackSend(
          color: 'good',
          message: [
            "${getJenkinsLink(env)} *PASSED*.",
            "${getArtifactReport(env, params)}",
            "${getBuildReport(currentBuild)}"].join("\n"),
          channel: getSlackChannel(env)
        )
      }
    }
    unsuccessful {
      script {
        slackSend(
          color: 'danger',
          message: "${getJenkinsLink(env)} *NOT SUCCESSFUL*.\n\n${getBuildReport(currentBuild)}",
          channel: getSlackChannel(env)
        )
      }
    }
  }
}

static boolean shouldPublishArtifacts(env, params) {
  return params.PUBLISH_ARTIFACTS || !isPullRequest(env)
}

static String getBranchOrTag(String tagName, String branch) {
  if (isTag(tagName)) {
    return tagName;
  }
  return branch
}

static boolean isPullRequest(env) {
  return env.CHANGE_ID != null
}

static boolean isTag(String tagName) {
  tagName != null && !tagName.isEmpty() && !tagName.equalsIgnoreCase("null")
}

static String getSlackChannel(env) {
  return isPullRequest(env) ? "@${env.CHANGE_AUTHOR}" : '#bamboo-alerts'
}

static String getBuildReport(currentBuild) {
  AbstractTestResultAction testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
  def testReport = getTestReport(testResultAction)
  def changeLog = getChangeLog(currentBuild.changeSets)
  return [
    "```${testReport}```\n",
    "_Change Log_",
    "${changeLog}",
    "_Time taken: ${currentBuild.durationString}_"].join("\n")
}

static String getArtifactReport(env, params) {
  if (!shouldPublishArtifacts(env, params)) {
    return ""
  }
  return "${env.BUILD_VERSION}"
}

static String getTestReport(AbstractTestResultAction testResultAction) {
  if (testResultAction == null) {
    return "Test results are unavailable (possible build failure)"
  }
  def total = testResultAction.totalCount
  def failed = testResultAction.failCount
  def skipped = testResultAction.skipCount
  def passed = total - failed - skipped
  return "Passed: ${passed}, Failed: ${failed} ${testResultAction.failureDiffString}, Skipped: ${skipped}"
}

static String getChangeLog(changeLogSets) {
  if (changeLogSets.size() == 0) {
    return "`No changes detected. Build was triggered by branch indexing.`"
  }
  return changeLogSets.collect { it.items }.flatten().collect { commit ->
    def shortHash = "${commit.commitId}".substring(0, 7)
    def author = "No Reply".equalsIgnoreCase("${commit.author}") ? "" : " *by ${commit.author}*"
    return "`${shortHash}` ${commit.msg} ${author}"
  }.join('\n')
}

static String getJenkinsLink(env) {
  def shortHash = "${env.GIT_COMMIT}".substring(0, 7)
  def jenkinsUrlLabel = "${env.JOB_NAME} [${env.BUILD_DISPLAY_NAME} =&gt; ${shortHash}]"
  def blueOceanUrl = getBlueOceanUrl("${env.BUILD_URL}")
  return "<${blueOceanUrl}|${jenkinsUrlLabel}>"
}

static String getBlueOceanUrl(String jenkinsUrl) {
  return jenkinsUrl.replaceFirst("job", "blue/organizations/jenkins").replaceFirst("job", "detail")
}
