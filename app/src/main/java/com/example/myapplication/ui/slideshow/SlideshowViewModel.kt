package com.example.myapplication.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SlideshowViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = """
            Stock Chess là phần mềm chơi cờ vua được phát triển bởi Nguyễn Thành Đạt (21020064), Phạm Thanh Sơn (21020027) và Ngô Văn Tuân (21020031). App gồm có các chức năng sau:
            1. Chơi offline với bot AI, cho phép người chơi chọn mức độ khó của bot như Beginner, Intermediate, Advanced, World Class,…
            2. Chế độ chơi 2 người 1 máy với multitouch.
            3. Các chế độ time control như Bullet (Cờ 1-2 phút), Blitz (3-8 phút), Rapid (10-20 phút) hay Classic (Hơn 1 tiếng).
            4. Tích hợp youtube giúp người dùng truy cập một thư viện video về cờ vua.
            5. Tính năng phân tích trận đấu, đánh giá nước vừa đi tốt hay không, sử dụng Stockfish engine.
            6. Chơi online với người khác, người chơi có thể đăng nhập sử dụng tài khoản Lichess (hoặc Google, Facebook,…).
            7. Gửi tin nhắn trực tiếp với đối thủ.
            8. Notification: Khi app chạy nền, gửi thông báo về daily puzzle, tin nhắn hay nước đi mới từ đối thủ.
        """.trimIndent()
    }
    val text: LiveData<String> = _text
}