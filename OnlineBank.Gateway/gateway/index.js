const express = require("express");
const app = express();
const { createProxyMiddleware } = require("http-proxy-middleware");
const port = 3000;

// http://127.0.0.1:3000/ => http://127.0.0.1:8080/

app.use(
  "/api/bank-accounts",
  createProxyMiddleware({
    target: "http://127.0.0.1:8080",
  })
);
app.use(
  "/api/transactions",
  createProxyMiddleware({
    target: "http://127.0.0.1:8080",
  })
);
app.use(
  "/user_api",
  createProxyMiddleware({
    target: "http://127.0.0.1:7788",
  })
);
app.use(
  "/loan_api",
  createProxyMiddleware({
    target: "http://127.0.0.1:8877",
  })
);


app.listen(port, () => {
  console.log(`API-gateway on ${port}`);
});
