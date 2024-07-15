// 가상의 사용자 데이터베이스
const users = [
{ username: "user1", password: "password1" },
{ username: "user2", password: "password2" },
{ username: "user3", password: "password3" }
];

document.getElementById("loginForm").addEventListener("submit", function(event) {
event.preventDefault(); // 기본 동작 방지

const username = document.getElementById("username").value;
const password = document.getElementById("password").value;

// 사용자 인증
const authenticatedUser = users.find(user => user.username === username && user.password === password);

if (authenticatedUser) {
localStorage.setItem("username", authenticatedUser.username); // 로컬 스토리지에 사용자 저장
window.location.href = "user_list.html"; // 사용자 목록 페이지로 이동
} else {
alert("Invalid username or password");
}
});