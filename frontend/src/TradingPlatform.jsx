import React, { useState, useEffect } from 'react';
import { Layout, Menu, Table, Card, Button, Modal, Form, Input, Select, message, Statistic, Row, Col, Tag } from 'antd';
import { ShoppingCartOutlined, BarChartOutlined, UserOutlined, LogoutOutlined, MonitorOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const { Header, Sider, Content } = Layout;
const { Option } = Select;

const TradingPlatform = ({ user, onLogout }) => {
  const navigate = useNavigate();
  const [selectedMenu, setSelectedMenu] = useState('products');
  const [products, setProducts] = useState([]);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [orderModalVisible, setOrderModalVisible] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [form] = Form.useForm();

  // 获取产品列表
  const fetchProducts = async () => {
    setLoading(true);
    try {
      const response = await axios.get('/api/trade/products/tradable');
      console.log('产品列表响应:', response.data); // 调试日志
      if (response.data.success) {
        // 后端返回的是 PageInfo 对象，需要从 list 字段获取产品数组
        const pageInfo = response.data.data;
        if (pageInfo && Array.isArray(pageInfo.list)) {
          setProducts(pageInfo.list);
        } else {
          console.warn('产品数据格式异常:', pageInfo);
          setProducts([]);
        }
      } else {
        message.error('获取产品列表失败');
      }
    } catch (error) {
      console.error('获取产品列表错误:', error);
      // 显示后端返回的具体错误信息
      if (error.response && error.response.data && error.response.data.message) {
        message.error(error.response.data.message);
      } else if (error.message) {
        message.error(error.message);
      } else {
        message.error('网络请求失败');
      }
    }
    setLoading(false);
  };

  // 获取订单列表
  const fetchOrders = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`/api/trade/orders/user/${user.id}`);
      console.log('订单列表响应:', response.data); // 调试日志
      if (response.data.success) {
        // 检查是否是分页数据结构
        const orderData = response.data.data;
        if (orderData && Array.isArray(orderData.list)) {
          // 如果是分页结构，从list字段获取订单数组
          console.log('设置订单数据(分页):', orderData.list); // 调试日志
          const processedOrders = orderData.list.map((order, index) => ({
            ...order,
            id: order.id || `temp_${Date.now()}_${index}` // 确保每个订单都有唯一的id
          }));
          setOrders(processedOrders);
        } else if (Array.isArray(orderData)) {
          // 如果直接是数组
          console.log('设置订单数据(数组):', orderData); // 调试日志
          const processedOrders = orderData.map((order, index) => ({
            ...order,
            id: order.id || `temp_${Date.now()}_${index}` // 确保每个订单都有唯一的id
          }));
          setOrders(processedOrders);
        } else {
          console.warn('订单数据格式异常:', orderData);
          setOrders([]);
        }
      } else {
        setOrders([]); // 确保orders始终是数组
        message.error(response.data.message || '获取订单列表失败');
      }
    } catch (error) {
      console.error('获取订单列表错误:', error);
      setOrders([]); // 确保orders始终是数组
      // 显示后端返回的具体错误信息
      if (error.response && error.response.data && error.response.data.message) {
        message.error(error.response.data.message);
      } else if (error.message) {
        message.error(error.message);
      } else {
        message.error('网络请求失败');
      }
    }
    setLoading(false);
  };

  useEffect(() => {
    if (selectedMenu === 'products') {
      fetchProducts();
    } else if (selectedMenu === 'orders') {
      fetchOrders();
    }
  }, [selectedMenu, user.id]);

  // 下单
  const handleOrder = async (values) => {
    try {
      // 验证用户信息
      if (!user || !user.id) {
        message.error('用户信息缺失，请重新登录');
        return;
      }
      
      const orderData = {
        userId: user.id,
        productCode: selectedProduct.code,
        productName: selectedProduct.name,
        orderType: values.orderType,
        price: parseFloat(values.price),
        quantity: parseInt(values.quantity),
        source: 1
      };
      
      console.log('提交订单数据:', orderData); // 调试日志
      
      const response = await axios.post('/api/trade/orders', orderData);
      if (response.data.success) {
        const orderTypeText = values.orderType === 1 ? '买入' : '卖出';
        message.success(`${orderTypeText}订单提交成功！`);
        setOrderModalVisible(false);
        form.resetFields();
        // 下单成功后自动切换到订单页面并刷新订单列表
        setSelectedMenu('orders');
        fetchOrders();
      } else {
        message.error(response.data.message || '下单失败');
      }
    } catch (error) {
      console.error('下单失败:', error);
      // 显示后端返回的具体错误信息
      if (error.response && error.response.data && error.response.data.message) {
        message.error(error.response.data.message);
      } else if (error.message) {
        message.error(error.message);
      } else {
        message.error('网络请求失败');
      }
    }
  };

  // 撤销订单
  const handleCancelOrder = async (orderId) => {
    try {
      // 验证用户信息
      if (!user || !user.id) {
        message.error('用户信息缺失，请重新登录');
        return;
      }
      
      console.log('撤销订单请求:', { orderId, userId: user.id }); // 详细日志
      
      const response = await axios.put(`/api/trade/orders/${orderId}/cancel?userId=${user.id}`);
      
      console.log('撤销订单响应:', response); // 详细日志
      
      if (response.data.success) {
        message.success('撤销成功！');
        fetchOrders();
      } else {
        message.error(response.data.message || '撤销失败');
      }
    } catch (error) {
      console.error('撤销订单失败 - 完整错误信息:', error);
      console.error('错误响应数据:', error.response?.data);
      console.error('错误状态码:', error.response?.status);
      console.error('错误状态文本:', error.response?.statusText);
      
      // 显示后端返回的具体错误信息
      if (error.response && error.response.data && error.response.data.message) {
        message.error(`撤销失败: ${error.response.data.message}`);
      } else if (error.response && error.response.status) {
        message.error(`撤销失败: HTTP ${error.response.status} - ${error.response.statusText || '服务器错误'}`);
      } else if (error.message) {
        message.error(`撤销失败: ${error.message}`);
      } else {
        message.error('撤销失败: 网络请求失败');
      }
    }
  };

  // 产品列表列定义
  const productColumns = [
    {
      title: '产品代码',
      dataIndex: 'code',
      key: 'code',
    },
    {
      title: '产品名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      render: (type) => {
        const typeMap = {
          1: '股票',
          2: '基金',
          3: '债券'
        };
        return typeMap[type] || '未知类型';
      }
    },
    {
      title: '当前价格',
      dataIndex: 'currentPrice',
      key: 'currentPrice',
      render: (price) => `¥${price?.toFixed(2) || '0.00'}`,
    },
    {
      title: '昨收价',
      dataIndex: 'previousClose',
      key: 'previousClose',
      render: (price) => `¥${price?.toFixed(2) || '0.00'}`,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => {
        const statusMap = {
          1: { color: 'green', text: '交易中' },
          2: { color: 'orange', text: '停牌' },
          3: { color: 'red', text: '退市' }
        };
        const statusInfo = statusMap[status] || { color: 'default', text: '未知状态' };
        return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>;
      }
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Button 
          type="primary" 
          size="small"
          disabled={record.status !== 1}
          onClick={() => {
            setSelectedProduct(record);
            setOrderModalVisible(true);
          }}
        >
          交易
        </Button>
      ),
    },
  ];

  // 订单列表列定义
  const orderColumns = [
    {
      title: '订单ID',
      dataIndex: 'id',
      key: 'id',
    },
    {
      title: '产品代码',
      dataIndex: 'productCode',
      key: 'productCode',
    },
    {
      title: '产品名称',
      dataIndex: 'productName',
      key: 'productName',
    },
    {
      title: '订单类型',
      dataIndex: 'orderType',
      key: 'orderType',
      render: (type) => type === 1 ? <Tag color="green">买入</Tag> : <Tag color="red">卖出</Tag>,
    },
    {
      title: '委托价格',
      dataIndex: 'price',
      key: 'price',
      render: (price) => `¥${price?.toFixed(2) || '0.00'}`,
    },
    {
      title: '委托数量',
      dataIndex: 'quantity',
      key: 'quantity',
    },
    {
      title: '已成交',
      dataIndex: 'filledQuantity',
      key: 'filledQuantity',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => {
        const statusMap = {
          1: { color: 'blue', text: '待成交' },
          2: { color: 'orange', text: '部分成交' },
          3: { color: 'green', text: '完全成交' },
          4: { color: 'red', text: '已撤销' }
        };
        const statusInfo = statusMap[status] || { color: 'default', text: '未知状态' };
        return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>;
      }
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      render: (time) => new Date(time).toLocaleString(),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        record.status === 1 || record.status === 2 ? (
          <Button 
            type="link" 
            danger
            size="small"
            onClick={() => handleCancelOrder(record.id)}
          >
            撤销
          </Button>
        ) : null
      ),
    },
  ];

  const menuItems = [
    {
      key: 'products',
      icon: <BarChartOutlined />,
      label: '产品行情',
    },
    {
      key: 'orders',
      icon: <ShoppingCartOutlined />,
      label: '我的订单',
    },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', background: '#001529' }}>
        <div style={{ color: 'white', fontSize: '20px', fontWeight: 'bold' }}>
          交易平台
        </div>
        <div style={{ color: 'white', display: 'flex', alignItems: 'center' }}>
          <Button 
            type="text" 
            icon={<MonitorOutlined />} 
            style={{ color: 'white', marginRight: 16 }}
            onClick={() => navigate('/monitor')}
          >
            系统监控
          </Button>
          <UserOutlined style={{ marginRight: 8 }} />
          {user?.username || '用户'}
          <Button 
            type="text" 
            icon={<LogoutOutlined />} 
            style={{ color: 'white', marginLeft: 16 }}
            onClick={onLogout}
          >
            退出
          </Button>
        </div>
      </Header>
      
      <Layout>
        <Sider width={200} style={{ background: '#fff' }}>
          <Menu
            mode="inline"
            selectedKeys={[selectedMenu]}
            style={{ height: '100%', borderRight: 0 }}
            items={menuItems}
            onClick={({ key }) => setSelectedMenu(key)}
          />
        </Sider>
        
        <Layout style={{ padding: '24px' }}>
          <Content style={{ background: '#fff', padding: 24, margin: 0, minHeight: 280 }}>
            {selectedMenu === 'products' && (
              <div>
                <Row gutter={16} style={{ marginBottom: 24 }}>
                  <Col span={6}>
                    <Card>
                      <Statistic title="可交易产品" value={products.length} />
                    </Card>
                  </Col>
                  <Col span={6}>
                    <Card>
                      <Statistic title="交易中产品" value={Array.isArray(products) ? products.filter(p => p.status === 1).length : 0} />
                    </Card>
                  </Col>
                </Row>
                <Table
                  columns={productColumns}
                  dataSource={Array.isArray(products) ? products : []}
                  rowKey="id"
                  loading={loading}
                  pagination={{ pageSize: 10 }}
                />
              </div>
            )}
            
            {selectedMenu === 'orders' && (
              <div>
                <Row gutter={16} style={{ marginBottom: 24 }}>
                  <Col span={6}>
                    <Card>
                      <Statistic title="总订单数" value={orders.length} />
                    </Card>
                  </Col>
                  <Col span={6}>
                    <Card>
                      <Statistic title="待成交" value={Array.isArray(orders) ? orders.filter(o => o.status === 1).length : 0} />
                    </Card>
                  </Col>
                  <Col span={6}>
                    <Card>
                      <Statistic title="已成交" value={Array.isArray(orders) ? orders.filter(o => o.status === 3).length : 0} />
                    </Card>
                  </Col>
                </Row>
                <Table
                  columns={orderColumns}
                  dataSource={Array.isArray(orders) ? orders : []}
                  rowKey="id"
                  loading={loading}
                  pagination={{ pageSize: 10 }}
                />
              </div>
            )}
          </Content>
        </Layout>
      </Layout>
      
      {/* 下单弹窗 */}
      <Modal
        title={`交易 - ${selectedProduct?.name}`}
        open={orderModalVisible}
        onCancel={() => {
          setOrderModalVisible(false);
          form.resetFields();
        }}
        footer={null}
      >
        <Form
          form={form}
          onFinish={handleOrder}
          layout="vertical"
        >
          <Form.Item
            label="订单类型"
            name="orderType"
            rules={[{ required: true, message: '请选择订单类型' }]}
          >
            <Select placeholder="请选择订单类型">
              <Option value={1}>买入</Option>
              <Option value={2}>卖出</Option>
            </Select>
          </Form.Item>
          
          <Form.Item
            label="委托价格"
            name="price"
            rules={[{ required: true, message: '请输入委托价格' }]}
            initialValue={selectedProduct?.currentPrice}
          >
            <Input type="number" step="0.01" addonAfter="元" />
          </Form.Item>
          
          <Form.Item
            label="委托数量"
            name="quantity"
            rules={[
              { required: true, message: '请输入委托数量' },
              { type: 'number', min: 100, message: '委托数量不能少于100股', transform: (value) => Number(value) }
            ]}
          >
            <Input type="number" addonAfter="股" placeholder="最少100股" />
          </Form.Item>
          
          <Form.Item>
            <Button type="primary" htmlType="submit" block>
              提交订单
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </Layout>
  );
};

export default TradingPlatform;