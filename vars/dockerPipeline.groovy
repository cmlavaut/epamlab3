def call(Map config) {
    pipeline {
        agent any

        environment {
            IMAGE_NAME = config.imageName
            BRANCH_NAME = env.BRANCH_NAME
        }

        stages {

            stage('Login Docker Hub') {
                steps {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                        sh "echo $PASS | docker login -u $USER --password-stdin"
                    }
                }
            }

            stage('Push Image') {
                steps {
                    sh "docker push ${IMAGE_NAME}"
                }
            }

            stage('Trigger Deploy Pipeline') {
                when { expression { return config.triggerDeploy } }
                steps {
                    script {
                        if (BRANCH_NAME == "master") {
                            build job: "Deploy_to_master", parameters: [string(name: 'IMAGE_NAME', value: IMAGE_NAME)]
                        } else if (BRANCH_NAME == "dev") {
                            build job: "Deploy_to_dev", parameters: [string(name: 'IMAGE_NAME', value: IMAGE_NAME)]
                        }
                    }
                }
            }
        }
    }
}