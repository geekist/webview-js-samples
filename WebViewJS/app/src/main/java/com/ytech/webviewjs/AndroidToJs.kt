package com.ytech.webviewjs

import android.webkit.JavascriptInterface

class AndroidToJs  {
    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    fun hello(msg: String?) {
        println(msg)
    }
}