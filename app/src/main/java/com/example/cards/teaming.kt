package com.example.cards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.example.cards.databinding.ActivityTeamingBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class teaming : AppCompatActivity() {

    private lateinit var binding: ActivityTeamingBinding

    lateinit var gameCode:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameCode = intent.getStringExtra("GAME_CODE") ?: ""

        GameDataFirebase.fetchGameModel(gameCode , OnDocumentDeleted = {
            // Handle game deletion, e.g., navigate back to main activity or show an alert
            runOnUiThread {
                // Show an alert to the user
                AlertDialog.Builder(this@teaming)
                    .setTitle("Game Deleted")
                    .setMessage("The game has been deleted by the host.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                        // Navigate to main activity
                        startActivity(Intent(this@teaming, MainActivity::class.java))
                        finish() // Close the current activity
                    }
                    .show()
            }
        })
        Log.d("TeamingActivity", "GameModel fetched")


        val playerId = intent.getStringExtra("PLAYER_ID") ?: ""
        val playerName = intent.getStringExtra("PLAYER_NAME") ?: ""
        val profileImageResId = intent.getIntExtra("PROFILE_IMAGE_RES_ID", R.drawable.profile12)

        // Fetch the game model

        binding.codeText.text = "CODE-${gameCode}"


        GameDataFirebase.gamemodel.observe(this, Observer { value ->

            if (value == null) {
                Log.w("GameModel", "GameModel is null")
                return@Observer
            }

            if(value.status == GameModelFirebase.Status.DELETED){
                startActivity(Intent(this@teaming, MainActivity::class.java))
                finish()
            }

            Log.w("Listen Observed", "INSIDE")

            // Check if team 0 exists and has players
            if (value.teams.isNotEmpty() && value.teams.size > 0) {
                val team1 = value.teams[0]

                // Check if team 1 has at least one player

                if (team1.players.isNotEmpty() && team1.players.size > 0) {
                    binding.profileImageT1.setImageResource(value.teams[0].players[0].profileImageResId)
                    binding.playerNameT1.text = value.teams[0].players[0].name
                    binding.profileImageT2.setImageResource(R.drawable.backgrund2)
                    binding.playerNameT2.text = ""
                }

                // Check if team 1 has at least two players
                if (team1.players.size > 1) {
                    binding.profileImageT2.setImageResource(value.teams[0].players[1].profileImageResId)
                    binding.playerNameT2.text = value.teams[0].players[1].name
                }
        }

            // Check if team 1 exists and has players
            if (value.teams.size > 1) {
                val team2 = value.teams[1]

                if(team2.players.isEmpty()){
                    binding.profileImageT3.setImageResource(R.drawable.backgrund2)
                    binding.playerNameT3.text = ""
                    binding.profileImageT4.setImageResource(R.drawable.backgrund2)
                    binding.playerNameT4.text = ""
                }
                // Check if team 2 has at least one player
                if (team2.players.isNotEmpty() && team2.players.size > 0) {
                    binding.profileImageT3.setImageResource(value.teams[1].players[0].profileImageResId)
                    binding.playerNameT3.text = value.teams[1].players[0].name
                    binding.profileImageT4.setImageResource(R.drawable.profile12)
                    binding.playerNameT4.text = ""
                }

                // Check if team 2 has at least two players
                if (team2.players.size > 1) {
                        binding.profileImageT4.setImageResource(value.teams[1].players[1].profileImageResId)
                        binding.playerNameT4.text = value.teams[1].players[1].name
                }
            }
        })

        binding.startButton.setOnClickListener(View.OnClickListener { startActivity(Intent(this@teaming, MainActivity::class.java)) })
        binding.backButton.setOnClickListener(View.OnClickListener {

            val currentPlayerId = intent.getStringExtra("PLAYER_ID") ?: ""

            startActivity(Intent(this@teaming, MainActivity::class.java))

            GameDataFirebase.gamemodel.value?.let {gameModel ->
                // Check if the player is team[0].players[0]
                if (gameModel.teams.isNotEmpty() && gameModel.teams[0].players.isNotEmpty() &&
                    gameModel.teams[0].players[0].id == currentPlayerId) {

                    // If the current player is the first player in the first team, delete the entire game
                    Firebase.firestore.collection("games").document(gameCode)
                        .delete()
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error deleting game", e)
                        }
                }else {
                    // Otherwise, find and remove only the current player
                   for (team in gameModel.teams) {
                        val playerToRemove = team.players.find { it.id == currentPlayerId }
                        if (playerToRemove != null) {
                           team.players.remove(playerToRemove) // Remove the player locally

                            GameDataFirebase.saveGameModel(gameModel){}
                            break
                        }
                    }
                }
            }

        })

    }
}