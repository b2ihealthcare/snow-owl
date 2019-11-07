import 'antd/dist/antd.css';
import 'swagger-ui-react/swagger-ui.css';

import React from 'react';
import SwaggerUI from "swagger-ui-react"
import { Layout, BackTop, Menu } from 'antd';

const { Content, Sider } = Layout;

class App extends React.Component {

  state = {
    selectedKey: 'admin'
  }

  onMenuSelect = (e) => {
    this.setState({
      selectedKey: e.key
    })
  }

  render() {
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
              <Menu.Item key="admin">Admin API</Menu.Item>
              <Menu.Item key="snomed">SNOMED CT API</Menu.Item>
              <Menu.Item key="fhir">FHIR API</Menu.Item>
              <Menu.Item key="cis">CIS API</Menu.Item>
            </Menu>
          </Sider>
          <Content>
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
