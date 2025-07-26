import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import RegisterForm from './RegisterForm';
import LoginForm from './LoginForm';
import TradingPlatform from './TradingPlatform';
import 'antd/dist/reset.css';
import { Button } from 'antd';
import axios from 'axios';

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
