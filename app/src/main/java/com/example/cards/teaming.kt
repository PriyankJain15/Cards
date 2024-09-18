package com.example.cards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.cards.databinding.ActivityTeamingBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class teaming : AppCompatActivity() {

    private lateinit var binding: ActivityTeamingBinding

    private var gamemodel:GameModelFirebase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val gameCode = intent.getStringExtra("GAME_CODE") ?: ""
//        GameDataFirebase.fetchGameModel()
        Log.d("TeamingActivity", "GameModel fetched")


        val playerId = intent.getStringExtra("PLAYER_ID") ?: ""
        val playerName = intent.getStringExtra("PLAYER_NAME") ?: ""
        val profileImageResId = intent.getIntExtra("PROFILE_IMAGE_RES_ID", R.drawable.profile12)

        // Fetch the game model
        GameDataFirebase.gamemodel.observe(this){

            Log.d("TeamingActivity", "GameModel updated: ")
            gamemodel = it
            gamemodel?.let {
//                updateUI( playerId, playerName, profileImageResId)
            }
        }




        binding.codeText.text = "CODE-$gameCode"


        binding.backButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@teaming, MainActivity::class.java))
        })

    }

//    private fun updateUI( playerId: String, playerName: String, profileImageResId: Int) {
//        gamemodel?.apply {
//                // Update UI with player data
//                if (teams[0].players.getOrNull(0)?.id == playerId) {
//                    Log.w("uui",playerId)
//                    binding.profileImageT1.setImageResource(profileImageResId)
//                    binding.playerNameT1.text = playerName
//                }
//                if (teams[0].players.getOrNull(1)?.id == playerId) {
//                    Log.w("uui",playerId)
//                    binding.profileImageT2.setImageResource(profileImageResId)
//                    binding.playerNameT2.text = playerName
//                }
//                if (teams[1].players.getOrNull(0)?.id == playerId) {
//                    Log.w("uui",playerId)
//                    binding.profileImageT3.setImageResource(profileImageResId)
//                    binding.playerNameT3.text = playerName
//
//                }
//                if (teams[1].players.getOrNull(1)?.id == playerId) {
//                    Log.w("uui",playerId)
//                    binding.profileImageT4.setImageResource(profileImageResId)
//                    binding.playerNameT4.text = playerName
//                }
//        }
//    }
}