package com.example.cards

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object GameDataFirebase {
    private var _gamemodel:MutableLiveData<GameModelFirebase> = MutableLiveData()
    internal var gamemodel:MutableLiveData<GameModelFirebase>
        get() {return _gamemodel}
        set(value) {_gamemodel = value }

    fun saveGameModel( model: GameModelFirebase, onSuccess: () -> Unit) {
        Firebase.firestore.collection("games").document(model.gameCode)
            .set(model)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("GameSave", "Error saving GameModel", exception)
                return@addOnFailureListener
            }
    }
    fun fetchGameModel(gameCode:String,OnDocumentDeleted:()->Unit) {
        Firebase.firestore.collection("games").document(gameCode).addSnapshotListener { value, error ->
            if(error != null){
                Log.w("Listen Failed",error)
                return@addSnapshotListener
            }
            if(value != null && value.exists()){
                val gamemodel = value.toObject(GameModelFirebase::class.java)
                _gamemodel.postValue(gamemodel)
            }else{
                OnDocumentDeleted()
            }
        }
    }

}
