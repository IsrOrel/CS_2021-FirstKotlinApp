package com.example.orelrestaurant
import android.animation.Animator
import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

interface MyAnimatorListenerAdapter : Animator.AnimatorListener {
    override fun onAnimationStart(animation: Animator) = Unit
    override fun onAnimationRepeat(animation: Animator) = Unit
    override fun onAnimationCancel(animation: Animator) = Unit
    override fun onAnimationEnd(animation: Animator) = Unit
}

class MainActivity : AppCompatActivity(), MyAnimatorListenerAdapter {

    private lateinit var dateEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeHomeScreenLayout()
    }

    private fun initializeHomeScreenLayout() {
        enableEdgeToEdge()
        setContentView(R.layout.homescreen)


        val menu_btn = findViewById<Button>(R.id.menu)
        val table_btn = findViewById<Button>(R.id.table)
        val text1 = findViewById<TextView>(R.id.welcome_text)
        val text2 = findViewById<TextView>(R.id.about_text)
        val card = findViewById<CardView>(R.id.reserve_table_card_view)


        val handler = Handler()
        handler.postDelayed({

            menu_btn.visibility = View.VISIBLE
            table_btn.visibility = View.VISIBLE
            text1.visibility = View.VISIBLE
            text2.visibility = View.VISIBLE
        }, 10000)
        menu_btn.setOnClickListener { performMenuAction() }

        table_btn.setOnClickListener { card.visibility = View.VISIBLE }
        val spinner = findViewById<Spinner>(R.id.number_of_guests_picker)
        val nums = (0..10).toList().map { it.toString() }.toTypedArray()
        val adapter = ArrayAdapter(
            this,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            nums
        )
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val spinner2 = findViewById<Spinner>(R.id.number_of_vegetarian_guests_picker)
        spinner2.adapter = adapter

        val okButton = findViewById<Button>(R.id.ok_button)
        okButton.setOnClickListener {
            displayUserInformation()
        }

        dateEditText = findViewById(R.id.date_edit_text)
        dateEditText.setOnClickListener { showDatePicker() }
        setupTimeSpinner()
        val exitButton = findViewById<MaterialButton>(R.id.exit_button)
        exitButton.setOnClickListener {
            card.visibility = View.GONE
        }

    }


    private fun performMenuAction() {
        setContentView(R.layout.product_info)

        val imageButtonIds = arrayOf(
            R.id.hotdog_text,
            R.id.chips_text,
            R.id.pizza_text,
            R.id.heiniken_text,
            R.id.cola_text,
            R.id.wine_text

        )

        val imageButton = findViewById<ImageButton>(R.id.selectedProductImageButton)
        val productDetailsMap = mapOf(
            "hotdogbr" to getString(R.string.hotdog_desc),
            "chipsbg" to getString(R.string.chips_desc),
            "pizzabr" to getString(R.string.pizza_desc),
            "hbr" to getString(R.string.heiniken_desc),
            "wine" to getString(R.string.wine_desc),
            "colabr" to getString(R.string.cola_desc)

        )

        val productNameMap = mapOf(
            "hotdogbr" to getString(R.string.hotdog_name),
            "chipsbg" to getString(R.string.chips_name),
            "pizzabr" to getString(R.string.pizza_name),
            "hbr" to getString(R.string.heiniken_name),
            "wine" to getString(R.string.wine_name),
            "colabr" to getString(R.string.cola_name)


        )

        imageButtonIds.forEach { resourceId ->
            val textView = findViewById<TextView>(resourceId)
            textView.setOnClickListener {
                val productName = it.tag.toString()
                val drawableId = resources.getIdentifier(productName, "drawable", packageName)
                if (drawableId != 0) {
                    imageButton.setImageResource(drawableId)
                    Log.e("Tag", "Tag is null for product: $productName")
                    onProductSelected(productNameMap[productName], productDetailsMap[productName])
                } else {
                    Log.e("Tag", "Tag is null for product: $productName")
                }
            }
        }
        val exitButton_menu = findViewById<MaterialButton>(R.id.exit_button_menu)
        exitButton_menu.setOnClickListener {
            initializeHomeScreenLayout()
        }
    }


    private fun onProductSelected(productName: String?, productDetails: String?) {
        if (!productName.isNullOrBlank() && !productDetails.isNullOrBlank()) {
            val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_and_move)
            animation.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    showProductInfoDialog(productName!!, productDetails!!)
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })

            val imageButton = findViewById<ImageButton>(R.id.selectedProductImageButton)
            imageButton.startAnimation(animation) // Start the animation
        }
    }


    private fun showProductInfoDialog(productName: String, productDetails: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("$productName")
            .setMessage(productDetails)
            .setPositiveButton("OK", null)
            .create()
        dialog.show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val minDate = calendar.timeInMillis // Set minimum date to current date
        calendar.add(Calendar.MONTH, 1) // Add 1 month to the current date
        val maxDate = calendar.timeInMillis
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                updateDateEditText(selectedDate)

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = minDate
        datePickerDialog.datePicker.maxDate = maxDate

        datePickerDialog.show()
    }

    private fun updateDateEditText(calendar: Calendar) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateEditText.setText(dateFormat.format(calendar.time))
    }

    private fun setupTimeSpinner() {
        val times = arrayOf(
            "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
            "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30",
            "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30",
            "23:00", "23:30", "00:00"
        )

        val spinner = findViewById<Spinner>(R.id.time_spinner)


        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, times)


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        spinner.adapter = adapter


        spinner.setSelection(times.indexOf("11:00"))
    }

    private fun displayUserInformation() {

        val firstName = findViewById<EditText>(R.id.first_name_edit_text).text.toString()
        val lastName = findViewById<EditText>(R.id.last_name_edit_text).text.toString()
        val phoneNumber = findViewById<EditText>(R.id.phone_edit_text).text.toString()
        val email = findViewById<EditText>(R.id.email_edit_text).text.toString()
        val numberOfGuests =
            findViewById<Spinner>(R.id.number_of_guests_picker).selectedItem.toString()
        val numberOfVegetarianGuests =
            findViewById<Spinner>(R.id.number_of_vegetarian_guests_picker).selectedItem.toString()
        val paymentMethod = findViewById<RadioButton>(R.id.cash_radio_button).let {
            if (it.isChecked) getString(R.string.cash) else getString(R.string.credit)
        }
        val spinner = findViewById<Spinner>(R.id.time_spinner)
        val selectedTime = spinner.selectedItem.toString()
        val date = findViewById<EditText>(R.id.date_edit_text).text.toString()

        if (validateGuests(
                numberOfGuests,
                numberOfVegetarianGuests,
                firstName,
                phoneNumber,
                email
            )
        ) {

            val username = getString(R.string.user_name_format, firstName, lastName)
            val userphone = getString(R.string.user_phone_format, phoneNumber)
            val useremail = getString(R.string.user_email_format, email)
            val numberofguests = getString(R.string.user_guests_format, numberOfGuests)
            val numberofVguests = getString(R.string.user_vegan_guests_format, numberOfVegetarianGuests)
            val formattedTime = getString(R.string.user_time_format, selectedTime)
            val formattedDate = getString(R.string.user_date_format, date)

            val dialogView = layoutInflater.inflate(R.layout.user_info_dialog, null)
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton(getString(R.string.dialog_positive_button)) { dialog, _ -> dialog.dismiss() }
                .create()

            dialogView.findViewById<TextView>(R.id.name_text_view).text = username
            dialogView.findViewById<TextView>(R.id.phone_text_view).text = userphone
            dialogView.findViewById<TextView>(R.id.email_text_view).text = useremail
            dialogView.findViewById<TextView>(R.id.num_guests_text_view).text = numberofguests
            dialogView.findViewById<TextView>(R.id.num_veg_guests_text_view).text = numberofVguests
            dialogView.findViewById<TextView>(R.id.payment_method_text_view).text = paymentMethod
            dialogView.findViewById<TextView>(R.id.date_text_view).text = formattedDate
            dialogView.findViewById<TextView>(R.id.time_text_view).text = formattedTime

            dialog.show()
        }
    }


    private fun validateGuests(
        numberOfGuests: String,
        numberOfVegetarianGuests: String,
        name: String,
        phoneNumber: String,
        email: String
    ): Boolean {
        if (numberOfGuests == "0" && numberOfVegetarianGuests == "0") {
            displayErrorDialog(getString(R.string.error_invalid_guests))
            return false
        }

        if (name.isBlank()) {
            displayErrorDialog(getString(R.string.error_enter_name))
            return false
        }

        if (phoneNumber.isBlank()) {
            displayErrorDialog(getString(R.string.error_enter_phone))
            return false
        }
        if (!phoneNumber.startsWith("05")) {
            displayErrorDialog(getString(R.string.error_phone_format))
            return false
        }

        if (email.isBlank()) {
            displayErrorDialog(getString(R.string.error_enter_email))
            return false
        }
        if (!email.contains("@")) {
            displayErrorDialog(getString(R.string.error_invalid_email))
            return false
        }
        return true
    }

    private fun displayErrorDialog(message: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.error_title))
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create()
        alertDialog.show()
    }
}
