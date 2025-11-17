import React from "react";
import { useNavigate } from "react-router-dom";

function HomePage({ auth, onLogout }) {
    const navigate = useNavigate();

    const handleLogoutClick = () => {
        onLogout();
        navigate("/");
    };

    return (
        <div className="home-container">
            <div className="home-card">
                <h1>Welcome, {auth.username} 👋</h1>
                <p>You are now logged in.</p>

                <button onClick={handleLogoutClick}>Log out</button>

                {/* 这里以后可以加：展示用户信息、调用其他受保护 API 等 */}
            </div>
        </div>
    );
}

export default HomePage;
