
let cameraStream = null;

const $ = (id) => document.getElementById(id);


/* ==============================
   ANDROID BRIDGE
   ============================== */

function bridgeAvailable() {
    return typeof window.Android !== "undefined";
}


function setStatus(message) {
    $("status").textContent = message;
}


/* ==============================
   CALL HISTORY
   ============================== */

function loadCalls() {

    if (!bridgeAvailable()) {
        setStatus("● Browser mode");
        return;
    }

    try {

        const raw =
            Android.getCallLog();

        const calls =
            JSON.parse(raw || "[]");

        const box =
            $("calls");

        box.innerHTML = "";

        if (calls.length === 0) {

            box.textContent =
                "No call history available.";

            return;
        }

        calls.forEach(call => {

            const item =
                document.createElement("div");

            item.className = "item";

            const date =
                new Date(
                    Number(call.date)
                );

            let type = "Other";

            if (call.type === 1) {
                type = "Incoming";
            }

            if (call.type === 2) {
                type = "Outgoing";
            }

            if (call.type === 3) {
                type = "Missed";
            }

            const name =
                call.name &&
                call.name !== "null"
                    ? call.name
                    : call.number;

            item.innerHTML = `
                <strong>📞 ${type}</strong><br>
                ${escapeHtml(name || "Unknown")}<br>
                <small>
                    ${date.toLocaleString()}
                    • ${call.duration || 0}s
                </small>
            `;

            box.appendChild(item);
        });

    } catch (error) {

        console.error(error);

        $("calls").textContent =
            "Unable to read call history.";
    }
}


/* ==============================
   INCOMING CALL EVENT
   ============================== */

function onPhoneEvent(raw) {

    try {

        const data =
            typeof raw === "string"
                ? JSON.parse(raw)
                : raw;

        const item =
            document.createElement("div");

        item.className = "item";

        let label = "Call event";

        if (
            data.state &&
            data.state.includes("RINGING")
        ) {
            label = "📞 Incoming call";
        }

        else if (
            data.state &&
            data.state.includes("OFFHOOK")
        ) {
            label = "📞 Call answered";
        }

        else if (
            data.state &&
            data.state.includes("IDLE")
        ) {
            label = "📞 Call ended";
        }

        const number =
            data.number || "Number unavailable";

        item.innerHTML = `
            <strong>${label}</strong><br>
            ${escapeHtml(number)}<br>
            <small>
                ${new Date(
                    Number(data.time)
                ).toLocaleTimeString()}
            </small>
        `;

        $("calls").prepend(item);

        setStatus(
            "● Incoming call event received"
        );

    } catch (error) {

        console.error(error);
    }
}


/* ==============================
   SMS EVENT
   ============================== */

function onSmsEvent(raw) {

    try {

        const data =
            typeof raw === "string"
                ? JSON.parse(raw)
                : raw;

        const item =
            document.createElement("div");

        item.className = "item";

        item.innerHTML = `
            <strong>💬 ${escapeHtml(
                data.sender || "Unknown"
            )}</strong><br>

            ${escapeHtml(
                data.body || ""
            )}

            <br>

            <small>
                ${new Date(
                    Number(data.time)
                ).toLocaleTimeString()}
            </small>
        `;

        $("messages").prepend(item);

        setStatus(
            "● New SMS received"
        );

    } catch (error) {

        console.error(error);
    }
}


/* ==============================
   NOTIFICATION EVENT
   ============================== */

function onNotificationEvent(raw) {

    try {

        const data =
            typeof raw === "string"
                ? JSON.parse(raw)
                : raw;

        const item =
            document.createElement("div");

        item.className = "item";

        const source =
            data.title ||
            data.package ||
            "Notification";

        item.innerHTML = `
            <strong>🔔 ${escapeHtml(
                source
            )}</strong><br>

            ${escapeHtml(
                data.text || ""
            )}

            <br>

            <small>
                ${new Date(
                    Number(data.time)
                ).toLocaleTimeString()}
            </small>
        `;

        $("notifications").prepend(item);

        setStatus(
            "● New notification received"
        );

    } catch (error) {

        console.error(error);
    }
}


/* ==============================
   NOTIFICATION SETTINGS
   ============================== */

function enableNotifications() {

    if (!bridgeAvailable()) {

        alert(
            "Open this dashboard inside the APK."
        );

        return;
    }

    Android.openNotificationSettings();
}


/* ==============================
   APP PERMISSION SETTINGS
   ============================== */

function openSettings() {

    if (!bridgeAvailable()) {

        alert(
            "This option is available inside the APK."
        );

        return;
    }

    Android.openAppSettings();
}


/* ==============================
   CAMERA
   ============================== */

async function startCamera() {

    try {

        cameraStream =
            await navigator.mediaDevices
                .getUserMedia({
                    video: {
                        facingMode:
                            "environment"
                    },
                    audio: false
                });

        $("camera").srcObject =
            cameraStream;

        setStatus(
            "● Camera active"
        );

    } catch (error) {

        console.error(error);

        alert(
            "Camera permission denied or camera unavailable."
        );
    }
}


function capture() {

    if (!cameraStream) {

        alert(
            "Start the camera first."
        );

        return;
    }

    const video =
        $("camera");

    const canvas =
        $("canvas");

    canvas.width =
        video.videoWidth;

    canvas.height =
        video.videoHeight;

    const context =
        canvas.getContext("2d");

    context.drawImage(
        video,
        0,
        0,
        canvas.width,
        canvas.height
    );

    const image =
        canvas.toDataURL(
            "image/jpeg",
            0.92
        );

    $("preview").src =
        image;

    $("preview").style.display =
        "block";
}


function stopCamera() {

    if (cameraStream) {

        cameraStream
            .getTracks()
            .forEach(track =>
                track.stop()
            );

        cameraStream = null;
    }

    $("camera").srcObject =
        null;

    setStatus(
        "● Dashboard ready"
    );
}


/* ==============================
   FILE PICKER
   ============================== */

$("files").addEventListener(
    "change",
    function(event) {

        const files =
            [...event.target.files];

        const list =
            $("fileList");

        list.innerHTML = "";

        files.forEach(file => {

            const item =
                document.createElement("div");

            item.className =
                "item";

            const size =
                (
                    file.size /
                    1048576
                ).toFixed(2);

            item.textContent =
                `${file.name} — ${size} MB`;

            list.appendChild(item);
        });
    }
);


/* ==============================
   HTML SAFETY
   ============================== */

function escapeHtml(value) {

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


/* ==============================
   STARTUP
   ============================== */

window.addEventListener(
    "load",
    function() {

        if (bridgeAvailable()) {

            setStatus(
                "● Android bridge connected"
            );

            loadCalls();

        } else {

            setStatus(
                "● Browser mode"
            );
        }
    }
);
