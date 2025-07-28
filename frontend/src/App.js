import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import RegisterForm from './RegisterForm';
import LoginForm from './LoginForm';
import TradingPlatform from './TradingPlatform';
import MonitorDashboard from './MonitorDashboard';
import 'antd/dist/reset.css';
import { Button } from 'antd';
import axios from 'axios';

// 配置axios响应拦截器
axios.interceptors.response.use(
  (response) => {
    // 对于2xx状态码，直接返回响应
    return response;
  },
  (error) => {
    // 对于非2xx状态码，检查是否有响应数据
    if (error.response && error.response.data) {
      // 如果后端返回了标准的ResponseVO格式，将其转换为正常响应
      // 这样前端就可以统一处理success字段
      const responseData = error.response.data;
      if (responseData.hasOwnProperty('success') && responseData.hasOwnProperty('message')) {
        // 创建一个模拟的成功响应，但保持原始数据
        return Promise.resolve({
          ...error.response,
          status: 200,
          data: responseData
        });
      }
    }
    // 对于其他错误，继续抛出
    return Promise.reject(error);
  }
);

// 认证页面组件
function AuthPage({ user, onLoginSuccess }) {
  const navigate = useNavigate();
  const location = useLocation();
  const isLoginPage = location.pathname === '/login';

  return (
    <div className="App">
      <div style={{ textAlign: 'center', margin: 24 }}>
        <Button 
          type={isLoginPage ? 'primary' : 'default'} 
          onClick={() => navigate('/login')} 
          style={{ marginRight: 8 }}
        >
          登录
        </Button>
        <Button 
          type={!isLoginPage ? 'primary' : 'default'} 
          onClick={() => navigate('/register')}
        >
          注册
        </Button>
      </div>
      {isLoginPage ? <LoginForm onLoginSuccess={onLoginSuccess} /> : <RegisterForm />}
    </div>
  );
}

function App() {
  const [user, setUser] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  // 应用启动时检查本地存储的登录状态
  useEffect(() => {
    const savedUser = localStorage.getItem('user');
    const savedToken = localStorage.getItem('token');
    
    if (savedUser && savedToken) {
      try {
        const userData = JSON.parse(savedUser);
        setUser(userData);
        setIsLoggedIn(true);
        // 设置 axios 默认请求头
        axios.defaults.headers.common['Authorization'] = `Bearer ${savedToken}`;
        console.log('恢复登录状态:', userData);
      } catch (error) {
        console.error('解析用户数据失败:', error);
        // 清除无效数据
        localStorage.removeItem('user');
        localStorage.removeItem('token');
      }
    }
    setIsLoading(false);
  }, []);

  const handleLoginSuccess = (userData) => {
    setUser(userData);
    setIsLoggedIn(true);
    // 保存到本地存储
    localStorage.setItem('user', JSON.stringify(userData));
    if (userData.token) {
      localStorage.setItem('token', userData.token);
      // 设置 axios 默认请求头
      axios.defaults.headers.common['Authorization'] = `Bearer ${userData.token}`;
    }
    console.log('保存登录状态:', userData);
  };

  const handleLogout = () => {
    setUser(null);
    setIsLoggedIn(false);
    // 清除本地存储
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    // 清除 axios 默认请求头
    delete axios.defaults.headers.common['Authorization'];
    console.log('清除登录状态');
  };

  // 应用加载中显示
  if (isLoading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh',
        fontSize: '16px'
      }}>
        加载中...
      </div>
    );
  }

  return (
    <Router>
      <Routes>
        {isLoggedIn && user ? (
          <>
            <Route 
              path="/" 
              element={<TradingPlatform user={user} onLogout={handleLogout} />} 
            />
            <Route 
              path="/monitor" 
              element={<MonitorDashboard user={user} onLogout={handleLogout} />} 
            />
            <Route path="*" element={<Navigate to="/" replace />} />
          </>
        ) : (
          <>
            <Route 
              path="/login" 
              element={<AuthPage user={user} onLoginSuccess={handleLoginSuccess} />} 
            />
            <Route 
              path="/register" 
              element={<AuthPage user={user} onLoginSuccess={handleLoginSuccess} />} 
            />
            <Route path="*" element={<Navigate to="/login" replace />} />
          </>
        )}
      </Routes>
    </Router>
  );
}

export default App;
