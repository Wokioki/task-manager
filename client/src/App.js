import { useEffect, useState } from "react";
import { login, register } from "./api/authApi";
import { createTask, deleteTask, getTasks, updateTask } from "./api/tasksApi";
import "./App.css";

function getStoredUser() {
    try {
        const raw = localStorage.getItem("user");
        return raw ? JSON.parse(raw) : null;
    } catch {
        return null;
    }
}

function App() {
    const [user, setUser] = useState(getStoredUser);
    const [authMode, setAuthMode] = useState("login");

    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const [tasks, setTasks] = useState([]);
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [search, setSearch] = useState("");
    const [statusFilter, setStatusFilter] = useState("all");

    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const [editingTaskId, setEditingTaskId] = useState(null);
    const [editTitle, setEditTitle] = useState("");
    const [editDescription, setEditDescription] = useState("");

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const isAuthenticated = Boolean(localStorage.getItem("token"));

    async function load(q = search, done = statusFilter, nextPage = page, showLoading = true) {
        if (showLoading) {
            setLoading(true);
        }

        setError("");

        try {
            const data = await getTasks({
                page: nextPage,
                size: 5,
                q,
                done,
            });

            setTasks(data.content || []);
            setPage(data.number || 0);
            setTotalPages(data.totalPages || 0);
        } catch (e) {
            setError(e.message || "Failed to load tasks");

            if (!localStorage.getItem("token")) {
                setUser(null);
            }
        } finally {
            if (showLoading) {
                setLoading(false);
            }
        }
    }

    useEffect(() => {
        if (isAuthenticated) {
            load();
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isAuthenticated]);

    async function onAuthSubmit(e) {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            let data;

            if (authMode === "register") {
                if (!username.trim()) {
                    throw new Error("Username is required");
                }

                data = await register({
                    username: username.trim(),
                    email: email.trim(),
                    password,
                });
            } else {
                data = await login({
                    email: email.trim(),
                    password,
                });
            }

            localStorage.setItem("token", data.token);
            localStorage.setItem(
                "user",
                JSON.stringify({
                    id: data.id,
                    username: data.username,
                    email: data.email,
                })
            );

            setUser({
                id: data.id,
                username: data.username,
                email: data.email,
            });

            setUsername("");
            setEmail("");
            setPassword("");
            setStatusFilter("all");
            setSearch("");
            await load("", "all", 0);
        } catch (e) {
            setError(e.message || "Authentication failed");
        } finally {
            setLoading(false);
        }
    }

    function onLogout() {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        setUser(null);
        setTasks([]);
        setTitle("");
        setDescription("");
        setSearch("");
        setStatusFilter("all");
        setPage(0);
        setTotalPages(0);
        setEditingTaskId(null);
        setError("");
    }

    async function onStatusFilterChange(nextFilter) {
        setStatusFilter(nextFilter);
        setEditingTaskId(null);
        await load(search, nextFilter, 0, false);
    }

    async function onPreviousPage() {
        if (page === 0) return;
        setEditingTaskId(null);
        await load(search, statusFilter, page - 1, false);
    }

    async function onNextPage() {
        if (page >= totalPages - 1) return;
        setEditingTaskId(null);
        await load(search, statusFilter, page + 1, false);
    }

    async function onSearchSubmit(e) {
        e.preventDefault();
        setEditingTaskId(null);
        await load(search, statusFilter, 0, false);
    }

    async function onClearSearch() {
        setSearch("");
        setEditingTaskId(null);
        await load("", statusFilter, 0, false);
    }

    async function onCreate(e) {
        e.preventDefault();
        setError("");

        if (!title.trim()) {
            setError("Title is required");
            return;
        }

        try {
            await createTask({
                title: title.trim(),
                description: description.trim(),
            });

            setTitle("");
            setDescription("");
            setEditingTaskId(null);
            await load(search, statusFilter, 0);
        } catch (e) {
            setError(e.message || "Create failed");
        }
    }

    async function onDelete(id) {
        setError("");

        try {
            await deleteTask(id);
            setEditingTaskId(null);
            await load(search, statusFilter, page);
        } catch (e) {
            setError(e.message || "Delete failed");
        }
    }

    function onStartEdit(task) {
        setEditingTaskId(task.id);
        setEditTitle(task.title);
        setEditDescription(task.description || "");
    }

    function onCancelEdit() {
        setEditingTaskId(null);
        setEditTitle("");
        setEditDescription("");
    }

    async function onSaveEdit(task) {
        setError("");

        if (!editTitle.trim()) {
            setError("Title is required");
            return;
        }

        try {
            await updateTask(task.id, {
                title: editTitle.trim(),
                description: editDescription.trim(),
                done: task.done,
            });

            setEditingTaskId(null);
            setEditTitle("");
            setEditDescription("");

            await load(search, statusFilter, page);
        } catch (e) {
            setError(e.message || "Update failed");
        }
    }

    async function onToggleDone(task) {
        setError("");

        try {
            await updateTask(task.id, {
                title: task.title,
                description: task.description,
                done: !task.done,
            });

            setEditingTaskId(null);
            await load(search, statusFilter, page);
        } catch (e) {
            setError(e.message || "Update failed");
        }
    }

    if (!isAuthenticated || !user) {
        return (
            <div className="page">
                <div className="auth-card">
                    <div className="brand">
                        <div className="logo">✓</div>
                        <div>
                            <h1>Task Manager</h1>
                            <p>Sign in to manage your personal tasks.</p>
                        </div>
                    </div>

                    <div className="tabs">
                        <button
                            className={authMode === "login" ? "tab active" : "tab"}
                            onClick={() => {
                                setAuthMode("login");
                                setError("");
                            }}
                            type="button"
                        >
                            Login
                        </button>

                        <button
                            className={authMode === "register" ? "tab active" : "tab"}
                            onClick={() => {
                                setAuthMode("register");
                                setError("");
                            }}
                            type="button"
                        >
                            Register
                        </button>
                    </div>

                    <form onSubmit={onAuthSubmit}>
                        {authMode === "register" && (
                            <label className="field">
                                <span>Username</span>
                                <input
                                    className="input"
                                    placeholder="danylo"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                />
                            </label>
                        )}

                        <label className="field">
                            <span>Email</span>
                            <input
                                className="input"
                                type="email"
                                placeholder="mail@test.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                        </label>

                        <label className="field">
                            <span>Password</span>
                            <input
                                className="input"
                                type="password"
                                placeholder="••••••"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                            />
                        </label>

                        {error && <div className="error">{error}</div>}

                        <button className="btn primary full" type="submit" disabled={loading}>
                            {loading
                                ? "Please wait..."
                                : authMode === "login"
                                    ? "Login"
                                    : "Create account"}
                        </button>
                    </form>
                </div>
            </div>
        );
    }

    return (
        <div className="page">
            <div className="wrap">
                <header className="topbar">
                    <div>
                        <h1>Task Manager</h1>
                        <p className="muted">
                            Signed in as <strong>{user.username}</strong> · {user.email}
                        </p>
                    </div>

                    <button className="btn" onClick={onLogout}>
                        Logout
                    </button>
                </header>

                <form className="card" onSubmit={onCreate}>
                    <h2>Create task</h2>

                    <input
                        className="input"
                        placeholder="Title"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                    />

                    <textarea
                        className="input"
                        placeholder="Description (optional)"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                    />

                    <button className="btn primary" type="submit" disabled={loading}>
                        Create
                    </button>
                </form>

                {error && <div className="error">{error}</div>}
                {loading && tasks.length === 0 && <div className="muted">Loading…</div>}

                <div className="card">
                    <div className="section-title">
                        <h2>Tasks</h2>
                        <button
                            className="btn small"
                            onClick={() => load(search, statusFilter, page)}
                            disabled={loading}
                        >
                            Refresh
                        </button>
                    </div>

                    <form className="search-row" onSubmit={onSearchSubmit}>
                        <input
                            className="input search-input"
                            placeholder="Search by task title..."
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                        />

                        <button className="btn small" type="submit" disabled={loading}>
                            Search
                        </button>

                        {search && (
                            <button className="btn small" type="button" onClick={onClearSearch}>
                                Clear
                            </button>
                        )}
                    </form>

                    <div className="filter-row">
                        <button
                            className={statusFilter === "all" ? "filter-btn active" : "filter-btn"}
                            type="button"
                            onClick={() => onStatusFilterChange("all")}
                        >
                            All
                        </button>

                        <button
                            className={statusFilter === "false" ? "filter-btn active" : "filter-btn"}
                            type="button"
                            onClick={() => onStatusFilterChange("false")}
                        >
                            Active
                        </button>

                        <button
                            className={statusFilter === "true" ? "filter-btn active" : "filter-btn"}
                            type="button"
                            onClick={() => onStatusFilterChange("true")}
                        >
                            Done
                        </button>
                    </div>

                    {tasks.length === 0 && !loading ? (
                        <div className="empty">
                            <div className="empty-icon">☕</div>
                            <h3>No tasks yet</h3>
                            <p>
                                {search
                                    ? "No tasks match your search."
                                    : "Create your first task above."}
                            </p>
                        </div>
                    ) : (
                        <div className="tasks-scroll">
                            <ul className="list">
                                {tasks.map((task, index) => (
                                    <li key={task.id} className={task.done ? "item done" : "item"}>
                                        <div className="info">
                                            {editingTaskId === task.id ? (
                                                <div className="edit-box">
                                                    <input
                                                        className="input"
                                                        value={editTitle}
                                                        onChange={(e) => setEditTitle(e.target.value)}
                                                        placeholder="Task title"
                                                    />

                                                    <textarea
                                                        className="input"
                                                        value={editDescription}
                                                        onChange={(e) => setEditDescription(e.target.value)}
                                                        placeholder="Task description"
                                                    />
                                                </div>
                                            ) : (
                                                <>
                                                    <div className="title">
                                                        #{page * 5 + index + 1} {task.title}
                                                    </div>

                                                    {task.description && (
                                                        <div className="desc">{task.description}</div>
                                                    )}

                                                    <div className="meta">
                                                        Status: {task.done ? "Done" : "Active"}
                                                    </div>
                                                </>
                                            )}
                                        </div>

                                        <div className="actions">
                                            {editingTaskId === task.id ? (
                                                <>
                                                    <button className="btn primary" onClick={() => onSaveEdit(task)}>
                                                        Save
                                                    </button>

                                                    <button className="btn" onClick={onCancelEdit}>
                                                        Cancel
                                                    </button>
                                                </>
                                            ) : (
                                                <>
                                                    <button className="btn" onClick={() => onToggleDone(task)}>
                                                        {task.done ? "Undone" : "Done"}
                                                    </button>

                                                    <button className="btn" onClick={() => onStartEdit(task)}>
                                                        Edit
                                                    </button>

                                                    <button
                                                        className="btn danger"
                                                        onClick={() => onDelete(task.id)}
                                                    >
                                                        Delete
                                                    </button>
                                                </>
                                            )}
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}

                    {totalPages > 1 && (
                        <div className="pagination-row">
                            <button
                                className="btn small"
                                type="button"
                                onClick={onPreviousPage}
                                disabled={page === 0 || loading}
                            >
                                Previous
                            </button>

                            <span className="page-info">
                                Page {page + 1} of {totalPages}
                            </span>

                            <button
                                className="btn small"
                                type="button"
                                onClick={onNextPage}
                                disabled={page >= totalPages - 1 || loading}
                            >
                                Next
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default App;