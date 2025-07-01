@echo off
chcp 65001 >nul
echo ===========================================
echo FastBee 启动问题诊断脚本
echo ===========================================
echo.

echo [1] 检查 Java 环境...
java -version
if %ERRORLEVEL% neq 0 (
    echo ❌ Java 未安装或未配置到 PATH
    pause
    exit /b 1
)
echo ✅ Java 环境正常
echo.

echo [2] 检查 MySQL 连接...
mysql -uroot -p123456 -h localhost -P 3306 -e "SELECT 1;" 2>nul
if %ERRORLEVEL% neq 0 (
    echo ❌ MySQL 连接失败，请检查：
    echo    - MySQL 服务是否启动
    echo    - 用户名密码是否正确 (root/123456)
    echo    - 端口是否为 3306
    echo    - fastbee 数据库是否存在
) else (
    echo ✅ MySQL 连接正常
)
echo.

echo [3] 检查 Redis 连接...
redis-cli -h localhost -p 6379 ping 2>nul
if %ERRORLEVEL% neq 0 (
    echo ⚠️  Redis 连接失败，但应用可以继续启动
    echo    建议检查 Redis 服务是否启动
) else (
    echo ✅ Redis 连接正常
)
echo.

echo [4] 检查端口占用...
netstat -ano | findstr :8080
if %ERRORLEVEL% equ 0 (
    echo ⚠️  端口 8080 被占用，可能需要更换端口
) else (
    echo ✅ 端口 8080 可用
)
echo.

echo [5] 使用简化配置启动 FastBee...
echo.
echo 选择启动方式：
echo [1] 正常启动 (使用 dev 配置)
echo [2] 简化启动 (使用 dev-simple 配置，跳过部分外部依赖)
echo [3] 调试启动 (显示详细日志)
echo.
set /p choice="请输入选择 (1-3): "

if "%choice%"=="1" (
    echo 正常启动中...
    java -jar fastbee-admin/target/fastbee-admin.jar --spring.profiles.active=dev
) else if "%choice%"=="2" (
    echo 简化启动中...
    java -jar fastbee-admin/target/fastbee-admin.jar --spring.profiles.active=dev-simple
) else if "%choice%"=="3" (
    echo 调试启动中...
    java -jar fastbee-admin/target/fastbee-admin.jar --spring.profiles.active=dev --logging.level.root=DEBUG --debug
) else (
    echo 无效选择，使用默认启动方式...
    java -jar fastbee-admin/target/fastbee-admin.jar --spring.profiles.active=dev
)

echo.
echo 如果启动失败，请检查上述环境配置
pause 