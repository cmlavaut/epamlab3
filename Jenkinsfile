// Jenkinsfile (para multibranch CICD)
@Library('epamlab3') _

pipeline {
  agent any
  environment {
    // por defecto, la variable PORT será seteada en 'script' según BRANCH_NAME
    IMAGE_NAME = "myapp-${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
  }

  stages {
    stage('Prepare') {
      steps {
        script {
          if (env.BRANCH_NAME == 'master') {
            env.APP_PORT = '3000'
          } else if (env.BRANCH_NAME == 'dev') {
            env.APP_PORT = '3001'
          } else {
            env.APP_PORT = '3001' // default
          }
          echo "Branch: ${env.BRANCH_NAME} -> PORT=${env.APP_PORT}"
        }
      }
    }

    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build') {
      steps {
        // Ajusta a tu build: ejemplo Node.js
        sh '''
          if [ -f package.json ]; then
            npm ci
            npm run build || true
          fi
        '''
      }
    }

    stage('Test') {
      steps {
        sh '''
          if [ -f package.json ]; then
            npm test --silent || echo "tests may have failed"
          fi
        '''
      }
    }

    stage('Build Docker Image') {
      steps {
        script {
          sh "docker build -t ${IMAGE_NAME} -f ./Dockerfile ."
        }
      }
    }
    
    stage ('Push and Deploy') {
      steps {
        dockerPipeline(
          image: "${IMAGE_NAME}",
          triggerDeploy: true
        )
      }
    }
  
  } 
}

post {
    always {
      echo "Pipeline finalizado para ${env.BRANCH_NAME}"
    }
}