// 가상의 사용자 목록 데이터
const userList = [
    { userId: 1, username: "user1" },
    { userId: 2, username: "user2" },
    { userId: 3, username: "user3" }
];

document.addEventListener("DOMContentLoaded", function() {
    const username = localStorage.getItem("username");

    if (!username) {
        window.location.href = "login.html"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        return;
    }

    const userListElement = document.getElementById("userList");

    // 사용자 목록을 출력
    userList.forEach(user => {
        const li = document.createElement("li");
        li.className = "list-group-item";
        li.innerHTML = `<a href="chat.html?recipient=${user.userId}">${user.username}</a>`;
        userListElement.appendChild(li);
    });
});