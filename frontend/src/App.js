import React, { useState } from 'react';
import RegisterForm from './RegisterForm';
import LoginForm from './LoginForm';
import 'antd/dist/reset.css';
import { Button } from 'antd';

function App() {
  const [showLogin, setShowLogin] = useState(true);
  return (
    <div className="App">
      <div style={{ textAlign: 'center', margin: 24 }}>
        <Button type={showLogin ? 'primary' : 'default'} onClick={() => setShowLogin(true)} style={{ marginRight: 8 }}>登录</Button>
        <Button type={!showLogin ? 'primary' : 'default'} onClick={() => setShowLogin(false)}>注册</Button>
      </div>
      {showLogin ? <LoginForm /> : <RegisterForm />}
    </div>
  );
}

export default App;
