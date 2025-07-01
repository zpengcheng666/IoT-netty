@echo off
echo 正在检查Maven依赖...

REM 检查Maven是否安装
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo Maven未安装或不在PATH中，请使用IDE进行编译
    pause
    exit /b 1
)

echo Maven已找到，开始编译...
mvn clean compile -DskipTests

if %errorlevel% equ 0 (
    echo 编译成功！
) else (
    echo 编译失败，请检查错误信息
)

pause 