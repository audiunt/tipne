package com.souillard.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.souillard.BasesDeDonnées.AppDataBase;
import com.souillard.BasesDeDonnées.listes.ListesDAO;
import com.souillard.BasesDeDonnées.mots.Mots;
import com.souillard.BasesDeDonnées.mots.MotsDAO;
import com.souillard.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class GameOneActivity extends Activity {

    private TextView nomListe;
    private TextView motFr;
    private int idList;
    private ListesDAO listesDAO;
    private MotsDAO motsDAO;
    private List<Mots> mots;
    private Button restart;
    private Button confirm;
    private  Button[] buttons = new Button[12];
    private TextView motADeviner;
    private int indice;
    private int score;
    private Toast toast;
    private LinearLayout bodyEval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_1);

        listesDAO = AppDataBase.getAppDatabase(getApplicationContext()).ListesDAO();
        motsDAO = AppDataBase.getAppDatabase(getApplicationContext()).MotsDao();

        Bundle extras = getIntent().getExtras();


        idList = listesDAO.idDeListe((extras.getString("nameList")));

        nomListe = findViewById(R.id.ListName);
        nomListe.setText(listesDAO.getProperName(idList));
        mots = motsDAO.getList(idList);

        motADeviner = findViewById(R.id.motadeviner);
        motADeviner.setText("");

        motFr = findViewById(R.id.motFr);


        restart = findViewById(R.id.restartButton);
        confirm = findViewById(R.id.confirmationButton);
        buttons[0] = findViewById(R.id.char1);
        buttons[1] = findViewById(R.id.char2);
        buttons[2] = findViewById(R.id.char3);
        buttons[3] = findViewById(R.id.char4);
        buttons[4] = findViewById(R.id.char5);
        buttons[5] = findViewById(R.id.char6);
        buttons[6] = findViewById(R.id.char7);
        buttons[7] = findViewById(R.id.char8);
        buttons[8] = findViewById(R.id.char9);
        buttons[9] = findViewById(R.id.char10);
        buttons[10] = findViewById(R.id.char11);
        buttons[11] = findViewById(R.id.char12);


        Collections.shuffle(mots);

        indice =0;
        updateView(mots.get(getNewWord()));

        for (int j = 0; j < 12; j++){
            buttons[j].setOnClickListener(clickListenerButton);
        }

        restart.setOnClickListener(clickListenerRestart);
        confirm.setOnClickListener(clickListenerValidate);

    }


    private void updateNewRandomWord(Mots mots) {
        motFr.setText(mots.getMotsFr());
    }

    private String shuffleWord (String word) {
        List<Character> l = new ArrayList<>();
        for (char c : word.toCharArray()) //for each char of the word selectionned, put it in a list
            l.add(c);
        Collections.shuffle(l); //shuffle the list

        StringBuilder sb = new StringBuilder(); //now rebuild the word
        for (char c : l)
            sb.append(c);

        word = sb.toString();
        return word;
    }


    private void updateView (Mots mots){

        updateNewRandomWord(mots);
        String motMelange = shuffleWord(mots.getMotsEn());

        int i = 0;

        for (int x = 0; x < motMelange.length(); x++){
            buttons[x].setText(Character.toString(motMelange.charAt(x)));
            i++;
        }
        while(i<12) {
            buttons[i].setText("a");
            i++;
        }
    }

    private View.OnClickListener clickListenerButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button boutonCliqué = (Button) v;
            String lettreBouton = boutonCliqué.getText().toString();
            String temp = motADeviner.getText().toString();
            motADeviner.setText(temp + lettreBouton);
            boutonCliqué.setVisibility(View.INVISIBLE);
        }
    };

    private View.OnClickListener clickListenerRestart = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            motADeviner.setText("");
            for (int k = 0; k < 12; k++){
                buttons[k].setVisibility(View.VISIBLE);
            }
        }
    };

    private int getNewWord(){
        String test = new String();
        do {
            indice++;
            test = mots.get(indice).getMotsEn();
        }while (test.length() > 11);
        return (indice);
    };

    private View.OnClickListener clickListenerValidate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            valide();
        }
    };

    private void valide(){
        String motSolution = mots.get(indice).getMotsEn();
        if (motADeviner.getText().toString().equals(motSolution)) {
            score++;
            onRightAnswer();
        }
        else {
            onWrongAnswer();
        }
        updateView(mots.get(getNewWord()));
    };

    private void onRightAnswer(){
        toast = Toast.makeText(getBaseContext(), "Bonne réponse", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    };

    private void onWrongAnswer(){
        String toastText = "Réponse incorrecte, la(les) réponses\ncorrectes étaient :";
        int i;
        for (i = 0; i < mots.get(indice).getTabMotsEn().length; i++){
            toastText = toastText +"\n" + mots.get(indice).getTabMotsEn()[i];

        }
        toast = Toast.makeText(getBaseContext(), toastText, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    };

}