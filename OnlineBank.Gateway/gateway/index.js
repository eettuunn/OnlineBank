const express = require("express");
const app = express();
const { createProxyMiddleware } = require("http-proxy-middleware");
const port = 3000;

const http = require("http").Server(app);

// const host = 'http://92.255.79.135';
// const host = "http://92.118.114.182";
const host = "http://localhost";

app.use(
  "/api/bank-accounts",
  createProxyMiddleware({
    target: `${host}:8080`,
    ws: true,
    changeOrigin: true,
  })
);
app.use(
  "/api/transactions",
  createProxyMiddleware({
    target: `${host}:8080`,
    ws: true,
    changeOrigin: true,
  })
);
app.use(
  "/user_api",
  createProxyMiddleware({
    target: `${host}:7788`,
  })
);
app.use(
  "/loan_api",
  createProxyMiddleware({
    target: `${host}:8877`,
  })
);
app.use(
  "/ws",
  createProxyMiddleware({
    target: `${host}:8080`,
    ws: true,
    changeOrigin: true,
  })
);

http.listen(port, () => {
  console.log(`API-gateway on ${port}`);
});
