package com.example.bash

import android.util.Log
import androidx.core.text.HtmlCompat
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup

class bash {
    fun LastPage(): Int {
        val doc = Jsoup.connect("https://bash.im")
            .userAgent("Chrome/4.0.249.0 Safari/532.5")
            .referrer("http://www.google.com")
            .get()
        return doc.select("input.pager__input").attr("value").toInt()
    }
/*    private fun getData(ar: Int) {
        var err = false
        Log.d("cheked", "$id")

        val o = Observable.create<String> {
            //            getID(200000)
            Log.d("cheked", "последняя страница ${LastPage()}")

            val doc = Jsoup.connect("https://bash.im/quote/$id")
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get()
            Log.d("cheked", "Скачали")
            Log.d("cheked", doc.title())
            post = doc.select("div.quote__body").first().html()
                .replace("&lt;", "<font color=\"#F7F7F7\"><i>")
                .replace("&gt;", ":</i></font>")
                .replace(doc.select("div.quote__strips").outerHtml(), "")
            Log.d("cheked", doc.select("div.quote__strips").outerHtml())
            Log.d("cheked", post)
            val last = doc.select("a.quote__header_permalink").first().text()
                .replace("#", "").toInt()
            if (post.contains("Утверждено") && id > 1 && id < last) {
                err = true
                Log.d("cheked", doc.select("a.quote__header_permalink").first().text().replace("#", ""))
                when (ar) {
                    1 -> {
                        id++
                        getData(1)
                    }
                    0 -> {
                        id--

                        getData(0)
                    }
                }


            }
            when {
                id < 1 -> {
                    id++
                    getData(1)
                }
                id > last -> {
                    id--
                    post = "$last последняя цитата на сегодня"
                    textView.text = HtmlCompat.fromHtml(post.trimMargin(), HtmlCompat.FROM_HTML_MODE_LEGACY)
                }
            }

            if (id < 1 || id > last) err = true

            it.onNext(post)
        }
        o.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            editText.hint = "цитата $id"
            editText.text = null
            if (!err) {
                textView.text = HtmlCompat.fromHtml(post.trimMargin(), HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
            Log.d("cheked", "присвоили")
        }, {})


    }*/
}