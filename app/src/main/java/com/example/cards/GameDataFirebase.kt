package com.example.cards

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

object GameDataFirebase {
    var _gameModel:MutableLiveData<GameModelFirebase> = MutableLiveData()
    var gamemodel:LiveData<GameModelFirebase> = _gameModel

    fun saveGameModel( model: GameModelFirebase, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        _gameModel.postValue(model)

        Firebase.firestore.collection("games").document(model.gameCode)
            .set(model)
            .addOnSuccessListener {
                Log.d("GameSave", "GameModel saved successfully")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("GameSave", "Error saving GameModel", exception)
                onFailure(exception)
            }
    }


    public fun fetchGameModel() {
        Log.d("GameFetch", "inside fetchmodel ")
        gamemodel.value?.apply {
            Log.d("GameFetch", "GameModel fetched: $gameCode")
             Firebase.firestore.collection("games")
                 .document(gameCode)
                 .addSnapshotListener() {value,error->
                     Log.d("GameFetch", "checking model")
                     val model = value?.toObject(GameModelFirebase::class.java)
                     Log.d("GameFetch", "GameModel fetched: $model")
                     _gameModel.postValue(model)

                 }
        }
    }

}
