@echo off
REM 检查 JAVA_HOME 是否已设置
if "%JAVA_HOME%"=="" (
    echo 请先配置 JAVA_HOME 环境变量，并指向 JDK 17 或更高版本
    pause
    exit /b 1
)

REM 显示 Java 版本
"%JAVA_HOME%\bin\java" -version

REM 用 Maven 启动 Spring Boot
cd /d "%~dp0"
mvn clean spring-boot:run

pause 