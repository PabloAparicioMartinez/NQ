package com.example.nq.recyclerViewDiscos

import com.example.nq.R

object DiscosRepository {
    val discos = mutableListOf(
        DiscosData(
            R.drawable.ic_disco_back_stage,
            "BACK&STAGE",
            "Abando, Bilbao",
            "A 3 km de ti",
            "Reggeaton | Latina"
        ),
        DiscosData(
            R.drawable.ic_disco_fever,
            "FEVER",
            "Bolueta, Bilbao",
            "A 10 km de ti",
            "Reggeaton | Elecrónica"),
        DiscosData(
            R.drawable.ic_disco_sonora,
            "SONORA",
            "Erandio, Bilbao",
            "A 14 km de ti",
            "Reggeaton | Tecno")
    )
}