// App.jsx
import React, { useState } from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import HomePage from "./pages/HomePage";
import RegisterPage from "./pages/RegisterPage";

const API_BASE_URL = "http://localhost:9091";

function App() {
    const [auth, setAuth] = useState({
        user: null,        // { id, username, role, ... }
        basicToken: null,  // "Basic xxxx"
    });

    const handleLoginSuccess = (user, basicToken) => {
        setAuth({ user, basicToken });
    };

    const handleUserUpdate = (newUser) => {
        setAuth((prev) => ({ ...prev, user: newUser }));
    };

    const handleLogout = () => {
        setAuth({ user: null, basicToken: null });
    };

    const isLoggedIn = !!auth.user;

    return (
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
                        <HomePage
                            apiBaseUrl={API_BASE_URL}
                            auth={auth}
                            onLogout={handleLogout}
                            onUserUpdate={handleUserUpdate}
                        />
                    ) : (
                        <Navigate to="/" replace />
                    )
                }
            />
        </Routes>
    );
}

export default App;
