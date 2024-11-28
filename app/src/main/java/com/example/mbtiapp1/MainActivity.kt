package com.example.mbtiapp1

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    // 리소스 맵 정의
    private val mbtiImageMap = mapOf(
        "infp" to R.drawable.infp,
        "enfp" to R.drawable.enfp,
        "infj" to R.drawable.infj,
        "enfj" to R.drawable.enfj,
        "intj" to R.drawable.intj,
        "entj" to R.drawable.entj,
        "intp" to R.drawable.intp,
        "entp" to R.drawable.entp,
        "isfp" to R.drawable.isfp,
        "esfp" to R.drawable.esfp,
        "istp" to R.drawable.istp,
        "estp" to R.drawable.estp,
        "isfj" to R.drawable.isfj,
        "esfj" to R.drawable.esfj,
        "istj" to R.drawable.istj,
        "estj" to R.drawable.estj
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinnerMyMbti = findViewById<AutoCompleteTextView>(R.id.spinnerMyMbti)
        val spinnerFriendMbti = findViewById<AutoCompleteTextView>(R.id.spinnerFriendMbti)
        val imageViewMBTI = findViewById<ImageView>(R.id.imageViewMBTI)
        val imageViewMBTI2 = findViewById<ImageView>(R.id.imageViewMBTI2)
        val buttonResult  = findViewById<Button>(R.id.buttonResult)
        val resultTextView = findViewById<TextView>(R.id.textViewResult)

        // MBTI 데이터 연결
        val mbtiArray = resources.getStringArray(R.array.mbti_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mbtiArray)
        spinnerMyMbti.setAdapter(adapter)
        spinnerFriendMbti.setAdapter(adapter)

        // 버튼 클릭
        buttonResult .setOnClickListener {
            val myMbti = spinnerMyMbti.text.toString()
            val friendMbti = spinnerFriendMbti.text.toString()

            // 결과 표시
            if (myMbti.isNotEmpty() && friendMbti.isNotEmpty()) {
                val result = parseMbtiJson(myMbti, friendMbti)
                resultTextView.text = result
            } else {
                resultTextView.text = "MBTI를 선택해주세요."
            }

            // 내 MBTI 이미지 설정
            mbtiImageMap[myMbti.lowercase()]?.let { imageResId ->
                imageViewMBTI.setImageResource(imageResId)
            }

            // 친구 MBTI 이미지 설정
            mbtiImageMap[friendMbti.lowercase()]?.let { imageResId ->
                imageViewMBTI2.setImageResource(imageResId)
            }
        }
    }

    private fun parseMbtiJson(myMbti: String, friendMbti: String): String {
        return try {
            val inputStream: InputStream = assets.open("mbti_compatibility.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val json = String(buffer, Charsets.UTF_8)

            val jsonObject = JSONObject(json)
            val myMbtiObject = jsonObject.getJSONObject(myMbti)
            myMbtiObject.getString(friendMbti)
        } catch (e: Exception) {
            e.printStackTrace()
            "오류가 발생했습니다. 다시 시도해주세요."
        }
    }
}
