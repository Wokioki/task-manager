import { useEffect, useState } from "react";
import { createTask, deleteTask, getTasks, updateTask } from "./api/tasksApi";
import "./App.css";

function App() {
    const [tasks, setTasks] = useState([]);

    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    async function load() {
        setLoading(true);
        setError("");
        try {
            const data = await getTasks({ page: 0, size: 10 });
            setTasks(data.content || []);
        } catch (e) {
            setError(e.message || "Error");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        load();
    }, []);

    async function onCreate(e) {
        e.preventDefault();
        setError("");

        if (!title.trim()) {
            setError("Title is required");
            return;
        }

        try {
            await createTask({ title: title.trim(), description: description.trim() });
            setTitle("");
            setDescription("");
            await load();
        } catch (e) {
            setError(e.message || "Create failed");
        }
    }

    async function onDelete(id) {
        setError("");
        try {
            await deleteTask(id);
            await load();
        } catch (e) {
            setError(e.message || "Delete failed");
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
            await load();
        } catch (e) {
            setError(e.message || "Update failed");
        }
    }

    return (
        <div className="wrap">
            <h1>Task Manager</h1>

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

                <button className="btn" type="submit" disabled={loading}>
                    Create
                </button>
            </form>

            {error && <div className="error">{error}</div>}
            {loading && <div className="muted">Loading…</div>}

            <div className="card">
                <h2>Tasks</h2>

                {tasks.length === 0 && !loading ? (
                    <div className="muted">No tasks yet.</div>
                ) : (
                    <ul className="list">
                        {tasks.map((task) => (
                            <li key={task.id} className={task.done ? "item done" : "item"}>
                                <div className="info">
                                    <div className="title">
                                        #{task.id} {task.title}
                                    </div>
                                    {task.description && <div className="desc">{task.description}</div>}
                                </div>

                                <div className="actions">
                                    <button className="btn" onClick={() => onToggleDone(task)}>
                                        {task.done ? "Undone" : "Done"}
                                    </button>
                                    <button className="btn danger" onClick={() => onDelete(task.id)}>
                                        Delete
                                    </button>
                                </div>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
}

export default App;