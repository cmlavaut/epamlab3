def call(Map config) {
    def image_dock= config.get('image')
    def extraArgs = config.get('extraArgs', '') 

    echo "Iniciando Pipeline para image: ${image_dock}"
    
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
