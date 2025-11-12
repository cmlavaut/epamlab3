def call(Map config = [:]) {
    def image_dock= config.get ('image')
    def triggerDeploy = config.get('triggerDeploy', false)
    def extraArgs = config.get('extraArgs', '') 

    stage('Login Docker Hub') {
        steps {
            withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                        sh "echo $PASS | docker login -u $USER --password-stdin"
            }
        }
    }
    stage('Push Image') {
        steps {
                    sh "docker push ${image_dock}"
        }
    }
    
    if (triggerDeploy) {
        stage('Trigger Deploy Pipeline') {
            echo "Deploying container from image: ${image_dock}"
            sh """
            docker stop ${image_dock} || true
            docker rm ${image_dock} || true
            docker run -d --name ${image_dock} ${extraArgs} ${image_dock}
            """
        }
    }
}
