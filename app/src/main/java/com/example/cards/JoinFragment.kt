package com.example.cards

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore



class JoinFragment : Fragment() {
    private lateinit var joinButton: Button
    private lateinit var gameCodeText:EditText
    private lateinit var gamecode:String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        joinButton.setOnClickListener(View.OnClickListener {
            gamecode = gameCodeText.text.toString()
            if(gamecode.isEmpty()) {
                Log.w("gamecode","is empty")
                return@OnClickListener}

//            Firebase.firestore.collection("games").document(gamecode)
//                .addSnapshotListener{ snapshot, error ->
//                    if (error != null) {
//                        Log.w("gamecode", "Listen failed.", error)
//                        return@addSnapshotListener
//                    }
//
//                    if (snapshot != null && snapshot.exists()) {
//                        val gameModel = snapshot.toObject(GameModelFirebase::class.java)
//                        // Update the UI based on the game model
//                        GameDataFirebase._gameModel.postValue(gameModel)
//                    } else {
//                        Log.w("gamecode", "Current data: null")
//                    }
//                }

            Firebase.firestore.collection("games").document(gamecode).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Game exists, now add the player to a team
                        addPlayerToGame(gamecode)
                    }else {
                        Log.w("gamecode","not exist")
                    }
                }.addOnFailureListener { exception ->
                    Log.w("gamecode","failure")
                }


        })

    }

    // Set up real-time updates listener
@SuppressLint("SuspiciousIndentation")
private fun addPlayerToGame(gamecode: String) {

        var pref: SharedPreferences? = activity?.getSharedPreferences("userdata", AppCompatActivity.MODE_PRIVATE)

        val playerId = (0..9999).random().toString()
        val playerName =  pref?.getString("username", "Guest").toString() // Example: Replace with actual player name
        val profileImageResId = pref?.getInt("profileImage", R.drawable.profile12) as Int // Example: Replace with actual profile image resource ID

        Firebase.firestore.collection("games").document(gamecode).get().addOnSuccessListener {

            Log.w("gamecode","inside")
            var gameModel = it?.toObject(GameModelFirebase::class.java)
                gameModel.let{

                    if (it?.teams?.size !!< 2) {
                        // Initialize missing teams if necessary
                        while (it.teams.size < 2) {
                            it.teams = (it.teams + GameModelFirebase.Team()).toMutableList()
                        }
                    }

                    val teamToJoin = when {
                        it.teams[0].players.size < 2 -> 0
                        it.teams[1].players.size < 2 -> 1
                        else -> -1 // Both teams are full
                    }

                    Log.w("gamecode",teamToJoin.toString())

                if(teamToJoin!=-1) {
                    val newPlayer = GameModelFirebase.Team.Player(
                        name = playerName,
                        profileImageResId = profileImageResId,
                        id = playerId,
                        teamId = null
                    )
                    Log.w("gamecode", "new player assign")
                    Log.w("gamecode", it.teams[teamToJoin].players.toString())

                    it.teams[teamToJoin].players = (it.teams[teamToJoin].players + newPlayer).toMutableList()
                    Log.w("gamecode", it.teams[teamToJoin].players.toString())
                    // Update the game in Firestore
                    Firebase.firestore.collection("games").document(gamecode)
                        .set(it)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Navigate to TeamingActivity
                                navigateToTeamingActivity(
                                    gamecode,
                                    playerId,
                                    playerName,
                                    profileImageResId
                                )
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Failed to join game: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }else{
                    Log.w("gamecode","full")
                }
                }
        }
    }
    private fun navigateToTeamingActivity(gameCode: String, playerId: String, playerName: String, profileImageResId: Int) {
        val intent = Intent(activity, teaming::class.java).apply {
            putExtra("GAME_CODE", gameCode)
            putExtra("PLAYER_ID", playerId)
            putExtra("PLAYER_NAME", playerName)
            putExtra("PROFILE_IMAGE_RES_ID", profileImageResId)
        }
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        var view:View=inflater.inflate(R.layout.fragment_join, container, false)

        joinButton = view.findViewById(R.id.joinButton)
        gameCodeText = view.findViewById(R.id.codeText)

        return view
    }


}