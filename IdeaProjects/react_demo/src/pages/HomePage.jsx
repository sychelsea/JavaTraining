// src/pages/HomePage.jsx
import React, { useEffect, useState } from "react";

function HomePage({ apiBaseUrl, auth, onLogout, onUserUpdate }) {
    const { user, basicToken } = auth;

    // 当前表单要操作的用户（可以是自己，也可以是 search 出来的）
    const [formId, setFormId] = useState(user?.id || "");
    const [formUsername, setFormUsername] = useState(user?.username || "");
    const [formRole, setFormRole] = useState(user?.role || "");

    // Delete 用的 id
    const [deleteId, setDeleteId] = useState(user?.id || "");

    // Search
    const [searchId, setSearchId] = useState("");
    const [searchResult, setSearchResult] = useState(null);

    // 消息提示
    const [message, setMessage] = useState(null); // success/info
    const [error, setError] = useState(null);     // error

    useEffect(() => {
        // 登录进来时，用当前 user 初始化表单
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

    /* 🔍 Search: 根据 id 查 user，并把结果填进表单（方便 update/delete） */
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
            const res = await fetch(`${apiBaseUrl}/v2/api/user/${searchId}`, {
                method: "GET",
                headers: { Authorization: basicToken },
            });

            if (res.status === 401 || res.status === 403) {
                setError("You are not allowed to search this user.");
                return;
            }

            if (res.status === 404) {
                setError(`User ${searchId} not found.`);
                return;
            }

            if (!res.ok) {
                setError(`Search failed: ${res.status}`);
                return;
            }

            const data = await res.json();
            setSearchResult(data);
            setMessage(`Found user ${data.username} (id=${data.id}).`);

            // 用 search 到的结果填充 Update / Delete 表单
            setFormId(data.id);
            setFormUsername(data.username || "");
            setFormRole(data.role || "");
            setDeleteId(data.id);
        } catch (e) {
            console.error(e);
            setError("Network error during search.");
        }
    };

    /* ✏️ Update：只更新非空字段 */
    const handleUpdate = async (e) => {
        e.preventDefault();
        setMessage(null);
        setError(null);

        if (!formId) {
            setError("Please provide user id for update.");
            return;
        }

        // ⚠️ 关键点：只把非空字段放进 payload
        const payload = {};

        // id 一般作为 path variable，body 里可以不放，也可以放，按你后端来
        // 这里示例：只要填了，就带上
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
            const res = await fetch(`${apiBaseUrl}/v2/api/user/${formId}`, {
                method: "PUT", // 如果你后端是 POST，就改成 POST
                headers: authJsonHeaders,
                body: JSON.stringify(payload),
            });

            if (res.status === 401 || res.status === 403) {
                setError("You are not allowed to update this user.");
                return;
            }

            if (!res.ok) {
                const text = await res.text();
                setError(`Update failed: ${res.status} ${text}`);
                return;
            }

            const updated = await res.json();
            setMessage("User updated successfully.");

            // 如果更新的是当前登录的 user，就顺便更新全局 auth.user
            if (String(updated.id) === String(user.id)) {
                onUserUpdate(updated);
            }

            // 更新 searchResult 显示
            if (searchResult && String(searchResult.id) === String(updated.id)) {
                setSearchResult(updated);
            }
        } catch (e) {
            console.error(e);
            setError("Network error during update.");
        }
    };

    /* 🗑 Delete */
    const handleDelete = async (e) => {
        e.preventDefault();
        setMessage(null);
        setError(null);

        if (!deleteId) {
            setError("Please provide user id to delete.");
            return;
        }

        try {
            const res = await fetch(`${apiBaseUrl}/v2/api/user/${deleteId}`, {
                method: "DELETE",
                headers: { Authorization: basicToken },
            });

            if (res.status === 401 || res.status === 403) {
                setError("You are not allowed to delete this user.");
                return;
            }

            if (res.status === 404) {
                setError(`User ${deleteId} not found.`);
                return;
            }

            if (!res.ok) {
                const text = await res.text();
                setError(`Delete failed: ${res.status} ${text}`);
                return;
            }

            setMessage(`User ${deleteId} deleted successfully.`);

            // 如果删的是自己，自动 logout
            if (String(deleteId) === String(user.id)) {
                setMessage("You deleted your own account, logging out…");
                setTimeout(() => handleLogoutClick(), 1500);
            }
        } catch (e) {
            console.error(e);
            setError("Network error during delete.");
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
    );
}

export default HomePage;
