package com.example.nq.listHelpQuestions

object HelpRepository {

    private val questionList = listOf(
        "¿Cómo funciona NQ?",
        "¿Puedo devolver una entrada comprada?",
        "¿Puedo cambiar la fecha de una entrada comprada?"
    )

    private val answerList = listOf(
        listOf("Compras una entrada y ya."),
        listOf("Hmmm puede ser."),
        listOf("Sólo si quedan entradas para la nueva fecha, además tendrás una penalización."),
    )

    fun returnHelpList(): List<HelpData> {
        val helpList = mutableListOf<HelpData>()

        for (i in questionList.indices) {
            val question = questionList[i]
            val answer = answerList[i]
            val helpData = HelpData(question, answer)
            helpList.add(helpData)
        }

        return helpList
    }
}