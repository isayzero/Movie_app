package com.example.movie_app

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var btnRegister: Button
    lateinit var edtName: EditText
    lateinit var edtId: EditText
    lateinit var edtPassword: EditText
    lateinit var edtEmail: EditText
    lateinit var rgGender: RadioGroup
    lateinit var rbGenderMale: RadioButton
    lateinit var rbGenderFemale: RadioButton
    lateinit var chkAct: CheckBox
    lateinit var chkCom: CheckBox
    lateinit var chkDra: CheckBox
    lateinit var chkFam: CheckBox
    lateinit var chkFst: CheckBox
    lateinit var chkHis: CheckBox
    lateinit var chkHor: CheckBox
    lateinit var chkRom: CheckBox
    lateinit var chkSF: CheckBox
    lateinit var chkThr: CheckBox
    lateinit var chkAdv: CheckBox
    lateinit var chkAni: CheckBox
    lateinit var chkCri: CheckBox
    lateinit var chkMus: CheckBox
    lateinit var chkMst: CheckBox
    lateinit var chkWar: CheckBox
    lateinit var chkWes: CheckBox

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister = findViewById(R.id.btnRegister)
        edtName = findViewById(R.id.edtName)
        edtId = findViewById(R.id.edtID)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        rgGender = findViewById(R.id.rgGender)
        rbGenderMale = findViewById(R.id.rbGenderMale)
        rbGenderFemale = findViewById(R.id.rbGenderFemale)
        chkAct = findViewById(R.id.chkAct)
        chkCom = findViewById(R.id.chkCom)
        chkDra = findViewById(R.id.chkDra)
        chkFam = findViewById(R.id.chkFam)
        chkFst = findViewById(R.id.chkFst)
        chkHis = findViewById(R.id.chkHis)
        chkHor = findViewById(R.id.chkHor)
        chkRom = findViewById(R.id.chkRom)
        chkSF = findViewById(R.id.chkSF)
        chkThr = findViewById(R.id.chkThr)
        chkAdv = findViewById(R.id.chkAdv)
        chkAni = findViewById(R.id.chkAni)
        chkCri = findViewById(R.id.chkCri)
        chkMus = findViewById(R.id.chkMus)
        chkMst = findViewById(R.id.chkMst)
        chkWar = findViewById(R.id.chkWar)
        chkWes = findViewById(R.id.chkWes)

        dbManager = DBManager(this, "personnel", null, 1)

        // 회원가입 버튼 클릭 리스너 설정
        btnRegister.setOnClickListener {
            val name = edtName.text.toString()
            val id = edtId.text.toString()
            val password = edtPassword.text.toString()
            val email = edtEmail.text.toString()  // 이메일 입력 값 가져오기
            val gender = when (rgGender.checkedRadioButtonId) {
                R.id.rbGenderMale -> rbGenderMale.text.toString()
                R.id.rbGenderFemale -> rbGenderFemale.text.toString()
                else -> ""
            }

            // 선택된 장르 수집
            val genres = mutableListOf<String>()
            if (chkAct.isChecked) genres.add("액션")
            if (chkCom.isChecked) genres.add("코미디")
            if (chkDra.isChecked) genres.add("드라마")
            if (chkFam.isChecked) genres.add("가족")
            if (chkFst.isChecked) genres.add("판타지")
            if (chkHis.isChecked) genres.add("역사")
            if (chkHor.isChecked) genres.add("공포")
            if (chkRom.isChecked) genres.add("로맨스")
            if (chkSF.isChecked) genres.add("SF")
            if (chkThr.isChecked) genres.add("스릴러")
            if (chkAdv.isChecked) genres.add("어드벤처")
            if (chkAni.isChecked) genres.add("애니메이션")
            if (chkCri.isChecked) genres.add("범죄")
            if (chkMus.isChecked) genres.add("음악")
            if (chkMst.isChecked) genres.add("미스터리")
            if (chkWar.isChecked) genres.add("전쟁")
            if (chkWes.isChecked) genres.add("서부")

            val genresString = genres.joinToString(", ")

            // 비밀번호 길이 체크
            if (password.length < 6) {
                Toast.makeText(this, "비밀번호는 6자리 이상으로 설정해 주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 아이디 중복 체크
            sqlitedb = dbManager.readableDatabase
            val cursor = sqlitedb.rawQuery("SELECT * FROM personnel WHERE id = ?", arrayOf(id))

            if (cursor.count > 0) {
                Toast.makeText(this, "동일한 아이디가 존재합니다.", Toast.LENGTH_SHORT).show()
                cursor.close()
                sqlitedb.close()
                return@setOnClickListener
            }

            cursor.close()
            sqlitedb.close()

            // 데이터베이스에 사용자 정보 저장
            sqlitedb = dbManager.writableDatabase
            try {
                sqlitedb.execSQL(
                    "INSERT INTO personnel (name, id, password, email, gender, genres) VALUES (?, ?, ?, ?, ?, ?)",
                    arrayOf(name, id, password, email, gender, genresString)
                )
                Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()

                // 사용자 정보를 SharedPreferences에 저장
                val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("userId", id)
                editor.putString("userName", name)
                editor.putString("userGender", gender)
                editor.putString("userEmail", email)
                editor.putString("userGenres", genresString)
                editor.apply()

                // 로그인 화면으로 이동
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "회원가입에 실패했습니다. 다시 시도해 주세요. ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            } finally {
                sqlitedb.close()
            }
        }
    }
}
