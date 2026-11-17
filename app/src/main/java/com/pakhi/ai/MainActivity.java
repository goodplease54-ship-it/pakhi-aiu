
package com.pakhi.ai;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.view.Gravity;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    TextView message;
    TextToSpeech speaker;
    SpeechRecognizer recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);
        layout.setBackgroundColor(Color.rgb(12, 20, 45));

        TextView title = new TextView(this);
        title.setText("🐦 পাখি AI");
        title.setTextSize(34);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        message = new TextView(this);
        message.setText("আমি পাখি। আমাকে ডাকুন!");
        message.setTextSize(20);
        message.setTextColor(Color.WHITE);
        message.setGravity(Gravity.CENTER);
        message.setPadding(10, 50, 10, 50);

        Button speakButton = new Button(this);
        speakButton.setText("🎤 পাখির সাথে কথা বলুন");

        layout.addView(title);
        layout.addView(message);
        layout.addView(speakButton);

        setContentView(layout);

        speaker = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                speaker.setLanguage(new Locale("bn", "BD"));
                speaker.speak(
                    "আসসালামু আলাইকুম। আমি পাখি। বলুন, কী করতে হবে?",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "pakhi"
                );
            }
        });

        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this);
            recognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    message.setText("আমি শুনছি, বলুন...");
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> words =
                        results.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        );

                    if (words != null && !words.isEmpty()) {
                        String text = words.get(0);
                        message.setText("আপনি বলেছেন: " + text);
                        speaker.speak(
                            "আপনি বলেছেন " + text,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "reply"
                        );
                    }
                }

                @Override
                public void onError(int error) {
                    message.setText("আবার চেষ্টা করুন।");
                }

                @Override public void onBeginningOfSpeech() {}
                @Override public void onRmsChanged(float rmsdB) {}
                @Override public void onBufferReceived(byte[] buffer) {}
                @Override public void onEndOfSpeech() {}
                @Override public void onPartialResults(Bundle results) {}
                @Override public void onEvent(int eventType, Bundle params) {}
            });
        }

        speakButton.setOnClickListener(v -> startListening());
    }

    private void startListening() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                new String[]{Manifest.permission.RECORD_AUDIO}, 100
            );
            return;
        }

        if (recognizer == null) {
            message.setText("ভয়েস সার্ভিস পাওয়া যাচ্ছে না।");
            return;
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD");
        recognizer.startListening(intent);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        );

        if (requestCode == 100 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }

    @Override
    protected void onDestroy() {
        if (recognizer != null) recognizer.destroy();
        if (speaker != null) speaker.shutdown();
        super.onDestroy();
    }
        }
