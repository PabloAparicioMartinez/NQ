package com.example.nq.listHelpQuestions

object HelpRepository {

    private val questionList = listOf(
        "¿Cómo funciona NQ?",
        "¿Cómo puedo iniciar sesión en la aplicación?",
        "¿Puedo transferir o revender mis entradas?",
        "¿Puedo reembolsar mis entradas?",
        "¿Cómo recibo mis entradas después de comprarlas?",
        "¿Hay algún límite en el número de entradas que puedo comprar para un evento?",
        "¿Qué métodos de pago se aceptan?",
        "¿Hay algún coste por la compra de entradas?",
        "¿Cómo puedo ponerme en contacto con el servicio de atención al cliente si tengo algún problema con la compra de mis entradas?",
        "¿Está segura mi información personal y de pago en la aplicación?"
    )

    private val answerList = listOf(
        listOf("NQ es una aplicación destinada a facilitar la compra de entradas de discotecas para ti y/o para tus amigos:\n" +
                "- En la pantalla “Comprar entradas” puedes comprár entradas para ti y/o para tus amigos, para las discotecas y fiestas que quieras.\n" +
                "- En la pantalla “Mis entradas” tienes las entradas que has comprado, las cuales cuentan con un código QR con el que podrás entrar a la discoteca.\n" +
                "- En la pantalla “Mi perfil”, entre otras opciones, puedes añadir a tus amigos para poder comprar entradas para ellos o recibir las entradas que ellos te compren.\n"),
        listOf("Puedes iniciar sesión en NQ de dos maneras: mediante tu cuenta de Google o mediante un correo de verificación que te llegará a tu cuenta de correo electrónico."),
        listOf("No está permitido transferir ni revender ninguna entrada, únicamente está permitido reembolsarlas hasta 3 días antes del día de la fiesta para el que fue comprada la entrada."),
        listOf("Todas las entradas son reembolsables hasta 3 días antes del día de la fiesta para el que fue comprada la entrada, y este reembolso está sujeto a las políticas de reembolso de cada discoteca: de ellas depende la penalización que te será aplicada a la hora de vender tus entrada."),
        listOf("Si compras una entrada solo para ti, esta aparecerá automáticamente en la pantalla “Mis entradas” después de completar la compra. Así mismo, te llegará un correo a tu correo electrónico informándote de que el proceso de compra ha sido correcto.\n" +
                "Si compras una o varias entradas para ti y/o para tus amigos, estos recibirán una notificación a su aplicación NQ con la entrada que les has comprado, así como un correo electrónico informándoles de esto. \n"),
        listOf("Sí, puedes comprar hasta un total de 9 entradas por fiesta: 1 para ti y 8 para tus amigos."),
        listOf("Aceptamos varios métodos de pago, incluidas las principales tarjetas de crédito (Visa, Mastercard, American Express), tarjetas de débito y opciones de pago por móvil como Apple Pay y Google Pay."),
        listOf("Sí, se aplica una pequeña tarifa de servicio para cubrir los costes asociados al procesamiento de las entradas, el mantenimiento de la aplicación y la atención al cliente. Esta tasa está incluida en el importe total que aparece en el momento en el que realizas el pago de las entradas."),
        listOf("Si tienes algún problema o pregunta sobre la compra de tus entradas, puedes ponerte en contacto con nosotros a través de nuestra página web."),
        listOf("Damos prioridad a la seguridad de tu información personal y de pago. Nuestra aplicación emplea medidas de seguridad estándar del sector, como encriptación y protocolos seguros, para salvaguardar tus datos. También cumplimos la normativa sobre privacidad y no compartimos tu información con terceros no autorizados.")
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