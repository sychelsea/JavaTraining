import React, { useEffect, useState } from "react";
import axios from "axios";

function HomePage({ apiBaseUrl, auth, onLogout, onUserUpdate }) {
    const { user, basicToken } = auth;

    // user info
    const [formId, setFormId] = useState(user?.id || "");
    const [formUsername, setFormUsername] = useState(user?.username || "");
    const [formRole, setFormRole] = useState(user?.role || "");

    // Delete - id
    const [deleteId, setDeleteId] = useState(user?.id || "");

    // Search
    const [searchId, setSearchId] = useState("");
    const [searchResult, setSearchResult] = useState(null);

    // Message & Error State
    const [message, setMessage] = useState(null); // success/info
    const [error, setError] = useState(null);     // error

    // lifting state up, prop-driven updates, useEffect for synchronization
    useEffect(() => {
        // when login, init the form by the current user
        if (user) {
            setFormId(user.id);
            setFormUsername(user.username);
            setFormRole(user.role);
            setDeleteId(user.id);
        }
    }, [user]);

    const handleLogoutClick = () => {
        onLogout();
        window.location.href = "/";
    };

    const authJsonHeaders = {
        Authorization: basicToken,
        "Content-Type": "application/json",
    };

    /*  Search */
    const handleSearch = async (e) => {
        e.preventDefault();
        setMessage(null);
        setError(null);
        setSearchResult(null);

        if (!searchId) {
            setError("Please enter an ID to search.");
            return;
        }

        try {
            const res = await axios.get(
                `${apiBaseUrl}/v2/api/user/${searchId}`,
                {
                    headers: {
                        Authorization: basicToken,
                    },
                }
            );

            const data = res.data;
            setSearchResult(data);
            setMessage(`Found user ${data.username} (id=${data.id}).`);

            // put the result data into the form
            setFormId(data.id);
            setFormUsername(data.username || "");
            setFormRole(data.role || "");
            setDeleteId(data.id);
        } catch (err) {
            if (err.response) {
                const status = err.response.status;
                if (status === 401 || status === 403) {
                    setError("You are not allowed to search this user.");
                } else if (status === 404) {
                    setError(`User ${searchId} not found.`);
                } else {
                    setError(`Search failed: ${status}`);
                }
            } else {
                setError("Network error during search.");
            }
        }
    };

    /* Update */
    const handleUpdate = async (e) => {
        e.preventDefault();
        setMessage(null);
        setError(null);

        if (!formId) {
            setError("Please provide user id for update.");
            return;
        }

        // only update the non-null info
        const payload = {};

        if (String(formId).trim() !== "") {
            payload.id = formId;
        }
        if (formUsername.trim() !== "") {
            payload.username = formUsername;
        }
        if (formRole.trim() !== "") {
            payload.role = formRole;
        }

        try {
            const res = await axios.put(
                `${apiBaseUrl}/v2/api/user/${formId}`,
                payload,
                {
                    headers: authJsonHeaders,
                }
            );

            const updated = res.data;
            setMessage("User updated successfully.");

            // if it's the current user, update the displayed info
            // The API integration is not only about sending HTTP requests,
            // but also about how the responses drive the overall application state,
            // including authentication and navigation.
            if (String(updated.id) === String(user.id)) {
                onUserUpdate(updated);
            }

            // searchResult display
            if (searchResult && String(searchResult.id) === String(updated.id)) {
                setSearchResult(updated);
            }
        } catch (err) {
            if (err.response) {
                const status = err.response.status;
                const data = err.response.data;
                const text =
                    typeof data === "string" ? data : JSON.stringify(data);
                if (status === 401 || status === 403) {
                    setError("You are not allowed to update this user.");
                } else {
                    setError(`Update failed: ${status} ${text}`);
                }
            } else {
                setError("Network error during update.");
            }
        }
    };

    /* Delete */
    const handleDelete = async (e) => {
        e.preventDefault();
        setMessage(null);
        setError(null);

        if (!deleteId) {
            setError("Please provide user id to delete.");
            return;
        }

        try {
            await axios.delete(
                `${apiBaseUrl}/v2/api/user/${deleteId}`,
                {
                    headers: {
                        Authorization: basicToken,
                    },
                }
            );

            setMessage(`User ${deleteId} deleted successfully.`);

            // if it's the current user, logout
            if (String(deleteId) === String(user.id)) {
                setMessage("You deleted your own account, logging out…");
                setTimeout(() => handleLogoutClick(), 1500);
            }
        } catch (err) {
            if (err.response) {
                const status = err.response.status;
                const data = err.response.data;
                const text =
                    typeof data === "string" ? data : JSON.stringify(data);

                if (status === 401 || status === 403) {
                    setError("You are not allowed to delete this user.");
                } else if (status === 404) {
                    setError(`User ${deleteId} not found.`);
                } else {
                    setError(`Delete failed: ${status} ${text}`);
                }
            } else {
                setError("Network error during delete.");
            }
        }
    };

    if (!user) {
        return (
            <div className="home-container">
                <div className="home-card">
                    <p>No user loaded.</p>
                    <button onClick={handleLogoutClick}>Back to login</button>
                </div>
            </div>
        );
    }

    return (
        <>
        <div className="header">
            <div className="header-title">User Dashboard</div>
            <button onClick={handleLogoutClick}>Log out</button>
        </div>
        <div className="home-container">
            <div className="home-card">
                {/* display user info */}
                <h1>User Info</h1>
                <p><strong>ID:</strong> {user.id}</p>
                <p><strong>Username:</strong> {user.username}</p>
                <p><strong>Role:</strong> {user.role}</p>

                {/* Search */}
                <h3 style={{ marginTop: "20px" }}>Search User by ID</h3>
                <form onSubmit={handleSearch} className="auth-form">
                    <label>
                        ID*
                        <input
                            type="text"
                            value={searchId}
                            onChange={(e) => setSearchId(e.target.value)}
                        />
                    </label>
                    <button type="submit">Search</button>
                </form>

                {searchResult && (
                    <div style={{ marginTop: "10px", fontSize: 14 }}>
                        <p><strong>Search Result:</strong></p>
                        <p>ID: {searchResult.id}</p>
                        <p>Username: {searchResult.username}</p>
                        <p>Role: {searchResult.role}</p>
                    </div>
                )}

                {/* Update */}
                <h3 style={{ marginTop: "24px" }}>Update User</h3>
                <form onSubmit={handleUpdate} className="auth-form">
                    <label>
                        ID*
                        <input
                            type="text"
                            value={formId}
                            onChange={(e) => setFormId(e.target.value)}
                        />
                    </label>
                    <label>
                        Username
                        <input
                            type="text"
                            value={formUsername}
                            onChange={(e) => setFormUsername(e.target.value)}
                            placeholder="(no change if empty)"
                        />
                    </label>
                    <label>
                        Role
                        <input
                            type="text"
                            value={formRole}
                            onChange={(e) => setFormRole(e.target.value)}
                            placeholder="(no change if empty)"
                        />
                    </label>
                    <button type="submit">Update</button>
                </form>

                {/* Delete */}
                <h3 style={{ marginTop: "24px" }}>Delete User</h3>
                <form onSubmit={handleDelete} className="auth-form">
                    <label>
                        ID*
                        <input
                            type="text"
                            value={deleteId}
                            onChange={(e) => setDeleteId(e.target.value)}
                        />
                    </label>
                    <button type="submit">Delete</button>
                </form>

                {/* message */}
                {message && <p className="message">{message}</p>}
                {error && <p className="error">{error}</p>}

                <button
                    onClick={handleLogoutClick}
                    style={{ marginTop: "24px", background: "#6b7280" }}
                >
                    Log out
                </button>
            </div>
        </div>
        </>
    );
}

export default HomePage;
