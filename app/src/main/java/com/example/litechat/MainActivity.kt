package com.example.litechat

import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.litechat.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    lateinit var bindihg: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var adapter: UserAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindihg = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindihg.root)
        auth = Firebase.auth
        val database = Firebase.database
        val myRef = database.getReference("message")
        bindihg.tbtnSend.setOnClickListener {
            myRef.child(myRef.push().key ?: "add text").setValue(
                User(
                    auth.currentUser?.displayName,
                    bindihg.tinpetInputText.text.toString()
                )
            )
            bindihg.tinpetInputText.setText("")
        }
        onGiveDataFromDbListener(myRef)
        setActionBar()
        initRcView()
    }


    private fun onGiveDataFromDbListener(dbRef: DatabaseReference) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<User>()
                for (el in snapshot.children) {
                    val user = el.getValue(User::class.java)
                    if (user != null) list.add(user)
                }
                adapter.submitList(list)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun initRcView() = with(bindihg) {
        adapter = UserAdapter()
        rcView.layoutManager = LinearLayoutManager(this@MainActivity)
        rcView.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            auth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setActionBar() {
        Thread {
            val bitMap = Picasso.get().load(auth.currentUser?.photoUrl).get()
            val drawbleIcon = BitmapDrawable(resources, bitMap)
            runOnUiThread {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(drawbleIcon)
                supportActionBar?.title = auth.currentUser?.displayName
            }
        }.start()
    }

}