package com.timmy.randomgetcard.kang.step2

import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow

//傳入一個隨機數字陣列與角色列表、機率，回傳1個與N個抽卡結果 //康Sir想法：先抽出等級再抽角色
fun main(vararg string: String) {

    val randomArray = arrayListOf(
        RandomType("SSR", 0.005).apply {
            role.addAll(
                arrayListOf(
                    Role(this.type, 1, "SSR_AAA"),
                    Role(this.type, 6, "SSR_BBB"),
                    Role(this.type, 1, "SSR_CCC")
                )
            )
        },
        RandomType("SR", 0.03).apply {
            role.addAll(
                arrayListOf(
                    Role(this.type, 1, "SR_DDD"),
                    Role(this.type, 1, "SR_EEE"),
                    Role(this.type, 3, "SR_FFF"),
                    Role(this.type, 1, "SR_GGG"),
                )
            )
        },
        RandomType("S", 0.07).apply {
            role.addAll(
                arrayListOf(
                    Role(this.type, 1, "S_HHH"),
                    Role(this.type, 1, "S_III"),
                    Role(this.type, 3, "S_JJJ")
                )
            )
        },
        RandomType("R", 0.295).apply {
            role.addAll(
                arrayListOf(
                    Role(this.type, 2, "R_KKK"),
                    Role(this.type, 1, "R_LLL"),
                )
            )
        },
        RandomType("N", 0.6).apply {
            role.addAll(
                arrayListOf(
                    Role(this.type, 1, "N_MMM"),
                )
            )
        },
    )

    val cardList: ArrayList<Card> = arrayListOf()
    var roleMap: TreeMap<String, ArrayList<Role>> = TreeMap<String, ArrayList<Role>>()
    calculateExecutionTimeFun("開始備卡，請稍待。", "備卡完成") {
        cardList.addAll(getCardList(randomArray))
        roleMap = getRoleMap(randomArray)
    }

    println("準備好的卡牌庫的大小是=>${cardList.size}")
//    println("作好的RoleMap是=>${roleMap}")

//  抽一張卡
    val getCard = cardList.getACard(roleMap)
    println("抽一張卡的結果是=>${getCard}")

//  正式版循環抽卡環節：
    val times = 100000000

    var getCardsResult = TreeMap<String, Int>()
    calculateExecutionTimeFun("康Sir抽卡準備完成，抽卡中，請稍待。", "抽卡${times}次完成") {
        getCardsResult = cardList.getCards(times, roleMap)
    }

    println("最終抽卡結果是=>${getCardsResult}")
    println("各Type抽出比例是=>${getCardsResult.statisticsResult()}")
}

//藉由角色數量一一填入抽角池
fun getRoleMap(randomArray: ArrayList<RandomType>): TreeMap<String, ArrayList<Role>> {
    val result = TreeMap<String, ArrayList<Role>>()
    randomArray.forEachIndexed { _, randomType ->
        randomType.role.forEachIndexed { _, role ->
            (0 until role.random).forEach { _ ->
                val arrayList = result[role.type] ?: arrayListOf()
                arrayList.add(role)
                result[role.type] = arrayList
            }
        }
    }
    return result

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

fun List<Card>.getACard(roleMap: TreeMap<String, ArrayList<Role>>) = this[(this.indices).random()].getRole(roleMap)

fun List<Card>.getCards(times: Int, roleMap: TreeMap<String, ArrayList<Role>>): TreeMap<String, Int> {
    val getCardsResult = TreeMap<String, Int>()

    (0 until times).forEach { _ ->
        val getCard = this.getACard(roleMap) //這次抽到的卡
        getCardsResult[getCard.name] = (getCardsResult[getCard.name] ?: 0) + 1
    }
    return getCardsResult
}

//依照Double陣列塞進List回傳一個牌堆 // 依照每個角色的機率不同，來抽卡。
fun getCardList(randomArray: List<RandomType>): List<Card> {
    val result = arrayListOf<Card>()
    val numTimes = getNumTimes(randomArray)
    randomArray.forEach {
        val thisDigit = (it.random * numTimes).toInt()
        (0 until thisDigit).forEach { _ ->
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


/**
 * 多個不連續數字的最小公倍數
 */
fun caculateLCM(arrays: ArrayList<Int>): Int {
    var result = 0
//    arrays.sortDescending()
    outer@ for (nowTest in 2 until Int.MAX_VALUE) {
        //針對每一個數字，測試arrays是否可以將其整除
        var isLast = false //是否驗證到最後一個arrays可整除
        array@ for (test in arrays) {
            isLast = false
            if (nowTest % test != 0) // 其中一個不可整除了
                break@array // 直接測下一個數字
            else {
                isLast = true
                result = nowTest
            }
        }
        if (isLast)
            break@outer
    }
    if (result > 0) {
        println(arrays + "的最小公倍數為:" + result)
    }
    return result
}

class RandomType(
    val type: String = "",
    val random: Double = 0.0,
    val role: ArrayList<Role> = arrayListOf()
)


data class Role(
    val type: String = "",
    val random: Int = 1,
    val name: String = ""
)

data class Card(
    val type: String = "",
    val name: String = ""
) {
    fun getRole(roleMap: TreeMap<String, ArrayList<Role>>): Card { //藉由傳入的ThreeMap來回傳一個卡給他
        val random = roleMap[this.type]?.random() ?: return Card()
        return Card(random.type, random.name)
    }
}