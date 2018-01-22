package com.calculator.eka.calculator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    var str = ""
    var isCalculated: Boolean? = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        num0.setOnClickListener(this)
        num1.setOnClickListener(this)
        num2.setOnClickListener(this)
        num3.setOnClickListener(this)
        num4.setOnClickListener(this)
        num5.setOnClickListener(this)
        num6.setOnClickListener(this)
        num7.setOnClickListener(this)
        num8.setOnClickListener(this)
        num9.setOnClickListener(this)
        multiply.setOnClickListener(this)
        result.setOnClickListener(this)
        equal.setOnClickListener(this)
        division.setOnClickListener(this)
        minus.setOnClickListener(this)
        plus.setOnClickListener(this)
        dot.setOnClickListener(this)
        delete.setOnClickListener {
            if (str.isNotEmpty()) {
                str = str.substring(0, str.length - 1)
                calculator.text = str
            }
        }
    }

    override fun onClick(v: View) {
        val textView = v as TextView
        //산술기호 일 때
        check@ when (textView.text[0]) {
            '+', '-', '*', '/', '.' -> {
                if (textView.text[0] == '.') {
                    val a = str
                    val operands = a.split("[+\\-*/]".toRegex())
                    if (operands[operands.lastIndex].matches("[0-9]+\\.[0-9]*".toRegex()))
                        return@check
                }
                if (str[str.length - 1] != '+'
                        && str[str.length - 1] != '-'
                        && str[str.length - 1] != '*'
                        && str[str.length - 1] != '/'
                        && str[str.length - 1] != '.')
                    str += textView.text
                isCalculated = false
            }
        // 계산 꼬우
            '=' -> {
                isCalculated = true

                str = (Math.round(getResult(makePostfix(str)) * 10000.0) / 10000.0).toString() + ""
            }
            else -> {
                if (isCalculated!!) {
                    str = ""
                    isCalculated = false
                }
                str += textView.text
            }
        }
        calculator.text = str
    }

    private fun makePostfix(a: String): ArrayList<Any> {
        val result = ArrayList<Any>()
        val exp = ArrayList<Char>()
        val operands = ArrayList<Double>().apply {
            a.split("[+\\-*/]".toRegex()).forEach {
                add(it.toDouble())
            }
        }
        val operators = ArrayList<Char>().apply {
            a.split("[0-9]+(\\.[0-9]+)?".toRegex()).forEachIndexed { i, it ->
                if (i != a.split("[0-9]+(\\.[0-9]+)?".toRegex()).lastIndex && i != 0)
                    add(it[0])
            }
        }
        operands.forEachIndexed { i, it ->
            result.add(it)
            if (i != operands.size - 1)
                if (exp.isEmpty())
                    exp.add(operators[i])
                else {
                    val op: Char = operators[i]
                    when (op) {
                        '-', '+' -> {
                            if (exp.last() == '-' || exp.last() == '+')
                                result.add(op)
                            else {
                                result.add(exp.last())
                                exp.removeAt(exp.lastIndex)
                                exp.add(op)
                            }
                        }
                        '/', '*' -> {
                            if (exp.last() == '/' || exp.last() == '*') {
                                result.add(exp.last())
                                exp.removeAt(exp.lastIndex)
                                exp.add(op)
                            } else {
                                exp.add(op)
                            }
                        }
                    }
                }
        }
        exp.reversed().forEach { result.add(it) }
        return result
    }

    private fun getResult(postfix: ArrayList<Any>): Double {
        val operands = ArrayList<Double>()
        postfix.forEach {
            when (it) {
                is Double -> operands.add(it)
                is Char -> {
                    val n1 = operands[operands.lastIndex]
                    operands.removeAt(operands.lastIndex)
                    val n2 = operands[operands.lastIndex]
                    operands.removeAt(operands.lastIndex)
                    operands.add(when (it) {
                        '-' -> n2 - n1
                        '+' -> n2 + n1
                        '*' -> n2 * n1
                        '/' -> n2 / n1
                        else -> 0.0
                    })
                }
            }
        }

        return operands[0]
    }
}
