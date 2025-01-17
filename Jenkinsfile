pipeline {
    agent any
    environment {
        SCRIPT_PATH = '/var/jenkins_home/nangpago'
        SPRING_DIR = 'NangPaGo-be'
        REACT_DIR = 'NangPaGo-fe'
        DISCORD_WEBHOOK = credentials('discord-webhook')
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
                script {
                    def version = sh(script: "cd ${SPRING_DIR} && ./gradlew properties -q | grep 'version:' | awk '{print \$2}'", returnStdout: true).trim()

                    // 환경변수로 버전 설정
                    env.APP_VERSION = version
                }
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
    post {
        success {
            discordSend description: """
                🎉 **빌드 및 배포 성공**

                **프로젝트**: ${env.JOB_NAME}
                **빌드 번호**: #${env.BUILD_NUMBER}
                **버전**: ${env.APP_VERSION}
                **소요 시간**: ${currentBuild.durationString}
                """,
                link: env.BUILD_URL,
                result: currentBuild.currentResult,
                title: "NangPaGo 빌드/배포 성공",
                webhookURL: DISCORD_WEBHOOK
        }

        failure {
            discordSend description: """
                ❌ **빌드 또는 배포 실패**

                **프로젝트**: ${env.JOB_NAME}
                **빌드 번호**: #${env.BUILD_NUMBER}
                **버전**: ${env.APP_VERSION}
                **소요 시간**: ${currentBuild.durationString}
                """,
                link: env.BUILD_URL,
                result: currentBuild.currentResult,
                title: "NangPaGo 빌드/배포 실패",
                webhookURL: DISCORD_WEBHOOK
        }
    }
}
