import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";

function LoginPage({ apiBaseUrl, onLoginSuccess, isLoggedIn }) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(null);
    const [submitting, setSubmitting] = useState(false);
    const navigate = useNavigate();

    if (isLoggedIn) {
        navigate("/home");  // Auto-redirect if already logged in
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setSubmitting(true);

        const basicToken = "Basic " + window.btoa(`${username}:${password}`); // window.btoa() converts "user:pass" into Base64

        try {
            const res = await axios.get(`${apiBaseUrl}/v2/api/me`, {
                headers: {
                    Authorization: basicToken,
                },
            });

            const user = res.data; // {id, username, role}
            onLoginSuccess(user, basicToken);
            navigate("/home");
        } catch (err) {
            if (err.response) {
                if (err.response.status === 401) {
                    setError("Invalid username or password.");
                } else {
                    setError(`Login failed: ${err.response.status}`);
                }
            } else {
                setError("Network error, please try again.");
            }
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
                        />
                    </label>

                    <label>
                        Password
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
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
