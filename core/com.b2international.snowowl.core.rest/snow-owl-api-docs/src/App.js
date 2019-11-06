import 'antd/dist/antd.css';
//import 'swagger-ui-react/swagger-ui.css';
import 'swagger-ui-themes/themes/3.x/theme-flattop.css'

import React from 'react';
import SwaggerUI from "swagger-ui-react"
import { Layout, Tabs, BackTop, Menu, Icon } from 'antd';

const { TabPane } = Tabs;
const { Header, Content, Sider } = Layout;

const soBaseUrl = process.env.REACT_APP_SO_BASE_URL

const App = () => (
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
          <img src={`${process.env.PUBLIC_URL}/logo-snow-owl.png`} alt="logo-snow-owl" height="50" style={{"max-width":"100%"}} />
        </div>
        <Menu
          style={{ width: 256 }}
          defaultSelectedKeys={['1']}
          defaultOpenKeys={['sub1']}
          mode="inline"
          theme="dark"
        >
          <Menu.Item key="1">Admin API</Menu.Item>
          <Menu.Item key="2">SNOMED CT API</Menu.Item>
          <Menu.Item key="3">FHIR API</Menu.Item>
          <Menu.Item key="4">CIS API</Menu.Item>
        </Menu>
      </Sider>
      <Content>
        {/* <Tabs defaultActiveKey="admin">
          <TabPane tab="Admin API" key="admin">
            <SwaggerUI url={`${soBaseUrl}/api-docs?group=admin`} />
          </TabPane>
          <TabPane tab="SNOMED CT API" key="snomed">
            <SwaggerUI url={`${soBaseUrl}/api-docs?group=snomed`} />
          </TabPane>
          <TabPane tab="FHIR API" key="fhir">
            <SwaggerUI url={`${soBaseUrl}/api-docs?group=fhir`} />
          </TabPane>
          <TabPane tab="CIS API" key="cis">
            <SwaggerUI url={`${soBaseUrl}/api-docs?group=cis`} />
          </TabPane>
        </Tabs> */}
      </Content>
    </Layout>
  </>
)

export default App;
