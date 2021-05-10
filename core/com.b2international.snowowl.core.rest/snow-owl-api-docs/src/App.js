import 'antd/dist/antd.css';
import 'swagger-ui-react/swagger-ui.css';

import React from 'react';
import SwaggerUI from "swagger-ui-react"
import { Layout, BackTop, Menu } from 'antd';

const { Content, Sider } = Layout;

class App extends React.Component {

  state = {
    selectedKey: 'core',
    apis: []
  }

  componentDidMount() {
    fetch(`${process.env.REACT_APP_SO_BASE_URL}/apis`)
      .then(response => response.json())
      .then(data => this.setState({ apis: data.items }));
  }

  onMenuSelect = (e) => {
    this.setState({
      selectedKey: e.key
    })
  }

  render() {
    const apis = this.state.apis
    return (
      <>
        <BackTop />
        <Layout>
          <Sider
            style={{
              overflow: 'auto',
              height: '100vh',
              position: 'fixed',
              left: 0,
            }}
          >
            <div className="logo">
              <img src={`${process.env.PUBLIC_URL}/logo-snow-owl.png`} alt="logo-snow-owl" height="60" style={{maxWidth:"100%"}} />
            </div>
            <Menu
              onSelect = { this.onMenuSelect }
              selectedKeys = { [ this.state.selectedKey ] }
              mode="inline"
              theme="dark"
            >
              { 
                apis.map(api => <Menu.Item key={`${api.id}`}>{api.title}</Menu.Item>)
              }
            </Menu>
          </Sider>
          <Content style={{ marginLeft: 200 }}>
            <SwaggerUI 
              url={`${process.env.REACT_APP_SO_BASE_URL}/api-docs?group=${this.state.selectedKey}`} 
              docExpansion = "list"
            />
          </Content>
        </Layout>
      </>
    )
  }

}

export default App;
