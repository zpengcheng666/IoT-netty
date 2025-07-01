#!/bin/bash

# FastBee系统监控脚本
# 用于监控压力测试期间的系统性能指标

# 配置参数
MONITOR_INTERVAL=${1:-5}  # 监控间隔(秒)
LOG_DIR="./monitor-logs"
MAX_LOG_SIZE=100000000   # 100MB
ALERT_CPU_THRESHOLD=80   # CPU使用率告警阈值
ALERT_MEM_THRESHOLD=80   # 内存使用率告警阈值
ALERT_DISK_THRESHOLD=90  # 磁盘使用率告警阈值

# 创建日志目录
mkdir -p ${LOG_DIR}

echo "==================== FastBee系统监控 ===================="
echo "监控间隔: ${MONITOR_INTERVAL}秒"
echo "日志目录: ${LOG_DIR}"
echo "CPU告警阈值: ${ALERT_CPU_THRESHOLD}%"
echo "内存告警阈值: ${ALERT_MEM_THRESHOLD}%"
echo "磁盘告警阈值: ${ALERT_DISK_THRESHOLD}%"
echo "======================================================="

# 检查必要工具
check_dependencies() {
    local missing_tools=()
    
    for tool in top free df netstat ss iostat vmstat; do
        if ! command -v $tool &> /dev/null; then
            missing_tools+=("$tool")
        fi
    done
    
    if [ ${#missing_tools[@]} -gt 0 ]; then
        echo "警告: 以下工具未安装，相关监控功能将不可用："
        printf '  - %s\n' "${missing_tools[@]}"
        echo "建议安装: sudo apt-get install sysstat net-tools procps"
        echo ""
    fi
}

# 获取CPU使用率
get_cpu_usage() {
    if command -v top &> /dev/null; then
        top -bn1 | grep "Cpu(s)" | sed "s/.*, *\([0-9.]*\)%* id.*/\1/" | awk '{print 100 - $1}'
    else
        echo "N/A"
    fi
}

# 获取内存使用率
get_memory_usage() {
    if command -v free &> /dev/null; then
        free | grep Mem | awk '{printf("%.1f"), $3/$2 * 100.0}'
    else
        echo "N/A"
    fi
}

# 获取磁盘使用率
get_disk_usage() {
    if command -v df &> /dev/null; then
        df -h / | awk 'NR==2{print $5}' | sed 's/%//'
    else
        echo "N/A"
    fi
}

# 获取网络连接数
get_network_connections() {
    if command -v ss &> /dev/null; then
        local tcp_connections=$(ss -t state established | wc -l)
        local tcp_listening=$(ss -tln | wc -l)
        echo "${tcp_connections},${tcp_listening}"
    elif command -v netstat &> /dev/null; then
        local tcp_connections=$(netstat -tn | grep ESTABLISHED | wc -l)
        local tcp_listening=$(netstat -tln | wc -l)
        echo "${tcp_connections},${tcp_listening}"
    else
        echo "N/A,N/A"
    fi
}

# 获取负载平均值
get_load_average() {
    if [ -f /proc/loadavg ]; then
        cat /proc/loadavg | awk '{print $1","$2","$3}'
    else
        echo "N/A,N/A,N/A"
    fi
}

# 获取Java进程信息
get_java_process_info() {
    local java_pids=$(pgrep -f "java.*FastBee")
    if [ -n "$java_pids" ]; then
        local total_cpu=0
        local total_mem=0
        local process_count=0
        
        for pid in $java_pids; do
            if [ -d "/proc/$pid" ]; then
                local cpu_usage=$(ps -p $pid -o %cpu= | tr -d ' ')
                local mem_usage=$(ps -p $pid -o %mem= | tr -d ' ')
                
                if [[ $cpu_usage =~ ^[0-9]+\.?[0-9]*$ ]]; then
                    total_cpu=$(echo "$total_cpu + $cpu_usage" | bc -l)
                fi
                
                if [[ $mem_usage =~ ^[0-9]+\.?[0-9]*$ ]]; then
                    total_mem=$(echo "$total_mem + $mem_usage" | bc -l)
                fi
                
                ((process_count++))
            fi
        done
        
        echo "${total_cpu},${total_mem},${process_count}"
    else
        echo "0,0,0"
    fi
}

# 获取JVM信息
get_jvm_info() {
    local java_pids=$(pgrep -f "java.*FastBee")
    if [ -n "$java_pids" ] && command -v jstat &> /dev/null; then
        local pid=$(echo $java_pids | awk '{print $1}')  # 取第一个进程
        
        # 获取GC信息
        local gc_info=$(jstat -gc $pid 2>/dev/null | tail -1)
        if [ $? -eq 0 ] && [ -n "$gc_info" ]; then
            local young_gen=$(echo $gc_info | awk '{print $3+$4}')     # S0U+S1U
            local old_gen=$(echo $gc_info | awk '{print $6}')          # OU
            local young_gen_max=$(echo $gc_info | awk '{print $1+$2+$5}') # S0C+S1C+EC
            local old_gen_max=$(echo $gc_info | awk '{print $7}')      # OC
            local gc_count=$(echo $gc_info | awk '{print $12+$13}')    # YGC+FGC
            local gc_time=$(echo $gc_info | awk '{print $14+$15}')     # YGCT+FGCT
            
            echo "${young_gen},${old_gen},${young_gen_max},${old_gen_max},${gc_count},${gc_time}"
        else
            echo "N/A,N/A,N/A,N/A,N/A,N/A"
        fi
    else
        echo "N/A,N/A,N/A,N/A,N/A,N/A"
    fi
}

# 获取数据库连接数
get_database_connections() {
    # 这里需要根据实际的数据库配置进行调整
    # 示例：MySQL连接数监控
    local mysql_host="localhost"
    local mysql_user="root"
    local mysql_password="123456"
    
    if command -v mysql &> /dev/null; then
        local connections=$(mysql -h"$mysql_host" -u"$mysql_user" -p"$mysql_password" \
                          -e "SHOW STATUS LIKE 'Threads_connected';" 2>/dev/null | \
                          awk 'NR==2{print $2}')
        echo "${connections:-N/A}"
    else
        echo "N/A"
    fi
}

# 获取Redis信息
get_redis_info() {
    if command -v redis-cli &> /dev/null; then
        local redis_info=$(redis-cli info memory 2>/dev/null | grep "used_memory_human:")
        local redis_connections=$(redis-cli info clients 2>/dev/null | grep "connected_clients:")
        
        local memory_usage=$(echo "$redis_info" | cut -d: -f2 | tr -d '\r')
        local client_count=$(echo "$redis_connections" | cut -d: -f2 | tr -d '\r')
        
        echo "${memory_usage:-N/A},${client_count:-N/A}"
    else
        echo "N/A,N/A"
    fi
}

# 发送告警
send_alert() {
    local alert_type=$1
    local current_value=$2
    local threshold=$3
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    echo "[$timestamp] 🚨 告警: ${alert_type} 使用率 ${current_value}% 超过阈值 ${threshold}%" | tee -a ${LOG_DIR}/alerts.log
    
    # 这里可以添加邮件、短信、钉钉等告警通知
    # 示例：发送邮件告警
    # echo "告警内容" | mail -s "FastBee监控告警" admin@example.com
}

# 检查告警条件
check_alerts() {
    local cpu_usage=$1
    local mem_usage=$2
    local disk_usage=$3
    
    # CPU告警
    if [[ $cpu_usage =~ ^[0-9]+\.?[0-9]*$ ]] && (( $(echo "$cpu_usage > $ALERT_CPU_THRESHOLD" | bc -l) )); then
        send_alert "CPU" "$cpu_usage" "$ALERT_CPU_THRESHOLD"
    fi
    
    # 内存告警
    if [[ $mem_usage =~ ^[0-9]+\.?[0-9]*$ ]] && (( $(echo "$mem_usage > $ALERT_MEM_THRESHOLD" | bc -l) )); then
        send_alert "内存" "$mem_usage" "$ALERT_MEM_THRESHOLD"
    fi
    
    # 磁盘告警
    if [[ $disk_usage =~ ^[0-9]+$ ]] && [ $disk_usage -gt $ALERT_DISK_THRESHOLD ]; then
        send_alert "磁盘" "$disk_usage" "$ALERT_DISK_THRESHOLD"
    fi
}

# 轮转日志文件
rotate_log_if_needed() {
    local log_file=$1
    
    if [ -f "$log_file" ] && [ $(stat -f%z "$log_file" 2>/dev/null || stat -c%s "$log_file" 2>/dev/null || echo 0) -gt $MAX_LOG_SIZE ]; then
        mv "$log_file" "${log_file}.$(date +%Y%m%d_%H%M%S)"
        echo "日志文件已轮转: $log_file"
    fi
}

# 监控主循环
start_monitoring() {
    local system_log="${LOG_DIR}/system_metrics.csv"
    local performance_log="${LOG_DIR}/performance_metrics.csv"
    
    # 创建CSV表头
    if [ ! -f "$system_log" ]; then
        echo "Timestamp,CPU_Usage,Memory_Usage,Disk_Usage,Load_1m,Load_5m,Load_15m,TCP_Connections,TCP_Listening" > "$system_log"
    fi
    
    if [ ! -f "$performance_log" ]; then
        echo "Timestamp,Java_CPU,Java_Memory,Java_Processes,Young_Gen,Old_Gen,Young_Max,Old_Max,GC_Count,GC_Time,DB_Connections,Redis_Memory,Redis_Connections" > "$performance_log"
    fi
    
    echo "开始系统监控..."
    echo "按 Ctrl+C 停止监控"
    
    while true; do
        local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
        
        # 系统指标
        local cpu_usage=$(get_cpu_usage)
        local mem_usage=$(get_memory_usage)
        local disk_usage=$(get_disk_usage)
        local load_avg=$(get_load_average)
        local network_conn=$(get_network_connections)
        
        # 应用指标
        local java_info=$(get_java_process_info)
        local jvm_info=$(get_jvm_info)
        local db_connections=$(get_database_connections)
        local redis_info=$(get_redis_info)
        
        # 记录到CSV文件
        echo "$timestamp,$cpu_usage,$mem_usage,$disk_usage,$load_avg,$network_conn" >> "$system_log"
        echo "$timestamp,$java_info,$jvm_info,$db_connections,$redis_info" >> "$performance_log"
        
        # 控制台输出
        printf "\r[%s] CPU: %s%% | 内存: %s%% | 磁盘: %s%% | 负载: %s | 连接数: %s" \
               "$timestamp" "$cpu_usage" "$mem_usage" "$disk_usage" \
               "$(echo $load_avg | cut -d, -f1)" \
               "$(echo $network_conn | cut -d, -f1)"
        
        # 检查告警
        check_alerts "$cpu_usage" "$mem_usage" "$disk_usage"
        
        # 轮转日志
        rotate_log_if_needed "$system_log"
        rotate_log_if_needed "$performance_log"
        
        sleep $MONITOR_INTERVAL
    done
}

# 生成监控报告
generate_monitor_report() {
    local report_file="${LOG_DIR}/monitor_report_$(date '+%Y%m%d_%H%M%S').html"
    
    cat > "$report_file" << 'EOF'
<!DOCTYPE html>
<html>
<head>
    <title>FastBee系统监控报告</title>
    <meta charset="utf-8">
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .header { background-color: #f0f0f0; padding: 10px; border-radius: 5px; }
        .metrics { display: flex; flex-wrap: wrap; gap: 20px; margin: 20px 0; }
        .metric-card { background-color: #fff; border: 1px solid #ddd; padding: 15px; border-radius: 5px; min-width: 200px; }
        .metric-title { font-weight: bold; color: #333; }
        .metric-value { font-size: 24px; color: #007bff; margin: 10px 0; }
        .alert { background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 10px; border-radius: 5px; margin: 10px 0; }
        table { border-collapse: collapse; width: 100%; margin: 20px 0; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
    <div class="header">
        <h1>FastBee系统监控报告</h1>
        <p>生成时间: $(date '+%Y-%m-%d %H:%M:%S')</p>
    </div>
EOF

    # 分析最新的系统指标
    if [ -f "${LOG_DIR}/system_metrics.csv" ]; then
        local latest_metrics=$(tail -1 "${LOG_DIR}/system_metrics.csv")
        local cpu_latest=$(echo "$latest_metrics" | cut -d, -f2)
        local mem_latest=$(echo "$latest_metrics" | cut -d, -f3)
        local disk_latest=$(echo "$latest_metrics" | cut -d, -f4)
        local load_latest=$(echo "$latest_metrics" | cut -d, -f5)
        
        cat >> "$report_file" << EOF
    <div class="metrics">
        <div class="metric-card">
            <div class="metric-title">CPU使用率</div>
            <div class="metric-value">${cpu_latest}%</div>
        </div>
        <div class="metric-card">
            <div class="metric-title">内存使用率</div>
            <div class="metric-value">${mem_latest}%</div>
        </div>
        <div class="metric-card">
            <div class="metric-title">磁盘使用率</div>
            <div class="metric-value">${disk_latest}%</div>
        </div>
        <div class="metric-card">
            <div class="metric-title">系统负载</div>
            <div class="metric-value">${load_latest}</div>
        </div>
    </div>
EOF
    fi
    
    # 添加告警信息
    if [ -f "${LOG_DIR}/alerts.log" ]; then
        cat >> "$report_file" << EOF
    <h2>告警信息</h2>
    <div class="alert">
        <h3>最近告警</h3>
        <pre>$(tail -10 "${LOG_DIR}/alerts.log" 2>/dev/null || echo "无告警记录")</pre>
    </div>
EOF
    fi
    
    # 添加详细数据表格
    cat >> "$report_file" << EOF
    <h2>最近监控数据</h2>
    <table>
        <tr>
            <th>时间</th>
            <th>CPU使用率</th>
            <th>内存使用率</th>
            <th>磁盘使用率</th>
            <th>系统负载</th>
            <th>TCP连接数</th>
        </tr>
EOF
    
    if [ -f "${LOG_DIR}/system_metrics.csv" ]; then
        tail -20 "${LOG_DIR}/system_metrics.csv" | while IFS=, read -r timestamp cpu mem disk load1 load5 load15 tcp_conn tcp_listen; do
            if [ "$timestamp" != "Timestamp" ]; then
                cat >> "$report_file" << EOF
        <tr>
            <td>$timestamp</td>
            <td>$cpu%</td>
            <td>$mem%</td>
            <td>$disk%</td>
            <td>$load1</td>
            <td>$tcp_conn</td>
        </tr>
EOF
            fi
        done
    fi
    
    cat >> "$report_file" << EOF
    </table>
</body>
</html>
EOF
    
    echo "监控报告已生成: $report_file"
}

# 脚本使用说明
usage() {
    echo "用法: $0 [INTERVAL]"
    echo ""
    echo "参数说明:"
    echo "  INTERVAL     监控间隔(秒) (默认: 5)"
    echo ""
    echo "功能说明:"
    echo "  - 实时监控系统CPU、内存、磁盘使用率"
    echo "  - 监控网络连接数和系统负载"
    echo "  - 监控Java进程和JVM状态"
    echo "  - 监控数据库连接数"
    echo "  - 自动告警和日志轮转"
    echo ""
    echo "示例:"
    echo "  $0      # 使用默认5秒间隔"
    echo "  $0 10   # 使用10秒间隔"
    echo ""
    echo "生成报告:"
    echo "  $0 --report  # 生成HTML监控报告"
}

# 主函数
main() {
    check_dependencies
    
    # 捕获中断信号，优雅退出
    trap 'echo -e "\n\n监控已停止"; generate_monitor_report; exit 0' INT TERM
    
    start_monitoring
}

# 检查参数
case "$1" in
    "-h"|"--help")
        usage
        exit 0
        ;;
    "--report")
        generate_monitor_report
        exit 0
        ;;
    "")
        main
        ;;
    *)
        if [[ $1 =~ ^[0-9]+$ ]]; then
            MONITOR_INTERVAL=$1
            main
        else
            echo "错误: 无效参数 '$1'"
            usage
            exit 1
        fi
        ;;
esac 