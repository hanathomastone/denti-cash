pipeline {
  agent any

  tools {
    jdk("java-17-amazon-corretto")
  }

  environment {
    IMAGE_NAME = 'dentix'
    SSH_CONNECTION = 'ncloud@118.67.154.179'
    SSH_CONNECTION_CREDENTIAL = 'ncp-dt2023-api-ssh'
  }

  stages {

    stage('Clean Build') {
      steps {
        sh "SPRING_PROFILES_ACTIVE=dev ./gradlew clean build"
      }
    }

    stage('Build Docker Image') {
      steps {
        script {
          docker.build("${IMAGE_NAME}", "--build-arg SPRING_PROFILES_ACTIVE=${BRANCH_NAME} --build-arg CONFIG_SERVER_ENCRYPT_KEY=${CONFIG_SERVER_ENCRYPT_KEY} --build-arg CONFIG_SERVER_USERNAME=${CONFIG_SERVER_USERNAME} --build-arg CONFIG_SERVER_PASSWORD=${CONFIG_SERVER_PASSWORD} .")
        }
      }
    }

    stage('Save Docker Image') {
      steps {
        sh "docker save -o ${IMAGE_NAME}.tar ${IMAGE_NAME}"
        sh "docker rmi ${IMAGE_NAME}:latest"
      }
    }

    stage('Transfer Docker Image') {
      steps {
        sshagent(credentials: [SSH_CONNECTION_CREDENTIAL]) {
          sh "scp ${IMAGE_NAME}.tar ${SSH_CONNECTION}:."
        }
        sh "rm ${IMAGE_NAME}.tar"
      }
    }

    stage('Load Docker Image') {
      steps {
        sshagent(credentials: [SSH_CONNECTION_CREDENTIAL]) {
          sh "ssh -o StrictHostKeyChecking=no ${SSH_CONNECTION} ' \
            if [[ -n \$(docker ps -a -q --filter \"ancestor=${IMAGE_NAME}\") ]]; then docker rm -f \$(docker ps -a -q --filter \"ancestor=${IMAGE_NAME}\"); fi; \
            if [[ -n \$(docker images -q ${IMAGE_NAME}:latest) ]]; then docker rmi ${IMAGE_NAME}:latest; fi; \
            docker load -i ${IMAGE_NAME}.tar; \
          '"
        }
      }
    }

    stage('Server Run') {
      steps {
        sshagent(credentials: [SSH_CONNECTION_CREDENTIAL]) {
          sh "ssh -o StrictHostKeyChecking=no ${SSH_CONNECTION} ' \
            docker run -d -p 8082:8080 ${IMAGE_NAME}; \
            rm ${IMAGE_NAME}.tar; \
          '"
        }
      }
    }
  }
}