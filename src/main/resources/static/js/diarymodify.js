document.addEventListener('DOMContentLoaded', function () {
    const deleteImageCheckbox = document.getElementById('deleteImageCheckbox');
    const imageUploadSection = document.getElementById('image');
    const image = document.querySelectorAll('img');
    const imageCheckboxLabel = document.querySelector('label[for="deleteImageCheckbox"]');

    deleteImageCheckbox.addEventListener('change', function () {
        // 기존 이미지 삭제 체크
        if (deleteImageCheckbox.checked) {
            // 이미지 숨김
            image.forEach(function (img) {
                img.style.display = 'none';
            });
            // 이미지 업로드 창 보임
            imageUploadSection.classList.remove('hidden');
            // 이미지 삭제 체크박스 숨김
            deleteImageCheckbox.style.display = 'none';
            imageCheckboxLabel.style.display = 'none';
        } else {
            image.forEach(function (img) {
                img.style.display = 'block';
            });

            imageUploadSection.classList.add('hidden');
        }
    });
});
