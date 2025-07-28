import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './App.css';

const MonitorDashboard = ({ user, onLogout }) => {
  const navigate = useNavigate();
  const [healthData, setHealthData] = useState(null);
  const [performanceReport, setPerformanceReport] = useState('');
  const [asyncStatus, setAsyncStatus] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [autoRefresh, setAutoRefresh] = useState(false);
  const [refreshInterval, setRefreshInterval] = useState(null);

  // 获取健康状态数据
  const fetchHealthData = async () => {
    try {
      setLoading(true);
      const response = await axios.get('http://localhost:8082/api/monitor/health');
      if (response.data.success) {
        setHealthData(response.data.data);
        setError('');
      } else {
        setError(response.data.message || '获取健康状态失败');
      }
    } catch (err) {
      setError(err.response?.data?.message || '网络请求失败');
    } finally {
      setLoading(false);
    }
  };

  // 获取性能报告
  const fetchPerformanceReport = async () => {
    try {
      const response = await axios.get('http://localhost:8082/api/monitor/performance');
      if (response.data.success) {
        setPerformanceReport(response.data.data);
      }
    } catch (err) {
      console.error('获取性能报告失败:', err);
    }
  };

  // 获取异步处理器状态
  const fetchAsyncStatus = async () => {
    try {
      const response = await axios.get('http://localhost:8082/api/monitor/async-processor-status');
      if (response.data.success) {
        setAsyncStatus(response.data.data);
      }
    } catch (err) {
      console.error('获取异步处理器状态失败:', err);
    }
  };

  // 重置统计数据
  const resetStats = async () => {
    try {
      const response = await axios.post('http://localhost:8082/api/monitor/reset-stats');
      if (response.data.success) {
        alert('统计数据重置成功');
        fetchAllData();
      } else {
        alert(response.data.message || '重置失败');
      }
    } catch (err) {
      alert(err.response?.data?.message || '重置失败');
    }
  };

  // 手动触发撮合
  const triggerMatching = async (productCode) => {
    try {
      const response = await axios.post(`http://localhost:8082/api/monitor/trigger-matching/${productCode}`);
      if (response.data.success) {
        alert(`撮合任务已提交: ${productCode}`);
      } else {
        alert(response.data.message || '触发撮合失败');
      }
    } catch (err) {
      alert(err.response?.data?.message || '触发撮合失败');
    }
  };

  // 获取所有数据
  const fetchAllData = () => {
    fetchHealthData();
    fetchPerformanceReport();
    fetchAsyncStatus();
  };

  // 切换自动刷新
  const toggleAutoRefresh = () => {
    if (autoRefresh) {
      clearInterval(refreshInterval);
      setRefreshInterval(null);
    } else {
      const interval = setInterval(fetchAllData, 5000); // 每5秒刷新
      setRefreshInterval(interval);
    }
    setAutoRefresh(!autoRefresh);
  };

  // 组件挂载时获取数据
  useEffect(() => {
    fetchAllData();
    return () => {
      if (refreshInterval) {
        clearInterval(refreshInterval);
      }
    };
  }, []);

  // 获取健康状态颜色
  const getHealthColor = (status) => {
    if (status.includes('优秀')) return '#52c41a';
    if (status.includes('良好')) return '#1890ff';
    if (status.includes('一般')) return '#faad14';
    return '#f5222d';
  };

  return (
    <div className="monitor-dashboard">
      <div className="dashboard-header">
        <div className="header-left">
          <h2>系统监控面板</h2>
          <div className="user-info">
            欢迎，{user?.username || '用户'}
          </div>
        </div>
        <div className="header-controls">
          <button 
            onClick={fetchAllData} 
            disabled={loading}
            className="btn btn-primary"
          >
            {loading ? '刷新中...' : '手动刷新'}
          </button>
          <button 
            onClick={toggleAutoRefresh}
            className={`btn ${autoRefresh ? 'btn-warning' : 'btn-success'}`}
          >
            {autoRefresh ? '停止自动刷新' : '开启自动刷新'}
          </button>
          <button 
            onClick={resetStats}
            className="btn btn-danger"
          >
            重置统计
          </button>
          <button 
            onClick={() => navigate('/')}
            className="btn btn-secondary"
          >
            返回交易
          </button>
          <button 
            onClick={onLogout}
            className="btn btn-outline-danger"
          >
            退出登录
          </button>
        </div>
      </div>

      {error && (
        <div className="alert alert-danger">
          {error}
        </div>
      )}

      <div className="dashboard-grid">
        {/* 系统健康状态 */}
        <div className="card">
          <div className="card-header">
            <h3>系统健康状态</h3>
          </div>
          <div className="card-body">
            {healthData ? (
              <div className="health-metrics">
                <div className="metric-item">
                  <span className="metric-label">健康状态:</span>
                  <span 
                    className="metric-value health-status"
                    style={{ color: getHealthColor(healthData.status) }}
                  >
                    {healthData.status}
                  </span>
                </div>
                <div className="metric-item">
                  <span className="metric-label">平均处理时间:</span>
                  <span className="metric-value">
                    {healthData.averageProcessingTime.toFixed(2)} ms
                  </span>
                </div>
                <div className="metric-item">
                  <span className="metric-label">系统吞吐量:</span>
                  <span className="metric-value">
                    {healthData.throughput.toFixed(2)} 订单/分钟
                  </span>
                </div>
              </div>
            ) : (
              <div className="loading">加载中...</div>
            )}
          </div>
        </div>

        {/* 异步处理器状态 */}
        <div className="card">
          <div className="card-header">
            <h3>异步处理器状态</h3>
          </div>
          <div className="card-body">
            {asyncStatus ? (
              <div className="async-metrics">
                <div className="metric-section">
                  <h4>处理统计</h4>
                  <div className="metric-text">
                    {asyncStatus.processingStats}
                  </div>
                </div>
                <div className="metric-section">
                  <h4>线程池状态</h4>
                  <div className="metric-text">
                    {asyncStatus.threadPoolStatus}
                  </div>
                </div>
              </div>
            ) : (
              <div className="loading">加载中...</div>
            )}
          </div>
        </div>

        {/* 性能报告 */}
        <div className="card full-width">
          <div className="card-header">
            <h3>详细性能报告</h3>
          </div>
          <div className="card-body">
            {performanceReport ? (
              <pre className="performance-report">
                {performanceReport}
              </pre>
            ) : (
              <div className="loading">加载中...</div>
            )}
          </div>
        </div>

        {/* 操作控制 */}
        <div className="card">
          <div className="card-header">
            <h3>手动操作</h3>
          </div>
          <div className="card-body">
            <div className="operation-section">
              <h4>手动触发撮合</h4>
              <div className="trigger-buttons">
                <button 
                  onClick={() => triggerMatching('000001')}
                  className="btn btn-outline-primary"
                >
                  平安银行
                </button>
                <button 
                  onClick={() => triggerMatching('000002')}
                  className="btn btn-outline-primary"
                >
                  万科A
                </button>
                <button 
                  onClick={() => triggerMatching('600036')}
                  className="btn btn-outline-primary"
                >
                  招商银行
                </button>
                <button 
                  onClick={() => triggerMatching('600519')}
                  className="btn btn-outline-primary"
                >
                  贵州茅台
                </button>
                <button 
                  onClick={() => triggerMatching('510050')}
                  className="btn btn-outline-primary"
                >
                  50ETF
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <style jsx>{`
        .monitor-dashboard {
          padding: 20px;
          max-width: 1200px;
          margin: 0 auto;
        }

        .dashboard-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 20px;
          padding-bottom: 15px;
          border-bottom: 2px solid #e8e8e8;
        }

        .header-left {
          display: flex;
          flex-direction: column;
          gap: 5px;
        }

        .dashboard-header h2 {
          margin: 0;
          color: #333;
        }

        .user-info {
          color: #666;
          font-size: 14px;
        }

        .header-controls {
          display: flex;
          gap: 10px;
        }

        .btn {
          padding: 8px 16px;
          border: none;
          border-radius: 4px;
          cursor: pointer;
          font-size: 14px;
          transition: all 0.3s;
        }

        .btn:disabled {
          opacity: 0.6;
          cursor: not-allowed;
        }

        .btn-primary {
          background-color: #1890ff;
          color: white;
        }

        .btn-primary:hover:not(:disabled) {
          background-color: #40a9ff;
        }

        .btn-success {
          background-color: #52c41a;
          color: white;
        }

        .btn-success:hover {
          background-color: #73d13d;
        }

        .btn-warning {
          background-color: #faad14;
          color: white;
        }

        .btn-warning:hover {
          background-color: #ffc53d;
        }

        .btn-danger {
          background-color: #f5222d;
          color: white;
        }

        .btn-danger:hover {
          background-color: #ff4d4f;
        }

        .btn-outline-primary {
          background-color: transparent;
          color: #1890ff;
          border: 1px solid #1890ff;
        }

        .btn-outline-primary:hover {
          background-color: #1890ff;
          color: white;
        }

        .btn-secondary {
          background-color: #6c757d;
          color: white;
        }

        .btn-secondary:hover {
          background-color: #5a6268;
        }

        .btn-outline-danger {
          background-color: transparent;
          color: #f5222d;
          border: 1px solid #f5222d;
        }

        .btn-outline-danger:hover {
          background-color: #f5222d;
          color: white;
        }

        .alert {
          padding: 12px;
          border-radius: 4px;
          margin-bottom: 20px;
        }

        .alert-danger {
          background-color: #fff2f0;
          border: 1px solid #ffccc7;
          color: #a8071a;
        }

        .dashboard-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
          gap: 20px;
        }

        .card {
          background: white;
          border-radius: 8px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          overflow: hidden;
        }

        .card.full-width {
          grid-column: 1 / -1;
        }

        .card-header {
          background-color: #fafafa;
          padding: 16px 20px;
          border-bottom: 1px solid #e8e8e8;
        }

        .card-header h3 {
          margin: 0;
          color: #333;
          font-size: 16px;
        }

        .card-body {
          padding: 20px;
        }

        .health-metrics {
          display: flex;
          flex-direction: column;
          gap: 12px;
        }

        .metric-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 8px 0;
          border-bottom: 1px solid #f0f0f0;
        }

        .metric-item:last-child {
          border-bottom: none;
        }

        .metric-label {
          font-weight: 500;
          color: #666;
        }

        .metric-value {
          font-weight: 600;
          color: #333;
        }

        .health-status {
          font-size: 16px;
          font-weight: bold;
        }

        .async-metrics {
          display: flex;
          flex-direction: column;
          gap: 16px;
        }

        .metric-section h4 {
          margin: 0 0 8px 0;
          color: #333;
          font-size: 14px;
        }

        .metric-text {
          background-color: #f8f9fa;
          padding: 12px;
          border-radius: 4px;
          font-family: monospace;
          font-size: 12px;
          line-height: 1.4;
          color: #333;
          word-break: break-all;
        }

        .performance-report {
          background-color: #f8f9fa;
          padding: 16px;
          border-radius: 4px;
          font-family: monospace;
          font-size: 12px;
          line-height: 1.4;
          color: #333;
          white-space: pre-wrap;
          max-height: 400px;
          overflow-y: auto;
          margin: 0;
        }

        .operation-section h4 {
          margin: 0 0 12px 0;
          color: #333;
          font-size: 14px;
        }

        .trigger-buttons {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
        }

        .trigger-buttons .btn {
          font-size: 12px;
          padding: 6px 12px;
        }

        .loading {
          text-align: center;
          color: #666;
          padding: 20px;
        }

        @media (max-width: 768px) {
          .dashboard-grid {
            grid-template-columns: 1fr;
          }
          
          .dashboard-header {
            flex-direction: column;
            gap: 15px;
            align-items: stretch;
          }
          
          .header-controls {
            justify-content: center;
          }
        }
      `}</style>
    </div>
  );
};

export default MonitorDashboard;