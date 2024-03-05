const express = require("express");
const app = express();
const { createProxyMiddleware } = require("http-proxy-middleware");
const port = 3000;

// http://127.0.0.1:3000/core-service => http://127.0.0.1:8080/

app.use(
  "/core-service",
  createProxyMiddleware({
    target: "http://127.0.0.1:8080",
    pathRewrite: {
      "^/core-service": "",
    },
  })
);

app.listen(port, () => {
  console.log(`API-gateway on ${port}`);
});
