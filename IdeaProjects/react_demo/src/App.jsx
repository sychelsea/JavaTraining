import React, { useState } from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import HomePage from "./pages/HomePage";
import RegisterPage from "./pages/RegisterPage";

// centralize all backend configuration and avoid hardcoding URLs inside each page
const API_BASE_URL = "http://localhost:9091";

function App() {
    const [auth, setAuth] = useState({
        user: null,        // { id, username, role, ... }
        basicToken: null,  // "Basic xxxx"
    });

    const handleLoginSuccess = (user, basicToken) => {
        setAuth({ user, basicToken });
    };

    // Update the auth state in App with the new user data
    //   - child informs parent about some change via a callback prop
    //   - functional update form
    //       Direct value form: setAuth({ user: newUser, basicToken: "something" });
    //       "prev" is the previous auth state
    //   - spread + immutability
    //       { ...prev, user: newUser } does two things:
    //          1. ...prev copies all existing fields of auth (both user and basicToken) into a new object.
    //          2. user: newUser overrides the user field in that new object.
    // State should be treated as immutable. You always create a new object instead of modifying the old one in place.
    const handleUserUpdate = (newUser) => {
        setAuth((prev) => ({ ...prev, user: newUser }));
    };

    const handleLogout = () => {
        setAuth({ user: null, basicToken: null });
    };

    // !!: Object => Boolean (object - true, null - false)
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
