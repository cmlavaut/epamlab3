def call(Map config = [:]) {
    def image_dock= config.get('image')
    def triggerDeploy = config.get('triggerDeploy', false)
    def extraArgs = config.get('extraArgs', '') 

    echo "Iniciando Pipeline para image: ${image_dock}"

    stage('Login Docker Hub') {
        try {
            withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
             sh "echo $PASS | docker login -u $USER --password-stdin"
            }
        }
        catch (err) {
            echo "Error: ${err}"
        }        
    }

    stage('Push Image') {
        try {
            sh "docker push ${image_dock}"
            echo "Imagen en el DockerHub"
        }
        catch (err){
            echo "error en la imagen enviada: ${err}"
        }
        
    }
    
    if (triggerDeploy) {
        stage('Trigger Deploy Pipeline') {
            try {
                echo "Deploying container from image: ${image_dock}"
                sh """
                docker stop ${image_dock} || true
                docker rm ${image_dock} || true
                docker run -d --name ${image_dock} ${extraArgs} ${image_dock}
                """  
            } catch (err) {
                echo "Error en el despliegue: ${err}"
            }
        }
    }
}
