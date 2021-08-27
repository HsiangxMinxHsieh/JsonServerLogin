package com.timmy.randomgetcard.shamee.step1

import java.util.*
import kotlin.math.pow

//傳入一個隨機數字陣列，回傳1個與N個抽卡結果
fun main(vararg string: String) {
    val randomArray = arrayListOf(
        RandomType("SSR", 0.005),
        RandomType("SR", 0.03),
        RandomType("s", 0.07),
        RandomType("R", 0.295),
        RandomType("N", 0.6),
    )

    val cardList = getCardList(randomArray)

    println("準備好的卡牌庫的大小是=>${cardList.size}")

//  抽一張卡
    val getCard = cardList.getACard()
    println("抽一張卡的結果是=>${getCard.type}")

//  正式版循環抽卡環節：
    val times = 1000000

    var getCardsResult = mutableMapOf<String, Int>()
    calculateExecutionTimeFun("抽卡準備完成，抽卡中，請稍待。", "抽卡${times}次完成") {
        getCardsResult = cardList.getCards(times).toMutableMap()
    }

    println("結果是=>${getCardsResult}")
}

//統計各類抽出結果
private fun TreeMap<String, Int>.statisticsResult(): TreeMap<String, Int> {
    val result = TreeMap<String, Int>()
    this.forEach {
        val key = it.key.split("_")[0]
        result[key] = (result[key] ?: 0) + it.value
    }
    return result
}

fun calculateExecutionTimeFun(startTag: String, endTag: String, needCalculateFun: () -> Unit) {
    println(startTag)
    val nowTime = Date().time
    needCalculateFun.invoke()
    println("$endTag，共花費${Date().time - nowTime}毫秒。")
}

fun List<Card>.getACard() = this[(this.indices).random()]

fun List<Card>.getCards(times: Int): Map<String, Int> {
    val getCardsResult = TreeMap<String, Int>()

    (0 until times).forEach { _ ->
        val getCard = this.getACard() //這次抽到的卡
        getCardsResult[getCard.type] = (getCardsResult[getCard.type] ?: 0) + 1
    }
    return getCardsResult
}

//依照Double陣列塞進List回傳一個牌堆 // 依照每個角色的機率不同，來抽卡。
fun getCardList(randomArray: List<RandomType>): List<Card> {
    val result = arrayListOf<Card>()
    val numTimes = getNumTimes(randomArray)
    randomArray.forEach {
        val thisDigit = (it.random * numTimes).toInt()
        (0 until thisDigit).forEach{ _ ->
            result.add(Card(type = it.type))
        }
    }
    return result
}

//依照傳入的小數列表，取得最長的，轉換為100、1000、10000...
fun getNumTimes(random: List<RandomType>): Int {
    val num = random.maxByOrNull { it.random.toDigitLength() }?.random?.toDigitLength()
    return 10.0.pow((num?.toDouble()) ?: 1.0).toInt()  //乘以小數位數(100、1000、10000)
}

fun Double.toDigitLength() = (
        if (!this.toString().contains("E-")) //超過五位數會變成5.0E-5的形式
            this.toString().split(".").getOrNull(1)?.length
        else
            (this.toString().split("E-").getOrNull(1)?.toInt())?.plus(this.toString().indexOf("E") - this.toString().indexOf(".") - 1 - if (this.toString().contains("0E")) 1 else 0)) ?: 0

class RandomType(
    val type: String = "",
    val random: Double = 0.0,
)

data class Card(
    val type: String = "",
    val name: String = ""
)