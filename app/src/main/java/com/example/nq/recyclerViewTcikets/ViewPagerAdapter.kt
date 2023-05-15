package com.example.nq.recyclerViewTcikets

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alikatickets.databinding.FragmentQrBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ViewPagerAdapter(private val tickets: List<Ticket>)
    : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentQrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tickets[position])
    }

    override fun getItemCount(): Int {
        return tickets.size
    }

    inner class ViewHolder(private val binding: FragmentQrBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ticket: Ticket) {
            // Instanciar la variable de fechas
            val dateInstance = Fechas()
            // Declaramos las varibales que vamos a usar
            val nameToShow: String = ticket.name.uppercase()
            val discoNameToShow: String = ticket.discoName
            val dateToShow: String = dateInstance.formatDate(ticket.date)
            val emailToShow: String = ticket.email

            // Generate QR code asynchronously using coroutines
            CoroutineScope(Dispatchers.Default).launch {
                // Encriptar los datos que se van a utilizar para generar el QR Bitmap
                val qrData: String = "$nameToShow-$discoNameToShow-$dateToShow-$emailToShow".replace(" ", "")
                val encryptionUtility = EncryptionUtility()
                val qrDataEncrypted: String = encryptionUtility.encrypt(qrData)
                // Llamar a la función para generar el QR Bitmap
                val bitmapInstance = GenerateBitmap()
                val qrBitmapEncrypted: Bitmap = bitmapInstance.generateBitmap(qrDataEncrypted)

                withContext(Dispatchers.Main) {
                    // Update UI with the generated QR code bitmap
                    binding.textViewDiscoteca.text = discoNameToShow
                    binding.textViewFecha.text = dateToShow
                    binding.textViewNombre.text = nameToShow
                    binding.textViewEmail.text = emailToShow
                    binding.imageViewQR.setImageBitmap(qrBitmapEncrypted)
                }
            }
        }
    }
}