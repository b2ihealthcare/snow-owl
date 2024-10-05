import "antd/dist/antd.css";
import "rapidoc";

import Favicon from "./assets/favicon.svg?react";

import React from "react";
import { BackTop } from "antd";
import { parse } from "qs";

class App extends React.Component {
  state = {
    selectedKey: "core",
    apis: [],
    serverUrl: import.meta.env.VITE_SO_BASE_URL || import.meta.env.BASE_URL,
  };

  componentDidMount() {
    console.log(this.state.serverUrl);
    fetch(`${this.state.serverUrl}/apis`)
      .then((response) => response.json())
      .then((data) => {
        const queryParams = parse(window.location.search, {
          ignoreQueryPrefix: true,
          parameterLimit: 1,
        });
        this.setState({
          apis: data.items,
          selectedKey: queryParams?.api || "core",
        });
      });
  }

  onMenuSelect = (e) => {
    this.setState({
      selectedKey: e.target.value,
    });
  };

  render() {
    const { apis, serverUrl, selectedKey } = this.state;
    return (
      <>
        <BackTop />
        <rapi-doc
          key="api-docs"
          theme="light"
          spec-url={`${serverUrl}/api-docs/${this.state.selectedKey}`}
          server-url={`${serverUrl}`}
          route-prefix={`?api=${this.state.selectedKey}#`}
          render-style="focused"
          show-method-in-nav-bar="as-colored-text"
          nav-item-spacing="default"
          use-path-in-nav-bar="true"
          default-schema-tab="example"
          layout="row"
          schema-expand-level="3"
          style={{ height: "100vh", width: "100%" }}
          show-header="false"
          allow-spec-url-load="false"
          allow-spec-file-load="false"
          allow-server-selection="false"
          sort-endpoints-by="path"
          persist-auth="false"
        >
          <div slot="nav-logo">
            <div
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                fontSize: "24px",
              }}
            >
              <Favicon
                style={{ width: "40px", marginRight: "8px" }}
                alt="logo"
              />
              <span style={{ color: "#fff" }}> Snow Owl API Docs </span>
            </div>
            <div
              style={{
                display: "flex",
                flexDirection: "row",
                alignItems: "center",
                justifyContent: "center",
                paddingLeft: "8px",
              }}
            >
              <select
                style={{
                  width: "100%",
                  marginTop: "16px",
                  color: "var(--nav-hover-text-color)",
                  borderColor: "var(--nav-accent-color)",
                  backgroundColor: "var(--nav-hover-bg-color)",
                }}
                name="apis"
                onChange={(e) => this.onMenuSelect(e)}
                value={selectedKey}
              >
                {apis.map((api) => (
                  <option key={api.id} value={`${api.id}`}>
                    {api.title}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </rapi-doc>
      </>
    );
  }
}

export default App;
