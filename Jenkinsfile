pipeline {
    agent {
        docker {
            image 'openjdk:17-jdk-alpine'
        }
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', 
                    url: 'https://github.com/mikulobams/myweatherapp-tech-test-main.git'
            }
        }

        stage('Compile') {
            steps {
                // Run Maven on a Unix agent.
                sh '''
                    chmod +x mvnw
                    ./mvnw clean compile
                '''
            }
        }

        stage('Test') {
            steps {
                sh './mvnw test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build') {
            steps {
                // Run Maven on a Unix agent.
                sh '''
                    chmod +x mvnw
                    ./mvnw clean package
                '''
            }
        }

        stage('docker build and push') {
            steps {
                
            }
        }

        stage('docker run') {
            steps {
                
            }
        }
    }
}
