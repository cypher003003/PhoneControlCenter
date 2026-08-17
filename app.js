const $ = (id) => document.getElementById(id);

let cameraStream = null;

$("connectBtn").addEventListener("click", () => {
  $("status").textContent = "● Waiting for Android bridge…";
  $("transferStatus").textContent = "Phone bridge connection requested.";
});

$("startCamera").addEventListener("click", async () => {
  try {
    cameraStream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: "environment" },
      audio: false
    });
    $("camera").srcObject = cameraStream;
    $("status").textContent = "● Camera active";
  } catch (error) {
    alert("Camera permission was not granted or the camera is unavailable.");
    console.error(error);
  }
});

$("capture").addEventListener("click", () => {
  if (!cameraStream) {
    alert("Start the camera first.");
    return;
  }

  const video = $("camera");
  const canvas = $("snapshot");
  canvas.width = video.videoWidth;
  canvas.height = video.videoHeight;

  const ctx = canvas.getContext("2d");
  ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

  $("preview").src = canvas.toDataURL("image/jpeg", 0.92);
  $("preview").style.display = "block";
});

$("stopCamera").addEventListener("click", () => {
  if (cameraStream) {
    cameraStream.getTracks().forEach(track => track.stop());
    cameraStream = null;
    $("camera").srcObject = null;
    $("status").textContent = "● Web dashboard ready";
  }
});

$("filePicker").addEventListener("change", (event) => {
  const files = [...event.target.files];
  const list = $("fileList");
  list.innerHTML = "";

  files.forEach(file => {
    const item = document.createElement("div");
    item.className = "file-item";
    item.textContent = `${file.name} — ${(file.size / 1024 / 1024).toFixed(2)} MB`;
    list.appendChild(item);
  });
});

/*
  Android bridge will be added in the next step.

  Planned bridge calls:
    window.AndroidBridge.getCalls()
    window.AndroidBridge.getMessages()
    window.AndroidBridge.getNotifications()
    window.AndroidBridge.startLocalTransferServer()
*/
