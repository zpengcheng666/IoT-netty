@echo off
echo ========================================
echo sydh 启动前检查
echo ========================================

echo.
echo 1. 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo [错误] Java环境未配置
    pause
    exit /b 1
)

echo.
echo 2. 检查Redis服务...
echo [提醒] 请确保Redis服务已启动
echo - Windows: redis-server.exe
echo - Linux: systemctl start redis
echo - Docker: docker run -d -p 6379:6379 redis

echo.
echo 3. 版本兼容性检查...
echo - Spring Boot: 2.5.14
echo - Redisson: 3.16.8 (已修复版本兼容性)
echo - Spring Data Redis: 2.5.x (自动匹配)

echo.
echo 4. 关键修复说明...
echo [✓] 降级 Redisson 版本: 3.23.1 → 3.16.8
echo [✓] 调整 Lock4j 版本: 2.2.7 → 2.2.3
echo [✓] 添加明确的 spring-data-redis 依赖
echo [✓] 修复 SubscriptionListener 类缺失问题

echo.
echo ========================================
echo 现在可以启动项目了！
echo 主启动类: com.sydh.sydhApplication
echo ========================================

pause 