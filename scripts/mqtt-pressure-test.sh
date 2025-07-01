#!/bin/bash

# FastBee MQTT压力测试脚本
# 用于测试10万台设备并发连接

# 配置参数
MQTT_HOST=${1:-"177.7.0.12"}
MQTT_PORT=${2:-"1883"}
USERNAME=${3:-"fastbee"}
PASSWORD=${4:-"fastbee"}
LOG_DIR="./test-logs"

# 创建日志目录
mkdir -p ${LOG_DIR}

echo "==================== FastBee MQTT压力测试 ===================="
echo "MQTT服务器: ${MQTT_HOST}:${MQTT_PORT}"
echo "测试用户: ${USERNAME}"
echo "日志目录: ${LOG_DIR}"
echo "=============================================================="

# 检查emqtt-bench是否存在
if ! command -v emqtt_bench &> /dev/null; then
    echo "错误: emqtt_bench 未安装，请先安装 emqtt-bench"
    echo "下载地址: https://github.com/emqx/emqtt-bench/releases"
    exit 1
fi

# 函数：运行连接测试
run_connection_test() {
    local connections=$1
    local interval=$2
    local test_name=$3
    
    echo ""
    echo "开始测试: ${test_name} (${connections}个连接, 间隔${interval}ms)"
    echo "开始时间: $(date '+%Y-%m-%d %H:%M:%S')"
    
    # 运行连接测试
    timeout 300 emqtt_bench conn \
        -h ${MQTT_HOST} \
        -p ${MQTT_PORT} \
        -c ${connections} \
        -i ${interval} \
        -u ${USERNAME} \
        -P ${PASSWORD} \
        -k 60 \
        --log-level info \
        2>&1 | tee ${LOG_DIR}/conn_test_${connections}.log
    
    local exit_code=$?
    echo "结束时间: $(date '+%Y-%m-%d %H:%M:%S')"
    
    if [ $exit_code -eq 0 ]; then
        echo "✓ ${test_name} 测试成功"
    else
        echo "✗ ${test_name} 测试失败 (退出码: $exit_code)"
    fi
    
    # 等待5秒后继续下一个测试
    sleep 5
}

# 函数：运行发布测试
run_publish_test() {
    local connections=$1
    local rate=$2
    local duration=$3
    local test_name=$4
    
    echo ""
    echo "开始测试: ${test_name} (${connections}个连接, ${rate}消息/秒, 持续${duration}秒)"
    echo "开始时间: $(date '+%Y-%m-%d %H:%M:%S')"
    
    # 运行发布测试
    timeout $((duration + 30)) emqtt_bench pub \
        -h ${MQTT_HOST} \
        -p ${MQTT_PORT} \
        -c ${connections} \
        -u ${USERNAME} \
        -P ${PASSWORD} \
        -t "/fastbee/test/%i" \
        -s 1024 \
        -q 0 \
        -r ${rate} \
        -d ${duration} \
        --log-level info \
        2>&1 | tee ${LOG_DIR}/pub_test_${connections}_${rate}.log
    
    local exit_code=$?
    echo "结束时间: $(date '+%Y-%m-%d %H:%M:%S')"
    
    if [ $exit_code -eq 0 ]; then
        echo "✓ ${test_name} 测试成功"
    else
        echo "✗ ${test_name} 测试失败 (退出码: $exit_code)"
    fi
    
    sleep 5
}

# 函数：运行订阅测试
run_subscribe_test() {
    local connections=$1
    local test_name=$2
    
    echo ""
    echo "开始测试: ${test_name} (${connections}个订阅连接)"
    echo "开始时间: $(date '+%Y-%m-%d %H:%M:%S')"
    
    # 运行订阅测试
    timeout 300 emqtt_bench sub \
        -h ${MQTT_HOST} \
        -p ${MQTT_PORT} \
        -c ${connections} \
        -u ${USERNAME} \
        -P ${PASSWORD} \
        -t "/fastbee/test/+" \
        -q 0 \
        -k 60 \
        --log-level info \
        2>&1 | tee ${LOG_DIR}/sub_test_${connections}.log &
    
    local sub_pid=$!
    
    # 等待订阅建立
    sleep 10
    
    # 发送一些测试消息
    emqtt_bench pub \
        -h ${MQTT_HOST} \
        -p ${MQTT_PORT} \
        -c 10 \
        -u ${USERNAME} \
        -P ${PASSWORD} \
        -t "/fastbee/test/data" \
        -s 512 \
        -q 0 \
        -r 10 \
        -d 30 \
        --log-level info \
        2>&1 | tee ${LOG_DIR}/sub_pub_test.log
    
    # 停止订阅
    sleep 5
    kill $sub_pid 2>/dev/null
    
    echo "结束时间: $(date '+%Y-%m-%d %H:%M:%S')"
    echo "✓ ${test_name} 测试完成"
    
    sleep 5
}

# 函数：生成测试报告
generate_report() {
    local report_file="${LOG_DIR}/test_report_$(date '+%Y%m%d_%H%M%S').txt"
    
    echo "================ FastBee MQTT压力测试报告 ================" > ${report_file}
    echo "测试时间: $(date '+%Y-%m-%d %H:%M:%S')" >> ${report_file}
    echo "MQTT服务器: ${MQTT_HOST}:${MQTT_PORT}" >> ${report_file}
    echo "" >> ${report_file}
    
    echo "测试结果汇总:" >> ${report_file}
    for log_file in ${LOG_DIR}/*.log; do
        if [ -f "$log_file" ]; then
            echo "文件: $(basename $log_file)" >> ${report_file}
            # 提取关键指标
            grep -E "(connected|published|received|error|failed)" "$log_file" | tail -5 >> ${report_file}
            echo "" >> ${report_file}
        fi
    done
    
    echo "测试报告已生成: ${report_file}"
}

# 主测试流程
main() {
    echo "开始FastBee MQTT压力测试流程..."
    
    # 1. 基础连接测试
    echo ""
    echo "========== 第一阶段：基础连接测试 =========="
    run_connection_test 1000 50 "1K设备连接测试"
    run_connection_test 5000 20 "5K设备连接测试"
    run_connection_test 10000 10 "1万设备连接测试"
    
    # 2. 中等规模连接测试
    echo ""
    echo "========== 第二阶段：中等规模连接测试 =========="
    run_connection_test 20000 5 "2万设备连接测试"
    run_connection_test 50000 3 "5万设备连接测试"
    
    # 3. 大规模连接测试
    echo ""
    echo "========== 第三阶段：大规模连接测试 =========="
    run_connection_test 80000 2 "8万设备连接测试"
    run_connection_test 100000 1 "10万设备连接测试"
    
    # 4. 消息发布测试
    echo ""
    echo "========== 第四阶段：消息发布测试 =========="
    run_publish_test 1000 100 60 "1K设备发布测试"
    run_publish_test 5000 50 60 "5K设备发布测试"
    run_publish_test 10000 20 60 "1万设备发布测试"
    
    # 5. 订阅测试
    echo ""
    echo "========== 第五阶段：订阅测试 =========="
    run_subscribe_test 1000 "1K设备订阅测试"
    run_subscribe_test 5000 "5K设备订阅测试"
    
    # 6. 混合场景测试
    echo ""
    echo "========== 第六阶段：混合场景测试 =========="
    echo "启动订阅连接..."
    emqtt_bench sub \
        -h ${MQTT_HOST} \
        -p ${MQTT_PORT} \
        -c 5000 \
        -u ${USERNAME} \
        -P ${PASSWORD} \
        -t "/fastbee/+/data" \
        -q 0 \
        --log-level info \
        2>&1 | tee ${LOG_DIR}/mixed_sub_test.log &
    
    local sub_pid=$!
    sleep 10
    
    echo "启动发布连接..."
    emqtt_bench pub \
        -h ${MQTT_HOST} \
        -p ${MQTT_PORT} \
        -c 5000 \
        -u ${USERNAME} \
        -P ${PASSWORD} \
        -t "/fastbee/%i/data" \
        -s 512 \
        -q 0 \
        -r 50 \
        -d 120 \
        --log-level info \
        2>&1 | tee ${LOG_DIR}/mixed_pub_test.log
    
    # 停止订阅
    kill $sub_pid 2>/dev/null
    echo "✓ 混合场景测试完成"
    
    # 生成测试报告
    echo ""
    echo "========== 生成测试报告 =========="
    generate_report
    
    echo ""
    echo "========== FastBee MQTT压力测试完成 =========="
    echo "所有日志文件保存在: ${LOG_DIR}"
}

# 脚本使用说明
usage() {
    echo "用法: $0 [MQTT_HOST] [MQTT_PORT] [USERNAME] [PASSWORD]"
    echo ""
    echo "参数说明:"
    echo "  MQTT_HOST    MQTT服务器地址 (默认: 177.7.0.12)"
    echo "  MQTT_PORT    MQTT服务器端口 (默认: 1883)"
    echo "  USERNAME     MQTT用户名 (默认: fastbee)"
    echo "  PASSWORD     MQTT密码 (默认: fastbee)"
    echo ""
    echo "示例:"
    echo "  $0                                    # 使用默认参数"
    echo "  $0 192.168.1.100 1883 admin admin123 # 使用自定义参数"
}

# 检查参数
if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
    usage
    exit 0
fi

# 执行主流程
main

echo "测试完成！" 