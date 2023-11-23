package com.threelab.apsensi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.threelab.apsensi.Fragment.viewabsenFragment

class EditProfilActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)

        val simpanPerubahan_button: Button = findViewById(R.id.simpanPerubahan_button)
        simpanPerubahan_button.setOnClickListener(this)




        }

    override fun onClick(v: View?) {

        if (v !=null) {
            when(v.id){
                R.id.simpanPerubahan_button -> {
                    val pindahIntent = Intent(this,ProfilActivity::class.java)
                    startActivity(pindahIntent)
                }
            }
    }


}


    }





