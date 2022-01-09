package com.gdsctsec.smartt.ui.edit

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity

import java.util.*
import android.graphics.Color

import android.text.TextWatcher
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.gdsctsec.smartt.R
import com.gdsctsec.smartt.data.TimeTable
import com.gdsctsec.smartt.data.Weekday
import com.gdsctsec.smartt.data.repository.LectureRepository
import com.gdsctsec.smartt.ui.main.HomeScreenFragment
import com.gdsctsec.smartt.viewmodel.EditScreenViewModel
import com.gdsctsec.smartt.viewmodel.EditscreenViewmodelfactory


class EditScreenActivity : AppCompatActivity() {
    private lateinit var lectureEditText: EditText
    private lateinit var starttimeTextView: TextView
    private lateinit var endtimeTextView: TextView
    private lateinit var dayTextInputEditText: AutoCompleteTextView
    private lateinit var saveTextview: TextView
    private lateinit var cancelTextView: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editscreen)

        lectureEditText = findViewById(R.id.add_lecture_edit_text)
        starttimeTextView = findViewById(R.id.textview_starttime)
        endtimeTextView = findViewById(R.id.textview_endtime)
        dayTextInputEditText = findViewById(R.id.textfield_day)
        saveTextview = findViewById(R.id.textView_save)
        cancelTextView = findViewById(R.id.textView_cancel)
        val viewModelFactory = EditscreenViewmodelfactory(this)
        var id : Int  = -1
        val viewModel = ViewModelProvider(this,viewModelFactory).get(EditScreenViewModel::class.java)
        val choice : String? = intent.getStringExtra("TAG").toString()
            if(choice == "HomeScreenFragment") {
                val startTime = intent.getStringExtra("Lecture_start_Time").toString()
                val endTime = intent.getStringExtra("Lecture_End_time").toString()
                val lecture = intent.getStringExtra("Lecture_Choosen_subject").toString()
                val weekDay = intent.getStringExtra("Lecture_Weekday").toString()
                id = Integer.parseInt(intent.getStringExtra("id").toString())

                lectureEditText.setText(lecture)
                starttimeTextView.setText(startTime)
                endtimeTextView.setText(endTime)
                dayTextInputEditText.setText(weekDay)
            }
           if(choice == "WeekdayActivity"){
               val weekDay = intent.getStringExtra("Weekday").toString()
               dayTextInputEditText.setText(weekDay)
                dayTextInputEditText.dropDownHeight = 0
               viewDisabled(dayTextInputEditText)
           }







        // dropdown
        var days = resources.getStringArray(com.gdsctsec.smartt.R.array.days)
        val ArrayAdapter = ArrayAdapter(
            this,
            com.gdsctsec.smartt.R.layout.dropdown, days
        )
        dayTextInputEditText.setAdapter(ArrayAdapter)

        // adding Textwatcher
        lectureEditText.addTextChangedListener(textWatcher)
        starttimeTextView.addTextChangedListener(textWatcher)
        endtimeTextView.addTextChangedListener(textWatcher)
        dayTextInputEditText.addTextChangedListener(textWatcher)


        starttimeTextView.setOnClickListener {
            timePick(starttimeTextView)
        }
        endtimeTextView.setOnClickListener {
            timePick(endtimeTextView)

        }
        saveTextview.setOnClickListener {
            // wil require all strings for saving in database
            var lecture: String = lectureEditText.text.toString()
            var day: String = dayTextInputEditText.text.toString()
            var starttime: String = starttimeTextView.text.toString()
            var endtime: String = endtimeTextView.text.toString()
            if(!choice.equals("HomeScreenFragment")){
                viewModel.addlecture(TimeTable(lec = lecture, weekday = Weekday.valueOf(day) , startTime = starttime, endTime = endtime))
                Toast.makeText(this, "adding", Toast.LENGTH_SHORT).show()
            }
            else{
                viewModel.updatelecture(TimeTable(lec = lecture, weekday = Weekday.valueOf(day) , startTime = starttime, endTime = endtime, id = id))
                Toast.makeText(this, "updating", Toast.LENGTH_SHORT).show()
            }
//
            dayTextInputEditText.height = WRAP_CONTENT
            viewEnabled(dayTextInputEditText)
           finish()
        }


        cancelTextView.setOnClickListener {
            dayTextInputEditText.height = WRAP_CONTENT
            viewEnabled(dayTextInputEditText)

            finish()
        }
    }
    private fun  btnDisabled(v : TextView){
        v.isEnabled = false
        v.setTextColor(Color.parseColor("#5D1BAACA"))
    }
    private fun  btnEnabled(v : TextView){
        v.isEnabled = true
        v.setTextColor(Color.parseColor("#1BAACA"))
    }

    private fun viewEnabled(v: AutoCompleteTextView) {
        v.isEnabled = true
        v.isPressed = true
        v. isFocusable = true
        v.isFocusableInTouchMode = true;
        v.inputType = EditorInfo.TYPE_CLASS_TEXT


    }

    private fun viewDisabled(v: AutoCompleteTextView) {
        v.isEnabled = false
        v.isPressed = false
        v.isFocusable = false
        v.isFocusableInTouchMode = false
        v.inputType = EditorInfo.TYPE_NULL

    }


    private fun timePick(v: TextView) {
        val mTimePickerstart: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        mTimePickerstart = TimePickerDialog(this,
            R.style.MyTimePickerDialogTheme,
            object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                v.setText(onTimeSet(hourOfDay, minute))
            }
        }, hour, minute, false)
        mTimePickerstart.show()

    }

    private fun onTimeSet(hourOfDay: Int, minute: Int): String {
        // Set a variable to hold the current time AM PM Status
        // Initially we set the variable value to AM


        var status = "am"
        if (hourOfDay > 11) {
            // If the hour is greater than or equal to 12
            // Then the current AM PM status is PM
            status = "pm"
        }

        // Initialize a new variable to hold 12 hour format hour value
        val hour_of_12_hour_format: Int
        hour_of_12_hour_format = if (hourOfDay > 12) {

            // If the hour is greater than or equal to 12
            // Then we subtract 12 from the hour to make it 12 hour format time
            hourOfDay - 12
        } else {
            hourOfDay
        }

        // Get the calling activity TextView reference

        // Display the 12 hour format time in app interface
        if (minute >= 0 && minute <= 9) {
            val time = "$hour_of_12_hour_format:0$minute $status"
            return time
        } else {
            val time = "$hour_of_12_hour_format:$minute $status"
            return time
        }

    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            var lecture: String? = lectureEditText.text.toString()
            var day: String? = dayTextInputEditText.text.toString()
            var starttime: String? = starttimeTextView.text.toString()
            var endtime: String? = endtimeTextView.text.toString()
            if (lecture != "" && day != "Select day" && starttime != "" && endtime != "") {
                btnEnabled(saveTextview)
            } else {
                btnDisabled(saveTextview)
            }
        }
    }


}