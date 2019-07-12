package com.happypossum.bash

import android.content.Intent


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem

import android.widget.Toast

import com.squareup.picasso.Picasso

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_strip.*
import org.jsoup.Jsoup

var nextUrl: String = ""
var actUrl: String = ""
var prevUrl: String = ""


class StripActivity : AppCompatActivity() {

    private fun getStrip(url: String) {
        actUrl = url


        val o = Observable.create<String> {
            val doc = Jsoup.connect("https://bash.im$url")
                .userAgent("    Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get()
            Log.d("cheked", "$url скачали")
            Log.d("cheked", doc.title())
            post.body = doc.select("img.quote__img").attr("data-src").toString()
            Log.d("cheked", post.body)
            post.striplink = doc.select("div.quote__author").select("a").last().attr("href").toString()
            Log.d("cheked", post.striplink)
            /*   val doc1 = Jsoup.connect("https://bash.im${post.striplink}")
                   .userAgent("    Chrome/4.0.249.0 Safari/532.5")
                   .referrer("http://www.google.com")
                   .get()
               Log.d("cheked", "скачали")
               post.striplink=doc1.select("div.quote__body").first().html()
                   .replace("&lt;", "<font color=\"#F7F7F7\"><i>")
                   .replace("&gt;", ":</i></font>")
                   .replace(doc1.select("div.quote__strips").outerHtml(), "")
               Log.d("cheked", post.striplink)*/

            Log.d("cheked", "скачали")


            Log.d("cheked", post.body)
            nextUrl = doc.select("a.pager__item")[1].attr("href").toString()
            Log.d("cheked", "next: $nextUrl")
            if (doc.select("a.pager__item").size > 2) {
                prevUrl = doc.select("a.pager__item")[2].attr("href").toString()
            }

            Log.d("cheked", "prev: $prevUrl")



            it.onNext(post.body)
        }
        o.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Picasso.get().load("https://bash.im${post.body}").into(image)
//            text1.text ="Комикс по цитате №${post.striplink.replace("/quote/", "")}"

            Log.d("cheked", "https://bash.im${post.body}")

        }, {})


    }

    fun onClickShare(item: MenuItem) {
        Log.d("cheked", "поделится $post")

        var sendIntent = Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://bash.im${post.body}");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_strip, menu)
        return true

    }

    fun onClickBash(item: MenuItem) {
        Log.d("cheked", "пошли в цитаты")
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_strip)
        actUrl = "${getSharedPreferences("url", 0).getString("url", "/strip/20070801")}"

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {

            KeyEvent.KEYCODE_VOLUME_UP -> {

                getStrip(nextUrl)
                Toast.makeText(this, "Следуюущий комикс", Toast.LENGTH_SHORT)
                    .show()
                return true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {

                getStrip(prevUrl)
                Toast.makeText(this, "Предыдущий комикс ", Toast.LENGTH_SHORT)
                    .show()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {

        super.onResume()
        getStrip(actUrl)
        nextStrip.setOnClickListener {
            getStrip(nextUrl)
        }
        prevStrip.setOnClickListener {
            getStrip(prevUrl)
        }
    }

    override fun onPause() {
        super.onPause()
        getSharedPreferences("url", 0).edit().putString("url", actUrl).apply()
        Log.d("cheked", "пауза")
    }
}
