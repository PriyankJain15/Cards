package com.example.cards

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore


class CreateFragment() : Fragment() {
    lateinit var createButton:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_create, container, false)
        createButton = view.findViewById(R.id.createButton)

        createButton.setOnClickListener(View.OnClickListener {
            val activity = activity
            val gameCode = generateGameCode()
            var pref: SharedPreferences? = activity?.getSharedPreferences("userdata", AppCompatActivity.MODE_PRIVATE)

            var playerId = createGameInFirebase(gameCode,
                pref?.getString("username", "Guest").toString(),
                pref?.getInt("profileImage", R.drawable.profile12) as Int)

            

            val intent = Intent(activity, teaming::class.java).apply {
                putExtra("GAME_CODE", gameCode)
                putExtra("PLAYER_ID", playerId)
                putExtra("PLAYER_NAME", pref.getString("username", "Guest").toString(),)
                putExtra("PROFILE_IMAGE_RES_ID", pref.getInt("profileImage", R.drawable.profile12))
            }
            startActivity(intent)


        })
        return view
    }


    fun generateGameCode(): String {
        return "${(1000..9999).random()}"
    }


    fun createGameInFirebase(gameCode: String, playerName: String, profileImageResId: Int): String {

        val playerId = (0..999).random()
         // Assign team ID (1 or 2) based on your logic

        // Create a new Player
        val player = GameModelFirebase.Team.Player(
            name = playerName,
            profileImageResId = profileImageResId,
            id = playerId.toString(),
            teamId = null
        )

        // Create a new Team and add the player
        val team = GameModelFirebase.Team(
            players = listOf(player),
            score = 0,
            oppscore = 0,
            highestBid = 0,
            totalTricksWon = 0,
            teamId = null
        )

        // Create a new GameModelFirebase object
        val gameModel = GameModelFirebase(
            gameCode = gameCode,
            status = GameModelFirebase.Status.WAITING,
            teams = listOf(team),
            currentTrick = null,
            highestBidSuit = null
        )

        // Save the game to Firebase
        Firebase.firestore.collection("games")
            .document(gameModel.gameCode)
            .set(gameModel)


        return playerId.toString() // Return the player ID for further use
    }



}