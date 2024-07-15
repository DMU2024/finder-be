document.addEventListener("DOMContentLoaded", function() {
    const urlParams = new URLSearchParams(window.location.search);
    const recipientId = urlParams.get('recipient');

    const username = localStorage.getItem("username");
    const recipientName = getUserById(recipientId).username;

    if (!username) {
        window.location.href = "login.html"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        return;
    }

    // 채팅 대상자 이름 표시
    document.getElementById("recipientName").textContent = recipientName;

    // WebSocket 연결
    let ws = new WebSocket("ws://localhost:8081/ws/chat");

    ws.onopen = function() {
        console.log("Connected to the chat server");
        ws.send(`${username} has entered the chat`); // 입장 메시지 전송
    };

    ws.onmessage = function(event) {
        const messageElement = document.createElement("div");
        messageElement.className = "mb-3";

        if (event.data.startsWith(username)) {
            messageElement.innerHTML = `<div class="alert alert-primary">${event.data}</div>`;
        } else {
            messageElement.innerHTML = `<div class="alert alert-secondary">${event.data}</div>`;
        }

        document.getElementById("chatArea").appendChild(messageElement);
        document.getElementById("chatArea").scrollTop = document.getElementById("chatArea").scrollHeight;
    };

    ws.onclose = function() {
        console.log("Disconnected from the chat server");
    };

    // 메시지 전송
    window.sendMessage = function() {
        const messageInput = document.getElementById("messageInput");
        const message = messageInput.value.trim();

        if (message) {
            ws.send(`${username}: ${message}`);
            messageInput.value = "";
        }
    };

    // 사용자 목록에서 userId로 사용자 정보 가져오기 (가상의 함수)
    function getUserById(userId) {
        return userList.find(user => user.userId == userId);
    }
});