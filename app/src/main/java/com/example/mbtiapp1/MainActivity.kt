package com.example.mbtiapp1

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private val mbtiImageMap = mapOf(
        "INFP" to R.drawable.infp,
        "ENFP" to R.drawable.enfp,
        "INFJ" to R.drawable.infj,
        "ENFJ" to R.drawable.enfj,
        "INTJ" to R.drawable.intj,
        "ENTJ" to R.drawable.entj,
        "INTP" to R.drawable.intp,
        "ENTP" to R.drawable.entp,
        "ISFP" to R.drawable.isfp,
        "ESFP" to R.drawable.esfp,
        "ISTP" to R.drawable.istp,
        "ESTP" to R.drawable.estp,
        "ISFJ" to R.drawable.isfj,
        "ESFJ" to R.drawable.esfj,
        "ISTJ" to R.drawable.istj,
        "ESTJ" to R.drawable.estj
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupSpinners()
        setupButton()
    }
    
    // 스피너 설정
    private fun setupSpinners() {
        val spinnerMyMbti = findViewById<AutoCompleteTextView>(R.id.spinnerMyMbti)
        val spinnerFriendMbti = findViewById<AutoCompleteTextView>(R.id.spinnerFriendMbti)

        val mbtiArray = resources.getStringArray(R.array.mbti_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mbtiArray)

        spinnerMyMbti.setAdapter(adapter)
        spinnerFriendMbti.setAdapter(adapter)
    }
    
    // 버튼 설정
    private fun setupButton() {
        val buttonResult = findViewById<Button>(R.id.buttonResult)
        buttonResult.setOnClickListener {
            val myMbti = findViewById<AutoCompleteTextView>(R.id.spinnerMyMbti)
                .text.toString().uppercase()
            val friendMbti = findViewById<AutoCompleteTextView>(R.id.spinnerFriendMbti)
                .text.toString().uppercase()

            if (myMbti.isNotEmpty() && friendMbti.isNotEmpty()) {
                showCompatibilityResult(myMbti, friendMbti)
            } else {
                showToast("MBTI를 선택해주세요.")
            }
        }
    }
    
    // 궁합 결과 표시
    private fun showCompatibilityResult(myMbti: String, friendMbti: String) {
        try {
            // MBTI 데이터 로드
            val mbtiData = loadMbtiData()
            val compatData = loadCompatibilityData()
            val myMbtiInfo = mbtiData.getJSONObject(myMbti)
            
            // CardView 표시
//            findViewById<CardView>(R.id.cardViewResult).visibility = View.VISIBLE

            // 궁합 메시지 표시
            val detailedMessage = getDetailedCompatibilityMessage(compatData, myMbti, friendMbti)
            findViewById<TextView>(R.id.textViewResult).text = detailedMessage

            // 이미지 업데이트
            updateImages(myMbti, friendMbti)

            // MBTI 타입과 성격 이름 표시
            updateTypeInfo(myMbti, friendMbti, mbtiData)

            // 궁합 목록 표시
            updateCompatibilityLists(myMbtiInfo)

        } catch (e: Exception) {
            e.printStackTrace()
            showToast("오류가 발생했습니다. 다시 시도해주세요.")
        }
    }

    // MBTI 궁합 별 상세 메시지 표시
    private fun getDetailedCompatibilityMessage(compatData: JSONObject, myMbti: String, friendMbti: String): String {
        return try {
            compatData.getJSONObject(myMbti).getString(friendMbti)
        } catch (e: Exception) {
            "상세 궁합 정보를 불러올 수 없습니다."
        }
    }
    
    // mbti.json 불러오기
    private fun loadMbtiData(): JSONObject {
        val inputStream: InputStream = assets.open("mbti.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return JSONObject(String(buffer, Charsets.UTF_8))
    }
    
    // mbti_compatibility.json 불러오기
    private fun loadCompatibilityData(): JSONObject {
        val inputStream: InputStream = assets.open("mbti_compatibility.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return JSONObject(String(buffer, Charsets.UTF_8))
    }
    
    // MBTI 별 이미지 불러오기
    private fun updateImages(myMbti: String, friendMbti: String) {
        val imageViewMBTI = findViewById<ImageView>(R.id.imageViewMBTI)
        val imageViewMBTI2 = findViewById<ImageView>(R.id.imageViewMBTI2)

        mbtiImageMap[myMbti]?.let { imageResId ->
            imageViewMBTI.setImageResource(imageResId)
        }
        mbtiImageMap[friendMbti]?.let { imageResId ->
            imageViewMBTI2.setImageResource(imageResId)
        }
    }

    // MBTI 별 성격 이름 불러오기
    private fun updateTypeInfo(myMbti: String, friendMbti: String, mbtiData: JSONObject) {
        val myInfo = mbtiData.getJSONObject(myMbti)
        val friendInfo = mbtiData.getJSONObject(friendMbti)

        findViewById<TextView>(R.id.textViewMyType).text =
            "$myMbti\n${myInfo.getString("성격 이름")}"
        findViewById<TextView>(R.id.textViewFriendType).text =
            "$friendMbti\n${friendInfo.getString("성격 이름")}"
    }

    // 궁합 별 MBTI 리스트 불러오기
    private fun updateCompatibilityLists(mbtiInfo: JSONObject) {
        findViewById<TextView>(R.id.textViewBest).text =
            "${mbtiInfo.getJSONArray("최고의 궁합").join()}"
        findViewById<TextView>(R.id.textViewGood).text =
            "${mbtiInfo.getJSONArray("좋은 궁합").join()}"
        findViewById<TextView>(R.id.textViewBad).text =
            "${mbtiInfo.getJSONArray("최악의 궁합").join()}"
    }

    // MBTI 리스트를 ,를 사용해 join
    private fun JSONArray.join(): String {
        val result = StringBuilder()
        for (i in 0 until length()) {
            if (i > 0) result.append(", ")
            result.append(getString(i))
        }
        return result.toString()
    }
    
    // 토스트를 표시
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}