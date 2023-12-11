package four.saudagar.notesapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MathChallengeActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var scoreTextView: TextView
    private lateinit var answerButton1: Button
    private lateinit var answerButton2: Button
    private lateinit var answerButton3: Button

    private var score: Int = 0
    private var correctAnswer: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_math_challenge)

        questionTextView = findViewById(R.id.questionTextView)
        scoreTextView = findViewById(R.id.scoreTextView)
        answerButton1 = findViewById(R.id.answerButton1)
        answerButton2 = findViewById(R.id.answerButton2)
        answerButton3 = findViewById(R.id.answerButton3)

        setupGame()
    }

    private fun setupGame() {
        generateQuestion()
        updateScore()

        answerButton1.setOnClickListener { checkAnswer(answerButton1.text.toString().toInt()) }
        answerButton2.setOnClickListener { checkAnswer(answerButton2.text.toString().toInt()) }
        answerButton3.setOnClickListener { checkAnswer(answerButton3.text.toString().toInt()) }
    }

    private fun generateQuestion() {
        val operand1 = Random.nextInt(1, 10)
        val operand2 = Random.nextInt(1, 10)
        correctAnswer = operand1 + operand2

        val incorrectAnswer1 = generateIncorrectAnswer()
        val incorrectAnswer2 = generateIncorrectAnswer()

        val options = listOf(correctAnswer, incorrectAnswer1, incorrectAnswer2).shuffled()

        questionTextView.text = "$operand1 + $operand2 = ?"
        answerButton1.text = options[0].toString()
        answerButton2.text = options[1].toString()
        answerButton3.text = options[2].toString()
    }

    private fun generateIncorrectAnswer(): Int {
        var incorrectAnswer: Int
        do {
            incorrectAnswer = Random.nextInt(1, 20)
        } while (incorrectAnswer == correctAnswer)
        return incorrectAnswer
    }

    private fun checkAnswer(selectedAnswer: Int) {
        if (selectedAnswer == correctAnswer) {
            score++
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Incorrect. Try again!", Toast.LENGTH_SHORT).show()
        }

        generateQuestion()
        updateScore()
    }

    private fun updateScore() {
        scoreTextView.text = "Score: $score"
    }
}
