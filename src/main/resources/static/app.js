let selectedTimeId = null;

const $ = (selector) => document.querySelector(selector);

async function request(url, options = {}) {
    const response = await fetch(url, {
        headers: { "Content-Type": "application/json", ...(options.headers || {}) },
        ...options,
    });
    if (!response.ok) {
        const error = await response.json().catch(() => ({ message: "요청을 처리하지 못했습니다." }));
        throw new Error(error.message);
    }
    if (response.status === 204) {
        return null;
    }
    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

function authHeaders() {
    const token = localStorage.getItem("accessToken");
    if (!token) {
        return {};
    }
    return { Authorization: `Bearer ${token}` };
}

function setMessage(selector, message, error = false) {
    const element = $(selector);
    element.textContent = message;
    element.classList.toggle("error", error);
}

function option(value, label) {
    const element = document.createElement("option");
    element.value = value;
    element.textContent = label;
    return element;
}

async function loadThemes() {
    const themes = await request("/themes");
    const reservationTheme = $("#reservation-theme");
    const changeTheme = $("#change-theme");
    reservationTheme.replaceChildren(...themes.map((theme) => option(theme.id, theme.name)));
    changeTheme.replaceChildren(...themes.map((theme) => option(theme.id, theme.name)));
    renderThemeList(themes);
}

async function loadTimes() {
    const times = await request("/times");
    const changeTime = $("#change-time");
    changeTime.replaceChildren(...times.map((time) => option(time.id, time.startAt)));
    renderTimeList(times);
}

async function loadPopularThemes() {
    const themes = await request("/themes/popular");
    $("#popular-themes").replaceChildren(...themes.map((theme) => {
        const item = document.createElement("div");
        item.className = "item";
        item.innerHTML = `
            <p class="item-title">${theme.name}</p>
            <p class="item-meta">${theme.description}</p>
            <p class="item-meta">최근 1주 예약 ${theme.reservationCount}건</p>
        `;
        return item;
    }));
}

async function loadAvailableTimes() {
    const date = $("#reservation-date").value;
    const themeId = $("#reservation-theme").value;
    if (!date || !themeId) {
        return;
    }
    const times = await request(`/available-times?date=${date}&themeId=${themeId}`);
    selectedTimeId = null;
    $("#available-times").replaceChildren(...times.map((time) => {
        const button = document.createElement("button");
        button.type = "button";
        button.textContent = time.startAt;
        button.setAttribute("aria-pressed", "false");
        button.addEventListener("click", () => {
            selectedTimeId = time.id;
            document.querySelectorAll("#available-times button")
                .forEach((node) => node.setAttribute("aria-pressed", "false"));
            button.setAttribute("aria-pressed", "true");
        });
        return button;
    }));
}

async function createReservation() {
    try {
        await request("/reservations", {
            method: "POST",
            body: JSON.stringify({
                name: $("#reservation-name").value,
                date: $("#reservation-date").value,
                timeId: selectedTimeId,
                themeId: Number($("#reservation-theme").value),
            }),
        });
        setMessage("#reservation-message", "예약이 생성되었습니다.");
        await loadAvailableTimes();
    } catch (error) {
        setMessage("#reservation-message", error.message, true);
    }
}

async function signup() {
    try {
        const member = await request("/members", {
            method: "POST",
            body: JSON.stringify({
                name: $("#signup-name").value,
                email: $("#signup-email").value,
                password: $("#signup-password").value,
            }),
        });
        setMessage("#signup-message", `${member.name}님, 가입되었습니다.`);
    } catch (error) {
        setMessage("#signup-message", error.message, true);
    }
}

async function login() {
    try {
        const result = await request("/login", {
            method: "POST",
            body: JSON.stringify({
                email: $("#login-email").value,
                password: $("#login-password").value,
            }),
        });
        localStorage.setItem("accessToken", result.accessToken);
        setMessage("#login-message", "로그인되었습니다.");
        await loadMe();
    } catch (error) {
        setMessage("#login-message", error.message, true);
    }
}

async function loadMe() {
    try {
        const member = await request("/members/me", { headers: authHeaders() });
        const item = document.createElement("div");
        item.className = "item";
        item.innerHTML = `
            <p class="item-title">${member.name}</p>
            <p class="item-meta">${member.email}</p>
        `;
        $("#member-info").replaceChildren(item);
    } catch (error) {
        setMessage("#login-message", error.message, true);
    }
}

async function loadMine() {
    try {
        const name = $("#mine-name").value;
        const reservations = await request(`/reservations/mine?name=${encodeURIComponent(name)}`);
        $("#mine-list").replaceChildren(...reservations.map((reservation) => {
            const item = document.createElement("div");
            item.className = "item";
            item.innerHTML = `
                <div class="item-row">
                    <p class="item-title">#${reservation.id} ${reservation.theme?.name ?? "관리자 예약"}</p>
                    <button class="delete" type="button">취소</button>
                </div>
                <p class="item-meta">${reservation.date} ${reservation.time?.startAt ?? reservation.time}</p>
            `;
            item.querySelector("button").addEventListener("click", async () => {
                await request(`/reservations/${reservation.id}?name=${encodeURIComponent(name)}`, { method: "DELETE" });
                await loadMine();
            });
            return item;
        }));
    } catch (error) {
        setMessage("#mine-message", error.message, true);
    }
}

async function changeReservation() {
    try {
        await request(`/reservations/${$("#change-id").value}`, {
            method: "PATCH",
            body: JSON.stringify({
                name: $("#change-name").value,
                date: $("#change-date").value,
                timeId: Number($("#change-time").value),
                themeId: Number($("#change-theme").value),
            }),
        });
        setMessage("#mine-message", "예약이 변경되었습니다.");
        await loadMine();
    } catch (error) {
        setMessage("#mine-message", error.message, true);
    }
}

function renderThemeList(themes) {
    $("#theme-list").replaceChildren(...themes.map((theme) => {
        const item = document.createElement("div");
        item.className = "item";
        item.innerHTML = `
            <div class="item-row">
                <p class="item-title">${theme.name}</p>
                <button class="delete" type="button">삭제</button>
            </div>
            <p class="item-meta">${theme.description}</p>
        `;
        item.querySelector("button").addEventListener("click", async () => {
            await request(`/themes/${theme.id}`, { method: "DELETE" });
            await loadThemes();
        });
        return item;
    }));
}

function renderTimeList(times) {
    $("#time-list").replaceChildren(...times.map((time) => {
        const item = document.createElement("div");
        item.className = "item";
        item.innerHTML = `
            <div class="item-row">
                <p class="item-title">${time.startAt}</p>
                <button class="delete" type="button">삭제</button>
            </div>
        `;
        item.querySelector("button").addEventListener("click", async () => {
            await request(`/times/${time.id}`, { method: "DELETE" });
            await loadTimes();
        });
        return item;
    }));
}

async function createTheme() {
    await request("/themes", {
        method: "POST",
        body: JSON.stringify({
            name: $("#theme-name").value,
            description: $("#theme-description").value,
            thumbnailUrl: $("#theme-thumbnail").value,
        }),
    });
    await loadThemes();
    await loadPopularThemes();
}

async function createTime() {
    await request("/times", {
        method: "POST",
        body: JSON.stringify({ startAt: $("#time-start").value }),
    });
    await loadTimes();
}

function bindEvents() {
    $("#signup").addEventListener("click", signup);
    $("#login").addEventListener("click", login);
    $("#load-me").addEventListener("click", loadMe);
    $("#refresh-times").addEventListener("click", loadAvailableTimes);
    $("#create-reservation").addEventListener("click", createReservation);
    $("#load-mine").addEventListener("click", loadMine);
    $("#change-reservation").addEventListener("click", changeReservation);
    $("#create-theme").addEventListener("click", createTheme);
    $("#create-time").addEventListener("click", createTime);
}

async function init() {
    const tomorrow = new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString().slice(0, 10);
    $("#reservation-date").value = tomorrow;
    $("#change-date").value = tomorrow;
    bindEvents();
    await Promise.all([loadThemes(), loadTimes(), loadPopularThemes()]);
}

init();
