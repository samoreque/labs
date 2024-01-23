package com.code.path.wordle

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.text.color
import androidx.core.text.toSpannable
import com.code.path.wordle.utils.FourLetterWordList
import com.github.jinatonic.confetti.CommonConfetti
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private var tries = 0

    private lateinit var wordExpectedText: TextView
    private lateinit var streakText: TextView
    private lateinit var guessGroup1: Group
    private lateinit var guessGroup2: Group
    private lateinit var guessGroup3: Group
    private lateinit var submitButton: Button
    private lateinit var resetButton: Button
    private lateinit var englishButton: Button
    private lateinit var spanishButton: Button
    private lateinit var guessInput: EditText
    private lateinit var guessInputLayout: TextInputLayout
    private lateinit var wordExpected: String

    private var confetti: CommonConfetti? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var streak = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        selectWord()
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        streak = sharedPreferences.getInt(PREFERENCE_STREAK, 0)
        guessGroup1 = findViewById(R.id.groupGuessOne)
        guessGroup2 = findViewById(R.id.groupGuessTwo)
        guessGroup3 = findViewById(R.id.groupGuessThree)
        guessInputLayout = findViewById(R.id.guessInputLayout)
        streakText = findViewById(R.id.streakText)
        setStreak(streak)
        guessInput = findViewById<EditText>(R.id.guessInput).apply {
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        guessEntered()
                        true
                    }

                    else -> false

                }
            }
        }
        wordExpectedText = findViewById(R.id.wordExpectedText)
        submitButton = findViewById<Button>(R.id.submitButton).also {
            it.setOnClickListener { guessEntered() }
        }

        resetButton = findViewById<Button>(R.id.resetButton).also {
            it.setOnClickListener { resetGame() }
        }
        englishButton = findViewById<Button>(R.id.englishButton).also {
            it.setOnClickListener {
                selectWord()
            }
        }

        spanishButton = findViewById<Button>(R.id.spanishButton).also {
            it.setOnClickListener {
                selectWord(FourLetterWordList.SpanishWordList)
            }
        }
    }

    private fun setStreak(newStreak: Int) {
        streak = newStreak
        streakText.text = getString(R.string.streak_template, newStreak.toString())
    }

    private fun guessEntered() {
        hideKeyboard()
        spanishButton.gone()
        englishButton.gone()
        val input = guessInput.text.toString()
        val inputUpperCase = input.uppercase()
        when {
            input.length != 4 -> {
                guessInputLayout.error = getString(R.string.input_size_error)
                return
            }

            !Regex(LETTER_REGEX).matches(input) -> {
                guessInputLayout.error = getString(R.string.input_letter_error)
                return
            }

            else -> Unit
        }

        guessInputLayout.error = null
        tries++
        val guessResult = checkGuess(inputUpperCase, wordExpected)
        when (tries) {
            1 -> bindGuess(
                guessGroup1, findViewById(R.id.guessOneText), findViewById(R.id.guessOneCheckText), guessResult, input
            )

            2 -> bindGuess(
                guessGroup2, findViewById(R.id.guessTwoText), findViewById(R.id.guessTwoCheckText), guessResult, input
            )

            else -> {
                bindGuess(
                    guessGroup3, findViewById(R.id.guessThreeText), findViewById(R.id.guessThreeCheckText), guessResult, input
                )
            }
        }

        isGameFinished(inputUpperCase, wordExpected, tries)
        guessInput.setText("")
    }

    private fun isGameFinished(guess: String, wordToGuess: String, tries: Int) {
        when {
            guess == wordToGuess -> {
                confetti = CommonConfetti.rainingConfetti(
                    findViewById(R.id.container),
                    intArrayOf(Color.YELLOW, Color.GREEN, Color.BLACK, Color.RED)
                ).also {
                    it.oneShot()
                }

                endGame(wordExpected, ++streak)
            }

            tries > NRO_TRIES - 1 -> endGame(wordExpected, 0)
        }
    }

    private fun selectWord(wordList: FourLetterWordList = FourLetterWordList.EnglishWordList) {
        wordExpected = wordList.getRandomFourLetterWord()
        Log.i("startGame", wordExpected)
    }

    private fun endGame(wordExpected: String, streak: Int) {
        resetButton.visible()
        submitButton.gone()
        wordExpectedText.text = wordExpected
        saveStreak(streak)
        setStreak(streak)
    }

    private fun saveStreak(newStreak: Int) {
        sharedPreferences.edit().apply {
            putInt(PREFERENCE_STREAK, newStreak)
            apply()
        }
    }

    private fun resetGame() {
        hideKeyboard()
        spanishButton.visible()
        englishButton.visible()
        confetti?.confettiManager?.terminate()
        wordExpectedText.text = ""
        guessInput.setText("")
        tries = 0
        guessGroup1.gone()
        guessGroup2.gone()
        guessGroup3.gone()
        resetButton.gone()
        submitButton.visible()
        selectWord()
    }

    private fun bindGuess(
        group: Group,
        guessTextView: TextView,
        guessCheckTextView: TextView,
        guessResult: Spannable,
        guessExpected: String
    ) {
        group.visible()
        guessTextView.text = guessExpected
        guessCheckTextView.text = guessResult
    }

    /**
     * Parameters / Fields:
     *   wordToGuess : String - the target word the user is trying to guess
     *   guess : String - what the user entered as their guess
     *
     * Returns a String of 'O', '+', and 'X', where:
     *   'O' represents the right letter in the right place
     *   '+' represents the right letter in the wrong place
     *   'X' represents a letter not in the target word
     */
    private fun checkGuess(guess: String, wordToGuess: String): Spannable =
        SpannableStringBuilder().apply {
            var result = ""
            for (i in 0 until WORD_SIZE) {
                result += if (guess[i] == wordToGuess[i]) {
                    color(Color.GREEN) { append(guess[i]) }
                } else if (guess[i] in wordToGuess) {
                    color(Color.RED) { append(guess[i]) }
                } else {
                    append(guess[i])
                }
            }
        }.toSpannable()
}

private fun View.gone() {
    visibility = View.GONE
}

private fun View.visible() {
    visibility = View.VISIBLE
}

private fun Activity.hideKeyboard() {
    this.currentFocus?.let { view ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

private const val WORD_SIZE = 4
private const val NRO_TRIES = 3
private const val LETTER_REGEX = "^[a-zA-Z]+\$"
private const val PREFERENCE_NAME = "WORDLE_PREFERENCES"
private const val PREFERENCE_STREAK = "STREAK"
