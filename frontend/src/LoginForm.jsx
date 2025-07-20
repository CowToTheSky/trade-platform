import React, { useState } from "react";
import { Form, Input, Button, message } from "antd";
import axios from "axios";

const LoginForm = () => {
  const [loading, setLoading] = useState(false);

  const onFinish = async (values) => {
    setLoading(true);
    try {
      const res = await axios.post("/api/user/login", values);
      if (res.data.success) {
        message.success("登录成功！");
      } else {
        message.error(res.data.message || "登录失败");
      }
    } catch (e) {
      message.error("请求失败");
    }
    setLoading(false);
  };

  return (
    <Form
      name="login"
      onFinish={onFinish}
      style={{ maxWidth: 400, margin: "50px auto" }}
      labelCol={{ span: 6 }}
      wrapperCol={{ span: 16 }}
    >
      <Form.Item
        label="用户名"
        name="username"
        rules={[{ required: true, message: "请输入用户名" }]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        label="密码"
        name="password"
        rules={[{ required: true, message: "请输入密码" }]}
      >
        <Input.Password />
      </Form.Item>
      <Form.Item wrapperCol={{ offset: 6, span: 16 }}>
        <Button type="primary" htmlType="submit" loading={loading}>
          登录
        </Button>
      </Form.Item>
    </Form>
  );
};

export default LoginForm; 