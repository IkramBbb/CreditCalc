package com.example.mycalculate

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var loanAmountEditText: EditText
    private lateinit var interestRateEditText: EditText
    private lateinit var loanTermEditText: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var calculateButton: Button
    private lateinit var listViewResult: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loanAmountEditText = findViewById(R.id.editTextLoanAmount)
        interestRateEditText = findViewById(R.id.editTextInterestRate)
        loanTermEditText = findViewById(R.id.editTextLoanTerm)
        radioGroup = findViewById(R.id.radioGroup)
        calculateButton = findViewById(R.id.buttonCalculate)
        listViewResult = findViewById(R.id.listViewResult)

        calculateButton.setOnClickListener {
            calculateLoan()
        }
    }

    private fun calculateLoan() {
        val loanAmount = loanAmountEditText.text.toString().toDouble()
        val interestRate = interestRateEditText.text.toString().toDouble()
        val loanTerm = loanTermEditText.text.toString().toInt()
        val selectedRadioButtonId = radioGroup.checkedRadioButtonId

        val result = when (selectedRadioButtonId) {
            R.id.radioButtonAnnuity -> calculateAnnuityLoan(loanAmount, interestRate, loanTerm)
            R.id.radioButtonDifferential -> calculateDifferentialLoan(loanAmount, interestRate, loanTerm)
            else -> 0.0
        }

        val resultArray = when (selectedRadioButtonId) {
            R.id.radioButtonAnnuity -> generateAnnuityTable(loanAmount, interestRate, loanTerm)
            R.id.radioButtonDifferential -> generateDifferentialTable(loanAmount, interestRate, loanTerm)
            else -> emptyArray()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, resultArray)
        listViewResult.adapter = adapter
    }

    private fun calculateAnnuityLoan(loanAmount: Double, interestRate: Double, loanTerm: Int): Double {
        val monthlyInterestRate = interestRate / 100 / 12
        val denominator = Math.pow(1 + monthlyInterestRate, loanTerm.toDouble()) - 1
        return loanAmount * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTerm.toDouble()) / denominator
    }

    private fun calculateDifferentialLoan(loanAmount: Double, interestRate: Double, loanTerm: Int): Double {
        // Расчет дифференциального кредита: сумма всех ежемесячных платежей = (сумма кредита / срок кредита) + (остаток долга * процентная ставка / 12)
        val monthlyPayment = loanAmount / loanTerm + (loanAmount * interestRate / 100 / 12)
        return monthlyPayment
    }

    private fun generateAnnuityTable(loanAmount: Double, interestRate: Double, loanTerm: Int): Array<String> {
        var remainingLoan = loanAmount
        val monthlyPayment = calculateAnnuityLoan(loanAmount, interestRate, loanTerm)
        val resultArray = mutableListOf<String>()

        for (month in 1..loanTerm) {
            val interestPayment = remainingLoan * interestRate / 100 / 12
            val principalPayment = monthlyPayment - interestPayment
            remainingLoan -= principalPayment

            resultArray.add("Месяц $month:\nОплата: $monthlyPayment\nПроценты: $interestPayment\nОсновной: $principalPayment\nОставшийся кредит: $remainingLoan")
        }

        return resultArray.toTypedArray()
    }

    private fun generateDifferentialTable(loanAmount: Double, interestRate: Double, loanTerm: Int): Array<String> {
        var remainingLoan = loanAmount
        val monthlyPayment = calculateDifferentialLoan(loanAmount, interestRate, loanTerm)
        val resultArray = mutableListOf<String>()

        for (month in 1..loanTerm) {
            val interestPayment = remainingLoan * interestRate / 100 / 12
            val principalPayment = monthlyPayment - interestPayment
            remainingLoan -= principalPayment

            resultArray.add("Месяц $month:\nОплата: $monthlyPayment\nПроценты: $interestPayment\nОсновной: $principalPayment\nОставшийся кредит: $remainingLoan")
        }

        return resultArray.toTypedArray()
    }
}
