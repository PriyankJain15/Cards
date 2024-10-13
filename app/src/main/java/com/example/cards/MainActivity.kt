package com.example.cards

import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.cards.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView


class MainActivity : AppCompatActivity(){

    private lateinit var binding:ActivityMainBinding
    private lateinit var database: DatabaseReference

    lateinit var create_dialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this,object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                moveTaskToBack(true)
            }
        })

        database = FirebaseDatabase.getInstance().reference

        // Assigning data to list
        val list:MutableList<Int> = mutableListOf(R.drawable.profile01,R.drawable.profile02,R.drawable.profile03,
                                                  R.drawable.profile04,R.drawable.profile05,R.drawable.profile06,
                                                  R.drawable.profile07,R.drawable.profile08,R.drawable.profile09,
                                                  R.drawable.profile10,R.drawable.profile11,R.drawable.profile12,
                                                  R.drawable.profile13,R.drawable.profile14,R.drawable.profile15)





        binding.settingIcon.setOnClickListener(View.OnClickListener {
            // Dialog box creation
            val dialog = Dialog(this@MainActivity)
            dialog.setContentView(R.layout.dialog_details)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Apply the scale animation when the dialog is shown
            dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

            dialog.window?.setDimAmount(1f) // Set dim amount

            // sharedpreference creation
            var pref:SharedPreferences = getSharedPreferences("userdata", MODE_PRIVATE)
            var editor = pref.edit()

            // Setting image and name both either default if not set and if set that to show when user opens it
            var profileImage: CircleImageView = dialog.findViewById(R.id.profile_image)
            var playerName:EditText= dialog.findViewById(R.id.player_name)
            profileImage.setImageResource(pref.getInt("profileImage", R.drawable.profile12))

            var st:String = pref.getString("username", "Guest${(100..999).random()}").toString()
            playerName.setText(st)

            //In adapter getting image value to store in run time in circle and update sharedprefernce since
            //profile image variable in this class so just passing method there and donig work here as lambda function
            var recyclerView:RecyclerView = dialog.findViewById(R.id.recycleView)
            var adapter = recyclerAdapter_settingDialog(list, this@MainActivity) { selectedImage ->
                profileImage.setImageResource(selectedImage)
                editor.putInt("profileImage",selectedImage)
            }
            recyclerView.adapter = adapter
            recyclerView.layoutManager =GridLayoutManager(this@MainActivity,4)
            recyclerView.setHasFixedSize(true)


            var btnSave:Button = dialog.findViewById(R.id.save_button)

            btnSave.setOnClickListener(View.OnClickListener {
                if(!playerName.text.toString().equals("")){
                    editor.putString("username",playerName.text.toString())
                    var pn = playerName.text.toString()
                    var rn = (100..999).random().toString()
                    pn = pn.plus(rn)
                    Log.w("Id",pn)
                    editor.putString("userId", pn)
                }else{
                    return@OnClickListener
                }

                editor.apply()
                dialog.dismiss()

            })
            dialog.show()
        })

        binding.banner.setOnClickListener{
            Log.w("banner","entered")
            create_dialog = Dialog(this@MainActivity)
            create_dialog.setContentView(R.layout.dialog_create_join)

            var viewPager: ViewPager2 = create_dialog.findViewById(R.id.viewpager)
            var tab:TabLayout = create_dialog.findViewById(R.id.tab)
            var viewAdapter = ViewPagerAdapter(supportFragmentManager,lifecycle)
            viewPager.adapter = viewAdapter

            TabLayoutMediator(tab,viewPager){
                tabb,position-> when(position){
                    0 -> tabb.text = "CREATE"
                    1 -> tabb.text = "JOIN"
                }
            }.attach()

            create_dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,800)
            create_dialog.window?.setDimAmount(1f)
            create_dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            create_dialog.window?.attributes?.windowAnimations = R.style.DialogBottomAnimation
            create_dialog.window?.setGravity(Gravity.BOTTOM)

            create_dialog.show()

        }
    }

}

