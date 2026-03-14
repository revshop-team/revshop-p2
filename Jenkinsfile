pipeline {

    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK21'
    }

    stages {

        stage('Clone Repository') {
            steps {
                git 'https://github.com/revshop-team/revshop-p2.git'
            }
        }

        stage('Build Application') {
            steps {
                bat 'mvn clean compile'
            }
        }

        stage('Run Tests & Generate Coverage') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Publish JaCoCo Report') {
            steps {
                jacoco execPattern: 'target/jacoco.exec',
                       classPattern: 'target/classes',
                       sourcePattern: 'src/main/java',
                       exclusionPattern: ''
            }
        }

        stage('Package Application') {
            steps {
                bat 'mvn package'
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar'
            }
        }

    }
}