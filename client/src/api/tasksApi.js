const BASE = process.env.REACT_APP_API_URL;

export async function getTasks({ page = 0, size = 10 } = {}) {
    const res = await fetch(`${BASE}/tasks?page=${page}&size=${size}&sort=createdAt,desc`);
    if (!res.ok) throw new Error("Failed to load tasks");
    return res.json();
}

export async function createTask({ title, description }) {
    const res = await fetch(`${BASE}/tasks`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ title, description }),
    });

    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err?.message || "Create failed");
    }

    return res.json();
}

export async function deleteTask(id) {
    const res = await fetch(`${BASE}/tasks/${id}`, { method: "DELETE" });
    if (!res.ok) throw new Error("Delete failed");
}

export async function updateTask(id, payload) {
    const res = await fetch(`${BASE}/tasks/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
    });

    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err?.message || "Update failed");
    }

    return res.json();
}