@echo off
cd /d %~dp0\..
call gradlew build
if errorlevel 1 exit /b 1
attrib +r my.cnf
docker compose down -v --rmi all
if errorlevel 1 exit /b 1
docker-compose up -d --build
