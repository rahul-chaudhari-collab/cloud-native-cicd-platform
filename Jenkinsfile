
pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'
        ECR_REPO = '444166849721.dkr.ecr.us-east-1.amazonaws.com/devops06-app'
        EKS_CLUSTER = 'DevOps06-EKS-Cluster'
        K8S_DEPLOYMENT = 'devops06-app'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Java Application') {
            steps {
                dir('app') {
                    sh 'mvn clean package'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    dir('app') {
                        withCredentials([
                            string(
                                credentialsId: 'sonar-token',
                                variable: 'SONAR_TOKEN'
                            )
                        ]) {
                            sh '''
                                mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar  \
                                  -Dsonar.projectKey=cloud-native-cicd-platform \
                                  -Dsonar.host.url=$SONAR_HOST_URL \
                                  -Dsonar.token=$SONAR_TOKEN
                            '''
                        }
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir('app') {
                    sh 'docker build -t devops06-app:${BUILD_NUMBER} .'
                }
            }
        }

        stage('Login to Amazon ECR') {
            steps {
                sh '''
                    aws ecr get-login-password --region $AWS_REGION |
                    docker login --username AWS --password-stdin \
                    444166849721.dkr.ecr.us-east-1.amazonaws.com
                '''
            }
        }

        stage('Push Image to ECR') {
            steps {
                sh '''
                    docker tag devops06-app:${BUILD_NUMBER} ${ECR_REPO}:${BUILD_NUMBER}
                    docker push ${ECR_REPO}:${BUILD_NUMBER}
                '''
            }
        }

        stage('Deploy to EKS') {
            steps {
                sh '''
                    aws eks update-kubeconfig \
                      --region $AWS_REGION \
                      --name $EKS_CLUSTER

                    kubectl set image deployment/$K8S_DEPLOYMENT \
                      devops06-app=${ECR_REPO}:${BUILD_NUMBER}

                    kubectl rollout status deployment/$K8S_DEPLOYMENT \
                      --timeout=180s
                '''
            }
        }
    }

    post {
        success {
            echo 'Build, SonarQube analysis and deployment completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the Jenkins console output.'
        }
    }
}

