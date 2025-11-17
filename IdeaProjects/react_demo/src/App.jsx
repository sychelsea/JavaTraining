import React, { useState } from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import HomePage from "./pages/HomePage";

const API_BASE_URL = "http://localhost:9091";

function App() {
    // 简单的前端登录状态（demo 用）
    const [auth, setAuth] = useState({
        username: null,
        basicToken: null, // "Basic xxx"，用在调用需要认证的 API 上
    });

    const handleLoginSuccess = (username, basicToken) => {
        setAuth({ username, basicToken });
    };

    const handleLogout = () => {
        setAuth({ username: null, basicToken: null });
    };

    const isLoggedIn = !!auth.username;

    return (
        <div className="app-container">
            <Routes>
                <Route
                    path="/"
                    element={
                        <LoginPage
                            apiBaseUrl={API_BASE_URL}
                            onLoginSuccess={handleLoginSuccess}
                            isLoggedIn={isLoggedIn}
                        />
                    }
                />
                <Route
                    path="/register"
                    element={<RegisterPage apiBaseUrl={API_BASE_URL} />}
                />
                <Route
                    path="/home"
                    element={
                        isLoggedIn ? (
                            <HomePage auth={auth} onLogout={handleLogout} />
                        ) : (
                            <Navigate to="/" replace />
                        )
                    }
                />
            </Routes>
        </div>
    );
}

export default App;
