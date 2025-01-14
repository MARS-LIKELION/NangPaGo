pipeline {
    agent any
    environment {
        SCRIPT_PATH = '/var/jenkins_home/nangpago'
        SPRING_DIR = 'NangPaGo-be'
        REACT_DIR = 'NangPaGo-fe'
    }
    tools {
        gradle 'gradle-8.11'
        nodejs 'node-18-alpine'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Create Config Files') {
            steps {
                withCredentials([file(credentialsId: 'env-file', variable: 'ENV_FILE')]) {
                    sh '''
                        mkdir -p ./${SPRING_DIR}/src/main/resources
                        cp $ENV_FILE ./${SPRING_DIR}/src/main/resources/.env
                        cp $ENV_FILE ./${REACT_DIR}/.env
                    '''
                }
            }
        }
        stage('Create Firebase Config') {
            steps {
                withCredentials([file(credentialsId: 'FIREBASE_KEY_FILE', variable: 'FIREBASE_KEY_PATH')]) {
                    sh '''
                        mkdir -p ./${SPRING_DIR}/src/main/resources/firebase
                        cp $FIREBASE_KEY_PATH ./${SPRING_DIR}/src/main/resources/firebase/nangpago.json
                    '''
                }
            }
        }
        stage('Prepare'){
            steps {
                dir(SPRING_DIR) {
                    sh 'gradle clean'
                }
                dir(REACT_DIR) {
                    sh 'npm install'
                }
            }
        }
        stage('Build') {
            steps {
                dir(SPRING_DIR) {
                    sh 'gradle build -x test'
                }
                dir(REACT_DIR) {
                    sh 'npm run build'
                }
            }
        }
        stage('Deploy') {
            steps {
                sh '''
                    mkdir -p ${SCRIPT_PATH}/NangPaGo-fe
                    mkdir -p ${SCRIPT_PATH}/NangPaGo-be

                    # 도커 파일 복사
                    cp ./docker/docker-compose.blue.yml ${SCRIPT_PATH}/
                    cp ./docker/docker-compose.green.yml ${SCRIPT_PATH}/

                    # 프론트엔드 파일 복사
                    cp -r ./${REACT_DIR}/* ${SCRIPT_PATH}/NangPaGo-fe/

                    # 백엔드 파일 복사
                    cp ./${SPRING_DIR}/build/libs/*.jar ${SCRIPT_PATH}/NangPaGo-be/
                    cp ./${SPRING_DIR}/Dockerfile-be ${SCRIPT_PATH}/NangPaGo-be/

                    cp ./scripts/deploy.sh ${SCRIPT_PATH}/
                    chmod +x ${SCRIPT_PATH}/deploy.sh
                '''

                withCredentials([file(credentialsId: 'env-file', variable: 'ENV_FILE')]) {
                    sh '''
                        cp $ENV_FILE ${SCRIPT_PATH}/.env
                        cp $ENV_FILE ${SCRIPT_PATH}/NangPaGo-fe/.env
                        ${SCRIPT_PATH}/deploy.sh
                    '''
                }
            }
        }
    }
}
