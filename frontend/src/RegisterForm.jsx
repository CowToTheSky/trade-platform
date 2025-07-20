import React, { useState } from "react";
import { Form, Input, Button, message } from "antd";
import axios from "axios";

const RegisterForm = () => {
  const [loading, setLoading] = useState(false);

  const onFinish = async (values) => {
    setLoading(true);
    try {
      const res = await axios.post("/api/user/register", values);
      if (res.data.success) {
        message.success("注册成功！");
      } else {
        message.error(res.data.message || "注册失败");
      }
    } catch (e) {
      message.error("请求失败");
    }
    setLoading(false);
  };

  return (
    <Form
      name="register"
      onFinish={onFinish}
      style={{ maxWidth: 400, margin: "50px auto" }}
      labelCol={{ span: 6 }}
      wrapperCol={{ span: 16 }}
    >
      <Form.Item
        label="用户名"
        name="username"
        rules={[
          { required: true, message: "请输入用户名" },
          { min: 4, max: 20, message: "用户名长度4-20位" },
        ]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        label="密码"
        name="password"
        rules={[
          { required: true, message: "请输入密码" },
          { min: 6, max: 20, message: "密码长度6-20位" },
        ]}
      >
        <Input.Password />
      </Form.Item>
      <Form.Item label="邮箱" name="email">
        <Input />
      </Form.Item>
      <Form.Item label="手机号" name="phone">
        <Input />
      </Form.Item>
      <Form.Item wrapperCol={{ offset: 6, span: 16 }}>
        <Button type="primary" htmlType="submit" loading={loading}>
          注册
        </Button>
      </Form.Item>
    </Form>
  );
};

export default RegisterForm; 