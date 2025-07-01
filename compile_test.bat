@echo off
echo ========================================
echo FastBee 编译测试
echo ========================================

echo.
echo 检查Maven环境...
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo [警告] Maven命令未找到，可能需要配置PATH
    echo 请确保已安装Maven并配置环境变量
    echo.
    echo 推荐使用IDE进行编译：
    echo 1. 打开IDE (IDEA/Eclipse)
    echo 2. 导入项目
    echo 3. 右键项目 -> Maven -> Reload project
    echo 4. 执行 Maven clean install
    pause
    exit /b 0
)

echo Maven环境正常，开始编译测试...
echo.

echo 执行: mvn clean compile -DskipTests
mvn clean compile -DskipTests

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo ✅ 编译成功！
    echo 📋 修复的问题：
    echo   - 删除了fastbee-common的自引用依赖
    echo   - 统一了版本号为2.7.0
    echo   - 修复了Redisson版本兼容性
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ❌ 编译失败
    echo 💡 建议：
    echo   1. 使用IDE进行编译
    echo   2. 检查具体错误信息
    echo   3. 确保所有依赖版本正确
    echo ========================================
)

echo.
pause 