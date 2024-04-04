var today = new Date();
var currentYear = today.getFullYear();
var currentMonth = today.getMonth();

var monthYearElement = document.getElementById('currentMonthYear');

function applyDiaryWrittenStyle(cell, dateString) {
    isDiaryWritten(dateString, function (written) {
        if (written) {
            cell.classList.add('written'); // css에서 스타일 지정
        }
    });
}

function renderCalendar(year, month) {
    var calendarBody = document.getElementById('calendar-body');
    var daysInMonth = new Date(year, month + 1, 0).getDate();
    var firstDayOfMonth = new Date(year, month, 1).getDay();

    calendarBody.innerHTML = '';
    monthYearElement.textContent = year + '년 ' + getMonthName(month);

    var dateCount = 1;
    for (var i = 0; i < 6; i++) {
        var row = document.createElement('tr');
        for (var j = 0; j < 7; j++) {
            var cell = document.createElement('td');
            if (i === 0 && j < firstDayOfMonth) {
                var emptyCell = document.createTextNode('');
                cell.appendChild(emptyCell);
            } else if (dateCount > daysInMonth) {
                // 해당 월의 일 수를 초과하면 반복문 종료
                break;
            } else {
                // 날짜 표시 셀을 생성
                var day = dateCount;
                var dayCell = document.createTextNode(day);

                var monthFormatted = (month + 1) < 10 ? '0' + (month + 1) : (month + 1);
                var dateFormatted = day < 10 ? '0' + day : day; // 항상 두자리 숫자로 표시
                var dateString = year + '-' + monthFormatted + '-' + dateFormatted;
                cell.setAttribute('data-date', dateString);
                cell.addEventListener('click', diaryWrite);
                cell.appendChild(dayCell);
                dateCount++;

                // 일기가 작성된 날짜에만 배경색 설정
                applyDiaryWrittenStyle(cell, dateString);
            }
            row.appendChild(cell);
        }
        calendarBody.appendChild(row);
    }
}

function isDiaryWritten(dateString, callback) {
    var xhr = new XMLHttpRequest();
    var url = '/diary/checkDate?date=';
    var params = dateString;
    xhr.open('GET', url + params, true);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                var response = xhr.responseText;
                if (response === "exists") {
                    callback(true);
                } else if (response === "not exists") {
                    callback(false);
                } else {
                    callback(false);
                }
            } else {
                callback(false);
            }
        }
    }
    xhr.send();
}

function prevMonth() {
    currentMonth--;
    if (currentMonth < 0) {
        currentMonth = 11;
        currentYear--;
    }
    renderCalendar(currentYear, currentMonth);
}

function nextMonth() {
    currentMonth++;
    if (currentMonth > 11) {
        currentMonth = 0;
        currentYear++;
    }
    renderCalendar(currentYear, currentMonth);
}

function viewDiaryListByMonth(year, month) {
    window.location.href = '/diary/list/' + year + '/' + month;
}

function getMonthName(month) {
    var monthNames = ["1월", "2월", "3월", "4월", "5월", "6월",
        "7월", "8월", "9월", "10월", "11월", "12월"];
    return monthNames[month];
}

function diaryWrite(event) {
    var selectedDate = event.target.getAttribute('data-date');
    var currentDate = new Date();
    var selectedDateTime = new Date(selectedDate);

    if (selectedDateTime > currentDate) { // 미래 날짜 선택
        alert("미래의 일기는 아직 작성할 수 없어요!")
    } else {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', '/diary/checkDate?date=' + selectedDate, true);
        xhr.onreadystatechange = function () {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    var response = xhr.responseText;
                    if (response === "exists") {
                        // 데이터베이스에 해당 날짜가 존재하면 view 페이지로 이동
                        window.location.href = '/diary/view/' + selectedDate;
                    } else if (response === "not exists") {
                        // 데이터베이스에 해당 날짜가 존재하지 않으면 write 페이지로 이동
                        window.location.href = '/diary/write/' + selectedDate;
                    } else {
                        alert('날짜 확인 요청에 실패했습니다.')
                    }
                } else {
                    // 요청 실패 시 알림을 표시
                    alert('로그인이 필요합니다.')
                }
            }
        };
        xhr.send();
    }
}

renderCalendar(currentYear, currentMonth);
