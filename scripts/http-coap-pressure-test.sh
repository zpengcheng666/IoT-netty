#!/bin/bash

# FastBee HTTP/CoAP压力测试脚本
# 用于测试HTTP和CoAP协议性能

# 配置参数
HTTP_HOST=${1:-"177.7.0.13"}
HTTP_PORT=${2:-"8081"}
COAP_HOST=${3:-"177.7.0.13"}
COAP_PORT=${4:-"5683"}
USERNAME=${5:-"S-D1K031BE49X2-146-1:FastBee"}
PASSWORD=${6:-"PB82DDUP88P8GRPI"}
TEST_TYPE=${7:-"all"}
LOG_DIR="./test-logs"

# 创建日志目录
mkdir -p ${LOG_DIR}

echo "==================== FastBee HTTP/CoAP压力测试 ===================="
echo "HTTP服务器: ${HTTP_HOST}:${HTTP_PORT}"
echo "CoAP服务器: ${COAP_HOST}:${COAP_PORT}"
echo "测试用户: ${USERNAME}"
echo "测试类型: ${TEST_TYPE}"
echo "日志目录: ${LOG_DIR}"
echo "=================================================================="

# 检查测试工具
check_tools() {
    local missing_tools=()
    
    if ! command -v ab &> /dev/null; then
        missing_tools+=("apache2-utils (ab)")
    fi
    
    if ! command -v wrk &> /dev/null; then
        missing_tools+=("wrk")
    fi
    
    if ! command -v coap-client &> /dev/null; then
        missing_tools+=("libcoap-bin (coap-client)")
    fi
    
    if [ ${#missing_tools[@]} -gt 0 ]; then
        echo "警告: 以下工具未安装，相关测试将跳过："
        printf '  - %s\n' "${missing_tools[@]}"
        echo ""
    fi
}

# 准备测试数据
prepare_test_data() {
    # 创建HTTP测试数据文件
    cat > ${LOG_DIR}/property_data.json << EOF
{
    "id": "1234567890",
    "params": [
        {
            "identifier": "temperature",
            "value": 25.6,
            "name": "温度传感器"
        },
        {
            "identifier": "humidity", 
            "value": 65,
            "name": "湿度传感器"
        },
        {
            "identifier": "pressure",
            "value": 1013,
            "name": "气压传感器"
        }
    ],
    "method": "thing.event.property.post"
}
EOF

    # 创建事件数据文件
    cat > ${LOG_DIR}/event_data.json << EOF
{
    "id": "1234567890",
    "params": [
        {
            "identifier": "alarm_high_temp",
            "name": "高温报警",
            "value": {
                "temperature": 45
            }
        }
    ],
    "method": "thing.event.post"
}
EOF

    # 创建设备信息数据文件
    cat > ${LOG_DIR}/info_data.json << EOF
{
    "id": "1234567890",
    "params": {
        "imei": "861536030073854",
        "iccid": "89860318740035532710",
        "firmwareVersion": "1.0.0",
        "status": 1,
        "rssi": -45,
        "longitude": 116.404,
        "latitude": 39.915
    },
    "method": "thing.info.post"
}
EOF

    echo "测试数据文件已创建"
}

# HTTP压力测试 - Apache Bench
run_ab_test() {
    local endpoint=$1
    local data_file=$2
    local requests=$3
    local concurrency=$4
    local test_name=$5
    
    if ! command -v ab &> /dev/null; then
        echo "跳过AB测试: ${test_name} (ab未安装)"
        return
    fi
    
    echo ""
    echo "开始AB测试: ${test_name}"
    echo "请求数: ${requests}, 并发数: ${concurrency}"
    echo "开始时间: $(date '+%Y-%m-%d %H:%M:%S')"
    
    local url="http://${HTTP_HOST}:${HTTP_PORT}${endpoint}"
    
    ab -n ${requests} -c ${concurrency} \
       -T "application/json" \
       -p ${data_file} \
       -H "Authorization: Bearer test-token" \
       -H "Content-Type: application/json" \
       ${url} \
       2>&1 | tee ${LOG_DIR}/ab_${test_name}_${requests}_${concurrency}.log
    
    local exit_code=$?
    echo "结束时间: $(date '+%Y-%m-%d %H:%M:%S')"
    
    if [ $exit_code -eq 0 ]; then
        echo "✓ AB测试成功: ${test_name}"
    else
        echo "✗ AB测试失败: ${test_name} (退出码: $exit_code)"
    fi
    
    sleep 3
}

# HTTP压力测试 - wrk
run_wrk_test() {
    local endpoint=$1
    local data_file=$2
    local threads=$3
    local connections=$4
    local duration=$5
    local test_name=$6
    
    if ! command -v wrk &> /dev/null; then
        echo "跳过WRK测试: ${test_name} (wrk未安装)"
        return
    fi
    
    echo ""
    echo "开始WRK测试: ${test_name}"
    echo "线程数: ${threads}, 连接数: ${connections}, 持续时间: ${duration}s"
    echo "开始时间: $(date '+%Y-%m-%d %H:%M:%S')"
    
    # 创建wrk lua脚本
    cat > ${LOG_DIR}/wrk_script.lua << EOF
wrk.method = "POST"
wrk.body   = io.open("${data_file}"):read("*all")
wrk.headers["Content-Type"] = "application/json"
wrk.headers["Authorization"] = "Bearer test-token"
EOF
    
    local url="http://${HTTP_HOST}:${HTTP_PORT}${endpoint}"
    
    wrk -t${threads} -c${connections} -d${duration}s \
        -s ${LOG_DIR}/wrk_script.lua \
        ${url} \
        2>&1 | tee ${LOG_DIR}/wrk_${test_name}_${threads}_${connections}_${duration}.log
    
    local exit_code=$?
    echo "结束时间: $(date '+%Y-%m-%d %H:%M:%S')"
    
    if [ $exit_code -eq 0 ]; then
        echo "✓ WRK测试成功: ${test_name}"
    else
        echo "✗ WRK测试失败: ${test_name} (退出码: $exit_code)"
    fi
    
    sleep 3
}

# CoAP压力测试
run_coap_test() {
    local path=$1
    local data_file=$2
    local requests=$3
    local test_name=$4
    
    if ! command -v coap-client &> /dev/null; then
        echo "跳过CoAP测试: ${test_name} (coap-client未安装)"
        return
    fi
    
    echo ""
    echo "开始CoAP测试: ${test_name}"
    echo "请求数: ${requests}"
    echo "开始时间: $(date '+%Y-%m-%d %H:%M:%S')"
    
    local log_file="${LOG_DIR}/coap_${test_name}_${requests}.log"
    local success_count=0
    local error_count=0
    
    # 记录开始时间
    local start_time=$(date +%s%3N)
    
    for ((i=1; i<=requests; i++)); do
        local data=$(cat ${data_file})
        coap-client -m post -t 0 \
                   -e "${data}" \
                   "coap://${COAP_HOST}:${COAP_PORT}${path}" \
                   >> ${log_file} 2>&1
        
        if [ $? -eq 0 ]; then
            ((success_count++))
        else
            ((error_count++))
        fi
        
        if [ $((i % 100)) -eq 0 ]; then
            echo "已完成 $i 个请求"
        fi
        
        # 控制发送频率，避免过快
        sleep 0.01
    done
    
    # 记录结束时间
    local end_time=$(date +%s%3N)
    local duration=$((end_time - start_time))
    
    echo "结束时间: $(date '+%Y-%m-%d %H:%M:%S')"
    echo "总耗时: ${duration}ms"
    echo "成功请求: ${success_count}"
    echo "失败请求: ${error_count}"
    echo "成功率: $(echo "scale=2; $success_count * 100 / $requests" | bc)%"
    
    # 将统计信息写入日志
    cat >> ${log_file} << EOF

========== 测试统计 ==========
总请求数: ${requests}
成功请求: ${success_count}
失败请求: ${error_count}
成功率: $(echo "scale=2; $success_count * 100 / $requests" | bc)%
总耗时: ${duration}ms
平均响应时间: $(echo "scale=2; $duration / $requests" | bc)ms
===============================
EOF
    
    echo "✓ CoAP测试完成: ${test_name}"
    sleep 3
}

# 并发HTTP连接测试
run_concurrent_http_test() {
    local endpoint=$1
    local data_file=$2
    local processes=$3
    local requests_per_process=$4
    local test_name=$5
    
    echo ""
    echo "开始并发HTTP测试: ${test_name}"
    echo "进程数: ${processes}, 每进程请求数: ${requests_per_process}"
    echo "总请求数: $((processes * requests_per_process))"
    echo "开始时间: $(date '+%Y-%m-%d %H:%M:%S')"
    
    local pids=()
    local start_time=$(date +%s%3N)
    
    # 启动多个并发进程
    for ((i=1; i<=processes; i++)); do
        {
            local log_file="${LOG_DIR}/concurrent_http_${test_name}_process_${i}.log"
            local success=0
            local errors=0
            
            for ((j=1; j<=requests_per_process; j++)); do
                curl -X POST \
                     -H "Content-Type: application/json" \
                     -H "Authorization: Bearer test-token" \
                     -d @${data_file} \
                     "http://${HTTP_HOST}:${HTTP_PORT}${endpoint}" \
                     >> ${log_file} 2>&1
                
                if [ $? -eq 0 ]; then
                    ((success++))
                else
                    ((errors++))
                fi
            done
            
            echo "进程${i}: 成功=${success}, 失败=${errors}" >> ${LOG_DIR}/concurrent_summary.log
        } &
        pids+=($!)
    done
    
    # 等待所有进程完成
    for pid in "${pids[@]}"; do
        wait $pid
    done
    
    local end_time=$(date +%s%3N)
    local duration=$((end_time - start_time))
    
    echo "结束时间: $(date '+%Y-%m-%d %H:%M:%S')"
    echo "总耗时: ${duration}ms"
    
    # 统计总体结果
    local total_success=$(grep "成功=" ${LOG_DIR}/concurrent_summary.log | awk -F'成功=' '{sum+=$2} END {print sum}' | cut -d',' -f1)
    local total_errors=$(grep "失败=" ${LOG_DIR}/concurrent_summary.log | awk -F'失败=' '{sum+=$2} END {print sum}')
    
    echo "总成功请求: ${total_success}"
    echo "总失败请求: ${total_errors}"
    echo "QPS: $(echo "scale=2; $total_success * 1000 / $duration" | bc)"
    
    echo "✓ 并发HTTP测试完成: ${test_name}"
    rm -f ${LOG_DIR}/concurrent_summary.log
    sleep 3
}

# 生成测试报告
generate_report() {
    local report_file="${LOG_DIR}/http_coap_test_report_$(date '+%Y%m%d_%H%M%S').txt"
    
    echo "================ FastBee HTTP/CoAP压力测试报告 ================" > ${report_file}
    echo "测试时间: $(date '+%Y-%m-%d %H:%M:%S')" >> ${report_file}
    echo "HTTP服务器: ${HTTP_HOST}:${HTTP_PORT}" >> ${report_file}
    echo "CoAP服务器: ${COAP_HOST}:${COAP_PORT}" >> ${report_file}
    echo "" >> ${report_file}
    
    echo "测试结果汇总:" >> ${report_file}
    
    # 分析AB测试结果
    echo "=== Apache Bench (AB) 测试结果 ===" >> ${report_file}
    for log_file in ${LOG_DIR}/ab_*.log; do
        if [ -f "$log_file" ]; then
            echo "文件: $(basename $log_file)" >> ${report_file}
            grep -E "(Requests per second|Time per request|Transfer rate)" "$log_file" >> ${report_file}
            echo "" >> ${report_file}
        fi
    done
    
    # 分析WRK测试结果
    echo "=== WRK 测试结果 ===" >> ${report_file}
    for log_file in ${LOG_DIR}/wrk_*.log; do
        if [ -f "$log_file" ]; then
            echo "文件: $(basename $log_file)" >> ${report_file}
            grep -E "(Requests/sec|Latency|Req/Sec)" "$log_file" >> ${report_file}
            echo "" >> ${report_file}
        fi
    done
    
    # 分析CoAP测试结果
    echo "=== CoAP 测试结果 ===" >> ${report_file}
    for log_file in ${LOG_DIR}/coap_*.log; do
        if [ -f "$log_file" ]; then
            echo "文件: $(basename $log_file)" >> ${report_file}
            tail -10 "$log_file" >> ${report_file}
            echo "" >> ${report_file}
        fi
    done
    
    echo "测试报告已生成: ${report_file}"
}

# 主测试流程
main() {
    echo "开始HTTP/CoAP压力测试..."
    
    # 检查工具
    check_tools
    
    # 准备测试数据
    prepare_test_data
    
    # HTTP测试 - Apache Bench
    echo ""
    echo "========== HTTP压力测试 - Apache Bench =========="
    run_ab_test "/property/post" "${LOG_DIR}/property_data.json" 1000 50 "property_basic"
    run_ab_test "/property/post" "${LOG_DIR}/property_data.json" 5000 100 "property_medium"
    run_ab_test "/property/post" "${LOG_DIR}/property_data.json" 10000 200 "property_high"
    run_ab_test "/event/post" "${LOG_DIR}/event_data.json" 2000 100 "event_test"
    run_ab_test "/info/post" "${LOG_DIR}/info_data.json" 1000 50 "info_test"
    
    # HTTP测试 - WRK
    echo ""
    echo "========== HTTP压力测试 - WRK =========="
    run_wrk_test "/property/post" "${LOG_DIR}/property_data.json" 10 100 60 "property_wrk"
    run_wrk_test "/property/post" "${LOG_DIR}/property_data.json" 20 500 120 "property_wrk_high"
    run_wrk_test "/event/post" "${LOG_DIR}/event_data.json" 10 200 60 "event_wrk"
    
    # 并发HTTP测试
    echo ""
    echo "========== 并发HTTP测试 =========="
    run_concurrent_http_test "/property/post" "${LOG_DIR}/property_data.json" 50 100 "concurrent_basic"
    run_concurrent_http_test "/property/post" "${LOG_DIR}/property_data.json" 100 200 "concurrent_high"
    
    # CoAP测试
    echo ""
    echo "========== CoAP压力测试 =========="
    run_coap_test "/property/post" "${LOG_DIR}/property_data.json" 1000 "property_coap"
    run_coap_test "/event/post" "${LOG_DIR}/event_data.json" 500 "event_coap"
    run_coap_test "/info/post" "${LOG_DIR}/info_data.json" 500 "info_coap"
    
    # 混合协议测试
    echo ""
    echo "========== 混合协议测试 =========="
    echo "同时运行HTTP和CoAP测试..."
    
    # 后台运行CoAP测试
    run_coap_test "/property/post" "${LOG_DIR}/property_data.json" 2000 "mixed_coap" &
    local coap_pid=$!
    
    # 前台运行HTTP测试
    run_ab_test "/property/post" "${LOG_DIR}/property_data.json" 5000 100 "mixed_http"
    
    # 等待CoAP测试完成
    wait $coap_pid
    echo "✓ 混合协议测试完成"
    
    # 生成测试报告
    echo ""
    echo "========== 生成测试报告 =========="
    generate_report
    
    echo ""
    echo "========== HTTP/CoAP压力测试完成 =========="
    echo "所有日志文件保存在: ${LOG_DIR}"
}

# 脚本使用说明
usage() {
    echo "用法: $0 [HTTP_HOST] [HTTP_PORT] [COAP_HOST] [COAP_PORT]"
    echo ""
    echo "参数说明:"
    echo "  HTTP_HOST    HTTP服务器地址 (默认: 177.7.0.13)"
    echo "  HTTP_PORT    HTTP服务器端口 (默认: 8081)"
    echo "  COAP_HOST    CoAP服务器地址 (默认: 177.7.0.13)"
    echo "  COAP_PORT    CoAP服务器端口 (默认: 5683)"
    echo ""
    echo "依赖工具:"
    echo "  - apache2-utils (ab命令)"
    echo "  - wrk"
    echo "  - libcoap-bin (coap-client命令)"
    echo "  - bc (计算工具)"
    echo ""
    echo "安装命令 (Ubuntu/Debian):"
    echo "  sudo apt-get install apache2-utils wrk libcoap-bin bc"
}

# 检查参数
if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
    usage
    exit 0
fi

# 执行主流程
main

echo "测试完成！" 