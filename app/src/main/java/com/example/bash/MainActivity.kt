package com.happypossum.bash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View

import androidx.core.text.HtmlCompat
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup

import android.widget.Toast




var firstID1 = 0
var lastID1 = 0
var page1 = 0
var post = Quote("", 0)
var id = 1
var zap = 0
var page = 1
var idQ = 0

class Quote(body: String, id: Int) {
    var id: Int = id
    var body: String = body
    var striplink = ""

}

@Suppress("UNREACHABLE_CODE")
class MainActivity : AppCompatActivity() {
    fun lastPage(): Int {
        val doc = Jsoup.connect("https://bash.im")
            .userAgent("Chrome/4.0.249.0 Safari/532.5")
            .referrer("http://www.google.com")
            .get()
        return doc.select("input.pager__input").attr("value").toInt()
    }


    @SuppressLint("CheckResult")
    fun getData(page1: Int, id1: Int) {
        page = page1
        id = id1

        Log.d("cheked", "id $id page $page")

        val o = Observable.create<String> {
            when {
                page < 1 -> page = 1
                page > lastPage() -> page = lastPage()
                else ->         Log.d("cheked", "все хорошо")
            }
            var doc = Jsoup.connect("https://bash.im/index/$page")
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get()
            Log.d("cheked", "Скачали")
            when {
                id < 0 -> {
                    page++
                    doc = Jsoup.connect("https://bash.im/index/$page")
                        .userAgent("Chrome/4.0.249.0 Safari/532.5")
                        .referrer("http://www.google.com")
                        .get()
                    id = doc.select("article.quote").size - 1
                    Log.d("cheked", "страница $page цитата $id")
                }
                id >= doc.select("article.quote").size -> {
                    page--
                    doc = Jsoup.connect("https://bash.im/index/$page")
                        .userAgent("Chrome/4.0.249.0 Safari/532.5")
                        .referrer("http://www.google.com")
                        .get()
                    id = 0
                }

            }
            post.body = doc.select("article.quote")[id].select("div.quote__body").html()
                .replace("&lt;", "<font color=\"#F7F7F7\"><i>")
                .replace("&gt;", ":</i></font>")
                .replace(doc.select("div.quote__strips").outerHtml(), "")
            Log.d("cheked", post.body)
            post.id = doc.select("article.quote")[id].attr("data-quote").toInt()






            it.onNext(post.body)
        }
        o.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            editText.hint = "страница $page"
            editText.text = null

            textView.text = HtmlCompat.fromHtml("<font color=\"#F7F7F7\"> Цитата №${post.id} </font> <br>${post.body}".trimMargin(), HtmlCompat.FROM_HTML_MODE_LEGACY)

            Log.d("cheked", "присвоили")
        }, {})


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return true

    }


    fun onClickStrip(item: MenuItem) {
        Log.d("cheked", "пошли в комиксы")
        val i = Intent(this, StripActivity::class.java)
        startActivity(i)

    }

    fun onClickShare(item: MenuItem) {
        Log.d("cheked", "поделится $post")

        var sendIntent =  Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textView.text);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("cheked", "setContentView(R.layout.activity_main)")

    }



    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {

            KeyEvent.KEYCODE_VOLUME_UP -> {
                id--
                getData(page, id)
                Toast.makeText(this, "Следуюущая цитата", Toast.LENGTH_SHORT)
                    .show()
                return true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                id++
                getData(page, id)
                Toast.makeText(this, "Предыдущая цитата ", Toast.LENGTH_SHORT)
                    .show()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }



    override fun onResume() {
        super.onResume()
        id = getSharedPreferences("id", 0).getInt("id", 1)
        page = getSharedPreferences("page", 0).getInt("page", 1)
        getData(page, id)

        next.setOnClickListener {
            id--
            getData(page, id)
        }
        prev.setOnClickListener {
            id++
            getData(page, id)
        }

        editText.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                page = editText.text.toString().toInt()

                id = 0

                getData(page, id)
                return@OnKeyListener true
            }
            false
        }
        )

    }

    override fun onPause() {
        super.onPause()
        getSharedPreferences("id", 0).edit().putInt("id", id).apply()
        getSharedPreferences("page", 0).edit().putInt("page", page).apply()
        Log.d("cheked", "пауза")
    }
}
