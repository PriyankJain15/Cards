package com.example.cards

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class CreateFragment() : Fragment() {
    lateinit var createButton:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_create, container, false)
        createButton = view.findViewById(R.id.createButton)

        createButton.setOnClickListener{
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



        }
        return view
    }


    fun generateGameCode(): String {
        return "${(1000..9999).random()}"
    }


    fun createGameInFirebase(gameCode: String, playerName: String, profileImageResId: Int): String {

        val playerId = (0..9999).random()
         // Assign team ID (1 or 2) based on your logic


        // Create a new Player
        val player = GameModelFirebase.Team.Player(
            name = playerName,
            profileImageResId = profileImageResId,
            id = playerId.toString()
        )

        // Create a new Team and add the player

        val team = GameModelFirebase.Team(
            players = mutableListOf(player)
        )

        // Create a new GameModelFirebase object
        val gameModel = GameModelFirebase(
            gameCode = gameCode,
            status = GameModelFirebase.Status.WAITING,
            teams = mutableListOf(team),
            roomCreatorId = playerId.toString()
        )

        // Save the game to Firebase
        Firebase.firestore.collection("games")
            .document(gameModel.gameCode)
            .set(gameModel)


        return playerId.toString() // Return the player ID for further use
    }



}