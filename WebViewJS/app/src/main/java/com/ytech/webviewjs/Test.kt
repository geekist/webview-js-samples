package com.ytech.webviewjs

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class Test : AppCompatActivity() {
    var mWebView: WebView? = null

    //    Button button;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mWebView = findViewById<View>(R.id.webView) as WebView
        val webSettings = mWebView!!.settings

        // 设置与Js交互的权限
        webSettings.javaScriptEnabled = true
        // 设置允许JS弹窗
        webSettings.javaScriptCanOpenWindowsAutomatically = true

        // 步骤1：加载JS代码
        // 格式规定为:file:///android_asset/文件名.html
        mWebView!!.loadUrl("file:///android_asset/javascript.html")


// 复写WebViewClient类的shouldOverrideUrlLoading方法
        mWebView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

                // 步骤2：根据协议的参数，判断是否是所需要的url
                // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
                //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）
                val uri = Uri.parse(url)
                // 如果url的协议 = 预先约定的 js 协议
                // 就解析往下解析参数
                if (uri.scheme == "js") {

                    // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    if (uri.authority == "webview") {

                        //  步骤3：
                        // 执行JS所需要调用的逻辑
                        println("js调用了Android的方法")
                        // 可以在协议上带有参数并传递到Android上
                        val params = HashMap<String, String>()
                        val collection = uri.queryParameterNames
                    }
                    return true
                }
                return super.shouldOverrideUrlLoading(view, url)
            }
        }
    }
}