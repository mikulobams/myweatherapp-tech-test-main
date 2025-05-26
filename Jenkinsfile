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

        stage('Build') {
            steps {
                // Run Maven on a Unix agent.
                sh '''
                    chmod +x mvnw
                    ./mvnw clean package
                '''

                // To run Maven on a Windows agent, use:
                // bat "mvn -Dmaven.test.failure.ignore=true clean package"
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
    }
}
