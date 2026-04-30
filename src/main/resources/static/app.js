let appToken = localStorage.getItem("bank-rest-token") || "";

function setOutput(data) {
    const el = document.getElementById("output");
    if (el) {
        el.textContent = typeof data === "string" ? data : JSON.stringify(data, null, 2);
    }
}

function setToken(token) {
    appToken = token || "";
    localStorage.setItem("bank-rest-token", appToken);
}

function getHeaders(json = true) {
    const headers = {};
    if (json) {
        headers["Content-Type"] = "application/json";
    }
    if (appToken) {
        headers["Authorization"] = "Bearer " + appToken;
    }
    return headers;
}

async function apiRequest(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        headers: {
            ...getHeaders(options.body !== undefined),
            ...(options.headers || {})
        }
    });

    const contentType = response.headers.get("content-type") || "";
    let body;

    if (contentType.includes("application/json")) {
        body = await response.json();
    } else {
        body = await response.text();
    }

    if (!response.ok) {
        throw new Error(typeof body === "string" ? body : JSON.stringify(body, null, 2));
    }

    return body;
}

async function apiGet(url) {
    return apiRequest(url, { method: "GET" });
}

async function apiPost(url, body) {
    return apiRequest(url, {
        method: "POST",
        body: JSON.stringify(body)
    });
}

async function apiDelete(url) {
    return apiRequest(url, { method: "DELETE" });
}

async function login() {
    try {
        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value;

        const data = await apiPost("/api/auth/login", { username, password });

        if (data && data.token) {
            setToken(data.token);
        }

        setOutput(data);
        setTimeout(() => {
            if (data && data.role === "ADMIN") {
                window.location.href = "/admin.html";
            } else {
                window.location.href = "/home.html";
            }
        }, 400);
    } catch (e) {
        setOutput({ error: e.message });
    }
}

async function register() {
    try {
        const username = document.getElementById("username").value.trim();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;

        const data = await apiPost("/api/auth/register", { username, email, password });

        setOutput(data);
        setTimeout(() => {
            window.location.href = "/login.html";
        }, 700);
    } catch (e) {
        setOutput({ error: e.message });
    }
}

function logout() {
    setToken("");
    window.location.href = "/login.html";
}