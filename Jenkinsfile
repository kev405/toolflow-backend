pipeline {
    agent any

    environment {
        // Variables de entorno para la aplicación, Docker y SSH
        APP_NAME = "toolflow-api"
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials-id')
        DOCKER_IMAGE = "kev405/toolflow-api"
        SSH_USER = "root"
        SSH_HOST = "62.171.188.201"
    }

    stages {
        stage('Checkout') {
            steps {
                // Clonar el repositorio que contiene el código fuente de la API en la rama main
                git branch: 'master', url: 'https://github.com/kev405/toolflow-backend.git'
            }
        }

        stage('Construir API') {
            steps {
                script {
                    echo "Asignando permisos de ejecución al archivo mvnw"
                }
                sh 'chmod +x mvnw'
                script {
                    echo "Construyendo la API Spring Boot con Maven"
                }
                sh './mvnw clean install package'
            }
        }

        stage('Ejecutar Pruebas') {
            steps {
                script {
                    echo "Asignando permisos de ejecución al archivo mvnw"
                }
                sh 'chmod +x mvnw'
                script {
                    echo "Construyendo la API Spring Boot con Maven"
                }
                sh './mvnw test'
            }
        }

        stage('Construir Imagen Docker') {
            steps {
                script {
                    echo "Construyendo la imagen Docker para la API Spring Boot"
                }
                // Construye la imagen Docker y la etiqueta como 'latest'
                sh 'docker build -t ${DOCKER_IMAGE}:latest .'
            }
        }

        stage('Login a DockerHub') {
            steps {
                script {
                    echo "Iniciando sesión en DockerHub"
                }
                // Autenticación en DockerHub utilizando las credenciales almacenadas en Jenkins
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
            }
        }

        stage('Push de Imagen Docker') {
            steps {
                script {
                    echo "Subiendo la imagen Docker a DockerHub"
                }
                // Envía la imagen Docker a DockerHub
                sh 'docker push ${DOCKER_IMAGE}:latest'
            }
        }

        stage('Desplegar en el Host') {
            steps {
                script {
                    echo "Desplegando la API en el host remoto utilizando sshpass"
                }
                // Se utiliza withCredentials para inyectar la contraseña SSH y sshpass para la conexión
                withCredentials([string(credentialsId: 'ssh-password-id', variable: 'SSH_PASSWORD')]) {
                    sh '''
                    sshpass -p "$SSH_PASSWORD" ssh -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} <<EOF
                    # Extraer la última imagen
                    docker pull ${DOCKER_IMAGE}:latest

                    # Eliminar imágenes anteriores (con tags diferentes a latest)
                    docker images ${DOCKER_IMAGE} --format "{{.Repository}}:{{.Tag}} {{.ID}}" | grep -v ":latest" | awk '{print $2}' | xargs -r docker rmi -f

                    # Detener y eliminar contenedores en ejecución (si existen)
                    docker stop ${APP_NAME} || true
                    docker rm ${APP_NAME} || true

                    # Iniciar un nuevo contenedor con la imagen actualizada
                    docker run -d --name ${APP_NAME} -p 9009:9009 ${DOCKER_IMAGE}:latest
                    EOF
                    '''
                }
            }
        }
    }
}
