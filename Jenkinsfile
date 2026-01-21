pipeline {
  agent any
  options { timestamps() }

  stages {
    stage('Context') {
      steps {
        echo "JOB_NAME=${env.JOB_NAME}"
        echo "BRANCH_NAME=${env.BRANCH_NAME}"
        sh 'pwd'
        sh 'ls -al'
      }
    }

    stage('Infra check') {
      steps {
        sh 'ls -al Infra || true'
        echo "✅ TEST PIPELINE OK"
      }
    }
  }

  post {
    success { echo "✅ SUCCESS" }
    failure { echo "❌ FAILURE" }
    always  { echo "🧾 DONE" }
  }
}
