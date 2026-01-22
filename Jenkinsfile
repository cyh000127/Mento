pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
  }

  stages {
    stage('Context') {
      steps {
        echo "===== CI TEST CONTEXT ====="
        sh 'date "+%Y-%m-%d %H:%M:%S %Z"'
        echo "JOB_NAME=${env.JOB_NAME}"
        echo "BUILD_NUMBER=${env.BUILD_NUMBER}"
        echo "BUILD_URL=${env.BUILD_URL}"
        echo "BRANCH_NAME=${env.BRANCH_NAME}"
        sh 'pwd'
        sh 'uname -a || true'
        sh 'git --version || true'
      }
    }

    stage('Git info') {
      steps {
        echo "===== GIT INFO ====="
        sh 'git rev-parse --abbrev-ref HEAD || true'
        sh 'git rev-parse HEAD || true'
        sh 'git log -1 --pretty=format:"%h | %an | %ad | %s" --date=iso || true'
      }
    }

    stage('What changed?') {
      steps {
        echo "===== CHANGED FILES (last commit) ====="
        sh '''
          set +e
          git show --name-status --pretty=format:"" -1
          echo ""
          echo "===== LAST 20 FILES IN REPO ROOT ====="
          ls -al | head -n 20
          exit 0
        '''
      }
    }

    stage('Smoke') {
      steps {
        echo "===== SMOKE TEST ====="
        echo "Webhook/SCM trigger CI test pipeline executed."
      }
    }
  }

  post {
    success {
      echo "SUCCESS"
      sh 'date "+%Y-%m-%d %H:%M:%S %Z"'
    }
    failure {
      echo "FAILURE"
      sh 'date "+%Y-%m-%d %H:%M:%S %Z"'
    }
    always {
      echo "DONE"
    }
  }
}
