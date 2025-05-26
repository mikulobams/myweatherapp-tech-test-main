pipeline {
    agent any
    
    
    // agent {
    //     docker {
    //         image 'openjdk:17-jdk-alpine'
    //     }
    // }
    

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
                    ./mvnw clean compile -DskipTests=true
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
                sh '''
                    ./mvnw clean package -DskipTests=true
                '''
            }
        }
        
        stage('Docker build and push') {
            steps {
                script{
                    withDockerRegistry(credentialsId: '2ff46f8f-0f21-46ea-91cd-65d733893dfe') {
                        sh 'docker build -t weather-app -f Dockerfile .'
                        sh 'docker tag weather-app mikulobams/weather-app:latest'
                        sh 'docker push mikulobams/weather-app:latest'
                    }     
                }
            }
        }      
    }
}
