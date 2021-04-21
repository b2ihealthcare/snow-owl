import 'antd/dist/antd.css';
import 'rapidoc';

import React from 'react';
import { Layout, BackTop, Menu } from 'antd';

const { Content, Sider } = Layout;

class App extends React.Component {

  state = {
    selectedKey: 'admin',
    apis: [],
    serverUrl: process.env.REACT_APP_SO_BASE_URL || process.env.PUBLIC_URL
  }

  componentDidMount() {
    fetch(`${this.state.serverUrl}/admin/apis`)
      .then(response => response.json())
      .then(data => this.setState({ apis: data.items }));
  }

  onMenuSelect = (e) => {
    this.setState({
      selectedKey: e.key
    })
  }

  render() {
    const { apis, serverUrl } = this.state
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
            <rapi-doc
              spec-url = {`${serverUrl}/api-docs?group=${this.state.selectedKey}`}
			  server-url = {`${serverUrl}`}
              render-style = "view"
              layout = "row"
              schema-expand-level = "3"
              style = {{ height: "100vh", width: "100%" }}
              allow-spec-url-load="false"
              allow-spec-file-load="false"
              allow-server-selection="false"
            />
          </Content>
        </Layout>
      </>
    )
  }

}

export default App;
