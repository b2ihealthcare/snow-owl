import 'antd/dist/antd.css';
import 'swagger-ui-react/swagger-ui.css';

import React from 'react';
import SwaggerUI from "swagger-ui-react"
import { Layout, Tabs, Row, Col, BackTop } from 'antd';

const { TabPane } = Tabs;
const { Header, Content } = Layout;

const soBaseUrl = process.env.SO_BASE_URL || 'http://localhost:8080/snowowl'

const App = () => (
  <>
    <BackTop />
    <Layout>
      <Header>
        <Row>
          <Col span={12} offset={6}>
            <img src={`${process.env.PUBLIC_URL}/logo-snow-owl.png`} alt="logo-snow-owl" height="50" style={{"max-width":"100%"}} />
          </Col>
        </Row>
      </Header>
      <Content>
        <Row>
          <Col span={12} offset={6}>
            <Tabs defaultActiveKey="admin">
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
            </Tabs>
          </Col>
        </Row>
      </Content>
    </Layout>
  </>
)

export default App;
