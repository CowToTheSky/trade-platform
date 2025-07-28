# 高并发交易模拟脚本
param(
    [int]$OrderCount = 30,
    [int]$DelayMs = 200
)

# 设置编码
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
chcp 65001 | Out-Null

# 配置参数
$baseUrl = "http://localhost:8082/api/trade"

# 产品信息
$products = @(
    @{code="000001"; name="平安银行"; minPrice=12.50; maxPrice=12.60}
)

# 用户ID范围
$userIds = 1001..1010

Write-Host "开始高并发交易模拟..." -ForegroundColor Magenta
Write-Host "将生成 $OrderCount 个订单，间隔 ${DelayMs}ms" -ForegroundColor Magenta
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

$successCount = 0
$failCount = 0

# 生成订单循环
for ($i = 1; $i -le $OrderCount; $i++) {
    # 随机选择参数
    $userId = Get-Random -InputObject $userIds
    $product = $products[0]
    $orderType = Get-Random -Minimum 1 -Maximum 3
    $price = [math]::Round((Get-Random -Minimum $product.minPrice -Maximum $product.maxPrice), 2)
    $quantity = (Get-Random -Minimum 1 -Maximum 3) * 100
    
    # 构建订单数据
    $orderData = @{
        userId = $userId
        productCode = $product.code
        orderType = $orderType
        price = $price
        quantity = $quantity
        source = 3
        remark = "高并发测试"
    } | ConvertTo-Json
    
    $orderTypeDesc = if ($orderType -eq 1) { "买入" } else { "卖出" }
    $timestamp = Get-Date -Format "HH:mm:ss.fff"
    
    # 提交订单
    try {
        $response = Invoke-WebRequest -Uri "$baseUrl/orders" -Method POST -Body $orderData -ContentType "application/json" -TimeoutSec 3
        $result = $response.Content | ConvertFrom-Json
        
        if ($result.code -eq 200) {
            $successCount++
            Write-Host "[$timestamp] 成功 $orderTypeDesc 用户$userId ${quantity}股@${price}" -ForegroundColor Green
        } else {
            $failCount++
            Write-Host "[$timestamp] 失败 $orderTypeDesc 失败: $($result.message)" -ForegroundColor Red
        }
    } catch {
        $failCount++
        Write-Host "[$timestamp] 错误 $orderTypeDesc 网络错误" -ForegroundColor Yellow
    }
    
    # 实时显示进度
    if ($i % 10 -eq 0) {
        $progress = [math]::Round(($i / $OrderCount) * 100, 1)
        Write-Host ""
        Write-Host "进度: $progress% ($i/$OrderCount) | 成功: $successCount | 失败: $failCount" -ForegroundColor Cyan
        Write-Host "================================================" -ForegroundColor Cyan
        Write-Host ""
    }
    
    Start-Sleep -Milliseconds $DelayMs
}

Write-Host ""
Write-Host "高并发模拟完成！" -ForegroundColor Magenta
Write-Host "总计: $OrderCount 个订单" -ForegroundColor White
Write-Host "成功: $successCount 个" -ForegroundColor Green
Write-Host "失败: $failCount 个" -ForegroundColor Red
Write-Host "成功率: $([math]::Round(($successCount / $OrderCount) * 100, 1))%" -ForegroundColor Yellow

# 最终统计
try {
    $finalStats = Invoke-WebRequest -Uri "http://localhost:8082/api/monitor/performance" -Method GET
    $stats = ($finalStats.Content | ConvertFrom-Json).data
    
    Write-Host ""
    Write-Host "系统性能统计:" -ForegroundColor Cyan
    Write-Host "   总处理订单数: $($stats.processedOrders)" -ForegroundColor White
    Write-Host "   平均处理时间: $([math]::Round($stats.averageProcessingTime, 2))ms" -ForegroundColor White
    Write-Host "   估算吞吐量: $([math]::Round($stats.estimatedThroughput, 2)) 订单/秒" -ForegroundColor White
} catch {
    Write-Host "无法获取系统统计" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "查看详细信息:" -ForegroundColor Cyan
Write-Host "   前端页面: http://localhost:3001" -ForegroundColor White
Write-Host "   性能监控: http://localhost:8082/api/monitor/performance" -ForegroundColor White