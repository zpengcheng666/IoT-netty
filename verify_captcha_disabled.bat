@echo off
echo ======================================
echo 验证验证码功能禁用情况
echo ======================================
echo.

echo 1. 检查 SysConfigServiceImpl.selectCaptchaEnabled() 方法...
findstr /n "return false" fastbee-service\fastbee-system-service\src\main\java\com\fastbee\system\service\impl\SysConfigServiceImpl.java
if %errorlevel%==0 (
    echo [✓] 验证码配置开关已禁用
) else (
    echo [×] 验证码配置开关未禁用
)
echo.

echo 2. 检查 SysLoginService.login() 方法...
findstr /n "开源版本：禁用验证码校验" fastbee-service\fastbee-system-service\src\main\java\com\fastbee\system\service\sys\SysLoginService.java
if %errorlevel%==0 (
    echo [✓] 登录验证码校验已禁用
) else (
    echo [×] 登录验证码校验未禁用
)
echo.

echo 3. 检查 SysLoginService.validateCaptcha() 方法...
findstr /n "跳过验证码校验，直接通过" fastbee-service\fastbee-system-service\src\main\java\com\fastbee\system\service\sys\SysLoginService.java
if %errorlevel%==0 (
    echo [✓] 登录验证码校验方法已禁用
) else (
    echo [×] 登录验证码校验方法未禁用
)
echo.

echo 4. 检查 ToolServiceImpl.register() 方法...
findstr /n "开源版本：禁用验证码校验" fastbee-service\fastbee-iot-service\src\main\java\com\fastbee\iot\service\impl\ToolServiceImpl.java
if %errorlevel%==0 (
    echo [✓] 注册验证码校验已禁用
) else (
    echo [×] 注册验证码校验未禁用
)
echo.

echo 5. 检查 SysRegisterService.register() 方法...
findstr /n "开源版本：禁用验证码校验" fastbee-service\fastbee-system-service\src\main\java\com\fastbee\system\service\sys\SysRegisterService.java
if %errorlevel%==0 (
    echo [✓] 系统注册验证码校验已禁用
) else (
    echo [×] 系统注册验证码校验未禁用
)
echo.

echo ======================================
echo 验证完成！
echo 如果所有项都显示 [✓]，说明验证码功能已完全禁用。
echo ======================================
pause 