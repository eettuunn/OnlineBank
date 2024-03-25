import { BrowserRouter } from "react-router-dom";
import "./App.css";
import LoginForm from "./LoginForm/LoginForm";

function App() {
  return (
    <BrowserRouter>
      <LoginForm />;
    </BrowserRouter>
  );
}

export default App;
