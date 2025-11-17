// src/pages/LoginPage.jsx
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

function LoginPage({ apiBaseUrl, onLoginSuccess, isLoggedIn }) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(null);
    const [submitting, setSubmitting] = useState(false);
    const navigate = useNavigate();

    // 如果已经登录，直接跳到 home
    if (isLoggedIn) {
        navigate("/home");
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setSubmitting(true);

        // Basic Auth: "Basic base64(username:password)"
        const basicToken =
            "Basic " + window.btoa(`${username}:${password}`);

        try {
            // ⚠️ 这里用一个“受保护的接口”来测试用户名密码是否正确
            // 你可以改成 /v2/api/user/me 之类的接口
            const response = await fetch(`${apiBaseUrl}/v2/api/user/1`, {
                method: "GET",
                headers: {
                    Authorization: basicToken,
                },
            });

            if (response.ok) {
                onLoginSuccess(username, basicToken);
                navigate("/home");
            } else if (response.status === 401) {
                setError("Invalid username or password.");
            } else {
                setError(`Login failed: ${response.status}`);
            }
        } catch (err) {
            console.error(err);
            setError("Network error, please try again.");
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <h2>Login</h2>
                <form onSubmit={handleSubmit} className="auth-form">
                    <label>
                        Username
                        <input
                            type="text"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            autoComplete="username"
                        />
                    </label>

                    <label>
                        Password
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            autoComplete="current-password"
                        />
                    </label>

                    <button type="submit" disabled={submitting}>
                        {submitting ? "Logging in..." : "Login"}
                    </button>
                </form>

                {error && <p className="error">{error}</p>}

                <p className="switch-link">
                    Don&apos;t have an account?{" "}
                    <Link to="/register">Create one</Link>
                </p>
            </div>
        </div>
    );
}

export default LoginPage;
