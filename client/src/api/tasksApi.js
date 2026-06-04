const BASE = process.env.REACT_APP_API_URL;

function getAuthHeaders() {
    const token = localStorage.getItem("token");

    if (!token) {
        return {};
    }

    return {
        Authorization: `Bearer ${token}`,
    };
}

async function handleResponse(res, fallbackMessage) {
    if (!res.ok) {
        const err = await res.json().catch(() => ({}));

        if (res.status === 401 || res.status === 403) {
            localStorage.removeItem("token");
            localStorage.removeItem("user");
        }

        throw new Error(err?.message || fallbackMessage);
    }

    if (res.status === 204) {
        return null;
    }

    return res.json();
}

export async function getTasks({ page = 0, size = 100, q = "" } = {}) {
    const params = new URLSearchParams();

    params.set("page", page);
    params.set("size", size);
    params.set("sort", "createdAt,desc");

    if (q.trim()) {
        params.set("q", q.trim());
    }

    const res = await fetch(`${BASE}/tasks?${params.toString()}`, {
        headers: {
            ...getAuthHeaders(),
        },
    });

    return handleResponse(res, "Failed to load tasks");
}

export async function createTask({ title, description }) {
    const res = await fetch(`${BASE}/tasks`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            ...getAuthHeaders(),
        },
        body: JSON.stringify({ title, description }),
    });

    return handleResponse(res, "Create failed");
}

export async function deleteTask(id) {
    const res = await fetch(`${BASE}/tasks/${id}`, {
        method: "DELETE",
        headers: {
            ...getAuthHeaders(),
        },
    });

    return handleResponse(res, "Delete failed");
}

export async function updateTask(id, payload) {
    const res = await fetch(`${BASE}/tasks/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            ...getAuthHeaders(),
        },
        body: JSON.stringify(payload),
    });

    return handleResponse(res, "Update failed");
}