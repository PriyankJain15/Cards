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

            var roomId = createGameInFirebase(gameCode,
                pref?.getString("username", "Guest").toString(),
                pref?.getInt("profileImage", R.drawable.profile12) as Int,
                pref.getString("userId", "Guesttt").toString())


            val intent = Intent(activity, teaming::class.java).apply {
                putExtra("GAME_CODE", gameCode)
                putExtra("PLAYER_ID", pref.getString("userId", "Guesttt").toString())
                putExtra("PLAYER_NAME", pref.getString("username", "Guest").toString())
                putExtra("PROFILE_IMAGE_RES_ID", pref.getInt("profileImage", R.drawable.profile12))
                putExtra("RoomID",roomId)
            }
            startActivity(intent)



        }
        return view
    }


    fun generateGameCode(): String {
        return "${(1000..9999).random()}"
    }


    fun createGameInFirebase(gameCode: String, playerName: String, profileImageResId: Int, playerId:String):String{

        // Create a new Player
        val player = GameModelFirebase.Team.Player(
            name = playerName,
            profileImageResId = profileImageResId,
            id = playerId
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
            roomCreatorId = playerId,
            roomId = playerId
        )

        // Save the game to Firebase
        Firebase.firestore.collection("games")
            .document(gameModel.gameCode)
            .set(gameModel)

        return gameModel.roomId

    }
}