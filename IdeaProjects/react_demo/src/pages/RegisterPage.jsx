// src/pages/RegisterPage.jsx
import React, { useState } from "react";
import { Link } from "react-router-dom";

function RegisterPage({ apiBaseUrl }) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState(null);
    const [submitting, setSubmitting] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage(null);
        setSubmitting(true);

        try {
            const response = await fetch(`${apiBaseUrl}/v2/api/user`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    id: null,             // 让后台自己用 @GeneratedValue
                    username,
                    password,
                    role: "ROLE_USER",
                    enabled: true,
                }),
            });

            if (response.ok) {
                setMessage("User created successfully. You can now login.");
                setUsername("");
                setPassword("");
            } else {
                const text = await response.text();
                setMessage(`Failed to create user: ${response.status} ${text}`);
            }
        } catch (err) {
            console.error(err);
            setMessage("Network error, please try again.");
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <h2>Create Account</h2>
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
                            autoComplete="new-password"
                        />
                    </label>
                    <button type="submit" disabled={submitting}>
                        {submitting ? "Creating..." : "Create User"}
                    </button>
                </form>

                {message && <p className="message">{message}</p>}

                <p className="switch-link">
                    Already have an account? <Link to="/">Back to Login</Link>
                </p>
            </div>
        </div>
    );
}

export default RegisterPage;
