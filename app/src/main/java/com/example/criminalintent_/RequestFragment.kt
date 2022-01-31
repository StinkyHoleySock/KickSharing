package com.example.criminalintent_

import android.app.Activity
import android.app.ProgressDialog.show
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Property.of
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_request.*
import java.io.File

import java.util.*


private const val ARG_REQUEST_ID = "request_id"
private const val TAG = "RequestFragment"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val REQUEST_CONTACT = 1
private const val DATE_FORMAT = "EEE, MMM, dd"

class RequestFragment: Fragment(), DatePickerFragment.Callbacks {

    private lateinit var request: Request
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var employeeButton: Button


    private val requestDetailViewModel: RequestDetailViewModel by lazy {
        ViewModelProviders.of(this).get(RequestDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        request = Request()
        val requestId: UUID = arguments?.getSerializable(ARG_REQUEST_ID) as UUID
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_request, container, false)

        titleField = view.findViewById(R.id.request_title) as EditText
        dateButton = view.findViewById(R.id.request_date) as Button
        solvedCheckBox = view.findViewById(R.id.request_solved) as CheckBox
        reportButton = view.findViewById(R.id.request_report) as Button
        employeeButton = view.findViewById(R.id.request_employee) as Button

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val requestId = arguments?.getSerializable(ARG_REQUEST_ID) as UUID
        requestDetailViewModel.loadRequest(requestId)
        requestDetailViewModel.requestLiveData.observe(
            viewLifecycleOwner,
            Observer { request ->
                request?.let {
                    this.request = request
                    updateUI()
                }
            })

        val appCompatActivity = activity as AppCompatActivity
        appCompatActivity.supportActionBar?.setTitle(R.string.new_request)
    }

    private fun updateUI() {
        titleField.setText(request.title)
        dateButton.text = request.currentDate.toString()
        solvedCheckBox.apply {
            isChecked = request.isSolved
            jumpDrawablesToCurrentState()
        }
        if (request.employee.isNotEmpty()) {
            employeeButton.text = request.employee
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return

            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri? = data.data

                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                val cursor = requireActivity().contentResolver
                        .query(contactUri!!, queryFields, null, null, null)
                cursor?.use{
                    if (it.count == 0) {
                        return
                    }

                    it.moveToFirst()
                    val employee = it.getString(0)
                    request.employee = employee
                    requestDetailViewModel.saveRequest(request)
                    employeeButton.text = employee
                }

            }
        }
    }


    private fun getRequestReport(): String {
        val solvedString = if (request.isSolved) {
            getString(R.string.request_report_solved)
        } else {
            getString(R.string.request_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, request.currentDate).toString()
        val employee = if (request.employee.isBlank()) {
            getString(R.string.request_report_no_employee)
        } else {
            getString(R.string.request_report_employee, request.employee)
        }

        return getString(R.string.request_report,
            request.title, dateString, solvedString, employee)
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This space intentionally left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                request.title = sequence.toString()

            }

            override fun afterTextChanged(sequence: Editable?) {
                // This one too
            }
        }

        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                request.isSolved = isChecked
            }
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(request.currentDate).apply {
                setTargetFragment(this@RequestFragment, REQUEST_DATE)
                show(this@RequestFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getRequestReport())
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.request_report_subject))

            }.also { intent ->
                val chooserIntent =
                    Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        employeeButton.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
            }
        }

    }

    override fun onStop() {
        super.onStop()
        requestDetailViewModel.saveRequest(request)
    }

    override fun onDateSelected(date: Date) {
        request.currentDate = date
        updateUI()
    }

    companion object {
        fun newInstance(requestId: UUID): RequestFragment {
            val args = Bundle().apply {
                putSerializable(ARG_REQUEST_ID, requestId)
            }
            return RequestFragment().apply {
                arguments = args
            }
        }
    }
}