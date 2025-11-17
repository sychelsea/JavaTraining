import React, { useState } from "react";
import { Link } from "react-router-dom";

function RegisterPage({ apiBaseUrl }) {
    const [username, setUsername] = useState("");  // Controlled input field (text)
    const [password, setPassword] = useState("");  // Controlled input field (password)
    const [message, setMessage] = useState(null);  // Feedback message ("User created successfully" / error)
    const [submitting, setSubmitting] = useState(false);  // Boolean flag to prevent multiple submissions (disables button)

    const handleSubmit = async (e) => {
        e.preventDefault();  // stops the browser from refreshing the page when the form is submitted
        setMessage(null);
        setSubmitting(true);   // for submit button

        try {
            const response = await fetch(`${apiBaseUrl}/v2/api/user`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    id: null,             // @GeneratedValue
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
