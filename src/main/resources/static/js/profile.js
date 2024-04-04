function refreshPage() {
    location.reload();
}

document.addEventListener("DOMContentLoaded", function () {
    // 파일 입력란 변경 이벤트 감지
    document.getElementById("image").addEventListener("change", function(event) {
        var reader = new FileReader();
        reader.onload = function() {
            var profileImage = document.querySelector(".profile-img img");
            profileImage.src = reader.result;

            // 이미지가 변경되었을 때만 저장 버튼 활성화
            document.getElementById("saveButton").disabled = false;
        };
        reader.readAsDataURL(event.target.files[0]);
    });
});

document.getElementById("saveButton").addEventListener("click", function (event) {
    refreshPage();
})

// 프로필 이미지 제거
function removeProfile() {
    // 기본 이미지 보여주기
    document.getElementById("profileImage").src = "/files/member/회원프로필.jpg";
    // x 버튼 숨기기
    document.querySelector(".remove-profile").style.display = "none";

    document.getElementById("saveButton").disabled = false;
}

// 계정 탈퇴 클릭 시 뜨는 확인 창
document.getElementById('leaveAccount').addEventListener('click', function (event) {
    event.preventDefault();

    // 확인 창 표시
    var confirmation = confirm("정말로 계정을 탈퇴하시겠습니다?");

    if (confirmation) {
        window.location.href = '/member/leaveaccount';
    }
});