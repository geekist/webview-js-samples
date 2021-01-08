package com.ytech.webviewjs

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val webSettings = webView.settings

        webSettings.javaScriptEnabled = true// 设置与Js交互的权限
        webSettings.javaScriptCanOpenWindowsAutomatically = true// 设置允许JS弹窗

        // 通过addJavascriptInterface()将Java对象映射到JS对象
        //参数1：Javascript对象名
        //参数2：Java对象名
        // 通过addJavascriptInterface()将Java对象映射到JS对象
        //参数1：Javascript对象名
        //参数2：Java对象名
        webView.addJavascriptInterface(AndroidToJs(), "test") //AndroidtoJS类对象映射到js的test对象


        // 先载入JS代码
        // 格式规定为:file:///android_asset/文件名.html
        // 服务器文件可以直接将url写入
        webView.loadUrl("file:///android_asset/javascript.html")

        button_loadUrl.setOnClickListener {
            webView.post { // 通过Handler发送消息
                // 注意调用的JS方法名要对应上
                webView!!.loadUrl("javascript:callJS()")// 调用javascript的callJS()方法
            }
        }

        button_evaluateJS.setOnClickListener {
            webView.evaluateJavascript(
                "javascript:callJS()"
            ) { str -> //此处为 js 返回的结果
                Log.d("", str)
                Toast.makeText(this@MainActivity, str, Toast.LENGTH_LONG).show()
            }
        }

        // 由于设置了弹窗检验调用结果,所以需要支持js对话框
        // WebView只是载体，内容的渲染需要使用WebViewChromClient类去实现
        // 通过设置WebChromeClient对象处理JavaScript的对话框
        //设置响应js 的Alert()函数
        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(
                view: WebView,
                url: String,
                message: String,
                result: JsResult
            ): Boolean {
                val b = AlertDialog.Builder(this@MainActivity)
                b.setTitle("Alert")
                b.setMessage(message)
                b.setPositiveButton(android.R.string.ok) { dialog, which -> result.confirm() }
                b.setCancelable(false)
                b.create().show()
                return true
            }

            override fun onJsPrompt(
                view: WebView?,
                url: String?,
                message: String?,
                defaultValue: String?,
                result: JsPromptResult
            ): Boolean {
                // 根据协议的参数，判断是否是所需要的url(原理同方式2)
                // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
                //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）
                val uri = Uri.parse(message)
                // 如果url的协议 = 预先约定的 js 协议
                // 就解析往下解析参数
                if (uri.scheme == "js") {

                    // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    if (uri.authority == "webview") {

                        //
                        // 执行JS所需要调用的逻辑
                        println("js调用了Android的方法")
                        // 可以在协议上带有参数并传递到Android上
                        val params = HashMap<String, String>()
                        val collection = uri.queryParameterNames

                        //参数result:代表消息框的返回值(输入值)
                        result.confirm("js调用了Android的方法成功啦")
                    }
                    return true
                }
                return super.onJsPrompt(view, url, message, defaultValue, result)
            }

        }

        webView.webViewClient = object : WebViewClient() {
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