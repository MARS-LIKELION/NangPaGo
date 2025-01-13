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
                        mkdir -p ./src/main/resources/firebase
                        cp $FIREBASE_KEY_PATH ./src/main/resources/firebase/nangpago.json
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
//         stage('Test') {
//             steps {
//                 dir(SPRING_DIR) {
//                     sh 'gradle test'
//                 }
//                 dir(REACT_DIR) {
//                     sh 'npm test -- --watchAll=false' // React 테스트
//                 }
//             }
//         }
        stage('Deploy') {
            steps {
                sh '''
                    mkdir -p ${SCRIPT_PATH}

                    # 도커 관련 파일들 복사
                    cp ./docker/docker-compose.blue.yml ${SCRIPT_PATH}/
                    cp ./docker/docker-compose.green.yml ${SCRIPT_PATH}/
                    cp ./${SPRING_DIR}/Dockerfile-be ${SCRIPT_PATH}/
                    cp ./${REACT_DIR}/Dockerfile-fe ${SCRIPT_PATH}/

                    # 배포 스크립트와 빌드 결과물 복사
                    cp ./scripts/deploy.sh ${SCRIPT_PATH}/
                    cp ./${SPRING_DIR}/build/libs/*.jar ${SCRIPT_PATH}/
                    cp -r ./${REACT_DIR}/dist ${SCRIPT_PATH}/

                    chmod +x ${SCRIPT_PATH}/deploy.sh
                    ${SCRIPT_PATH}/deploy.sh
                '''

                withCredentials([file(credentialsId: 'env-file', variable: 'ENV_FILE')]) {
                    sh '''
                        cp $ENV_FILE ${SCRIPT_PATH}/.env
                        ${SCRIPT_PATH}/deploy.sh
                    '''
                }
            }
        }
    }
}
