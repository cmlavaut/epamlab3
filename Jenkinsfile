@Library('epamlab3') _


pipeline {
  agent any
  stages {
    stage('Test') {
      steps {
        script {
          dockerPipeline(
            image: 'test:latest', 
            triggerDeploy: false,
            extraArgs: "-p 3000:3000")
        }
      }
    }
  }
}