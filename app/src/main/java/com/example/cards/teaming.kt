package com.example.cards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.example.cards.databinding.ActivityTeamingBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class teaming : AppCompatActivity() {

    private lateinit var binding: ActivityTeamingBinding
    lateinit var gameCode:String
    private var firstPlayer:GameModelFirebase.Team.Player? = null
    private var secondPlayer:GameModelFirebase.Team.Player? = null
    private var swapPlayerList:MutableList<GameModelFirebase.Team.Player> = mutableListOf()
    private var swapMode = false

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

        binding.codeText.text = "CODE-${gameCode}"

        GameDataFirebase.gamemodel.observe(this, Observer { value ->

            if (value == null) {
                Log.w("GameModel", "GameModel is null")
                return@Observer
            }

            checkCreatorId(value)
//            binding.swapButton.visibility = View.VISIBLE

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
                    binding.profileImageT4.setImageResource(R.drawable.backgrund2)
                    binding.playerNameT4.text = ""
                }

                // Check if team 2 has at least two players
                if (team2.players.size > 1) {
                        binding.profileImageT4.setImageResource(value.teams[1].players[1].profileImageResId)
                        binding.playerNameT4.text = value.teams[1].players[1].name
                }
            }
        })

        binding.swapIcon.setOnClickListener(View.OnClickListener {
            if(swapPlayerList.size == 2) {
                firstPlayer = swapPlayerList[0]
                secondPlayer = swapPlayerList[1]

                var team1Index = -1
                var team2Index = -1
                var player1Index = -1
                var player2Index = -1

                var gameModel = GameDataFirebase.gamemodel.value ?: return@OnClickListener

                    for (teamIndex in gameModel.teams.indices) {
                        val team = gameModel.teams[teamIndex]
                        val playerIndex1 = team.players.indexOfFirst { it.id == firstPlayer!!.id }
                        if (playerIndex1 != -1) {
                            team1Index = teamIndex
                            player1Index = playerIndex1
                            break
                        }
                    }
                    for (teamIndex in gameModel.teams.indices) {
                        val team = gameModel.teams[teamIndex]
                        val playerIndex2 = team.players.indexOfFirst { it.id == secondPlayer!!.id }
                        if (playerIndex2 != -1) {
                            team2Index = teamIndex
                            player2Index = playerIndex2
                            break
                        }
                    }

                    if (team1Index != -1 && team2Index != -1 && player1Index != -1 && player2Index != -1) {
                        var temp = gameModel.teams[team1Index].players[player1Index]
                        gameModel.teams[team1Index].players[player1Index] =
                            gameModel.teams[team2Index].players[player2Index]
                        gameModel.teams[team2Index].players[player2Index] = temp
                    }
                    GameDataFirebase.saveGameModel(gameModel) {
                        swapPlayerList.clear()
                        dimmingView(1f)
                        binding.swapButton.text = "SWAP"
                        binding.versus.visibility = View.VISIBLE
                        binding.swapIcon.visibility = View.INVISIBLE
                        swapMode = false
                    }
            }
        })

        binding.startButton.setOnClickListener{ startActivity(Intent(this@teaming, MainActivity::class.java)) }
        binding.backButton.setOnClickListener{

            val currentPlayerId = intent.getStringExtra("PLAYER_ID") ?: ""

            startActivity(Intent(this@teaming, MainActivity::class.java))

            GameDataFirebase.gamemodel.value?.let {gameModel ->
                if (gameModel.teams.isNotEmpty() && gameModel.teams[0].players.isNotEmpty() &&
                    gameModel.roomCreatorId == currentPlayerId) {

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

        }

        swapPlayerList.clear()
        binding.swapButton.setOnClickListener {

            if (!swapMode) {
                dimmingView(0.5f)
                binding.swapButton.text = "CANCEL"
                swapMode = true

                if (swapPlayerList.size < 2) {
                    toggleSelectionView(
                        binding.profileImageT1,
                        binding.playerNameT1,
                        0,
                        0,
                        swapMode
                    )
                    toggleSelectionView(
                        binding.profileImageT2,
                        binding.playerNameT2,
                        0,
                        1,
                        swapMode
                    )
                    toggleSelectionView(
                        binding.profileImageT3,
                        binding.playerNameT3,
                        1,
                        0,
                        swapMode
                    )
                    toggleSelectionView(
                        binding.profileImageT4,
                        binding.playerNameT4,
                        1,
                        1,
                        swapMode
                    )
                }

            } else {
                swapPlayerList.clear()
                dimmingView(1f)
                binding.swapButton.text = "SWAP"
                binding.versus.visibility = View.VISIBLE
                binding.swapIcon.visibility = View.INVISIBLE
                swapMode = false

                toggleSelectionView(binding.profileImageT1, binding.playerNameT1, 0, 0, swapMode)
                toggleSelectionView(binding.profileImageT2, binding.playerNameT2, 0, 1, swapMode)
                toggleSelectionView(binding.profileImageT3, binding.playerNameT3, 1, 0, swapMode)
                toggleSelectionView(binding.profileImageT4, binding.playerNameT4, 1, 1, swapMode)
            }
        }

    }

    private fun checkCreatorId(gameModelFirebase: GameModelFirebase){
        var currentPlayerId = intent.getStringExtra("PLAYER_ID") ?: ""
        if(gameModelFirebase.roomCreatorId == currentPlayerId){
            binding.swapButton.visibility = View.VISIBLE
        }
    }

    private fun dimmingView(opacity:Float){
        binding.apply {
            background.alpha = opacity
            backButton.alpha = opacity
            codeText.alpha = opacity
            versus.alpha = opacity
            startButton.alpha = opacity
            profileImageT1.alpha = opacity;playerNameT1.alpha = opacity
            profileImageT2.alpha = opacity;playerNameT2.alpha = opacity
            profileImageT3.alpha = opacity;playerNameT3.alpha = opacity
            profileImageT4.alpha = opacity;playerNameT4.alpha = opacity
        }
    }
    private fun toggleSelectionView(profile:CircleImageView, name:TextView, teamPos:Int, playerPos:Int,swapMode:Boolean){
        if(swapMode) {
            profile.setOnClickListener {
                handleSelection(profile, name, teamPos, playerPos)
            }
            name.setOnClickListener {
                handleSelection(profile, name, teamPos, playerPos)
            }
        }else{
            profile.setOnClickListener(null)
            name.setOnClickListener(null)
        }
    }

    private fun handleSelection(profile: CircleImageView, name: TextView, teamPos: Int, playerPos: Int) {

        if (name.text.toString().isEmpty()) {
            return
        }
        if(swapPlayerList.size < 2 && profile.alpha == 0.5f) {
            GameDataFirebase.gamemodel.observe(this) {
                if (name.text != "") {
                    swapPlayerList.add(it.teams[teamPos].players[playerPos])
                    profile.alpha = 1f
                    name.alpha = 1f

                    if (swapPlayerList.size == 2) {
                        binding.versus.visibility = View.INVISIBLE
                        binding.swapIcon.visibility = View.VISIBLE
                        binding.swapIcon.alpha = 1f
                        binding.swapIcon.bringToFront()
                    }
                }
            }
        }else{
            GameDataFirebase.gamemodel.observe(this){
                val player = it.teams[teamPos].players[playerPos]
                swapPlayerList.remove(player)
                profile.alpha = 0.5f
                name.alpha = 0.5f

                if (swapPlayerList.size < 2) {
                    binding.versus.visibility = View.VISIBLE
                    binding.swapIcon.visibility = View.INVISIBLE
                    binding.versus.alpha = 0.5f
                }

            }
        }
    }
}