import 'antd/dist/antd.css';
import 'rapidoc';

import React from 'react';
import { BackTop } from 'antd';

class App extends React.Component {

  state = {
    selectedKey: 'core',
    apis: [],
    serverUrl: process.env.REACT_APP_SO_BASE_URL || process.env.PUBLIC_URL
  }

  componentDidMount() {
    fetch(`${this.state.serverUrl}/apis`)
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
        <rapi-doc
          theme = "light"
          spec-url = {`${serverUrl}/api-docs/${this.state.selectedKey}`}
          server-url = {`${serverUrl}`}
          show-header="false"
          render-style = "focused"
          default-schema-tab = "example"
          layout = "row"
          schema-expand-level = "3"
          style = {{ height: "100vh", width: "100%" }}
          allow-spec-url-load="false"
          allow-spec-file-load="false"
          allow-server-selection="false"
        >
          <div slot="nav-logo" style={{ display: "flex", alignItems: "center", justifyContent: "center", fontSize: "24px" }}> 
            <img src = "./assets/favicon.svg" style={{ width: "40px", marginRight: "8px" }} alt="logo" />
            <span style={{ color: "#fff" }}> Snow Owl API </span>
          </div>
        </rapi-doc>
      </>
    )
  }

}

export default App;
