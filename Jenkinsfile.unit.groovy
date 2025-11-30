pipeline {
    agent {
        label 'docker'
    }

    stages {

        stage('Source') {
            steps {
                git 'https://github.com/FadedGuy/MasterDevOps-CICD-Act3'
            }
        }

        stage('Build') {
            steps {
                echo 'Building stage...'
                sh 'make build'
            }
        }

        stage('Unit tests') {
            steps {
                echo 'Running unit tests...'
                sh 'make test-unit'

                echo "Archiving coverage results..."
                archiveArtifacts artifacts: 'results/coverage*', fingerprint: true
            }
        }

        stage('API tests') {
            steps {
                echo 'Running API tests...'
                sh 'make test-api'     
            }
        }

        stage('E2E tests') {
            steps {
                echo 'Running E2E tests...'
                sh 'make test-e2e'      
            }
        }
    }

    post {

        always {
            echo "Archiving and publishing test results..."
            archiveArtifacts artifacts: 'results/*result.xml', fingerprint: true
            junit 'results/*_result.xml'
        }

        failure {
            mail to: "${EMAIL_RECIPIENT}",
                 subject: "Pipeline FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: """\
                    Pipeline has failed.

                    Job: ${env.JOB_NAME}
                    Build Number: #${env.BUILD_NUMBER}

                    Check Stage View for more details.
                    """
        }
    }
}
