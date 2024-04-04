document.addEventListener('DOMContentLoaded', function () {
    const delete_elements = document.querySelectorAll('.delete');
    Array.from(delete_elements).forEach(function (element) {
        element.addEventListener('click', function () {
            console.log("클릭 이벤트가 발생했습니다.")
            if(confirm("일기를 삭제할까요?")) {
                location.href = this.getAttribute('data-uri');
            };
        });
    });
});