pipeline {
  agent any

  tools {
    jdk("java-17-amazon-corretto")
  }

  environment {
    SLACK_CHANNEL = "#ai-voucher-jenkins-deploy"
    SLACK_SUCCESS_COLOR = "#2C953C";
    SLACK_FAIL_COLOR = "#FF3232";
    IMAGE_NAME = 'dentix-api'
    SSH_CONNECTION_CREDENTIAL_DEV = 'kaii-dep-ssh'
    SSH_CONNECTION_CREDENTIAL_PROD = 'ai-voucher-ssh'
    IMAGE_STORAGE_CREDENTIAL = 'ncp-container-registry'
  }

  stages {

    stage('Set Environment') {
      post {
        success {
          slackSend(channel: SLACK_CHANNEL, color: SLACK_SUCCESS_COLOR, message: "==================================================================\n배포 파이프라인이 시작되었습니다.\n${JOB_NAME}(${BUILD_NUMBER})\n${GIT_COMMIT_AUTHOR} - ${GIT_COMMIT_MESSAGE}\n${BUILD_URL}")
        }
      }
      steps {
        script {
          switch(BRANCH_NAME) {
            case 'dev' :
              env.IMAGE_STORAGE = 'dentix-dev.ncr.gov-ntruss.com'
              env.SSH_CONNECTION = 'ubuntu@54.180.249.23'
              break
            case 'prod' :
              env.IMAGE_STORAGE = 'dentix-prod.ncr.gov-ntruss.com'
              env.SSH_CONNECTION = 'ncloud@175.106.93.58'
              break
          }
          GIT_COMMIT_AUTHOR = sh(script: "git --no-pager show -s --format=%an ${GIT_COMMIT}", returnStdout: true).trim();
          GIT_COMMIT_MESSAGE = sh(script: "git --no-pager show -s --format=%B ${GIT_COMMIT}", returnStdout: true).trim();
        }
      }
    }

    stage('Clean Build') {
      post {
        success {
          slackSend(channel: SLACK_CHANNEL, color: SLACK_SUCCESS_COLOR, message: 'Clean Build에 성공하였습니다.')
        }
        failure {
          slackSend(channel: SLACK_CHANNEL, color: SLACK_FAIL_COLOR, message: 'Clean Build에 실패하였습니다.\n==================================================================')
        }
      }
      steps {
        sh "chmod +x ./gradlew; \
            SPRING_PROFILES_ACTIVE=dev ./gradlew clean build; \
        "
      }
    }

    stage('Build Docker Image') {
      post {
        success {
          slackSend(channel: SLACK_CHANNEL, color: SLACK_SUCCESS_COLOR, message: 'Docker Image Build에 성공하였습니다.')
        }
        failure {
          slackSend(channel: SLACK_CHANNEL, color: SLACK_FAIL_COLOR, message: 'Docker Image Build에 실패하였습니다.\n==================================================================')
        }
      }
      steps {
        script {
          image = docker.build("${IMAGE_STORAGE}/${IMAGE_NAME}", "--build-arg SPRING_PROFILES_ACTIVE=${BRANCH_NAME} --build-arg CONFIG_SERVER_ENCRYPT_KEY=${CONFIG_SERVER_ENCRYPT_KEY} --build-arg CONFIG_SERVER_USERNAME=${CONFIG_SERVER_USERNAME} --build-arg CONFIG_SERVER_PASSWORD=${CONFIG_SERVER_PASSWORD} .")
        }
      }
    }

    stage('Push Docker Image') {
      post {
        success {
          slackSend(channel: SLACK_CHANNEL, color: SLACK_SUCCESS_COLOR, message: 'Docker Image Push에 성공하였습니다.')
        }
        failure {
          slackSend(channel: SLACK_CHANNEL, color: SLACK_FAIL_COLOR, message: 'Docker Image Push에 실패하였습니다.\n==================================================================')
        }
      }
      steps {
        script {
          docker.withRegistry("https://${IMAGE_STORAGE}", IMAGE_STORAGE_CREDENTIAL) {
            image.push("${BUILD_NUMBER}")
            image.push("latest")
          }
        }
      }
    }

    stage('Remove Docker Image') {
      steps {
        sh "docker system prune -a --volumes -f" // 사용 안하는 것 전부 삭제
      }
    }

    stage('Server Run') {
      post {
        success {
          slackSend(channel: SLACK_CHANNEL, color: SLACK_SUCCESS_COLOR, message: '배포에 성공하였습니다.\n==================================================================')
        }
        failure {
          slackSend(channel: SLACK_CHANNEL, color: SLACK_FAIL_COLOR, message: '배포에 실패하였습니다.\n==================================================================')
        }
      }
      steps {
        script {
            switch(BRANCH_NAME) {
                case 'dev' :
                    sshagent (credentials: [SSH_CONNECTION_CREDENTIAL_DEV]) {
                        sh "ssh -o StrictHostKeyChecking=no ${SSH_CONNECTION} 'IMAGE_NAME=${IMAGE_NAME} IMAGE_STORAGE=${IMAGE_STORAGE} BUILD_NUMBER=${BUILD_NUMBER} dentix-api/deploy.sh'"
                    }
                    break
                case 'prod' :
                    sshagent (credentials: [SSH_CONNECTION_CREDENTIAL_PROD]) {가
                        sh "ssh -o StrictHostKeyChecking=no ${SSH_CONNECTION} 'IMAGE_NAME=${IMAGE_NAME} IMAGE_STORAGE=${IMAGE_STORAGE} BUILD_NUMBER=${BUILD_NUMBER} dentix-api/deploy.sh'"
                    }
                    break
                    }
        }
      }
    }
  }
}