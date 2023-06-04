package com.example.nq.recyclerViewDiscos

import com.example.nq.R

object DiscosRepository {

    val discos = mutableListOf(
        DiscosData(
            R.drawable.ic_disco_back_stage,
            "BACK&STAGE",
            "Abando, Bilbao",
            "Reggeaton | Latina"
        ),
        DiscosData(
            R.drawable.ic_disco_fever,
            "FEVER",
            "Bolueta, Bilbao",
            "Reggeaton | Elecrónica"),
        DiscosData(
            R.drawable.ic_disco_sonora,
            "SONORA",
            "Erandio, Bilbao",
            "Reggeaton | Tecno")
    )
}